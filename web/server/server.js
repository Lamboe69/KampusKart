const express = require('express');
const cors = require('cors');
const path = require('path');
const multer = require('multer');

// Run seed on first load
require('./seed');

const authRoutes = require('./routes/auth');
const productRoutes = require('./routes/products');
const orderRoutes = require('./routes/orders');
const walletRoutes = require('./routes/wallet');
const shopRoutes = require('./routes/shops');
const adminRoutes = require('./routes/admin');
const notificationRoutes = require('./routes/notifications');
const messageRoutes = require('./routes/messages');
const db = require('./database');

const app = express();
const PORT = process.env.PORT || 3001;
const { WebSocketServer } = require('ws');
const jwt = require('jsonwebtoken');

const upload = multer({ dest: path.join(__dirname, '..', 'uploads') });

app.use(cors());
app.use(express.json({ limit: '50mb' }));
app.use('/uploads', express.static(path.join(__dirname, '..', 'uploads')));

// Serve static files from parent directory (for kampuskart.html)
app.use(express.static(path.join(__dirname, '..'), { index: 'kampuskart.html' }));

// Fallback — serve kampuskart.html for any non-API route (SPA support)
app.get(/^\/(?!api\/).*/, (req, res) => {
  res.sendFile(path.join(__dirname, '..', 'kampuskart.html'));
});

// API routes
app.use('/api/auth', authRoutes);
app.use('/api/products', productRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/wallet', walletRoutes);
app.use('/api/shops', shopRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api/notifications', notificationRoutes);
app.use('/api/messages', messageRoutes);

// Categories and campuses endpoints
const CATEGORIES = [
  { id: 'all', name: 'All', icon: '🔍' },
  { id: 'electronics', name: 'Electronics', icon: '📱' },
  { id: 'fashion', name: 'Fashion', icon: '👕' },
  { id: 'books', name: 'Books & Notes', icon: '📚' },
  { id: 'food', name: 'Food & Snacks', icon: '🍔' },
  { id: 'beauty', name: 'Beauty', icon: '💄' },
  { id: 'services', name: 'Services', icon: '🔧' },
  { id: 'furniture', name: 'Furniture', icon: '🪑' },
];

const CAMPUSES = [
  { id: 'makerere', name: 'Makerere University', location: 'Kampala' },
  { id: 'kyambogo', name: 'Kyambogo University', location: 'Kampala' },
  { id: 'muk', name: 'Mbarara University', location: 'Mbarara' },
  { id: 'gulu', name: 'Gulu University', location: 'Gulu' },
  { id: 'busitema', name: 'Busitema University', location: 'Busia' },
];

app.get('/api/categories', (req, res) => res.json({ categories: CATEGORIES }));
app.get('/api/campuses', (req, res) => res.json({ campuses: CAMPUSES }));

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

const { authenticateToken } = require('./middleware/auth');

// File upload endpoint
app.post('/api/upload', authenticateToken, upload.single('file'), (req, res) => {
  if (!req.file) return res.status(400).json({ error: 'No file uploaded' });
  res.json({ url: `/uploads/${req.file.filename}`, filename: req.file.originalname });
});

// Search autocomplete
app.get('/api/search/suggestions', (req, res) => {
  const q = req.query.q || '';
  if (q.length < 2) return res.json({ suggestions: [], categories: [] });
  const suggestions = db.prepare(`
    SELECT DISTINCT title FROM products
    WHERE status = 'active' AND title LIKE ? ORDER BY rating DESC LIMIT 8
  `).all(`%${q}%`).map(r => r.title);
  const categories = db.prepare(`
    SELECT DISTINCT category FROM products
    WHERE status = 'active' AND category LIKE ? LIMIT 4
  `).all(`%${q}%`).map(r => r.category);
  res.json({ suggestions, categories });
});

// Wishlist endpoints
app.get('/api/wishlist', authenticateToken, (req, res) => {
  const items = db.prepare(`
    SELECT p.*, w.id as wishlist_id FROM wishlists w
    JOIN products p ON p.id = w.product_id
    WHERE w.user_id = ? AND p.status = 'active'
    ORDER BY w.created_at DESC
  `).all(req.user.id);
  res.json({ items });
});
app.get('/api/wishlist/ids', authenticateToken, (req, res) => {
  const ids = db.prepare('SELECT product_id FROM wishlists WHERE user_id = ?').all(req.user.id).map(r => r.product_id);
  res.json({ ids });
});
app.post('/api/wishlist/:productId', authenticateToken, (req, res) => {
  try { db.prepare('INSERT OR IGNORE INTO wishlists (user_id, product_id) VALUES (?, ?)').run(req.user.id, req.params.productId); res.json({ success: true }); }
  catch { res.status(400).json({ error: 'Failed' }); }
});
app.delete('/api/wishlist/:productId', authenticateToken, (req, res) => {
  db.prepare('DELETE FROM wishlists WHERE user_id = ? AND product_id = ?').run(req.user.id, req.params.productId);
  res.json({ success: true });
});

// Review endpoints
app.post('/api/reviews', authenticateToken, (req, res) => {
  const { product_id, order_id, rating, comment } = req.body;
  if (!product_id || !rating) return res.status(400).json({ error: 'product_id and rating required' });
  const existing = db.prepare('SELECT id FROM reviews WHERE user_id = ? AND order_id = ?').get(req.user.id, order_id);
  if (existing) return res.status(400).json({ error: 'Already reviewed this order' });
  db.prepare('INSERT INTO reviews (product_id, user_id, order_id, rating, comment) VALUES (?, ?, ?, ?, ?)')
    .run(product_id, req.user.id, order_id || null, rating, comment || '');
  const avg = db.prepare('SELECT AVG(rating) as avg, COUNT(*) as cnt FROM reviews WHERE product_id = ?').get(product_id);
  db.prepare('UPDATE products SET rating = ?, reviews_count = ? WHERE id = ?').run(Math.round(avg.avg * 10) / 10, avg.cnt, product_id);
  res.status(201).json({ success: true });
});
app.get('/api/reviews/:productId', (req, res) => {
  const reviews = db.prepare('SELECT r.*, u.name as user_name, u.image as user_image FROM reviews r JOIN users u ON u.id = r.user_id WHERE r.product_id = ? ORDER BY r.created_at DESC').all(req.params.productId);
  res.json({ reviews });
});

// Global error handler
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

const server = app.listen(PORT, () => {
  console.log(`KampusKart API server running on http://localhost:${PORT}`);
  console.log(`API docs: http://localhost:${PORT}/api/health`);
});

// WebSocket chat server
const JWT_SECRET = require('./middleware/auth').JWT_SECRET;
const wss = new WebSocketServer({ server });
const clients = {};

// Push notification to a user if they're connected via WebSocket
function pushNotification(userId, notification) {
  if (clients[userId]) {
    clients[userId].send(JSON.stringify({ type: 'notification', notification }));
  }
}
global.pushNotification = pushNotification;

wss.on('connection', (ws, req) => {
  const url = new URL(req.url, 'http://localhost');
  const token = url.searchParams.get('token');
  if (!token) { ws.close(); return; }

  let userId;
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    userId = decoded.id;
  } catch { ws.close(); return; }

  clients[userId] = ws;

  ws.on('message', (data) => {
    try {
      const parsed = JSON.parse(data);

      // Typing indicator
      if (parsed.type === 'typing') {
        if (clients[parsed.receiver_id]) {
          clients[parsed.receiver_id].send(JSON.stringify({ type: 'typing', user_id: userId }));
        }
        return;
      }

      // Read receipt
      if (parsed.type === 'read') {
        db.prepare('UPDATE messages SET read = 1 WHERE sender_id = ? AND receiver_id = ?').run(parsed.sender_id, userId);
        if (clients[parsed.sender_id]) {
          clients[parsed.sender_id].send(JSON.stringify({ type: 'read_receipt', user_id: userId }));
        }
        return;
      }

      const { receiver_id, message, image } = parsed;
      if (!receiver_id) return;

      if (image) {
        const result = db.prepare('INSERT INTO messages (sender_id, receiver_id, message, image) VALUES (?, ?, ?, ?)')
          .run(userId, receiver_id, message || '', image);
        const msg = db.prepare('SELECT * FROM messages WHERE id = ?').get(result.lastInsertRowid);
        if (clients[receiver_id]) clients[receiver_id].send(JSON.stringify({ type: 'new_message', message: msg }));
        ws.send(JSON.stringify({ type: 'sent', message: msg }));
        return;
      }

      const result = db.prepare('INSERT INTO messages (sender_id, receiver_id, message) VALUES (?, ?, ?)')
        .run(userId, receiver_id, message);
      const msg = db.prepare('SELECT * FROM messages WHERE id = ?').get(result.lastInsertRowid);

      if (clients[receiver_id]) {
        clients[receiver_id].send(JSON.stringify({ type: 'new_message', message: msg }));
      }

      ws.send(JSON.stringify({ type: 'sent', message: msg }));
    } catch {}
  });

  ws.on('close', () => {
    delete clients[userId];
  });

  ws.send(JSON.stringify({ type: 'connected', userId }));
});

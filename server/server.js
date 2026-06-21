const express = require('express');
const cors = require('cors');
const path = require('path');

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

app.use(cors());
app.use(express.json());

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
      const { receiver_id, message } = JSON.parse(data);
      if (!receiver_id || !message) return;

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

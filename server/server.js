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

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

// Serve static files from parent directory (for kampuskart.html)
app.use(express.static(path.join(__dirname, '..')));

// API routes
app.use('/api/auth', authRoutes);
app.use('/api/products', productRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/wallet', walletRoutes);
app.use('/api/shops', shopRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api/notifications', notificationRoutes);

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

app.listen(PORT, () => {
  console.log(`KampusKart API server running on http://localhost:${PORT}`);
  console.log(`API docs: http://localhost:${PORT}/api/health`);
});

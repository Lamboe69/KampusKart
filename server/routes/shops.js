const express = require('express');
const db = require('../database');

const router = express.Router();

router.get('/', (req, res) => {
  const { type, search, campus } = req.query;

  let sql = 'SELECT id, name, type, rating, reviews_count as reviews, image, description, campus, verified, created_at FROM users WHERE type IN (?, ?)';
  let params = ['seller', 'shop'];

  if (type && type !== 'all') {
    sql += ' AND type = ?';
    params.push(type);
  }
  if (search) {
    sql += ' AND (name LIKE ? OR description LIKE ?)';
    params.push(`%${search}%`, `%${search}%`);
  }
  if (campus && campus !== 'all') {
    sql += ' AND campus = ?';
    params.push(campus);
  }

  sql += ' ORDER BY verified DESC, rating DESC';

  const sellers = db.prepare(sql).all(...params).map(s => {
    const productCount = db.prepare('SELECT COUNT(*) as count FROM products WHERE seller_id = ? AND status = ?').get(s.id, 'active').count;
    const salesCount = db.prepare('SELECT COUNT(*) as count FROM orders WHERE seller_id = ? AND status = ?').get(s.id, 'completed').count;
    return { ...s, products: productCount, sales: salesCount, since: new Date(s.created_at).getFullYear().toString() };
  });

  res.json({ shops: sellers, count: sellers.length });
});

router.get('/:id', (req, res) => {
  const seller = db.prepare('SELECT id, name, email, phone, type, rating, reviews_count, image, description, campus, verified, created_at FROM users WHERE id = ? AND type IN (?, ?)').get(req.params.id, 'seller', 'shop');
  if (!seller) return res.status(404).json({ error: 'Shop not found' });

  const products = db.prepare('SELECT * FROM products WHERE seller_id = ? AND status = ?').all(req.params.id, 'active').map(p => ({
    ...p, delivery_zones: JSON.parse(p.delivery_zones || '[]')
  }));

  const salesCount = db.prepare('SELECT COUNT(*) as count FROM orders WHERE seller_id = ? AND status = ?').get(req.params.id, 'completed').count;

  res.json({
    ...seller,
    products,
    sales: salesCount,
    since: new Date(seller.created_at).getFullYear().toString()
  });
});

module.exports = router;

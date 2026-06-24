const express = require('express');
const db = require('../database');

const router = express.Router();

router.get('/', (req, res) => {
  const { type, search, campus, category } = req.query;

  let sql = 'SELECT DISTINCT u.id, u.name, u.type, u.rating, u.reviews_count as reviews, u.image, u.description, u.campus, u.verified, u.created_at FROM users u';
  let params = [];

  if (category && category !== 'all') {
    sql += ' JOIN products p ON p.seller_id = u.id AND p.status = ? AND p.category = ?';
    params.push('active', category);
  }

  sql += ' WHERE u.type IN (?, ?)';
  params.push('seller', 'shop');

  if (type && type !== 'all') {
    sql += ' AND u.type = ?';
    params.push(type);
  }
  if (search) {
    sql += ' AND (u.name LIKE ? OR u.description LIKE ?)';
    params.push(`%${search}%`, `%${search}%`);
  }
  if (campus && campus !== 'all') {
    sql += ' AND u.campus = ?';
    params.push(campus);
  }

  sql += ' ORDER BY u.verified DESC, u.rating DESC';

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

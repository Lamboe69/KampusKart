const express = require('express');
const db = require('../database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

router.get('/', (req, res) => {
  const { category, campus, search, minPrice, maxPrice, sort, seller_id } = req.query;

  let sql = 'SELECT * FROM products WHERE status = ?';
  let params = ['active'];

  if (category && category !== 'all') {
    sql += ' AND category = ?';
    params.push(category);
  }
  if (campus && campus !== 'all') {
    sql += ' AND (campus = ? OR delivery_zones LIKE ?)';
    params.push(campus, `%"${campus}"%`);
  }
  if (search) {
    sql += ' AND (title LIKE ? OR seller_name LIKE ? OR campus LIKE ? OR category LIKE ?)';
    params.push(`%${search}%`, `%${search}%`, `%${search}%`, `%${search}%`);
  }
  if (minPrice) {
    sql += ' AND price >= ?';
    params.push(parseInt(minPrice));
  }
  if (maxPrice) {
    sql += ' AND price <= ?';
    params.push(parseInt(maxPrice));
  }
  if (seller_id) {
    sql += ' AND seller_id = ?';
    params.push(seller_id);
  }

  const sortMap = { 'price-low': 'price ASC', 'price_asc': 'price ASC', 'price-high': 'price DESC', 'price_desc': 'price DESC', 'rating': 'rating DESC', 'newest': 'created_at DESC' };
  sql += ' ORDER BY ' + (sortMap[sort] || 'created_at DESC');

  const products = db.prepare(sql).all(...params);
  res.json({ products, count: products.length });
});

router.get('/:id', (req, res) => {
  const product = db.prepare('SELECT * FROM products WHERE id = ?').get(req.params.id);
  if (!product) {
    return res.status(404).json({ error: 'Product not found' });
  }
  try { product.delivery_zones = JSON.parse(product.delivery_zones || '[]'); } catch (e) { product.delivery_zones = []; }
  try { product.images = JSON.parse(product.images || '[]'); } catch (e) { product.images = []; }
  res.json(product);
});

router.post('/', authenticateToken, (req, res) => {
  const { title, price, original_price, category, condition, description, delivery_zones, delivery_fee, return_policy, image, images } = req.body;

  if (!title || !price || !category) {
    return res.status(400).json({ error: 'Title, price, and category are required' });
  }

  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.user.id);
  if (!user) return res.status(404).json({ error: 'User not found' });

  const imagesArr = images && Array.isArray(images) ? images : (image ? [image] : []);
  const result = db.prepare(`
    INSERT INTO products (title, price, original_price, category, seller_id, seller_name, seller_type, campus, condition, description, delivery_zones, delivery_fee, return_policy, image, images, verified, status)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `).run(
    title, parseInt(price), original_price ? parseInt(original_price) : null,
    category, req.user.id, user.name, user.type === 'shop' ? 'shop' : 'individual',
    user.campus, condition || 'Brand New', description || '',
    JSON.stringify(delivery_zones || ['makerere']),
    parseInt(delivery_fee || 0), return_policy || 'no-returns',
    (imagesArr[0] || image) || '', JSON.stringify(imagesArr), user.verified, 'active'
  );

  const product = db.prepare('SELECT * FROM products WHERE id = ?').get(result.lastInsertRowid);
  res.status(201).json(product);
});

router.put('/:id', authenticateToken, (req, res) => {
  const product = db.prepare('SELECT * FROM products WHERE id = ? AND seller_id = ?').get(req.params.id, req.user.id);
  if (!product) {
    return res.status(404).json({ error: 'Product not found or not authorized' });
  }

  const { title, price, original_price, category, condition, description, delivery_zones, delivery_fee, return_policy, image, images, badge } = req.body;

  let finalImages = undefined;
  if (images && Array.isArray(images)) {
    finalImages = JSON.stringify(images);
  } else if (image) {
    const existing = db.prepare('SELECT images FROM products WHERE id = ?').get(req.params.id);
    const existingArr = existing?.images ? JSON.parse(existing.images) : [];
    if (existingArr.length === 0 || !existingArr.includes(image)) {
      finalImages = JSON.stringify([image, ...existingArr.filter(u => u !== image)]);
    }
  }

  db.prepare(`
    UPDATE products SET
      title = COALESCE(?, title), price = COALESCE(?, price),
      original_price = COALESCE(?, original_price), category = COALESCE(?, category),
      condition = COALESCE(?, condition), description = COALESCE(?, description),
      delivery_zones = COALESCE(?, delivery_zones), delivery_fee = COALESCE(?, delivery_fee),
      return_policy = COALESCE(?, return_policy), image = COALESCE(?, image),
      images = COALESCE(?, images), badge = COALESCE(?, badge)
    WHERE id = ?
  `)  .run(
    title, price !== undefined ? parseInt(price) : null,
    original_price !== undefined ? parseInt(original_price) : null, category, condition, description,
    delivery_zones !== undefined ? JSON.stringify(delivery_zones) : null,
    delivery_fee !== undefined ? parseInt(delivery_fee) : null, return_policy, image, finalImages, badge,
    req.params.id
  );

  const updated = db.prepare('SELECT * FROM products WHERE id = ?').get(req.params.id);
  res.json(updated);
});

router.delete('/:id', authenticateToken, (req, res) => {
  const product = db.prepare('SELECT * FROM products WHERE id = ? AND seller_id = ?').get(req.params.id, req.user.id);
  if (!product) {
    return res.status(404).json({ error: 'Product not found or not authorized' });
  }

  db.prepare('UPDATE products SET status = ? WHERE id = ?').run('suspended', req.params.id);
  res.json({ message: 'Product removed' });
});

module.exports = router;

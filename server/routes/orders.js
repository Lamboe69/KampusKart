const express = require('express');
const db = require('../database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

router.get('/', authenticateToken, (req, res) => {
  const { status } = req.query;
  let sql = 'SELECT * FROM orders WHERE buyer_id = ? OR seller_id = ?';
  let params = [req.user.id, req.user.id];

  if (status && status !== 'all') {
    sql += ' AND status = ?';
    params.push(status);
  }

  sql += ' ORDER BY created_at DESC';
  const orders = db.prepare(sql).all(...params);
  res.json({ orders, count: orders.length });
});

router.get('/:id', authenticateToken, (req, res) => {
  const order = db.prepare('SELECT * FROM orders WHERE id = ?').get(req.params.id);
  if (!order) return res.status(404).json({ error: 'Order not found' });

  if (order.buyer_id !== req.user.id && order.seller_id !== req.user.id && req.user.type !== 'admin') {
    return res.status(403).json({ error: 'Not authorized' });
  }

  res.json(order);
});

router.post('/', authenticateToken, (req, res) => {
  const { product_id, quantity, delivery_to, delivery_fee, payment_method } = req.body;

  if (!product_id || !delivery_to) {
    return res.status(400).json({ error: 'Product ID and delivery destination are required' });
  }

  const product = db.prepare('SELECT * FROM products WHERE id = ? AND status = ?').get(product_id, 'active');
  if (!product) return res.status(404).json({ error: 'Product not found or unavailable' });

  if (product.seller_id === req.user.id) {
    return res.status(400).json({ error: 'Cannot order your own product' });
  }

  const qty = quantity || 1;
  const fee = delivery_fee !== undefined ? delivery_fee : product.delivery_fee;
  const total = (product.price * qty) + fee;

  const orderId = `ORD-${Date.now().toString(36).toUpperCase()}`;

  db.prepare(`
    INSERT INTO orders (id, product_id, product_title, buyer_id, buyer_name, seller_id, seller_name, amount, delivery_fee, total, status, campus, delivery_to, quantity, payment_method)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `).run(
    orderId, product_id, product.title,
    req.user.id, req.user.name,
    product.seller_id, product.seller_name,
    product.price * qty, fee, total,
    'pending', product.campus, delivery_to, qty, payment_method || 'mobile_money'
  );

  // Add notification for seller
  db.prepare(`
    INSERT INTO notifications (user_id, type, message)
    VALUES (?, ?, ?)
  `).run(product.seller_id, 'order', `New order: ${product.title} by ${req.user.name}`);

  const order = db.prepare('SELECT * FROM orders WHERE id = ?').get(orderId);
  res.status(201).json(order);
});

router.put('/:id/status', authenticateToken, (req, res) => {
  const { status } = req.body;
  const validStatuses = ['pending', 'shipped', 'delivered', 'disputed', 'completed', 'cancelled'];

  if (!validStatuses.includes(status)) {
    return res.status(400).json({ error: 'Invalid status' });
  }

  const order = db.prepare('SELECT * FROM orders WHERE id = ?').get(req.params.id);
  if (!order) return res.status(404).json({ error: 'Order not found' });

  const isSeller = order.seller_id === req.user.id;
  const isBuyer = order.buyer_id === req.user.id;
  const isAdmin = req.user.type === 'admin';

  if (!isSeller && !isBuyer && !isAdmin) {
    return res.status(403).json({ error: 'Not authorized' });
  }

  // Valid transitions based on role
  if (isSeller && status === 'shipped' && order.status !== 'pending') {
    return res.status(400).json({ error: 'Can only ship pending orders' });
  }
  if (isBuyer && status === 'delivered' && order.status !== 'shipped' && order.status !== 'delivered') {
    return res.status(400).json({ error: 'Can only confirm delivery on shipped orders' });
  }
  if (isBuyer && status === 'disputed' && order.status !== 'shipped' && order.status !== 'delivered') {
    return res.status(400).json({ error: 'Can only dispute shipped or delivered orders' });
  }

  db.prepare('UPDATE orders SET status = ?, updated_at = datetime("now") WHERE id = ?')
    .run(status, req.params.id);

  // If order completed, release funds to seller
  if (status === 'completed') {
    const seller = db.prepare('SELECT * FROM users WHERE id = ?').get(order.seller_id);
    const platformFee = Math.floor(order.amount * 0.1);
    const sellerPayout = order.amount - platformFee;

    db.prepare('UPDATE users SET wallet_balance = wallet_balance + ?, wallet_pending = wallet_pending - ?, wallet_total_earned = wallet_total_earned + ? WHERE id = ?')
      .run(sellerPayout, order.amount, sellerPayout, order.seller_id);

    db.prepare('INSERT INTO transactions (user_id, type, amount, description, order_id) VALUES (?, ?, ?, ?, ?)')
      .run(order.seller_id, 'credit', sellerPayout, `Sale: ${order.product_title}`, order.id);

    db.prepare(`INSERT INTO notifications (user_id, type, message) VALUES (?, ?, ?)`)
      .run(order.seller_id, 'payment', `UGX ${sellerPayout.toLocaleString()} credited for ${order.product_title}`);
  }

  // If cancelled, refund buyer
  if (status === 'cancelled') {
    db.prepare('UPDATE users SET wallet_balance = wallet_balance + ? WHERE id = ?')
      .run(order.total, order.buyer_id);

    db.prepare('INSERT INTO transactions (user_id, type, amount, description, order_id) VALUES (?, ?, ?, ?, ?)')
      .run(order.buyer_id, 'refund', order.total, `Refund: ${order.product_title}`, order.id);
  }

  const updated = db.prepare('SELECT * FROM orders WHERE id = ?').get(req.params.id);
  res.json(updated);
});

module.exports = router;

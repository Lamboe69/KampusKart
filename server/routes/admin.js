const express = require('express');
const db = require('../database');
const { authenticateToken, requireAdmin } = require('../middleware/auth');

const router = express.Router();
router.use(authenticateToken, requireAdmin);

router.get('/stats', (req, res) => {
  const totalUsers = db.prepare('SELECT COUNT(*) as count FROM users').get().count;
  const totalProducts = db.prepare('SELECT COUNT(*) as count FROM products WHERE status = ?').get('active').count;
  const totalOrders = db.prepare('SELECT COUNT(*) as count FROM orders').get().count;
  const totalRevenue = db.prepare('SELECT COALESCE(SUM(amount), 0) as total FROM orders WHERE status = ?').get('completed').total;
  const pendingVerifications = db.prepare('SELECT COUNT(*) as count FROM verifications WHERE status = ?').get('pending').count;
  const openDisputes = db.prepare('SELECT COUNT(*) as count FROM disputes WHERE status = ?').get('open').count;
  const pendingPayouts = db.prepare("SELECT COUNT(*) as count FROM transactions WHERE type = ? AND status = ?").get('debit', 'pending').count;

  res.json({
    totalUsers, totalProducts, totalOrders, totalRevenue,
    pendingVerifications, openDisputes, pendingPayouts
  });
});

router.get('/verifications', (req, res) => {
  const verifications = db.prepare(`
    SELECT v.*, u.name as user_name, u.email, u.campus
    FROM verifications v JOIN users u ON v.user_id = u.id
    ORDER BY v.status ASC, v.submitted_at DESC
  `).all();
  res.json({ verifications });
});

router.put('/verifications/:id', (req, res) => {
  const { status } = req.body;
  if (!['approved', 'rejected'].includes(status)) {
    return res.status(400).json({ error: 'Status must be approved or rejected' });
  }

  const verification = db.prepare('SELECT * FROM verifications WHERE id = ?').get(req.params.id);
  if (!verification) return res.status(404).json({ error: 'Verification not found' });

  db.prepare('UPDATE verifications SET status = ? WHERE id = ?').run(status, req.params.id);

  if (status === 'approved') {
    db.prepare('UPDATE users SET verified = ? WHERE id = ?').run(1, verification.user_id);
    db.prepare(`INSERT INTO notifications (user_id, type, message) VALUES (?, ?, ?)`)
      .run(verification.user_id, 'system', 'Your verification has been approved! You can now sell with a trust badge.');
  } else {
    db.prepare(`INSERT INTO notifications (user_id, type, message) VALUES (?, ?, ?)`)
      .run(verification.user_id, 'system', 'Your verification was rejected. Please resubmit with correct documents.');
  }

  res.json({ message: `Verification ${status}` });
});

router.get('/disputes', (req, res) => {
  const disputes = db.prepare('SELECT * FROM disputes ORDER BY status ASC, created_at DESC').all();
  res.json({ disputes });
});

router.put('/disputes/:id', (req, res) => {
  const { status, resolution } = req.body;
  if (!['resolved', 'refunded'].includes(status)) {
    return res.status(400).json({ error: 'Status must be resolved or refunded' });
  }

  const dispute = db.prepare('SELECT * FROM disputes WHERE id = ?').get(req.params.id);
  if (!dispute) return res.status(404).json({ error: 'Dispute not found' });

  db.prepare('UPDATE disputes SET status = ?, resolution = ? WHERE id = ?').run(status, resolution || null, req.params.id);

  if (status === 'refunded') {
    // Refund the buyer
    db.prepare('UPDATE users SET wallet_balance = wallet_balance + ? WHERE id = ?').run(dispute.amount, dispute.buyer_id);
    db.prepare('UPDATE orders SET status = ? WHERE id = ?').run('disputed', dispute.order_id);
    db.prepare('INSERT INTO transactions (user_id, type, amount, description, order_id) VALUES (?, ?, ?, ?, ?)')
      .run(dispute.buyer_id, 'refund', dispute.amount, `Dispute refund: ${dispute.issue}`, dispute.order_id);
    db.prepare(`INSERT INTO notifications (user_id, type, message) VALUES (?, ?, ?)`)
      .run(dispute.buyer_id, 'payment', `UGX ${dispute.amount.toLocaleString()} refunded for dispute #${dispute.id}`);
  } else {
    // Release to seller
    db.prepare('UPDATE users SET wallet_balance = wallet_balance + ? WHERE id = ?').run(dispute.amount, dispute.seller_id);
    db.prepare('UPDATE orders SET status = ? WHERE id = ?').run('completed', dispute.order_id);
    db.prepare('INSERT INTO transactions (user_id, type, amount, description, order_id) VALUES (?, ?, ?, ?, ?)')
      .run(dispute.seller_id, 'credit', dispute.amount, `Dispute resolved: ${dispute.issue}`, dispute.order_id);
    db.prepare(`INSERT INTO notifications (user_id, type, message) VALUES (?, ?, ?)`)
      .run(dispute.seller_id, 'payment', `UGX ${dispute.amount.toLocaleString()} released from dispute #${dispute.id}`);
  }

  res.json({ message: `Dispute ${status}` });
});

router.get('/payouts', (req, res) => {
  const payouts = db.prepare(`
    SELECT t.*, u.name as user_name, u.phone, u.email FROM transactions t
    JOIN users u ON t.user_id = u.id
    WHERE t.type = ? AND t.status = ?
    ORDER BY t.created_at DESC
  `).all('debit', 'pending');
  res.json({ payouts });
});

router.put('/payouts/:id', (req, res) => {
  const tx = db.prepare('SELECT * FROM transactions WHERE id = ? AND type = ?').get(req.params.id, 'debit');
  if (!tx) return res.status(404).json({ error: 'Payout not found' });

  db.prepare('UPDATE transactions SET status = ? WHERE id = ?').run('completed', req.params.id);
  res.json({ message: 'Payout processed' });
});

router.get('/listings', (req, res) => {
  const products = db.prepare(`
    SELECT p.*, u.name as seller_name FROM products p
    JOIN users u ON p.seller_id = u.id
    ORDER BY p.created_at DESC LIMIT 50
  `).all();
  res.json({ listings: products });
});

router.put('/listings/:id/suspend', (req, res) => {
  const product = db.prepare('SELECT * FROM products WHERE id = ?').get(req.params.id);
  if (!product) return res.status(404).json({ error: 'Product not found' });

  const newStatus = product.status === 'active' ? 'suspended' : 'active';
  db.prepare('UPDATE products SET status = ? WHERE id = ?').run(newStatus, req.params.id);

  db.prepare(`INSERT INTO notifications (user_id, type, message) VALUES (?, ?, ?)`)
    .run(product.seller_id, 'system', `Your listing "${product.title}" has been ${newStatus === 'suspended' ? 'suspended' : 'reactivated'} by admin.`);

  res.json({ message: `Listing ${newStatus}`, status: newStatus });
});

module.exports = router;

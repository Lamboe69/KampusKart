const express = require('express');
const db = require('../database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

router.get('/', authenticateToken, (req, res) => {
  const user = db.prepare('SELECT wallet_balance, wallet_pending, wallet_total_earned FROM users WHERE id = ?').get(req.user.id);
  res.json({
    balance: user.wallet_balance,
    pending: user.wallet_pending,
    totalEarned: user.wallet_total_earned
  });
});

router.get('/transactions', authenticateToken, (req, res) => {
  const txs = db.prepare('SELECT * FROM transactions WHERE user_id = ? ORDER BY created_at DESC LIMIT 50').all(req.user.id);
  res.json({ transactions: txs });
});

router.post('/withdraw', authenticateToken, (req, res) => {
  const { amount, method, account_number } = req.body;

  if (!amount || amount < 10000) {
    return res.status(400).json({ error: 'Minimum withdrawal is UGX 10,000' });
  }

  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.user.id);
  if (user.wallet_balance < amount) {
    return res.status(400).json({ error: 'Insufficient balance' });
  }

  db.prepare('UPDATE users SET wallet_balance = wallet_balance - ? WHERE id = ?').run(amount, req.user.id);

  db.prepare(`
    INSERT INTO transactions (user_id, type, amount, description, status)
    VALUES (?, ?, ?, ?, ?)
  `).run(req.user.id, 'debit', amount, `Withdrawal to ${method || 'MTN MoMo'}${account_number ? ' (' + account_number + ')' : ''}`, 'pending');

  db.prepare(`
    INSERT INTO notifications (user_id, type, message)
    VALUES (?, ?, ?)
  `).run(req.user.id, 'payment', `UGX ${amount.toLocaleString()} withdrawal initiated. Processing within 24 hours.`);

  res.json({ message: 'Withdrawal initiated', amount });
});

router.post('/topup', authenticateToken, (req, res) => {
  const { amount } = req.body;

  if (!amount || amount < 1000) {
    return res.status(400).json({ error: 'Minimum top-up is UGX 1,000' });
  }

  db.prepare('UPDATE users SET wallet_balance = wallet_balance + ? WHERE id = ?').run(amount, req.user.id);

  db.prepare(`
    INSERT INTO transactions (user_id, type, amount, description)
    VALUES (?, ?, ?, ?)
  `).run(req.user.id, 'credit', amount, 'Wallet top-up via MTN MoMo');

  const user = db.prepare('SELECT wallet_balance FROM users WHERE id = ?').get(req.user.id);
  res.json({ message: 'Wallet topped up', balance: user.wallet_balance });
});

module.exports = router;

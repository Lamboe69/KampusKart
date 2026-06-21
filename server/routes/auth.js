const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');
const db = require('../database');
const { authenticateToken, JWT_SECRET } = require('../middleware/auth');

const router = express.Router();

router.post('/register', (req, res) => {
  const { name, email, phone, password, type, campus } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({ error: 'Name, email, and password are required' });
  }

  const existing = db.prepare('SELECT id FROM users WHERE email = ?').get(email);
  if (existing) {
    return res.status(409).json({ error: 'Email already registered' });
  }

  const id = uuidv4();
  const password_hash = bcrypt.hashSync(password, 10);
  const userType = (type === 'admin') ? 'buyer' : (type || 'buyer');
  const userCampus = campus || 'makerere';

  db.prepare(`
    INSERT INTO users (id, name, email, phone, password_hash, type, campus)
    VALUES (?, ?, ?, ?, ?, ?, ?)
  `).run(id, name, email, phone || '', password_hash, userType, userCampus);

  const token = jwt.sign(
    { id, name, email, type: userType, campus: userCampus },
    JWT_SECRET,
    { expiresIn: '7d' }
  );

  res.status(201).json({
    token,
    user: { id, name, email, phone: phone || '', type: userType, campus: userCampus, verified: 0, wallet_balance: 0 }
  });
});

router.post('/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  const user = db.prepare('SELECT * FROM users WHERE email = ?').get(email);
  if (!user) {
    return res.status(401).json({ error: 'Invalid email or password' });
  }

  if (!bcrypt.compareSync(password, user.password_hash)) {
    return res.status(401).json({ error: 'Invalid email or password' });
  }

  const token = jwt.sign(
    { id: user.id, name: user.name, email: user.email, type: user.type, campus: user.campus },
    JWT_SECRET,
    { expiresIn: '7d' }
  );

  res.json({
    token,
    user: {
      id: user.id, name: user.name, email: user.email, phone: user.phone,
      type: user.type, campus: user.campus, verified: user.verified,
      wallet_balance: user.wallet_balance, wallet_pending: user.wallet_pending,
      wallet_total_earned: user.wallet_total_earned
    }
  });
});

router.get('/me', authenticateToken, (req, res) => {
  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.user.id);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }

  res.json({
    id: user.id, name: user.name, email: user.email, phone: user.phone,
    type: user.type, campus: user.campus, verified: user.verified,
    wallet_balance: user.wallet_balance, wallet_pending: user.wallet_pending,
    wallet_total_earned: user.wallet_total_earned, avatar: user.avatar,
    description: user.description, created_at: user.created_at
  });
});

router.put('/profile', authenticateToken, (req, res) => {
  const { name, phone, campus, description } = req.body;
  db.prepare(`
    UPDATE users SET name = COALESCE(?, name), phone = COALESCE(?, phone),
    campus = COALESCE(?, campus), description = COALESCE(?, description)
    WHERE id = ?
  `).run(name, phone, campus, description, req.user.id);

  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.user.id);
  res.json({ message: 'Profile updated', user });
});

router.get('/user/:id', authenticateToken, (req, res) => {
  const user = db.prepare('SELECT id, name, email, phone, type, campus, image, rating, reviews_count, description, created_at FROM users WHERE id = ?').get(req.params.id);
  if (!user) return res.status(404).json({ error: 'User not found' });
  res.json({ user });
});

router.put('/change-password', authenticateToken, (req, res) => {
  const { current_password, new_password } = req.body;
  if (!current_password || !new_password) {
    return res.status(400).json({ error: 'Current and new password are required' });
  }
  if (new_password.length < 6) {
    return res.status(400).json({ error: 'New password must be at least 6 characters' });
  }
  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.user.id);
  if (!bcrypt.compareSync(current_password, user.password_hash)) {
    return res.status(401).json({ error: 'Current password is incorrect' });
  }
  const password_hash = bcrypt.hashSync(new_password, 10);
  db.prepare('UPDATE users SET password_hash = ? WHERE id = ?').run(password_hash, req.user.id);
  res.json({ message: 'Password changed successfully' });
});

router.delete('/account', authenticateToken, (req, res) => {
  const { password } = req.body;
  if (!password) {
    return res.status(400).json({ error: 'Password is required' });
  }
  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.user.id);
  if (!bcrypt.compareSync(password, user.password_hash)) {
    return res.status(401).json({ error: 'Password is incorrect' });
  }
  db.prepare('DELETE FROM users WHERE id = ?').run(req.user.id);
  res.json({ message: 'Account deleted' });
});

module.exports = router;

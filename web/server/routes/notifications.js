const express = require('express');
const db = require('../database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

router.get('/', authenticateToken, (req, res) => {
  const notifs = db.prepare('SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50').all(req.user.id);
  const unreadCount = notifs.filter(n => !n.read).length;
  res.json({ notifications: notifs, unreadCount });
});

router.put('/:id/read', authenticateToken, (req, res) => {
  const notif = db.prepare('SELECT * FROM notifications WHERE id = ? AND user_id = ?').get(req.params.id, req.user.id);
  if (!notif) return res.status(404).json({ error: 'Notification not found' });

  db.prepare('UPDATE notifications SET read = ? WHERE id = ?').run(1, req.params.id);
  res.json({ message: 'Marked as read' });
});

router.put('/read-all', authenticateToken, (req, res) => {
  db.prepare('UPDATE notifications SET read = ? WHERE user_id = ?').run(1, req.user.id);
  res.json({ message: 'All notifications marked as read' });
});

module.exports = router;

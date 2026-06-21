const express = require('express');
const db = require('../database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

router.get('/conversations', authenticateToken, (req, res) => {
  const parts = db.prepare(`
    SELECT DISTINCT CASE WHEN sender_id = ? THEN receiver_id ELSE sender_id END AS other_id
    FROM messages WHERE sender_id = ? OR receiver_id = ?
  `).all(req.user.id, req.user.id, req.user.id);
  const conversations = parts.map(p => {
    const userData = db.prepare('SELECT name, phone, image FROM users WHERE id = ?').get(p.other_id);
    const lastMsg = db.prepare(`SELECT message, created_at FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY created_at DESC LIMIT 1`).get(req.user.id, p.other_id, p.other_id, req.user.id);
    const unread = db.prepare('SELECT COUNT(*) as c FROM messages WHERE receiver_id = ? AND sender_id = ? AND read = 0').get(req.user.id, p.other_id).c;
    return {
      other_id: p.other_id,
      other_name: userData?.name || 'Unknown',
      other_phone: userData?.phone || '',
      other_image: userData?.image || null,
      last_message: lastMsg?.message || null,
      last_time: lastMsg?.created_at || null,
      unread
    };
  });
  conversations.sort((a, b) => {
    if (!a.last_time) return 1;
    if (!b.last_time) return -1;
    return b.last_time.localeCompare(a.last_time);
  });
  res.json({ conversations });
});

router.get('/:userId', authenticateToken, (req, res) => {
  const msgs = db.prepare(`
    SELECT * FROM messages
    WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)
    ORDER BY created_at ASC
  `).all(req.user.id, req.params.userId, req.params.userId, req.user.id);

  db.prepare('UPDATE messages SET read = 1 WHERE receiver_id = ? AND sender_id = ?').run(req.user.id, req.params.userId);

  res.json({ messages: msgs });
});

router.post('/', authenticateToken, (req, res) => {
  const { receiver_id, message } = req.body;
  if (!receiver_id || !message) return res.status(400).json({ error: 'receiver_id and message required' });

  const result = db.prepare(`
    INSERT INTO messages (sender_id, receiver_id, message) VALUES (?, ?, ?)
  `).run(req.user.id, receiver_id, message);

  const msg = db.prepare('SELECT * FROM messages WHERE id = ?').get(result.lastInsertRowid);
  res.status(201).json(msg);
});

module.exports = router;

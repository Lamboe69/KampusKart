const Database = require('better-sqlite3');
const path = require('path');

const dbPath = path.join(__dirname, 'kampuskart.db');
const db = new Database(dbPath);

db.pragma('journal_mode = WAL');
db.pragma('foreign_keys = ON');

db.exec(`
  CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    password_hash TEXT NOT NULL,
    type TEXT DEFAULT 'buyer' CHECK(type IN ('buyer','seller','shop','admin')),
    verified INTEGER DEFAULT 0,
    campus TEXT DEFAULT 'makerere',
    wallet_balance INTEGER DEFAULT 0,
    wallet_pending INTEGER DEFAULT 0,
    wallet_total_earned INTEGER DEFAULT 0,
    avatar TEXT,
    description TEXT,
    created_at TEXT DEFAULT (datetime('now'))
  );

  CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    price INTEGER NOT NULL,
    original_price INTEGER,
    category TEXT NOT NULL,
    seller_id TEXT NOT NULL,
    seller_name TEXT NOT NULL,
    seller_type TEXT DEFAULT 'individual' CHECK(seller_type IN ('individual','shop')),
    campus TEXT NOT NULL,
    rating REAL DEFAULT 0,
    reviews_count INTEGER DEFAULT 0,
    image TEXT,
    condition TEXT DEFAULT 'Brand New',
    delivery_zones TEXT DEFAULT '[]',
    delivery_fee INTEGER DEFAULT 0,
    verified INTEGER DEFAULT 0,
    badge TEXT,
    description TEXT,
    return_policy TEXT DEFAULT 'no-returns',
    status TEXT DEFAULT 'active' CHECK(status IN ('active','suspended')),
    created_at TEXT DEFAULT (datetime('now'))
  );

  CREATE TABLE IF NOT EXISTS orders (
    id TEXT PRIMARY KEY,
    product_id INTEGER,
    product_title TEXT NOT NULL,
    buyer_id TEXT NOT NULL,
    buyer_name TEXT NOT NULL,
    seller_id TEXT NOT NULL,
    seller_name TEXT NOT NULL,
    amount INTEGER NOT NULL,
    delivery_fee INTEGER DEFAULT 0,
    total INTEGER NOT NULL,
    status TEXT DEFAULT 'pending' CHECK(status IN ('pending','shipped','delivered','disputed','completed','cancelled')),
    campus TEXT,
    delivery_to TEXT,
    delivery_address TEXT,
    payment_method TEXT,
    quantity INTEGER DEFAULT 1,
    created_at TEXT DEFAULT (datetime('now')),
    updated_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (product_id) REFERENCES products(id)
  );

  CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    type TEXT NOT NULL CHECK(type IN ('credit','debit','refund')),
    amount INTEGER NOT NULL,
    description TEXT,
    status TEXT DEFAULT 'completed',
    order_id TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (user_id) REFERENCES users(id)
  );

  CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    type TEXT DEFAULT 'system' CHECK(type IN ('order','message','payment','system')),
    message TEXT NOT NULL,
    read INTEGER DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (user_id) REFERENCES users(id)
  );

  CREATE TABLE IF NOT EXISTS disputes (
    id TEXT PRIMARY KEY,
    order_id TEXT NOT NULL,
    buyer_id TEXT NOT NULL,
    buyer_name TEXT NOT NULL,
    seller_id TEXT NOT NULL,
    seller_name TEXT NOT NULL,
    issue TEXT NOT NULL,
    amount INTEGER NOT NULL,
    status TEXT DEFAULT 'open' CHECK(status IN ('open','resolved','refunded')),
    resolution TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (order_id) REFERENCES orders(id)
  );

  CREATE TABLE IF NOT EXISTS verifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    name TEXT NOT NULL,
    type TEXT CHECK(type IN ('student','business')),
    campus TEXT,
    document_url TEXT,
    status TEXT DEFAULT 'pending' CHECK(status IN ('pending','approved','rejected')),
    submitted_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (user_id) REFERENCES users(id)
  );

  CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_id TEXT NOT NULL,
    receiver_id TEXT NOT NULL,
    message TEXT NOT NULL,
    read INTEGER DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now'))
  );

  CREATE TABLE IF NOT EXISTS reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    user_id TEXT NOT NULL,
    order_id TEXT,
    rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
  );

  CREATE TABLE IF NOT EXISTS wishlists (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    product_id INTEGER NOT NULL,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE(user_id, product_id)
  );
`);

// Add shop-related columns if they don't exist
try { db.exec('ALTER TABLE users ADD COLUMN image TEXT'); } catch(e) {}
try { db.exec('ALTER TABLE users ADD COLUMN rating REAL DEFAULT 0'); } catch(e) {}
try { db.exec('ALTER TABLE users ADD COLUMN reviews_count INTEGER DEFAULT 0'); } catch(e) {}
try { db.exec('ALTER TABLE messages ADD COLUMN image TEXT'); } catch(e) {}
try { db.exec('ALTER TABLE reviews ADD COLUMN order_id TEXT'); } catch(e) {}

module.exports = db;

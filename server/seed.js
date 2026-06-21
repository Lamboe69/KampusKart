const db = require('./database');
const bcrypt = require('bcryptjs');
const { v4: uuidv4 } = require('uuid');

function seed() {
  const userCount = db.prepare('SELECT COUNT(*) as count FROM users').get().count;
  if (userCount > 0) {
    console.log('Database already seeded. Skipping.');
    return;
  }

  console.log('Seeding database...');
  const hash = bcrypt.hashSync('password123', 10);

  const shopImages = {
    makerere: 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=400&h=400&fit=crop',
    kyambogo: 'https://images.unsplash.com/photo-1556742111-a301076d9d18?w=400&h=400&fit=crop',
    muk: 'https://images.unsplash.com/photo-1556761175-b413da4baf72?w=400&h=400&fit=crop',
    gulu: 'https://images.unsplash.com/photo-1567401893414-76b7b1e5a7a5?w=400&h=400&fit=crop',
    busitema: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400&h=400&fit=crop',
    fashion: 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=400&h=400&fit=crop',
    books: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400&h=400&fit=crop',
    tech: 'https://images.unsplash.com/photo-1468495244123-6c6c332eeece?w=400&h=400&fit=crop',
    beauty: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400&h=400&fit=crop',
    food: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=400&fit=crop',
    services: 'https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=400&h=400&fit=crop',
    furniture: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop',
  };
  const sellerAvatars = [
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop',
    'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&h=400&fit=crop',
    'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=400&fit=crop',
    'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&h=400&fit=crop',
    'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=400&fit=crop',
    'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&h=400&fit=crop',
  ];

  const users = [
    { id: uuidv4(), name: 'Alex Okello', email: 'alex@mak.ac.ug', phone: '+256 700 123 456', type: 'seller', campus: 'makerere', verified: 1, wallet_balance: 245000, wallet_pending: 85000, wallet_total_earned: 1280000, avatar: sellerAvatars[0], description: 'Selling quality electronics and gadgets at student-friendly prices. Fast delivery across Makerere.', rating: 4.3, reviews: 47, image: null },
    { id: uuidv4(), name: 'Admin User', email: 'admin@kampuskart.ug', phone: '+256 700 000 001', type: 'admin', campus: 'makerere', verified: 1, wallet_balance: 0, wallet_pending: 0, wallet_total_earned: 0, avatar: null, description: null, rating: 0, reviews: 0, image: null },
    { id: uuidv4(), name: 'Sarah N.', email: 'sarah@kyambogo.ac.ug', phone: '+256 700 111 222', type: 'seller', campus: 'kyambogo', verified: 0, wallet_balance: 85000, wallet_pending: 12000, wallet_total_earned: 450000, avatar: sellerAvatars[1], description: 'Affordable fashion and beauty products for campus queens. New arrivals weekly!', rating: 4.1, reviews: 32, image: null },
    { id: uuidv4(), name: 'David M.', email: 'david@kyambogo.ac.ug', phone: '+256 700 222 333', type: 'seller', campus: 'kyambogo', verified: 1, wallet_balance: 120000, wallet_pending: 30000, wallet_total_earned: 680000, avatar: sellerAvatars[2], description: 'Textbooks, study materials and academic resources. Helping students succeed.', rating: 4.6, reviews: 58, image: null },
    { id: uuidv4(), name: 'Grace A.', email: 'grace@muk.ac.ug', phone: '+256 700 333 444', type: 'seller', campus: 'muk', verified: 0, wallet_balance: 45000, wallet_pending: 15000, wallet_total_earned: 320000, avatar: sellerAvatars[3], description: 'Homemade meals and fresh snacks delivered to your hall. Order daily!', rating: 4.0, reviews: 21, image: null },
    { id: uuidv4(), name: 'PhoneHub Uganda', email: 'phonehub@kampuskart.ug', phone: '+256 700 444 555', type: 'shop', campus: 'kyambogo', verified: 1, wallet_balance: 340000, wallet_pending: 95000, wallet_total_earned: 2100000, avatar: null, description: 'Uganda\'s #1 phone and gadget store on campus. iPhones, Samsung, Tecno, and accessories. Warranty included on all devices.', rating: 4.7, reviews: 203, image: shopImages.kyambogo },
    { id: uuidv4(), name: 'BookWorm Store', email: 'bookworm@kampuskart.ug', phone: '+256 700 555 666', type: 'shop', campus: 'busitema', verified: 1, wallet_balance: 180000, wallet_pending: 42000, wallet_total_earned: 1500000, avatar: null, description: 'Your one-stop shop for textbooks, novels, and academic resources. New and used books at unbeatable prices. Free bookmark with every order!', rating: 4.5, reviews: 156, image: shopImages.busitema },
    { id: uuidv4(), name: 'Jane Buyer', email: 'jane@mak.ac.ug', phone: '+256 700 777 888', type: 'buyer', campus: 'makerere', verified: 1, wallet_balance: 500000, wallet_pending: 0, wallet_total_earned: 0, avatar: null, description: null, rating: 0, reviews: 0, image: null },
    { id: uuidv4(), name: 'Moses K.', email: 'moses@gulu.ac.ug', phone: '+256 700 888 999', type: 'seller', campus: 'gulu', verified: 1, wallet_balance: 75000, wallet_pending: 20000, wallet_total_earned: 390000, avatar: sellerAvatars[4], description: 'Furniture and home essentials for hostels. Quality pieces delivered to your door.', rating: 4.4, reviews: 35, image: null },
    { id: uuidv4(), name: 'Fatuma S.', email: 'fatuma@busitema.ac.ug', phone: '+256 700 999 000', type: 'seller', campus: 'busitema', verified: 1, wallet_balance: 60000, wallet_pending: 10000, wallet_total_earned: 280000, avatar: sellerAvatars[5], description: 'Fresh food and catering services for campus events. Bulk orders welcome!', rating: 4.2, reviews: 28, image: null },
    // New shops
    { id: uuidv4(), name: 'TechConnect Hub', email: 'techconnect@kampuskart.ug', phone: '+256 701 111 222', type: 'shop', campus: 'makerere', verified: 1, wallet_balance: 520000, wallet_pending: 110000, wallet_total_earned: 3200000, avatar: null, description: 'Makerere\'s premier tech store. Laptops, tablets, headphones, and computer accessories. Student discounts every semester!', rating: 4.6, reviews: 178, image: shopImages.makerere },
    { id: uuidv4(), name: 'Campus Bites', email: 'campusbites@kampuskart.ug', phone: '+256 701 333 444', type: 'shop', campus: 'muk', verified: 1, wallet_balance: 95000, wallet_pending: 25000, wallet_total_earned: 890000, avatar: null, description: 'Fresh, tasty meals delivered to MUBS and main campus. Rolex, Pilau, Chips, and daily specials. Order before 10am for lunch!', rating: 4.3, reviews: 89, image: shopImages.muk },
    { id: uuidv4(), name: 'Northern Essentials', email: 'northern@kampuskart.ug', phone: '+256 701 555 666', type: 'shop', campus: 'gulu', verified: 1, wallet_balance: 210000, wallet_pending: 45000, wallet_total_earned: 1250000, avatar: null, description: 'Northern Uganda\'s biggest campus shop. Fashion, furniture, and electronics. We deliver across Gulu University and beyond.', rating: 4.4, reviews: 112, image: shopImages.gulu },
    // 10 new shops
    { id: uuidv4(), name: 'Fashion Forward', email: 'fashionforward@kampuskart.ug', phone: '+256 702 111 001', type: 'shop', campus: 'makerere', verified: 1, wallet_balance: 195000, wallet_pending: 48000, wallet_total_earned: 980000, avatar: null, description: 'Trendy fashion for the modern campus student. Sneakers, streetwear, bags, and accessories. New drops every Friday!', rating: 4.2, reviews: 67, image: shopImages.fashion },
    { id: uuidv4(), name: 'GreenLeaf Books', email: 'greenleaf@kampuskart.ug', phone: '+256 702 111 002', type: 'shop', campus: 'kyambogo', verified: 1, wallet_balance: 142000, wallet_pending: 31000, wallet_total_earned: 720000, avatar: null, description: 'Eco-friendly campus bookstore. Buy, sell, and rent textbooks. We also stock novels, journals, and stationery at the best prices.', rating: 4.5, reviews: 94, image: shopImages.books },
    { id: uuidv4(), name: 'GadgetZone', email: 'gadgetzone@kampuskart.ug', phone: '+256 702 111 003', type: 'shop', campus: 'muk', verified: 1, wallet_balance: 380000, wallet_pending: 82000, wallet_total_earned: 1850000, avatar: null, description: 'All your gadgets in one place. From headphones to smartwatches, phone cases to chargers. Quality guaranteed with warranty.', rating: 4.3, reviews: 145, image: shopImages.tech },
    { id: uuidv4(), name: 'Beauty Bliss', email: 'beautybliss@kampuskart.ug', phone: '+256 702 111 004', type: 'shop', campus: 'busitema', verified: 1, wallet_balance: 88000, wallet_pending: 19000, wallet_total_earned: 410000, avatar: null, description: 'Your campus beauty destination. Cosmetics, skincare, perfumes, and hair products from top brands. Free makeover tips with every purchase!', rating: 4.1, reviews: 53, image: shopImages.beauty },
    { id: uuidv4(), name: 'FurnitureHub', email: 'furniturehub@kampuskart.ug', phone: '+256 702 111 005', type: 'shop', campus: 'makerere', verified: 1, wallet_balance: 275000, wallet_pending: 60000, wallet_total_earned: 1340000, avatar: null, description: 'Furnishing campus hostels since 2024. Desks, beds, chairs, shelves, and lighting. Assembly included. Pay in 3 installments!', rating: 4.6, reviews: 88, image: shopImages.furniture },
    { id: uuidv4(), name: 'SnackAttack', email: 'snackattack@kampuskart.ug', phone: '+256 702 111 006', type: 'shop', campus: 'gulu', verified: 1, wallet_balance: 45000, wallet_pending: 12000, wallet_total_earned: 230000, avatar: null, description: 'Fastest snack delivery in Gulu. Mandazi, samosas, chips, drinks, and sweet treats. Open late for night crammers!', rating: 4.0, reviews: 41, image: shopImages.food },
    { id: uuidv4(), name: 'FixIt Services', email: 'fixit@kampuskart.ug', phone: '+256 702 111 007', type: 'shop', campus: 'kyambogo', verified: 0, wallet_balance: 62000, wallet_pending: 15000, wallet_total_earned: 310000, avatar: null, description: 'Phone repairs, laptop servicing, and gadget accessories. We fix what others throw away. Free diagnostics, pay only if fixed!', rating: 4.4, reviews: 76, image: shopImages.services },
    { id: uuidv4(), name: 'Vintage Threads', email: 'vintagethreads@kampuskart.ug', phone: '+256 702 111 008', type: 'shop', campus: 'muk', verified: 1, wallet_balance: 134000, wallet_pending: 28000, wallet_total_earned: 670000, avatar: null, description: 'Curated vintage and thrift fashion for the budget-conscious student. Unique pieces you won\'t find anywhere else on campus.', rating: 4.2, reviews: 59, image: shopImages.fashion },
    { id: uuidv4(), name: 'Campus Computers', email: 'campuscomp@kampuskart.ug', phone: '+256 702 111 009', type: 'shop', campus: 'makerere', verified: 1, wallet_balance: 510000, wallet_pending: 115000, wallet_total_earned: 2600000, avatar: null, description: 'Makerere\'s #1 computer shop. Laptops, desktops, and accessories for students. Student financing available. Free MS Office with every laptop!', rating: 4.7, reviews: 231, image: shopImages.tech },
    { id: uuidv4(), name: 'FreshMart', email: 'freshmart@kampuskart.ug', phone: '+256 702 111 010', type: 'shop', campus: 'busitema', verified: 1, wallet_balance: 73000, wallet_pending: 18000, wallet_total_earned: 380000, avatar: null, description: 'Farm-fresh produce and groceries delivered to Busitema campus. Fruits, vegetables, dairy, and essentials. Order by 8am for same-day delivery.', rating: 4.3, reviews: 65, image: shopImages.food },
  ];

  const insertUser = db.prepare(`
    INSERT INTO users (id, name, email, phone, password_hash, type, verified, campus, wallet_balance, wallet_pending, wallet_total_earned, avatar, description, image, rating, reviews_count)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);
  for (const u of users) {
    insertUser.run(u.id, u.name, u.email, u.phone, hash, u.type, u.verified, u.campus, u.wallet_balance, u.wallet_pending, u.wallet_total_earned, u.avatar, u.description, u.image, u.rating, u.reviews);
  }

  // ── Product templates per category with matching Unsplash images ──────────

  const electronics = [
    { title: 'iPhone 16 Pro Max 512GB', price: 3200000, img: 'https://images.unsplash.com/photo-1678685888221-cda773a3dcdb?w=400&h=400&fit=crop' },
    { title: 'Samsung Galaxy S25 Ultra', price: 2900000, img: 'https://images.unsplash.com/photo-1610945264803-c22b62d2a7b3?w=400&h=400&fit=crop' },
    { title: 'MacBook Pro 16 M4 Pro', price: 5200000, img: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop' },
    { title: 'Dell XPS 15 Laptop', price: 2200000, img: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop' },
    { title: 'HP Spectre x360 14', price: 2400000, img: 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=400&h=400&fit=crop' },
    { title: 'Lenovo ThinkPad X1 Carbon', price: 2800000, img: 'https://images.unsplash.com/photo-1530893609608-32a9af3aa95c?w=400&h=400&fit=crop' },
    { title: 'iPad Air M3 11-inch', price: 1800000, img: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop' },
    { title: 'Samsung Galaxy Tab S10+', price: 1500000, img: 'https://images.unsplash.com/photo-1561154464-82e9adf32764?w=400&h=400&fit=crop' },
    { title: 'Sony WH-1000XM6 Headphones', price: 520000, img: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop' },
    { title: 'AirPods Pro 3', price: 380000, img: 'https://images.unsplash.com/photo-1603351154351-5e2d0600bb77?w=400&h=400&fit=crop' },
    { title: 'JBL Charge 6 Speaker', price: 320000, img: 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400&h=400&fit=crop' },
    { title: 'Bose SoundLink Max', price: 450000, img: 'https://images.unsplash.com/photo-1589003077984-894e133dabab?w=400&h=400&fit=crop' },
    { title: 'Canon EOS R50 Camera', price: 2200000, img: 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400&h=400&fit=crop' },
    { title: 'Sony Alpha A7 IV', price: 3800000, img: 'https://images.unsplash.com/photo-1510127034890-ba27508e9f1c?w=400&h=400&fit=crop' },
    { title: '27-inch 4K Monitor Dell', price: 950000, img: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400&h=400&fit=crop' },
    { title: 'Samsung 32" Odyssey G7', price: 1200000, img: 'https://images.unsplash.com/photo-1540821927689-2ee4c24d0bf5?w=400&h=400&fit=crop' },
    { title: 'Apple Watch Ultra 3', price: 1200000, img: 'https://images.unsplash.com/photo-1523270185683-eec4fc0a810d?w=400&h=400&fit=crop' },
    { title: 'Samsung Galaxy Watch 7', price: 650000, img: 'https://images.unsplash.com/photo-1546868871-af0de0ae72d8?w=400&h=400&fit=crop' },
    { title: 'Tecno Camon 30 Pro', price: 580000, img: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop' },
    { title: 'Xiaomi Redmi Note 14 Pro', price: 850000, img: 'https://images.unsplash.com/photo-1601784551446-20c9e07cdbdb?w=400&h=400&fit=crop' },
    { title: 'PS5 Slim Console', price: 2500000, img: 'https://images.unsplash.com/photo-1486401899868-0e435ed85128?w=400&h=400&fit=crop' },
    { title: 'Xbox Series X Console', price: 2300000, img: 'https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=400&h=400&fit=crop' },
    { title: 'Nintendo Switch OLED', price: 1800000, img: 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=400&h=400&fit=crop' },
    { title: 'DJI Mini 4 Pro Drone', price: 2800000, img: 'https://images.unsplash.com/photo-1579829366248-204fe8413f31?w=400&h=400&fit=crop' },
    { title: 'Logitech MX Master 3S', price: 180000, img: 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=400&fit=crop' },
    { title: 'Mechanical Keyboard RGB', price: 150000, img: 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400&h=400&fit=crop' },
    { title: 'HP LaserJet Pro M404', price: 850000, img: 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=400&h=400&fit=crop' },
    { title: 'Epson L3250 Printer', price: 450000, img: 'https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=400&h=400&fit=crop' },
    { title: 'Anker Power Bank 26800mAh', price: 120000, img: 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400&h=400&fit=crop' },
    { title: 'WD 2TB External SSD', price: 320000, img: 'https://images.unsplash.com/photo-1531492746076-161ca9bcad58?w=400&h=400&fit=crop' },
  ];

  const fashion = [
    { title: "Levi's 511 Slim Jeans", price: 95000, img: 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=400&fit=crop' },
    { title: 'Nike Air Force 1 Low', price: 280000, img: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop' },
    { title: 'Adidas Ultraboost Light', price: 320000, img: 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400&h=400&fit=crop' },
    { title: 'Puma RS-X Sneakers', price: 220000, img: 'https://images.unsplash.com/photo-1597045566677-8cf4eda8a0a2?w=400&h=400&fit=crop' },
    { title: "Men's Oxford Shirt White", price: 55000, img: 'https://images.unsplash.com/photo-1602810316693-3667c854239a?w=400&h=400&fit=crop' },
    { title: "Women's Floral Summer Dress", price: 65000, img: 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=400&h=400&fit=crop' },
    { title: 'Adidas Classic Backpack', price: 85000, img: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop' },
    { title: 'Ray-Ban Aviator Sunglasses', price: 150000, img: 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400&h=400&fit=crop' },
    { title: 'Leather Messenger Bag', price: 95000, img: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=400&h=400&fit=crop' },
    { title: 'Vintage Denim Jacket', price: 75000, img: 'https://images.unsplash.com/photo-1551537482-f2075a1d41f2?w=400&h=400&fit=crop' },
    { title: 'Nike Dri-FIT T-Shirt', price: 45000, img: 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=400&h=400&fit=crop' },
    { title: "Men's Chino Pants Khaki", price: 65000, img: 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400&h=400&fit=crop' },
    { title: 'Hoodie Cotton Oversized', price: 85000, img: 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=400&fit=crop' },
    { title: "Women's Blazer Office", price: 120000, img: 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=400&h=400&fit=crop' },
    { title: 'Casio G-Shock Watch', price: 180000, img: 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400&h=400&fit=crop' },
    { title: 'Leather Belt Brown', price: 35000, img: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop' },
    { title: 'Nike Pro Shorts', price: 40000, img: 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=400&h=400&fit=crop' },
    { title: "Women's Handbag Tote", price: 110000, img: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400&h=400&fit=crop' },
    { title: 'Converse Chuck Taylor All Star', price: 180000, img: 'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=400&h=400&fit=crop' },
    { title: "Men's Suit 2-Piece Slim Fit", price: 350000, img: 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=400&h=400&fit=crop' },
    { title: 'Baseball Cap Snapback', price: 25000, img: 'https://images.unsplash.com/photo-1521369909029-2afed882baee?w=400&h=400&fit=crop' },
    { title: 'Scarf Wool Winter', price: 30000, img: 'https://images.unsplash.com/photo-1520903920243-00d872a2d1c1?w=400&h=400&fit=crop' },
    { title: 'Silver Chain Necklace', price: 45000, img: 'https://images.unsplash.com/photo-1515562141589-6771c1d0c2f0?w=400&h=400&fit=crop' },
    { title: "Women's Sandals Flat", price: 35000, img: 'https://images.unsplash.com/photo-1594322436404-5a0526db4d13?w=400&h=400&fit=crop' },
    { title: 'Tommy Hilfiger Polo Shirt', price: 85000, img: 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=400&h=400&fit=crop' },
    { title: 'Cargo Pants Army Green', price: 55000, img: 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400&h=400&fit=crop' },
    { title: 'Winter Puffer Jacket', price: 180000, img: 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400&h=400&fit=crop' },
    { title: "Men's Loafers Brown", price: 120000, img: 'https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400&h=400&fit=crop' },
    { title: "Women's Jumpsuit Casual", price: 70000, img: 'https://images.unsplash.com/photo-1596728325488-58c87691e9af?w=400&h=400&fit=crop' },
    { title: 'Gold Hoop Earrings', price: 25000, img: 'https://images.unsplash.com/photo-1630019852942-f89202989a59?w=400&h=400&fit=crop' },
  ];

  const books = [
    { title: 'Clean Code by Robert C. Martin', price: 45000, img: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop' },
    { title: 'Engineering Mathematics Vol 1 & 2', price: 65000, img: 'https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=400&fit=crop' },
    { title: 'Business Law & Practice Textbook', price: 55000, img: 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400&h=400&fit=crop' },
    { title: 'Anatomy & Physiology 11th Ed', price: 95000, img: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=400&fit=crop' },
    { title: 'Introduction to Algorithms CLRS', price: 80000, img: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=400&fit=crop' },
    { title: 'Principles of Economics Mankiw', price: 60000, img: 'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400&h=400&fit=crop' },
    { title: 'Campbell Biology 12th Edition', price: 90000, img: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400&h=400&fit=crop' },
    { title: 'ACCA F7 Financial Reporting', price: 70000, img: 'https://images.unsplash.com/photo-1550399105-c4db5fb85c18?w=400&h=400&fit=crop' },
    { title: 'Physics for Scientists & Engineers', price: 85000, img: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=400&fit=crop' },
    { title: 'Organic Chemistry 8th Edition', price: 75000, img: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop' },
    { title: 'Calculus Early Transcendentals', price: 70000, img: 'https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=400&fit=crop' },
    { title: 'Microbiology Principles & Explorations', price: 88000, img: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=400&fit=crop' },
    { title: '财务会计基础 (Chinese)', price: 45000, img: 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400&h=400&fit=crop' },
    { title: 'Statistics for Business & Economics', price: 65000, img: 'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400&h=400&fit=crop' },
    { title: 'Database Systems Complete Book', price: 72000, img: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=400&fit=crop' },
    { title: 'Software Engineering Pressman', price: 68000, img: 'https://images.unsplash.com/photo-1550399105-c4db5fb85c18?w=400&h=400&fit=crop' },
    { title: 'Biochemistry Stryer 9th Ed', price: 95000, img: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=400&fit=crop' },
    { title: 'Communication Skills Handbook', price: 35000, img: 'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400&h=400&fit=crop' },
    { title: 'Thermodynamics Cengel 8th', price: 78000, img: 'https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=400&fit=crop' },
    { title: 'Introduction to Psychology', price: 55000, img: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop' },
    { title: 'Fluid Mechanics Frank White', price: 82000, img: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=400&fit=crop' },
    { title: 'Management 14th Ed Robbins', price: 60000, img: 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400&h=400&fit=crop' },
    { title: 'Python for Data Analysis', price: 55000, img: 'https://images.unsplash.com/photo-1550399105-c4db5fb85c18?w=400&h=400&fit=crop' },
    { title: 'Linear Algebra & Its Applications', price: 58000, img: 'https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=400&fit=crop' },
    { title: 'Essentials of Marketing Research', price: 62000, img: 'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400&h=400&fit=crop' },
    { title: "Gray's Anatomy for Students", price: 110000, img: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=400&fit=crop' },
    { title: 'Modern Control Engineering', price: 72000, img: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=400&fit=crop' },
    { title: 'International Relations Theory', price: 50000, img: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop' },
    { title: 'Data Structures & Algorithms Java', price: 65000, img: 'https://images.unsplash.com/photo-1550399105-c4db5fb85c18?w=400&h=400&fit=crop' },
    { title: 'Environmental Science 16th Ed', price: 70000, img: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400&h=400&fit=crop' },
  ];

  const food = [
    { title: 'Chicken Pilau Full Plate', price: 12000, img: 'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=400&h=400&fit=crop' },
    { title: 'Rolex Eggs and Chapati', price: 3500, img: 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=400&h=400&fit=crop' },
    { title: 'Fresh Mandazi 12 Pieces', price: 5000, img: 'https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=400&h=400&fit=crop' },
    { title: 'Beef Stew and Rice Combo', price: 10000, img: 'https://images.unsplash.com/photo-1547592180-85f173990554?w=400&h=400&fit=crop' },
    { title: 'Homemade Groundnut Paste 500g', price: 8000, img: 'https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=400&h=400&fit=crop' },
    { title: 'Samosa Pack 10 Pieces', price: 7000, img: 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400&h=400&fit=crop' },
    { title: 'Fresh Mango Juice 1 Litre', price: 4000, img: 'https://images.unsplash.com/photo-1546173159-315724a31696?w=400&h=400&fit=crop' },
    { title: 'Matooke and Groundnut Sauce', price: 8000, img: 'https://images.unsplash.com/photo-1574484284002-952d92456975?w=400&h=400&fit=crop' },
    { title: 'Fresh Fruit Salad Bowl', price: 6000, img: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=400&fit=crop' },
    { title: 'Chapati Roll with Chicken', price: 8000, img: 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=400&h=400&fit=crop' },
    { title: 'Chips Masala Full Portion', price: 8000, img: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=400&fit=crop' },
    { title: 'Grilled Tilapia Fish', price: 15000, img: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=400&fit=crop' },
    { title: 'Pancakes with Honey 6pcs', price: 7000, img: 'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&h=400&fit=crop' },
    { title: 'Fresh Passion Juice 1L', price: 5000, img: 'https://images.unsplash.com/photo-1546173159-315724a31696?w=400&h=400&fit=crop' },
    { title: 'Beef Samosa 5 Pieces', price: 4000, img: 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400&h=400&fit=crop' },
    { title: 'Chicken Wings BBQ 8pcs', price: 12000, img: 'https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=400&h=400&fit=crop' },
    { title: 'Vegetable Stir Fry Rice', price: 8000, img: 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=400&h=400&fit=crop' },
    { title: 'Smoothie Mix Fruit 500ml', price: 6000, img: 'https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=400&h=400&fit=crop' },
    { title: 'Egg Sandwich Fresh', price: 5000, img: 'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=400&h=400&fit=crop' },
    { title: 'French Toast with Syrup', price: 7000, img: 'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400&h=400&fit=crop' },
    { title: 'Nyama Choma 1kg', price: 25000, img: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=400&h=400&fit=crop' },
    { title: 'Cassava Chips Packet', price: 3000, img: 'https://images.unsplash.com/photo-1566478989037-eec17078402b?w=400&h=400&fit=crop' },
    { title: 'White Rice and Beans', price: 7000, img: 'https://images.unsplash.com/photo-1596797038530-2c107229654b?w=400&h=400&fit=crop' },
    { title: 'Pizza Margherita Medium', price: 25000, img: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=400&fit=crop' },
    { title: 'Fresh Orange Juice 500ml', price: 3500, img: 'https://images.unsplash.com/photo-1613478223719-2ab802602423?w=400&h=400&fit=crop' },
    { title: 'Tea Masala Chai Cup', price: 2000, img: 'https://images.unsplash.com/photo-1563822249366-3efb23b8e0c9?w=400&h=400&fit=crop' },
    { title: 'Coffee Latte Large', price: 5000, img: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&h=400&fit=crop' },
    { title: 'Chocolate Cake Slice', price: 5000, img: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=400&fit=crop' },
    { title: 'Ice Cream Vanilla Cup', price: 5000, img: 'https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=400&h=400&fit=crop' },
    { title: 'Mixed Fruit Platter', price: 8000, img: 'https://images.unsplash.com/photo-1610832958506-aa56368176cf?w=400&h=400&fit=crop' },
  ];

  const beauty = [
    { title: 'Maybelline Fit Me Foundation', price: 35000, img: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400&h=400&fit=crop' },
    { title: 'Tom Ford Black Orchid Perfume', price: 320000, img: 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=400&h=400&fit=crop' },
    { title: "L'Oreal Elvive Shampoo 400ml", price: 22000, img: 'https://images.unsplash.com/photo-1631730359585-3b2e6f2ea5a3?w=400&h=400&fit=crop' },
    { title: 'Neutrogena Hydro Boost Moisturizer', price: 48000, img: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&h=400&fit=crop' },
    { title: 'Revlon ColorStay Lipstick Set', price: 28000, img: 'https://images.unsplash.com/photo-1607779097040-26e80aa78e66?w=400&h=400&fit=crop' },
    { title: 'Braun Silk-epil Trimmer', price: 95000, img: 'https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400&h=400&fit=crop' },
    { title: 'Dior Sauvage EDP 100ml', price: 350000, img: 'https://images.unsplash.com/photo-1563170351-be82bc888aa4?w=400&h=400&fit=crop' },
    { title: 'Nivea Men Face Wash 100g', price: 15000, img: 'https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=400&h=400&fit=crop' },
    { title: 'MAC Cosmetics Lip Kit', price: 65000, img: 'https://images.unsplash.com/photo-1604654894610-df63bc536371?w=400&h=400&fit=crop' },
    { title: 'Cetaphil Gentle Skin Cleanser', price: 35000, img: 'https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?w=400&h=400&fit=crop' },
    { title: 'Dove Body Wash 500ml', price: 18000, img: 'https://images.unsplash.com/photo-1600857544200-b2f666a9a2ec?w=400&h=400&fit=crop' },
    { title: 'Garnier Micellar Water 400ml', price: 25000, img: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&h=400&fit=crop' },
    { title: 'NYX Professional Makeup Palette', price: 55000, img: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400&h=400&fit=crop' },
    { title: 'Pantene Pro-V Conditioner 300ml', price: 20000, img: 'https://images.unsplash.com/photo-1631730359585-3b2e6f2ea5a3?w=400&h=400&fit=crop' },
    { title: 'CeraVe Moisturizing Cream', price: 42000, img: 'https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?w=400&h=400&fit=crop' },
    { title: 'Lancome La Vie Est Belle', price: 380000, img: 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=400&h=400&fit=crop' },
    { title: 'The Ordinary Niacinamide Serum', price: 28000, img: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&h=400&fit=crop' },
    { title: 'Gillette Fusion5 Razor Kit', price: 35000, img: 'https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=400&h=400&fit=crop' },
    { title: 'Nivea Lip Balm Pack of 3', price: 12000, img: 'https://images.unsplash.com/photo-1607779097040-26e80aa78e66?w=400&h=400&fit=crop' },
    { title: 'OGX Coconut Oil Shampoo', price: 25000, img: 'https://images.unsplash.com/photo-1631730359585-3b2e6f2ea5a3?w=400&h=400&fit=crop' },
    { title: 'Victoria Secret Bombshell', price: 280000, img: 'https://images.unsplash.com/photo-1563170351-be82bc888aa4?w=400&h=400&fit=crop' },
    { title: 'Eucerin Sun Protection SPF50', price: 38000, img: 'https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?w=400&h=400&fit=crop' },
    { title: 'Maybelline Lash Sensational Mascara', price: 22000, img: 'https://images.unsplash.com/photo-1604654894610-df63bc536371?w=400&h=400&fit=crop' },
    { title: "Axe Body Spray 150ml", price: 12000, img: 'https://images.unsplash.com/photo-1600857544200-b2f666a9a2ec?w=400&h=400&fit=crop' },
    { title: 'Simple Kind To Skin Moisturizer', price: 22000, img: 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&h=400&fit=crop' },
    { title: 'Estee Lauder Double Wear Foundation', price: 85000, img: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400&h=400&fit=crop' },
    { title: 'Schwarzkopf Hair Colour Kit', price: 18000, img: 'https://images.unsplash.com/photo-1631730359585-3b2e6f2ea5a3?w=400&h=400&fit=crop' },
    { title: 'Vaseline Petroleum Jelly 200g', price: 8000, img: 'https://images.unsplash.com/photo-1600857544200-b2f666a9a2ec?w=400&h=400&fit=crop' },
    { title: 'Nail Art Kit 24 Colors', price: 35000, img: 'https://images.unsplash.com/photo-1604654894610-df63bc536371?w=400&h=400&fit=crop' },
    { title: 'Perfume Gift Set for Him', price: 150000, img: 'https://images.unsplash.com/photo-1563170351-be82bc888aa4?w=400&h=400&fit=crop' },
  ];

  const services = [
    { title: 'Laptop Repair and Diagnostics', price: 65000, img: 'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop' },
    { title: 'Guitar Lessons per Hour', price: 35000, img: 'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=400&h=400&fit=crop' },
    { title: 'Video Editing per Minute', price: 45000, img: 'https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=400&h=400&fit=crop' },
    { title: 'Mens Haircut and Grooming', price: 15000, img: 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=400&h=400&fit=crop' },
    { title: 'Calculus Tutoring 2hr Session', price: 40000, img: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&h=400&fit=crop' },
    { title: 'CV Writing and LinkedIn Optimization', price: 35000, img: 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400&h=400&fit=crop' },
    { title: 'Photography Session 1 Hour', price: 80000, img: 'https://images.unsplash.com/photo-1554048612-b6a482bc67e5?w=400&h=400&fit=crop' },
    { title: 'Graphic Design Logo and Branding', price: 70000, img: 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=400&fit=crop' },
    { title: 'Web Development Freelance', price: 250000, img: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400&h=400&fit=crop' },
    { title: 'Mobile App Development', price: 350000, img: 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=400&h=400&fit=crop' },
    { title: 'Physics Tutoring per Hour', price: 35000, img: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&h=400&fit=crop' },
    { title: 'Essay Proofreading per Page', price: 5000, img: 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400&h=400&fit=crop' },
    { title: 'Phone Screen Repair', price: 80000, img: 'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop' },
    { title: 'Fitness Training per Session', price: 25000, img: 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400&h=400&fit=crop' },
    { title: 'Massage Therapy 1 Hour', price: 50000, img: 'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=400&h=400&fit=crop' },
    { title: 'House Cleaning Service', price: 40000, img: 'https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=400&h=400&fit=crop' },
    { title: 'Event Photography Full Day', price: 250000, img: 'https://images.unsplash.com/photo-1554048612-b6a482bc67e5?w=400&h=400&fit=crop' },
    { title: 'Social Media Management Monthly', price: 150000, img: 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=400&h=400&fit=crop' },
    { title: 'Laundry Service per Kg', price: 5000, img: 'https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=400&h=400&fit=crop' },
    { title: 'Catering per Person Event', price: 25000, img: 'https://images.unsplash.com/photo-1555244162-803834f70033?w=400&h=400&fit=crop' },
    { title: 'Python Programming Tutoring', price: 40000, img: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400&h=400&fit=crop' },
    { title: 'Makeup Artistry Session', price: 60000, img: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400&h=400&fit=crop' },
    { title: 'Translation Services per Page', price: 10000, img: 'https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=400&h=400&fit=crop' },
    { title: 'Bicycle Repair Service', price: 25000, img: 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=400&h=400&fit=crop' },
    { title: 'Swimming Lessons per Session', price: 20000, img: 'https://images.unsplash.com/photo-1530549387789-4c1017266635?w=400&h=400&fit=crop' },
    { title: 'Music Production Beat Making', price: 80000, img: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400&h=400&fit=crop' },
    { title: 'Interior Design Consultation', price: 100000, img: 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=400&h=400&fit=crop' },
    { title: 'Accounting Services Monthly', price: 120000, img: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=400&h=400&fit=crop' },
    { title: 'Driving Lessons per Session', price: 30000, img: 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?w=400&h=400&fit=crop' },
    { title: 'DJ Services per Event', price: 200000, img: 'https://images.unsplash.com/photo-1571266028243-e4733b0f0bb0?w=400&h=400&fit=crop' },
  ];

  const furniture = [
    { title: 'Study Desk with Drawer Wooden', price: 95000, img: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop' },
    { title: 'Student Single Bed with Mattress', price: 350000, img: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop' },
    { title: '2-Door Steel Wardrobe', price: 280000, img: 'https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=400&h=400&fit=crop' },
    { title: '2-Seater Fabric Sofa', price: 320000, img: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop' },
    { title: 'Plastic Stackable Chairs Set of 4', price: 80000, img: 'https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=400&h=400&fit=crop' },
    { title: '5-Tier Wooden Bookshelf', price: 120000, img: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop' },
    { title: 'Office Chair Lumbar Support', price: 185000, img: 'https://images.unsplash.com/photo-1503602642458-232111445657?w=400&h=400&fit=crop' },
    { title: 'Mini Fridge 90L Black', price: 420000, img: 'https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?w=400&h=400&fit=crop' },
    { title: 'Floor Lamp Standing LED', price: 45000, img: 'https://images.unsplash.com/photo-1507473885765-e6ed7f5e4018?w=400&h=400&fit=crop' },
    { title: '6-Seater Dining Table Set', price: 450000, img: 'https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=400&h=400&fit=crop' },
    { title: 'Mattress Single 6-Inch Foam', price: 180000, img: 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400&h=400&fit=crop' },
    { title: 'Computer Desk with Shelf', price: 85000, img: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop' },
    { title: 'Bookshelf Wall Mounted', price: 65000, img: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop' },
    { title: 'Recliner Chair Leather', price: 380000, img: 'https://images.unsplash.com/photo-1503602642458-232111445657?w=400&h=400&fit=crop' },
    { title: 'Wardrobe 4-Door Sliding', price: 450000, img: 'https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=400&h=400&fit=crop' },
    { title: 'Coffee Table Glass', price: 95000, img: 'https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=400&h=400&fit=crop' },
    { title: 'Study Lamp USB Charging', price: 25000, img: 'https://images.unsplash.com/photo-1507473885765-e6ed7f5e4018?w=400&h=400&fit=crop' },
    { title: 'Shoe Rack 5-Tier', price: 55000, img: 'https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=400&h=400&fit=crop' },
    { title: 'Bed Side Table Nightstand', price: 45000, img: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop' },
    { title: 'Curtains Pair Blackout', price: 65000, img: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?w=400&h=400&fit=crop' },
    { title: 'Blanket Double Bed Warm', price: 45000, img: 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400&h=400&fit=crop' },
    { title: 'Pillow Memory Foam Pair', price: 35000, img: 'https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400&h=400&fit=crop' },
    { title: 'Mosquito Net King Size', price: 20000, img: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?w=400&h=400&fit=crop' },
    { title: 'Desk Organizer Bamboo', price: 25000, img: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop' },
    { title: 'Laundry Basket Plastic', price: 15000, img: 'https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=400&h=400&fit=crop' },
    { title: 'Wall Clock Large Modern', price: 30000, img: 'https://images.unsplash.com/photo-1563861826100-9cb8680ae1e7?w=400&h=400&fit=crop' },
    { title: 'Storage Ottoman Box', price: 35000, img: 'https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=400&h=400&fit=crop' },
    { title: 'Desk Mat Large Mouse Pad', price: 15000, img: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop' },
    { title: 'Area Rug Carpet 5x7', price: 85000, img: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?w=400&h=400&fit=crop' },
    { title: 'Folding Table Plastic 6ft', price: 65000, img: 'https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=400&h=400&fit=crop' },
  ];

  const categories = { electronics, fashion, books, food, beauty, services, furniture };
  const categoryKeys = Object.keys(categories);
  const campuses = ['makerere', 'kyambogo', 'muk', 'gulu', 'busitema'];
  const conditions = ['Brand New', 'Second Hand', 'Brand New', 'Brand New', 'Second Hand'];
  const badges = ['Hot Deal', 'Featured', 'Popular', 'Bestseller', 'New', null, null, null, null, null];
  const fees = [0, 2000, 4000, 5000, 8000, 10000, 12000, 15000];

  const extraImgs = {
    electronics: [
      'https://images.unsplash.com/photo-1468436139062-f60a71c5c892?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&h=400&fit=crop',
    ],
    fashion: [
      'https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1445205170230-053b83016050?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=400&h=400&fit=crop',
    ],
    books: [
      'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1471107340929-a87cd0f5b5f3?w=400&h=400&fit=crop',
    ],
    food: [
      'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=400&fit=crop',
    ],
    beauty: [
      'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1592812660686-8e0c17d9e22c?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1599733589046-10c7f0f8e0e7?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=400&h=400&fit=crop',
    ],
    services: [
      'https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1521791136064-7986c2920216?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=400&h=400&fit=crop',
    ],
    furniture: [
      'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop',
    ],
  };

  const sellerIds = users.filter(u => u.type !== 'admin' && u.type !== 'buyer')
    .map(u => ({ id: u.id, name: u.name, type: u.type === 'shop' ? 'shop' : 'individual', campus: u.campus }));

  const insertProduct = db.prepare(`
    INSERT INTO products (title, price, original_price, category, seller_id, seller_name, seller_type, campus, rating, reviews_count, image, condition, delivery_zones, delivery_fee, delivery_fees, verified, badge, status, images)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);

  const usedProductIds = [];
  let totalProducts = 0;

  // Generate ~2000 products by iterating over categories and creating variations
  for (const catKey of categoryKeys) {
    const templates = categories[catKey];
    // For each template, create ~9-10 variations spread across campuses
    // This gives ~30 * 10 = ~300 per category, ~2100 total
    for (let tIdx = 0; tIdx < templates.length; tIdx++) {
      const tpl = templates[tIdx];
      const variations = Math.floor(Math.random() * 3) + 8; // 8-10 variations per template

      for (let v = 0; v < variations; v++) {
        // Cycle through campuses to distribute products
        const campusIdx = (tIdx * 10 + v) % campuses.length;
        const campus = campuses[campusIdx];

        // Pick a random seller whose campus matches this product's campus (or any)
        const matchingSellers = sellerIds.filter(s => s.campus === campus);
        const seller = matchingSellers.length > 0
          ? matchingSellers[Math.floor(Math.random() * matchingSellers.length)]
          : sellerIds[Math.floor(Math.random() * sellerIds.length)];

        // Add variation to title for unique products
        const condition = conditions[Math.floor(Math.random() * conditions.length)];
        const conditionPrefix = condition === 'Second Hand' ? '(Used) ' : '';

        // Create meaningful product variation names
        const variants = ['', ' - Latest Model', ' - Premium', ' - Student Edition', ' - Plus', ' - Pro', '', '', '', ''];
        const variant = variants[v % variants.length];

        const title = `${conditionPrefix}${tpl.title}${variant}`;

        // Price with variation
        const priceVariation = 0.85 + Math.random() * 0.3;
        const price = Math.round(tpl.price * priceVariation / 100) * 100;

        // Original price for some products
        const origPrice = Math.random() > 0.5
          ? Math.round(tpl.price * (1.1 + Math.random() * 0.4) / 100) * 100
          : null;

        // Badge - some products get badges
        const badge = badges[Math.floor(Math.random() * badges.length)];

        // Delivery zones - the product's campus plus 1-3 random others
        const zoneCount = 1 + Math.floor(Math.random() * 3);
        const zones = [campus];
        const otherCampuses = campuses.filter(c => c !== campus);
        for (let z = 0; z < zoneCount && z < otherCampuses.length; z++) {
          zones.push(otherCampuses[Math.floor(Math.random() * otherCampuses.length)]);
        }
        const uniqueZones = [...new Set(zones)];

        const rating = parseFloat((3.0 + Math.random() * 2.0).toFixed(1));
        const reviews = Math.floor(Math.random() * 300) + 3;
        const deliveryFee = catKey === 'services' ? 0 : fees[Math.floor(Math.random() * fees.length)];
        const verified = Math.random() > 0.3 ? 1 : 0;

        const catImgs = extraImgs[catKey] || [];
        const productImgs = [tpl.img, ...catImgs.sort(() => Math.random() - 0.5).slice(0, 2 + Math.floor(Math.random() * 4))];

        const feesMap = {};
        uniqueZones.forEach(z => {
          if (z === campus) feesMap[z] = deliveryFee;
          else feesMap[z] = Math.round(deliveryFee * (0.7 + Math.random() * 0.6) / 100) * 100;
        });

        const result = insertProduct.run(
          title, price, origPrice, catKey,
          seller.id, seller.name, seller.type, campus,
          rating, reviews, tpl.img, condition,
          JSON.stringify(uniqueZones), deliveryFee, JSON.stringify(feesMap),
          verified, badge, 'active',
          JSON.stringify(productImgs)
        );
        usedProductIds.push(result.lastInsertRowid);
        totalProducts++;
      }
    }
  }

  console.log(`Seeded ${totalProducts} products`);

  // Seed orders (use first few product IDs)
  const buyerUser = users.find(u => u.type === 'buyer');
  const insertOrder = db.prepare(`
    INSERT INTO orders (id, product_id, product_title, buyer_id, buyer_name, seller_id, seller_name, amount, delivery_fee, total, status, campus, delivery_to, created_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);

  // Fetch some product titles for orders
  const sampleProducts = db.prepare('SELECT id, title, price, seller_id, seller_name, campus FROM products LIMIT 50').all();
  const orderStatuses = ['delivered', 'shipped', 'completed', 'pending', 'disputed', 'delivered'];

  for (let i = 0; i < 20 && i < sampleProducts.length; i++) {
    const p = sampleProducts[i];
    const orderId = `ORD-2605${String(i + 10).padStart(2, '0')}`;
    const deliveryFee = 10000;
    insertOrder.run(
      orderId, p.id, p.title,
      buyerUser.id, buyerUser.name, p.seller_id, p.seller_name,
      p.price, deliveryFee, p.price + deliveryFee,
      orderStatuses[i % orderStatuses.length], p.campus, campuses[i % campuses.length],
      `2026-05-${String(10 + i).padStart(2, '0')}`
    );
  }

  // Seed notifications
  const adminUser = users.find(u => u.type === 'admin');
  const insertNotif = db.prepare(`INSERT INTO notifications (user_id, type, message, read, created_at) VALUES (?, ?, ?, ?, ?)`);
  insertNotif.run(buyerUser.id, 'order', 'Your order #ORD-260510 has been shipped!', 0, '2026-05-10');
  insertNotif.run(buyerUser.id, 'payment', 'UGX 85,000 credited to your wallet', 1, '2026-05-15');
  insertNotif.run(buyerUser.id, 'system', 'Flash sale: 20% off all textbooks today!', 0, '2026-05-20');
  insertNotif.run(adminUser.id, 'system', 'New verification request from Sarah N.', 0, '2026-05-18');
  insertNotif.run(adminUser.id, 'system', 'Dispute opened for order ORD-260515', 0, '2026-05-22');

  // Seed transactions
  const insertTx = db.prepare(`INSERT INTO transactions (user_id, type, amount, description, status, created_at) VALUES (?, ?, ?, ?, ?, ?)`);
  insertTx.run(sellerIds[0].id, 'credit', 85000, 'Sale: Clean Code by Robert Martin', 'completed', '2026-05-10');
  insertTx.run(sellerIds[0].id, 'debit', 50000, 'Withdrawal to MTN MoMo', 'completed', '2026-05-12');
  insertTx.run(sellerIds[1].id, 'credit', 280000, 'Sale: Tom Ford Black Orchid', 'completed', '2026-05-15');
  insertTx.run(sellerIds[2].id, 'debit', 50000, 'Shop Subscription (Monthly)', 'completed', '2026-05-16');
  insertTx.run(sellerIds[3].id, 'credit', 120000, 'Sale: Samsung Galaxy S25 Ultra', 'pending', '2026-05-20');
  insertTx.run(sellerIds[4].id, 'refund', 45000, 'Refund: Vintage Denim Jacket (Dispute)', 'completed', '2026-05-22');

  // Seed disputes
  const insertDispute = db.prepare(`INSERT INTO disputes (id, order_id, buyer_id, buyer_name, seller_id, seller_name, issue, amount, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`);
  insertDispute.run('DISP-001', 'ORD-260515', buyerUser.id, buyerUser.name, sellerIds[4].id, sellerIds[4].name, 'Item not as described', 45000, 'open');
  insertDispute.run('DISP-002', 'ORD-260516', users[4].id, users[4].name, sellerIds[0].id, sellerIds[0].name, 'Perfume packaging damaged', 280000, 'open');

  // Seed verifications
  const insertVerif = db.prepare(`INSERT INTO verifications (user_id, name, type, campus, status) VALUES (?, ?, ?, ?, ?)`);
  insertVerif.run(sellerIds[2].id, 'Sarah N.', 'student', 'kyambogo', 'pending');
  insertVerif.run(sellerIds[5].id, 'PhoneHub Uganda', 'business', 'kyambogo', 'approved');
  insertVerif.run(sellerIds[3].id, 'Grace A.', 'student', 'muk', 'pending');

  // Seed reviews
  const insertReview = db.prepare(`INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?)`);
  insertReview.run(usedProductIds[0], buyerUser.id, 5, 'Amazing product, fast delivery!');
  insertReview.run(usedProductIds[1], users[3].id, 4, 'Genuine product, well packaged.');
  insertReview.run(usedProductIds[5], users[4].id, 5, 'Great quality, exactly as described.');

  console.log('Database seeded successfully!');
  console.log(`  ${users.length} users (including ${users.filter(u => u.type === 'shop').length} registered shops)`);
  console.log(`  ${totalProducts} products across all categories and campuses`);
  console.log(`  20 orders · 6 transactions · 2 disputes · 3 verifications · 3 reviews`);
  console.log();
  console.log('Login credentials (password: "password123"):');
  console.log('  Admin:  admin@kampuskart.ug');
  console.log('  Seller: alex@mak.ac.ug');
  console.log('  Buyer:  jane@mak.ac.ug');
  console.log('  Shops:  phonehub@kampuskart.ug, bookworm@kampuskart.ug, techconnect@kampuskart.ug, campusbites@kampuskart.ug, northern@kampuskart.ug, fashionforward@kampuskart.ug, greenleaf@kampuskart.ug, gadgetzone@kampuskart.ug, beautybliss@kampuskart.ug, furniturehub@kampuskart.ug, snackattack@kampuskart.ug, fixit@kampuskart.ug, vintagethreads@kampuskart.ug, campuscomp@kampuskart.ug, freshmart@kampuskart.ug');
}

seed();

package com.kampuskart.config;

import com.kampuskart.entity.*;
import com.kampuskart.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final TransactionRepository transactionRepo;
    private final NotificationRepository notificationRepo;
    private final DisputeRepository disputeRepo;
    private final VerificationRepository verificationRepo;
    private final ReviewRepository reviewRepo;
    private final PayoutRepository payoutRepo;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random(42);

    public DataSeeder(UserRepository userRepo, ProductRepository productRepo,
                      OrderRepository orderRepo, TransactionRepository transactionRepo,
                      NotificationRepository notificationRepo, DisputeRepository disputeRepo,
                      VerificationRepository verificationRepo, ReviewRepository reviewRepo,
                      PayoutRepository payoutRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.transactionRepo = transactionRepo;
        this.notificationRepo = notificationRepo;
        this.disputeRepo = disputeRepo;
        this.verificationRepo = verificationRepo;
        this.reviewRepo = reviewRepo;
        this.payoutRepo = payoutRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        long start = System.currentTimeMillis();
        String pw = passwordEncoder.encode("password123");

        List<User> users = createAllUsers(pw);
        users = userRepo.saveAll(users);

        List<String> sellerIds = new ArrayList<>();
        List<String> sellerNames = new ArrayList<>();
        List<String> sellerTypes = new ArrayList<>();
        List<User> sellerUsers = new ArrayList<>();
        for (User u : users) {
            if ("seller".equals(u.getRole()) || "shop".equals(u.getRole())) {
                sellerIds.add(u.getId());
                sellerNames.add(u.getName());
                sellerTypes.add(u.getSellerType() != null ? u.getSellerType() : "individual");
                sellerUsers.add(u);
            }
        }

        List<Product> products = createAllProducts(sellerIds, sellerNames, sellerTypes);
        int batchSize = 200;
        for (int i = 0; i < products.size(); i += batchSize) {
            productRepo.saveAll(products.subList(i, Math.min(i + batchSize, products.size())));
            productRepo.flush();
        }

        for (User u : sellerUsers) {
            long count = products.stream().filter(p -> u.getId().equals(p.getSellerId())).count();
            u.setProductsCount((int) count);
        }
        userRepo.saveAll(sellerUsers);

        User alex = users.get(1);
        User jane = users.get(2);
        User peter = users.get(3);
        User sarah = users.get(4);
        User john = users.stream().filter(u -> "john@ucu.ac.ug".equals(u.getEmail())).findFirst().orElse(users.get(5));

        List<Order> orders = createOrders(users, products);
        orderRepo.saveAll(orders);

        alex.setBalance(new BigDecimal("4500000"));
        alex.setTotalEarned(new BigDecimal("4500000"));
        jane.setBalance(new BigDecimal("1200000"));
        jane.setTotalEarned(new BigDecimal("1200000"));
        peter.setBalance(new BigDecimal("850000"));
        peter.setTotalEarned(new BigDecimal("850000"));
        sarah.setBalance(new BigDecimal("2100000"));
        sarah.setTotalEarned(new BigDecimal("2100000"));
        userRepo.saveAll(List.of(alex, jane, peter, sarah));

        List<Transaction> transactions = createTransactions(users);
        transactionRepo.saveAll(transactions);

        List<Notification> notifications = createNotifications(users);
        notificationRepo.saveAll(notifications);

        List<Dispute> disputes = createDisputes(orders, users);
        disputeRepo.saveAll(disputes);

        List<Verification> verifications = createVerifications(users);
        verificationRepo.saveAll(verifications);

        alex.setVerified(true);
        jane.setVerified(true);
        sellerUsers.get(7).setVerified(true);
        userRepo.saveAll(List.of(alex, jane, sellerUsers.get(7)));

        List<Review> reviews = createReviews(products, users);
        reviewRepo.saveAll(reviews);

        for (Review r : reviews) {
            Product p = productRepo.findById(r.getProductId()).orElse(null);
            if (p != null) {
                List<Review> pr = reviewRepo.findByProductIdOrderByCreatedAtDesc(p.getId());
                double avg = pr.stream().mapToInt(Review::getRating).average().orElse(0);
                p.setRating(BigDecimal.valueOf(Math.round(avg * 10.0) / 10.0));
                p.setReviewsCount(pr.size());
                productRepo.save(p);
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Database seeded with " + users.size() + " users, " + products.size() + " products, " + orders.size() + " orders in " + elapsed + "ms");
    }

    private List<User> createAllUsers(String pw) {
        List<User> users = new ArrayList<>();
        String[][] userData = {
            {"admin@kampuskart.ug", "Admin User", "admin", null, "makerere"},
            {"alex@mak.ac.ug", "Alex Muwanguzi", "seller", "individual", "makerere"},
            {"jane@mak.ac.ug", "Jane Nakato", "seller", "shop", "makerere"},
            {"peter@kyu.ac.ug", "Peter Okello", "seller", "individual", "kyambogo"},
            {"sarah@mubs.ac.ug", "Sarah Nabatanzi", "seller", "shop", "muk"},
            {"john@ucu.ac.ug", "John Mukasa", "buyer", null, "ucu"},
            {"mary@gu.ac.ug", "Mary Acan", "buyer", null, "gu"},
            {"david@must.ac.ug", "David Tumusiime", "buyer", null, "must"},
            {"sarah_k@kyambogo.ac.ug", "Sarah Kiggundu", "seller", "individual", "kyambogo"},
            {"david_k@muk.ac.ug", "David Kizza", "seller", "individual", "muk"},
            {"grace@gulu.ac.ug", "Grace Ochieng", "seller", "individual", "gu"},
            {"moses@must.ac.ug", "Moses Tumwine", "seller", "individual", "must"},
            {"fatuma@busitema.ac.ug", "Fatuma Namutebi", "seller", "individual", "busitema"},
            {"phonehub@kampuskart.ug", "PhoneHub Electronics", "shop", "shop", "makerere"},
            {"bookworm@kampuskart.ug", "BookWorm Library", "shop", "shop", "makerere"},
            {"techconnect@kampuskart.ug", "TechConnect Uganda", "shop", "shop", "kyambogo"},
            {"campusbites@kampuskart.ug", "Campus Bites", "shop", "shop", "makerere"},
            {"northern@kampuskart.ug", "Northern Stars", "shop", "shop", "gu"},
            {"fashionforward@kampuskart.ug", "Fashion Forward", "shop", "shop", "muk"},
            {"greenleaf@kampuskart.ug", "Green Leaf Organics", "shop", "shop", "busitema"},
            {"gadgetzone@kampuskart.ug", "Gadget Zone", "shop", "shop", "makerere"},
            {"beautybliss@kampuskart.ug", "Beauty Bliss", "shop", "shop", "kyambogo"},
            {"furniturehub@kampuskart.ug", "Furniture Hub", "shop", "shop", "muk"},
            {"snackattack@kampuskart.ug", "Snack Attack", "shop", "shop", "makerere"},
            {"fixit@kampuskart.ug", "Fix-It Services", "shop", "shop", "kyambogo"},
            {"vintagethreads@kampuskart.ug", "Vintage Threads", "shop", "shop", "makerere"},
            {"campuscomp@kampuskart.ug", "Campus Computers", "shop", "shop", "kyambogo"}
        };

        BigDecimal[] sellerBalances = {
            new BigDecimal("4500000"), new BigDecimal("1200000"), new BigDecimal("850000"),
            new BigDecimal("2100000"), new BigDecimal("3200000"), new BigDecimal("680000"),
            new BigDecimal("520000"), new BigDecimal("900000"), new BigDecimal("1500000"),
            new BigDecimal("2800000"), new BigDecimal("750000"), new BigDecimal("420000"),
            new BigDecimal("3800000"), new BigDecimal("2600000"), new BigDecimal("1900000"),
            new BigDecimal("4100000"), new BigDecimal("1400000"), new BigDecimal("3100000"),
            new BigDecimal("2300000"), new BigDecimal("1700000"), new BigDecimal("2900000"),
            new BigDecimal("3500000"), new BigDecimal("2000000"), new BigDecimal("1600000"),
            new BigDecimal("2400000"), new BigDecimal("3000000"), new BigDecimal("1100000")
        };

        for (int i = 0; i < userData.length; i++) {
            User u = new User(userData[i][0], pw, userData[i][1], userData[i][2]);
            u.setSellerType(userData[i][3]);
            u.setCampus(userData[i][4]);
            u.setImage("https://api.dicebear.com/7.x/avataaars/svg?seed=" + userData[i][1].replace(" ", "+"));
            if ("seller".equals(userData[i][2]) || "shop".equals(userData[i][2])) {
                u.setDescription("Trusted seller at " + userData[i][4] + " campus");
                u.setBalance(sellerBalances[i]);
                u.setTotalEarned(sellerBalances[i]);
                u.setProductsCount(0);
                u.setSalesCount(ThreadLocalRandom.current().nextInt(10, 150));
                u.setRating(BigDecimal.valueOf(3.5 + random.nextDouble() * 1.5).setScale(1, RoundingMode.HALF_UP));
                u.setReviewsCount(ThreadLocalRandom.current().nextInt(5, 50));
            } else if ("buyer".equals(userData[i][2])) {
                u.setBalance(new BigDecimal("500000"));
                u.setTotalEarned(BigDecimal.ZERO);
            }
            u.setPendingBalance(BigDecimal.ZERO);
            u.setVerified(false);
            u.setIsActive(true);
            u.setCreatedAt(LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(30, 365)));
            u.setUpdatedAt(u.getCreatedAt());
            users.add(u);
        }
        return users;
    }

    private List<Product> createAllProducts(List<String> sellerIds, List<String> sellerNames, List<String> sellerTypes) {
        String[] campuses = {"makerere", "kyambogo", "muk", "ucu", "gu", "must", "busitema"};
        String[] conditions = {"Brand New", "Brand New", "Brand New", "Brand New", "Second Hand"};
        String[] badges = {"Hot Deal", "Featured", "Popular", "Bestseller", "New", null, null, null};

        String[][] electronics = {
            {"MacBook Pro 2023", "Apple MacBook Pro 14-inch M3 chip 16GB RAM 512GB SSD", "4500000"},
            {"iPhone 15 Pro Max", "Apple iPhone 15 Pro Max 256GB Deep Purple", "3800000"},
            {"Samsung Galaxy S24 Ultra", "Samsung Galaxy S24 Ultra 256GB Titanium Black", "3500000"},
            {"iPad Air M2", "Apple iPad Air M2 11-inch 256GB WiFi", "2800000"},
            {"Sony WH-1000XM5", "Sony Wireless Noise Cancelling Headphones", "850000"},
            {"Dell 27\" Monitor", "Dell 27-inch 4K USB-C Monitor U2723QE", "1500000"},
            {"HP Laptop 15", "HP Laptop 15 Intel Core i7 16GB 512GB SSD", "2200000"},
            {"Canon EOS R50", "Canon EOS R50 Mirrorless Camera with 18-45mm Lens", "2800000"},
            {"Nintendo Switch OLED", "Nintendo Switch OLED Model 64GB White", "1800000"},
            {"AirPods Pro 2", "Apple AirPods Pro 2nd Gen USB-C MagSafe", "950000"},
            {"Samsung Galaxy Tab S9", "Samsung Galaxy Tab S9 128GB WiFi AMOLED", "2200000"},
            {"Logitech MX Master 3S", "Logitech MX Master 3S Wireless Mouse", "350000"},
            {"Mechanical Keyboard", "Royal Kludge RK84 RGB Mechanical Keyboard", "280000"},
            {"Samsung T7 SSD 1TB", "Samsung Portable SSD T7 1TB USB 3.2", "420000"},
            {"Anker PowerBank 20K", "Anker 325 PowerHouse 20000mAh Power Bank", "180000"},
            {"USB-C Hub 7-in-1", "Baseus 7-in-1 USB-C Hub HDMI SD Card Reader", "120000"},
            {"Logitech Webcam C920", "Logitech C920 HD Pro Webcam 1080p", "250000"},
            {"Apple Watch SE", "Apple Watch SE 2nd Gen 44mm GPS", "1200000"},
            {"JBL Flip 6", "JBL Flip 6 Portable Bluetooth Speaker", "450000"},
            {"TI-84 Plus CE", "Texas Instruments TI-84 Plus CE Graphing Calculator", "250000"},
            {"Mini Projector", "Vankyo Leisure 3 Portable Mini Projector 1080p", "650000"},
            {"Razer DeathAdder V3", "Razer DeathAdder V3 Gaming Mouse 30K DPI", "380000"},
            {"Ring Light 18\"", "Neewer 18\" Ring Light Kit with Tripod Stand", "150000"},
            {"Laptop Stand", "Nulaxy Adjustable Laptop Stand Aluminum", "95000"},
            {"Baseus 20000mAh", "Baseus 20000mAh 65W PD Fast Charge Power Bank", "220000"},
            {"SanDisk 256GB USB", "SanDisk Ultra Flair 256GB USB 3.0 Flash Drive", "65000"},
            {"TP-Link WiFi Adapter", "TP-Link Archer T3U Plus USB WiFi Adapter", "85000"},
            {"Spigen Phone Case", "Spigen Tough Armor Case for iPhone 15 Pro", "95000"},
            {"Tempered Glass 3-Pack", "Ecorn 9H Tempered Glass Screen Protector 3-Pack", "25000"},
            {"65W USB-C Charger", "Baseus 65W GaN USB-C Fast Charger 3-Port", "180000"}
        };

        String[][] fashion = {
            {"Nike Air Max 270", "Nike Air Max 270 Men's Running Shoes Black/White", "450000"},
            {"Adidas Originals Hoodie", "Adidas Originals Trefoil Hoodie Black", "280000"},
            {"Levi's 501 Original", "Levi's 501 Original Fit Jeans Indigo", "320000"},
            {"Levi's Denim Jacket", "Levi's Trucker Denim Jacket Classic Blue", "350000"},
            {"Nike Fleece Hoodie", "Nike Sportswear Club Fleece Pullover Hoodie", "180000"},
            {"Carhartt Cargo Pants", "Carhartt Loose Fit Heavyweight Cargo Pants", "220000"},
            {"Nike Air Zoom Pegasus", "Nike Air Zoom Pegasus 40 Running Shoes", "520000"},
            {"Herschel Backpack", "Herschel Little America Laptop Backpack 15\"", "350000"},
            {"Ray-Ban Aviator", "Ray-Ban Aviator Classic Sunglasses Gold/Green", "650000"},
            {"Casio G-Shock", "Casio G-Shock GA-2100 Analog-Digital Watch", "480000"},
            {"Gold Chain 18K", "18K Gold Plated Cuban Link Chain 22-inch", "280000"},
            {"Leather Belt", "Genuine Leather Belt Classic Black Brown Reversible", "120000"},
            {"Clarks Formal Shoes", "Clarks Tilden Cap Oxford Leather Shoes Black", "380000"},
            {"New Era Cap", "New Era 9FORTY One Size Adjustable Cap", "85000"},
            {"Fossil Wallet", "Fossil Leather Bifold Wallet RFID Blocking", "180000"},
            {"3-Pack Basic Tees", "Hanes 3-Pack Premium Cotton Crew T-Shirts", "75000"},
            {"Campus Hoodie", "Custom University Campus Hoodie Unisex", "120000"},
            {"Nike Swim Shorts", "Nike Conflict Boardshorts 18-inch Swim Shorts", "95000"},
            {"North Face Gloves", "The North Face Etip Recycled Fleece Gloves", "110000"},
            {"Birkenstock Arizona", "Birkenstock Arizona Soft Footbed Sandals", "350000"},
            {"Nike Court Vision", "Nike Court Vision Low Leather Sneakers", "280000"},
            {"Oxford Dress Shirt", "Calvin Klein Slim Fit Stretch Dress Shirt White", "180000"},
            {"Polo Ralph Lauren", "Polo Ralph Lauren Classic Fit Polo Shirt", "220000"},
            {"Chino Pants", "Dockers Classic Fit Signature Lux Cotton Chinos", "150000"},
            {"Converse Chuck 70", "Converse Chuck Taylor All Star 70 High Top", "320000"},
            {"Adidas Ultraboost", "Adidas Ultraboost 22 Running Shoes Core Black", "580000"},
            {"Carhartt Beanie", "Carhartt Acrylic Watch Hat Beanie Black", "65000"},
            {"Pashmina Scarf", "Cashmere Blend Pashmina Scarf Multi Color", "85000"},
            {"Havaianas Flip Flops", "Havaianas Brasil Logo Flip Flops Navy", "55000"},
            {"Timberland Boots", "Timberland 6-inch Premium Waterproof Boots", "650000"}
        };

        String[][] books = {
            {"Calculus Early Transcendentals", "Essential Calculus Early Transcendentals 9th Edition - James Stewart", "85000"},
            {"Python Crash Course", "Python Crash Course 3rd Edition - Eric Matthes", "65000"},
            {"Linear Algebra", "Linear Algebra and Its Applications 5th Edition - Lay", "75000"},
            {"Campbell Biology", "Campbell Biology 12th Edition - Urry et al", "120000"},
            {"Chemistry: The Central Science", "Chemistry: The Central Science 15th Edition", "110000"},
            {"University Physics", "University Physics with Modern Physics 15th Edition", "130000"},
            {"Principles of Economics", "Principles of Economics 9th Edition - Mankiw", "95000"},
            {"The Practice of Statistics", "The Practice of Statistics 6th Edition - Starnes", "88000"},
            {"Computer Networking", "Computer Networking: A Top-Down Approach 8th Ed", "95000"},
            {"Introduction to Algorithms", "Introduction to Algorithms 4th Edition - CLRS", "150000"},
            {"Operating System Concepts", "Operating System Concepts 10th Edition - Silberschatz", "105000"},
            {"Database System Concepts", "Database System Concepts 7th Edition - Silberschatz", "115000"},
            {"Head First Web Design", "Head First HTML and CSS 3rd Edition", "72000"},
            {"Hands-On Machine Learning", "Hands-On Machine Learning with Scikit-Learn 3rd Ed", "140000"},
            {"Fundamentals of Electric Circuits", "Fundamentals of Electric Circuits 7th Edition", "98000"},
            {"Organic Chemistry", "Organic Chemistry 9th Edition - Wade", "125000"},
            {"Introduction to Psychology", "Introduction to Psychology 12th Edition - Myers", "82000"},
            {"Sociology", "Sociology: A Brief Introduction 13th Edition", "78000"},
            {"Principles of Marketing", "Principles of Marketing 18th Edition - Kotler", "92000"},
            {"Fundamentals of Financial Management", "Fundamentals of Financial Management 15th Ed", "105000"},
            {"Accounting Principles", "Accounting Principles 13th Edition - Weygandt", "88000"},
            {"English Grammar in Use", "English Grammar in Use 5th Edition - Raymond Murphy", "62000"},
            {"Barron's AP Study Guide", "Barron's AP Physics C Study Guide 2025", "75000"},
            {"Engineering Mathematics", "Engineering Mathematics 9th Edition - Kreyszig", "135000"},
            {"Structural Analysis", "Structural Analysis 10th Edition - Hibbeler", "118000"},
            {"Architectural Design", "Architecture: Form, Space, and Order 4th Edition", "145000"},
            {"Stedman's Medical Dictionary", "Stedman's Medical Dictionary 28th Edition", "165000"},
            {"Black's Law Dictionary", "Black's Law Dictionary 12th Edition", "155000"},
            {"A History of Western Philosophy", "A History of Western Philosophy - Russell", "85000"},
            {"The Story of Civilization", "The Story of Civilization Vol. 1-11 Box Set", "250000"}
        };

        String[][] food = {
            {"Indomie Noodles 40-pack", "Indomie Instant Noodles Chicken Flavor 40 Pack", "35000"},
            {"Monster Energy 24-pack", "Monster Energy Drink Original 355ml x 24", "96000"},
            {"Nescafe Coffee 500g", "Nescafe Classic Instant Coffee 500g", "48000"},
            {"Protein Bars 12-pack", "Quest Protein Bars Mixed Flavor 12 Pack", "85000"},
            {"Kaking Oil 5L", "Kaking Gold Cooking Oil 5 Liters", "42000"},
            {"Premium Rice 10kg", "Tilda Pure Basmati Rice 10kg", "75000"},
            {"Pinto Beans 5kg", "Dried Pinto Beans Premium Quality 5kg", "32000"},
            {"Kabusukera Sugar 2kg", "Kabusukera White Sugar 2kg", "8500"},
            {"Quality loaf Bread", "Sunstar Quality Wheat Bread 400g", "3500"},
            {"Fresh Milk 1L", "Amasunzu Fresh Full Cream Milk 1 Liter", "4200"},
            {"Eggs Tray 30", "Fresh Chicken Eggs Tray of 30", "18000"},
            {"Fresh Juice 1L", "Delmonte 100% Pure Orange Juice 1 Liter", "12000"},
            {"Snack Variety Pack", "Lays Classic Variety Chip Pack 15 Bags", "28000"},
            {"Cadbury Dairy Milk", "Cadbury Dairy Milk 80g Chocolate Bar", "6500"},
            {"Britannia Biscuits", "Britannia Good Day Cashew Cookies 75g x 6", "18000"},
            {"Mixed Dried Fruits", "Premium Mixed Dried Fruits Trail Mix 500g", "35000"},
            {"Pure Honey 500ml", "Uganda Pure Raw Honey 500ml", "45000"},
            {"Tea Leaves 400g", "Lipton Yellow Label Tea Bags 100 count", "22000"},
            {"Kellogg's Corn Flakes", "Kellogg's Corn Flakes Cereal 500g", "18000"},
            {"Peanut Butter 500g", "Kraft Smooth Peanut Butter 500g", "25000"},
            {"Maize Flour 5kg", "Namuwongo Maize Flour 5kg", "15000"},
            {"Spice Set 6-Pack", "Premium Spice Set Turmeric Cumin Paprika 6 Pack", "42000"},
            {"Table Salt 1kg", "Kensalt Iodated Table Salt 1kg", "3500"},
            {"Bread Crumbs 500g", "Italian Style Bread Crumbs 500g", "12000"},
            {"All-Purpose Flour 2kg", "Namalke All Purpose Flour 2kg", "8500"},
            {"Spaghetti 500g", "Barilla Spaghetti No.5 Pasta 500g", "6500"},
            {"Tomato Ketchup", "Heinz Tomato Ketchup 1kg Bottle", "18000"},
            {"Mayonnaise 500ml", "Young's Mayonnaise 500ml", "15000"},
            {"Cheese Slices", "PBS Cheddar Cheese Slices 200g", "22000"},
            {"Butter 500g", "Meadow Gold Salted Butter 500g", "28000"}
        };

        String[][] beauty = {
            {"CeraVe Moisturizer", "CeraVe Daily Moisturizing Lotion 473ml", "85000"},
            {"Neutrogena Sunscreen", "Neutrogena Ultra Sheer Dry-Touch SPF 50 100ml", "72000"},
            {"Burt's Bees Lip Balm", "Burt's Bees Beeswax Lip Balm 4-Pack", "35000"},
            {"Cetaphil Face Wash", "Cetaphil Gentle Skin Cleanser 500ml", "65000"},
            {"Pantene Shampoo", "Pantene Pro-V Smooth and Silky Shampoo 400ml", "28000"},
            {"Pantene Conditioner", "Pantene Pro-V Smooth and Silky Conditioner 400ml", "30000"},
            {"Coconut Hair Oil", "Parachute 100% Pure Coconut Hair Oil 500ml", "22000"},
            {"Nivea Body Lotion", "Nivea Nourishing Body Lotion 400ml", "35000"},
            {"Sally Hansen Nail Polish", "Sally Hansen Complete Salon Manicure 6 Pack", "85000"},
            {"Designer Perfume", "Versace Eros EDT 100ml for Men", "380000"},
            {"MAC Foundation", "MAC Studio Fix Fluid SPF 15 Foundation", "185000"},
            {"Maybelline Mascara", "Maybelline Lash Sensational Mascara Black", "55000"},
            {"MAC Lipstick", "MAC Matte Lipstick Ruby Woo Classic Red", "95000"},
            {"Urban Decay Palette", "Urban Decay Naked3 Eyeshadow Palette 12 Colors", "220000"},
            {"Innisfree Face Mask", "Innisfree Sheet Mask Variety Pack 10 Count", "45000"},
            {"American Crew Gel", "American Crew Firm Hold Styling Gel 250ml", "65000"},
            {"Dove Deodorant", "Dove Advanced Care Deodorant 72hr 40ml", "22000"},
            {"Gillette Razor Set", "Gillette ProGlide Razor Blade Refills 8 Pack", "75000"},
            {"Oral-B Toothbrush", "Oral-B Pro-Health CrossAction Toothbrush 4 Pack", "32000"},
            {"Colgate Toothpaste", "Colgate Total 12 Clean Mint Toothpaste 150ml", "15000"},
            {"David Beckham Body Spray", "David Beckham Signature Body Spray 150ml", "45000"},
            {"Remington Hair Straightener", "Remington S9500 Pearl Hair Straightener", "185000"},
            {"BaByliss Curling Iron", "BaByliss Curl Pro 25mm Curling Iron", "165000"},
            {"Makeup Brush Set", "BEAKEY 15-Piece Professional Makeup Brush Set", "85000"},
            {"Cotton Pads 200ct", "Nivea Soft Cotton Facial Pads 200 Count", "12000"},
            {"Garnier Micellar Water", "Garnier SkinActive Micellar Cleansing Water 400ml", "42000"},
            {"O'Keeffe's Hand Cream", "O'Keeffe's Working Hands Hand Cream 91g", "38000"},
            {"Eucerin Foot Cream", "Eucerin Advanced Repair Foot Cream 100ml", "52000"},
            {"La Roche Eye Cream", "La Roche-Posay Hyalu Eye Cream 15ml", "125000"},
            {"Clean & Clear Acne Cream", "Clean & Clear Persa-Gel 10 Acne Treatment 90g", "28000"}
        };

        String[][] services = {
            {"Private Tutoring 1hr", "One-on-one academic tutoring session 1 hour", "25000"},
            {"Photo Shoot 1hr", "Professional photography session outdoor/indoor", "80000"},
            {"Haircut & Style", "Professional men's haircut with styling", "15000"},
            {"Laundry Service", "Wash and fold laundry service per kg", "5000"},
            {"Printing 100 pages", "B&W document printing 100 pages", "15000"},
            {"Document Translation", "Professional English-Swahili translation per page", "10000"},
            {"Guitar Lessons 1hr", "Professional guitar lesson for beginners", "35000"},
            {"Car Wash Premium", "Full exterior and interior car wash", "25000"},
            {"Personal Training 1mo", "30-day personal fitness training program", "250000"},
            {"CV/Resume Writing", "Professional resume writing and review service", "45000"},
            {"Event Planning", "Birthday/event planning coordination service", "350000"},
            {"Catering 50 ppl", "Full catering service for 50 people", "750000"},
            {"Interior Design Consult", "Home/room interior design consultation 2hrs", "120000"},
            {"Web Design", "Custom 5-page responsive website design", "850000"},
            {"Logo Design", "Professional logo design with 3 concepts", "150000"},
            {"Video Editing 5min", "Professional video editing up to 5 minutes", "85000"},
            {"Translation EN-AR", "English-Arabic document translation per page", "15000"},
            {"Moving Service Local", "Local moving service within campus area", "80000"},
            {"Plumbing Repair", "General plumbing repair service call", "50000"},
            {"Electrical Repair", "Electrical wiring and repair service call", "60000"},
            {"House Painting 1 Room", "Interior painting service per room", "120000"},
            {"Carpet Cleaning", "Professional carpet deep cleaning service", "75000"},
            {"Pest Control", "Home pest control treatment service", "95000"},
            {"Laptop Repair", "Laptop diagnosis and basic repair service", "65000"},
            {"Phone Screen Repair", "Smartphone screen replacement service", "85000"},
            {"Notary Services", "Official document notarization service", "20000"},
            {"Legal Consultation", "30-minute legal advice consultation", "50000"},
            {"Medical Checkup", "Basic health checkup with lab tests", "120000"},
            {"Driving Lessons 10hr", "10-hour driving lesson package", "350000"},
            {"Dance Lessons 1mo", "30-day dance class membership", "80000"}
        };

        String[][] furniture = {
            {"Study Desk", "Solid Wood Study Desk with Shelf 120x60cm", "180000"},
            {"Ergonomic Chair", "Ergonomic Mesh Office Chair Adjustable Height", "220000"},
            {"Single Bed Frame", "Metal Single Bed Frame with Headboard 90cm", "150000"},
            {"3-Tier Bookshelf", "Wooden 3-Tier Open Bookshelf Display Rack", "95000"},
            {"Double Door Wardrobe", "4-Door Wooden Wardrobe with Mirror", "450000"},
            {"4-Seater Dining Table", "Solid Wood 4-Seater Dining Table Set", "380000"},
            {"Fabric Sofa 2-Seater", "Modern Fabric Upholstered 2-Seater Sofa", "520000"},
            {"Coffee Table Glass", "Modern Glass Top Coffee Table with Shelf", "180000"},
            {"Bedside Table", "Solid Wood Bedside Nightstand with Drawer", "75000"},
            {"TV Stand 120cm", "Modern TV Console Stand Entertainment Unit", "220000"},
            {"Shoe Rack 5-Tier", "Metal 5-Tier Shoe Rack Organizer", "65000"},
            {"Wall Mounted Shelf", "Floating Wall Shelf Set of 3 Wooden", "45000"},
            {"Kitchen Cabinet", "Freestanding Kitchen Cabinet with Doors", "350000"},
            {"Memory Foam Mattress", "King Size Memory Foam Mattress 8-inch", "480000"},
            {"Queen Pillow Set", "Queen Size Bed Pillows Set of 2 Hypoallergenic", "45000"},
            {"Blackout Curtains", "Thermal Blackout Curtains 140x200cm Pair", "85000"},
            {"Area Rug 160x230", "Modern Geometric Area Rug 160x230cm", "120000"},
            {"LED Desk Lamp", "Adjustable LED Desk Lamp with USB Charging", "75000"},
            {"Ceiling Fan 42\"", "42-inch 3-Blade Ceiling Fan with Light", "180000"},
            {"Wall Clock", "Silent Non-Ticking Wooden Wall Clock 30cm", "55000"},
            {"Full Length Mirror", "Standing Full Length Mirror 45x150cm", "95000"},
            {"Coat Rack Stand", "Wooden Coat Rack Standing Hanger 5-Hook", "48000"},
            {"Storage Box Set", "Fabric Storage Box Set of 4 Foldable", "35000"},
            {"Ironing Board", "Foldable Ironing Board with Iron Rest", "75000"},
            {"Waste Bin 30L", "Stainless Steel Kitchen Waste Bin 30L Pedal", "45000"},
            {"Hanger Set 30", "Premium Velvet Non-Slip Hangers Set of 30", "25000"},
            {"Towel Rack", "Stainless Steel Bathroom Towel Rack 3-Bar", "35000"},
            {"Shower Curtain", "Waterproof Fabric Shower Curtain with Hooks", "28000"},
            {"Bath Mat", "Memory Foam Bathroom Bath Mat Non-Slip 50x80", "32000"},
            {"Ceramic Plant Pot", "Indoor Ceramic Plant Pot with Saucer Set of 3", "42000"}
        };

        String[][][] allCategories = {electronics, fashion, books, food, beauty, services, furniture};
        String[] categoryNames = {"Electronics", "Fashion", "Books", "Food", "Beauty", "Services", "Furniture"};

        List<Product> allProducts = new ArrayList<>();
        ThreadLocalRandom tlr = ThreadLocalRandom.current();

        for (int catIdx = 0; catIdx < allCategories.length; catIdx++) {
            String[][] templates = allCategories[catIdx];
            String catName = categoryNames[catIdx];

            for (int t = 0; t < templates.length; t++) {
                String title = templates[t][0];
                String desc = templates[t][1];
                BigDecimal basePrice = new BigDecimal(templates[t][2]);

                int variations = tlr.nextInt(8, 11);
                for (int v = 0; v < variations; v++) {
                    String sellerId = sellerIds.get(tlr.nextInt(sellerIds.size()));
                    String sellerName = sellerNames.get(tlr.nextInt(sellerNames.size()));
                    String sellerType = sellerTypes.get(sellerIds.indexOf(sellerId));
                    if (sellerType == null) sellerType = "individual";

                    double priceFactor = 0.85 + random.nextDouble() * 0.30;
                    BigDecimal price = basePrice.multiply(BigDecimal.valueOf(priceFactor)).setScale(0, RoundingMode.HALF_UP);

                    BigDecimal originalPrice = null;
                    if (random.nextDouble() > 0.4) {
                        double markup = 1.10 + random.nextDouble() * 0.20;
                        originalPrice = basePrice.multiply(BigDecimal.valueOf(markup)).setScale(0, RoundingMode.HALF_UP);
                    }

                    String campus = campuses[tlr.nextInt(campuses.length)];
                    String condition = conditions[tlr.nextInt(conditions.length)];
                    String badge = badges[tlr.nextInt(badges.length)];
                    BigDecimal rating = BigDecimal.valueOf(3.0 + random.nextDouble() * 2.0).setScale(1, RoundingMode.HALF_UP);

                    int numZones = tlr.nextInt(1, 5);
                    List<String> zones = new ArrayList<>();
                    for (int z = 0; z < numZones; z++) {
                        String zone = campuses[tlr.nextInt(campuses.length)];
                        if (!zones.contains(zone)) zones.add(zone);
                    }
                    String deliveryZones = "\"" + String.join("\",\"", zones) + "\"";

                    BigDecimal deliveryFee = "Services".equals(catName) ? BigDecimal.ZERO :
                        BigDecimal.valueOf(tlr.nextInt(0, 15001));

                    String variationTitle = variations > 1 ? title + (v > 0 ? " - " + ("ABCDEF".charAt(v % 6)) : "") : title;
                    int numImages = tlr.nextInt(2, 5);
                    List<String> images = new ArrayList<>();
                    String seed = (title + v).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    for (int img = 0; img < numImages; img++) {
                        images.add("https://picsum.photos/seed/" + seed + img + "/400/400");
                    }
                    String imagesJson = "\"" + String.join("\",\"", images) + "\"";

                    Product p = new Product();
                    p.setTitle(variationTitle);
                    p.setDescription(desc);
                    p.setPrice(price);
                    p.setOriginalPrice(originalPrice);
                    p.setDeliveryFee(deliveryFee);
                    p.setDeliveryZones("[" + deliveryZones + "]");
                    p.setCategory(catName);
                    p.setCampus(campus);
                    p.setCondition(condition);
                    p.setSellerName(sellerName);
                    p.setSellerId(sellerId);
                    p.setSellerType(sellerType);
                    p.setRating(BigDecimal.ZERO);
                    p.setReviewsCount(0);
                    p.setSalesCount(tlr.nextInt(0, 50));
                    p.setBadge(badge);
                    p.setReturnPolicy("Services".equals(catName) ? "no-returns" : "7-day-returns");
                    p.setIsActive(true);
                    p.setCreatedAt(LocalDateTime.now().minusDays(tlr.nextInt(1, 180)));
                    p.setUpdatedAt(p.getCreatedAt());
                    allProducts.add(p);
                }
            }
        }
        return allProducts;
    }

    private List<Order> createOrders(List<User> users, List<Product> products) {
        List<Order> orders = new ArrayList<>();
        String[] statuses = {"completed", "completed", "completed", "shipped", "shipped", "pending", "pending", "cancelled", "disputed"};
        String[] campuses = {"makerere", "kyambogo", "muk", "ucu", "gu"};
        String[] paymentMethods = {"mobile_money", "mobile_money", "wallet"};
        ThreadLocalRandom tlr = ThreadLocalRandom.current();

        List<User> buyers = users.stream().filter(u -> "buyer".equals(u.getRole())).toList();
        List<User> sellers = users.stream().filter(u -> "seller".equals(u.getRole()) || "shop".equals(u.getRole())).toList();

        for (int i = 0; i < 20; i++) {
            User buyer = buyers.get(tlr.nextInt(buyers.size()));
            User seller = sellers.get(tlr.nextInt(sellers.size()));
            Product product = products.get(tlr.nextInt(Math.min(products.size(), 200)));
            int qty = tlr.nextInt(1, 4);
            BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(qty)).add(product.getDeliveryFee());
            String status = statuses[tlr.nextInt(statuses.length)];

            Order o = new Order();
            o.setBuyerId(buyer.getId());
            o.setSellerId(seller.getId());
            o.setProductId(product.getId());
            o.setQuantity(qty);
            o.setTotal(total);
            o.setDeliveryFee(product.getDeliveryFee());
            o.setStatus(status);
            o.setDeliveryAddress("Hall " + tlr.nextInt(1, 20) + ", Room " + tlr.nextInt(1, 100));
            o.setDeliveryCampus(campuses[tlr.nextInt(campuses.length)]);
            o.setPaymentMethod(paymentMethods[tlr.nextInt(paymentMethods.length)]);
            o.setBuyerName(buyer.getName());
            o.setSellerName(seller.getName());
            o.setProductTitle(product.getTitle());
            o.setProductImage(product.getImage());
            o.setCreatedAt(LocalDateTime.now().minusDays(tlr.nextInt(1, 60)));
            o.setUpdatedAt(o.getCreatedAt());
            orders.add(o);
        }
        return orders;
    }

    private List<Transaction> createTransactions(List<User> users) {
        List<Transaction> transactions = new ArrayList<>();
        List<User> sellers = users.stream().filter(u -> "seller".equals(u.getRole()) || "shop".equals(u.getRole())).toList();
        ThreadLocalRandom tlr = ThreadLocalRandom.current();

        for (User seller : sellers) {
            if (seller.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                transactions.add(new Transaction(seller.getId(), seller.getBalance().divide(BigDecimal.valueOf(2), 0, RoundingMode.HALF_UP), "credit", "Payment for recent orders"));
            }
        }
        if (!sellers.isEmpty()) {
            transactions.add(new Transaction(sellers.get(0).getId(), new BigDecimal("500000"), "withdrawal", "Withdrawal to Mobile Money"));
        }
        User buyer = users.stream().filter(u -> "buyer".equals(u.getRole())).findFirst().orElse(null);
        if (buyer != null) {
            transactions.add(new Transaction(buyer.getId(), new BigDecimal("50000"), "topup", "Wallet top up"));
        }
        return transactions;
    }

    private List<Notification> createNotifications(List<User> users) {
        List<Notification> notifications = new ArrayList<>();
        User seller = users.stream().filter(u -> "seller".equals(u.getRole())).findFirst().orElse(null);
        User buyer = users.stream().filter(u -> "buyer".equals(u.getRole())).findFirst().orElse(null);
        if (seller != null) {
            notifications.add(new Notification(seller.getId(), "New Order", "You received a new order for MacBook Pro", "order"));
            notifications.add(new Notification(seller.getId(), "Payment Released", "UGX 4,500,000 released for order", "payment"));
            notifications.add(new Notification(seller.getId(), "Verification Approved", "Your seller verification has been approved", "info"));
        }
        if (buyer != null) {
            notifications.add(new Notification(buyer.getId(), "Order Confirmed", "Your order has been confirmed", "order"));
            notifications.add(new Notification(buyer.getId(), "Welcome", "Welcome to KampusKart!", "info"));
        }
        return notifications;
    }

    private List<Dispute> createDisputes(List<Order> orders, List<User> users) {
        List<Dispute> disputes = new ArrayList<>();
        User buyer = users.stream().filter(u -> "buyer".equals(u.getRole())).findFirst().orElse(null);
        if (buyer != null && !orders.isEmpty()) {
            Dispute d1 = new Dispute();
            d1.setOrderId(orders.get(0).getId());
            d1.setRaisedBy(buyer.getId());
            d1.setReason("item_not_received");
            d1.setDescription("I paid but never received the item");
            d1.setStatus("open");
            d1.setCreatedAt(LocalDateTime.now().minusDays(5));
            d1.setUpdatedAt(d1.getCreatedAt());
            disputes.add(d1);

            if (orders.size() > 1) {
                Dispute d2 = new Dispute();
                d2.setOrderId(orders.get(1).getId());
                d2.setRaisedBy(buyer.getId());
                d2.setReason("item_not_as_described");
                d2.setDescription("Received a different item than what was advertised");
                d2.setStatus("open");
                d2.setCreatedAt(LocalDateTime.now().minusDays(3));
                d2.setUpdatedAt(d2.getCreatedAt());
                disputes.add(d2);
            }
        }
        return disputes;
    }

    private List<Verification> createVerifications(List<User> users) {
        List<Verification> verifications = new ArrayList<>();
        List<User> sellers = users.stream().filter(u -> "seller".equals(u.getRole())).toList();
        String[] docTypes = {"national_id", "business_registration", "student_id"};

        for (int i = 0; i < Math.min(3, sellers.size()); i++) {
            Verification v = new Verification();
            v.setUserId(sellers.get(i).getId());
            v.setUserName(sellers.get(i).getName());
            v.setDocumentType(docTypes[i]);
            v.setDocumentUrl("/docs/" + docTypes[i] + "_" + sellers.get(i).getId() + ".pdf");
            v.setStatus(i == 0 ? "approved" : "pending");
            v.setCreatedAt(LocalDateTime.now().minusDays(10 - i * 3));
            v.setUpdatedAt(v.getCreatedAt());
            verifications.add(v);
        }
        return verifications;
    }

    private List<Review> createReviews(List<Product> products, List<User> users) {
        List<Review> reviews = new ArrayList<>();
        List<User> buyers = users.stream().filter(u -> "buyer".equals(u.getRole())).toList();
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        String[] comments = {
            "Great product, exactly as described!", "Fast delivery and good quality.",
            "Very satisfied with my purchase.", "Good value for money.",
            "Amazing quality, highly recommend!", "Product exceeded my expectations.",
            "Fair price and excellent condition.", "Will buy from this seller again.",
            "Item arrived in perfect condition.", "Best purchase I've made this semester."
        };

        Set<Long> usedProducts = new HashSet<>();
        for (int i = 0; i < Math.min(15, buyers.size() * 3); i++) {
            User buyer = buyers.get(tlr.nextInt(buyers.size()));
            long productId;
            do {
                productId = products.get(tlr.nextInt(Math.min(products.size(), 200))).getId();
            } while (usedProducts.contains(productId));
            usedProducts.add(productId);

            Review r = new Review();
            r.setProductId(productId);
            r.setUserId(buyer.getId());
            r.setUserName(buyer.getName());
            r.setRating(tlr.nextInt(3, 6));
            r.setComment(comments[tlr.nextInt(comments.length)]);
            r.setCreatedAt(LocalDateTime.now().minusDays(tlr.nextInt(1, 30)));
            reviews.add(r);
        }
        return reviews;
    }
}

package com.kampuskart.config;

import com.kampuskart.entity.*;
import com.kampuskart.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

        String pw = passwordEncoder.encode("password123");

        List<User> users = List.of(
            createUser("admin@kampuskart.ug", pw, "Admin User", "admin", null, "makerere"),
            createUser("alex@mak.ac.ug", pw, "Alex Muwanguzi", "seller", "individual", "makerere"),
            createUser("jane@mak.ac.ug", pw, "Jane Nakato", "seller", "shop", "makerere"),
            createUser("peter@kyu.ac.ug", pw, "Peter Okello", "seller", "individual", "kyambogo"),
            createUser("sarah@mubs.ac.ug", pw, "Sarah Nabatanzi", "seller", "shop", "muk"),
            createUser("john@ucu.ac.ug", pw, "John Mukasa", "customer", null, "ucu"),
            createUser("mary@gu.ac.ug", pw, "Mary Acan", "customer", null, "gu"),
            createUser("david@must.ac.ug", pw, "David Tumusiime", "customer", null, "must")
        );
        users.forEach(u -> {
            u.setImage("https://api.dicebear.com/7.x/avataaars/svg?seed=" + u.getName().replace(" ", ""));
            u.setDescription("Seller at " + u.getCampus());
            u.setSellerType(u.getRole().equals("seller") ? "individual" : u.getSellerType());
        });
        // Customize shop users
        users.get(2).setSellerType("shop");
        users.get(4).setSellerType("shop");

        users = userRepo.saveAll(users);

        User alex = users.get(1);
        User jane = users.get(2);
        User peter = users.get(3);
        User sarah = users.get(4);

        List<Product> products = List.of(
            createProduct("MacBook Pro 2023", "Apple MacBook Pro 14-inch M3 chip 16GB RAM 512GB SSD", new BigDecimal("4500000"), "Electronics", "makerere", alex.getName(), "individual", new BigDecimal("15000")),
            createProduct("iPhone 15 Pro Max", "Apple iPhone 15 Pro Max 256GB Deep Purple", new BigDecimal("3800000"), "Electronics", "makerere", alex.getName(), "individual", new BigDecimal("10000")),
            createProduct("Calculus Textbook", "Essential Calculus Early Transcendentals 9th Edition", new BigDecimal("85000"), "Books", "makerere", jane.getName(), "shop", new BigDecimal("5000")),
            createProduct("Second Hand Bicycle", "Trek Mountain Bike, lightly used", new BigDecimal("550000"), "Vehicles", "makerere", jane.getName(), "shop", new BigDecimal("20000")),
            createProduct("Study Desk", "Wooden study desk with shelf 120x60cm", new BigDecimal("180000"), "Furniture", "kyambogo", peter.getName(), "individual", new BigDecimal("15000")),
            createProduct("Sony WH-1000XM5", "Sony Wireless Noise Cancelling Headphones", new BigDecimal("850000"), "Electronics", "kyambogo", peter.getName(), "individual", new BigDecimal("10000")),
            createProduct("Python Programming Book", "Python Crash Course 3rd Edition", new BigDecimal("65000"), "Books", "muk", sarah.getName(), "shop", new BigDecimal("5000")),
            createProduct("Bed Frame", "Metal bed frame with mattress, single size", new BigDecimal("350000"), "Furniture", "muk", sarah.getName(), "shop", new BigDecimal("25000")),
            createProduct("Graphic Calculator", "Texas Instruments TI-84 Plus CE", new BigDecimal("250000"), "Electronics", "makerere", alex.getName(), "individual", new BigDecimal("8000")),
            createProduct("Samsung Galaxy Tab S9", "Samsung Galaxy Tab S9 128GB WiFi", new BigDecimal("2200000"), "Electronics", "makerere", alex.getName(), "individual", new BigDecimal("12000")),
            createProduct("Linear Algebra Book", "Linear Algebra and Its Applications 5th Edition", new BigDecimal("75000"), "Books", "makerere", jane.getName(), "shop", new BigDecimal("5000")),
            createProduct("Desk Chair", "Ergonomic office desk chair adjustable", new BigDecimal("220000"), "Furniture", "kyambogo", peter.getName(), "individual", new BigDecimal("15000")),
            createProduct("Samsung Buds2 Pro", "Samsung Galaxy Buds2 Pro Wireless Earbuds", new BigDecimal("350000"), "Electronics", "kyambogo", peter.getName(), "individual", new BigDecimal("8000")),
            createProduct("Networking Textbook", "Computer Networking: A Top-Down Approach 8th Ed", new BigDecimal("95000"), "Books", "muk", sarah.getName(), "shop", new BigDecimal("5000")),
            createProduct("Mini Fridge", "Portable mini fridge 30L for dorm room", new BigDecimal("450000"), "Electronics", "muk", sarah.getName(), "shop", new BigDecimal("20000")),
            createProduct("Canon EOS R50", "Canon EOS R50 Mirrorless Camera with 18-45mm Lens", new BigDecimal("2800000"), "Electronics", "makerere", alex.getName(), "individual", new BigDecimal("15000")),
            createProduct("Bookshelf", "3-tier wooden bookshelf", new BigDecimal("150000"), "Furniture", "makerere", jane.getName(), "shop", new BigDecimal("12000")),
            createProduct("Electric Kettle", "Stainless steel electric kettle 1.7L", new BigDecimal("45000"), "Electronics", "kyambogo", peter.getName(), "individual", new BigDecimal("5000")),
            createProduct("Biology Textbook", "Campbell Biology 12th Edition", new BigDecimal("120000"), "Books", "muk", sarah.getName(), "shop", new BigDecimal("5000")),
            createProduct("Dell Monitor", "Dell 27-inch 4K USB-C Monitor", new BigDecimal("1500000"), "Electronics", "makerere", alex.getName(), "individual", new BigDecimal("15000"))
        );
        productRepo.saveAll(products);

        Product macbook = products.get(0);
        Product iphone = products.get(1);
        Product desk = products.get(4);

        List<Order> orders = List.of(
            createOrder(users.get(5).getId(), alex.getId(), macbook.getId(), 1, macbook.getPrice().add(macbook.getDeliveryFee()), macbook.getDeliveryFee(), "completed", "John Mukasa", alex.getName(), macbook.getTitle(), macbook.getImage()),
            createOrder(users.get(6).getId(), jane.getId(), products.get(2).getId(), 2, products.get(2).getPrice().multiply(BigDecimal.valueOf(2)).add(products.get(2).getDeliveryFee()), products.get(2).getDeliveryFee(), "shipped", "Mary Acan", jane.getName(), products.get(2).getTitle(), products.get(2).getImage()),
            createOrder(users.get(5).getId(), peter.getId(), desk.getId(), 1, desk.getPrice().add(desk.getDeliveryFee()), desk.getDeliveryFee(), "pending", "John Mukasa", peter.getName(), desk.getTitle(), desk.getImage()),
            createOrder(users.get(7).getId(), alex.getId(), products.get(8).getId(), 1, products.get(8).getPrice().add(products.get(8).getDeliveryFee()), products.get(8).getDeliveryFee(), "pending", "David Tumusiime", alex.getName(), products.get(8).getTitle(), products.get(8).getImage()),
            createOrder(users.get(5).getId(), sarah.getId(), products.get(6).getId(), 3, products.get(6).getPrice().multiply(BigDecimal.valueOf(3)).add(products.get(6).getDeliveryFee()), products.get(6).getDeliveryFee(), "completed", "John Mukasa", sarah.getName(), products.get(6).getTitle(), products.get(6).getImage()),
            createOrder(users.get(6).getId(), peter.getId(), products.get(11).getId(), 1, products.get(11).getPrice().add(products.get(11).getDeliveryFee()), products.get(11).getDeliveryFee(), "shipped", "Mary Acan", peter.getName(), products.get(11).getTitle(), products.get(11).getImage()),
            createOrder(users.get(7).getId(), jane.getId(), products.get(16).getId(), 1, products.get(16).getPrice().add(products.get(16).getDeliveryFee()), products.get(16).getDeliveryFee(), "pending", "David Tumusiime", jane.getName(), products.get(16).getTitle(), products.get(16).getImage()),
            createOrder(users.get(5).getId(), alex.getId(), iphone.getId(), 1, iphone.getPrice().add(iphone.getDeliveryFee()), iphone.getDeliveryFee(), "cancelled", "John Mukasa", alex.getName(), iphone.getTitle(), iphone.getImage()),
            createOrder(users.get(6).getId(), sarah.getId(), products.get(13).getId(), 1, products.get(13).getPrice().add(products.get(13).getDeliveryFee()), products.get(13).getDeliveryFee(), "completed", "Mary Acan", sarah.getName(), products.get(13).getTitle(), products.get(13).getImage()),
            createOrder(users.get(7).getId(), alex.getId(), products.get(19).getId(), 1, products.get(19).getPrice().add(products.get(19).getDeliveryFee()), products.get(19).getDeliveryFee(), "pending", "David Tumusiime", alex.getName(), products.get(19).getTitle(), products.get(19).getImage())
        );
        orderRepo.saveAll(orders);

        // Credit alex's balance for completed orders
        alex.setBalance(new BigDecimal("4500000"));
        alex.setTotalEarned(new BigDecimal("4500000"));
        userRepo.save(alex);

        List<Transaction> transactions = List.of(
            new Transaction(alex.getId(), new BigDecimal("4500000"), "credit", "Payment for MacBook Pro order"),
            new Transaction(alex.getId(), new BigDecimal("500000"), "withdrawal", "Withdrawal to Mobile Money"),
            new Transaction(jane.getId(), new BigDecimal("170000"), "credit", "Payment for Calculus Textbook order"),
            new Transaction(sarah.getId(), new BigDecimal("195000"), "credit", "Payment for Python Programming Book order"),
            new Transaction(peter.getId(), new BigDecimal("220000"), "credit", "Payment for Desk Chair order"),
            new Transaction(users.get(5).getId(), new BigDecimal("50000"), "topup", "Wallet top up")
        );
        transactionRepo.saveAll(transactions);

        List<Notification> notifications = List.of(
            new Notification(alex.getId(), "New Order", "You received an order for MacBook Pro", "order"),
            new Notification(alex.getId(), "Payment Released", "UGX 4,500,000 released for order #1", "payment"),
            new Notification(jane.getId(), "New Order", "You received an order for Calculus Textbook", "order"),
            new Notification(users.get(5).getId(), "Order Confirmed", "Your order for MacBook Pro is confirmed", "order"),
            new Notification(users.get(5).getId(), "Welcome", "Welcome to KampusKart!", "info")
        );
        notificationRepo.saveAll(notifications);

        List<Dispute> disputes = List.of(
            createDispute(orders.get(7).getId(), users.get(5).getId(), "item_not_received", "I paid for the iPhone but never received it"),
            createDispute(orders.get(0).getId(), users.get(5).getId(), "wrong_item", "Received a different specification than advertised")
        );
        disputeRepo.saveAll(disputes);

        List<Verification> verifications = List.of(
            createVerification(alex.getId(), "Alex Muwanguzi", "national_id"),
            createVerification(jane.getId(), "Jane Nakato", "business_registration"),
            createVerification(peter.getId(), "Peter Okello", "student_id")
        );
        // Approve some
        verifications.get(0).setStatus("approved");
        verificationRepo.saveAll(verifications);

        alex.setVerified(true);
        userRepo.save(alex);

        List<Review> reviews = List.of(
            createReview(macbook.getId(), users.get(5).getId(), "John Mukasa", 5, "Great laptop, exactly as described!"),
            createReview(products.get(6).getId(), users.get(5).getId(), "John Mukasa", 4, "Good book, well packaged"),
            createReview(products.get(13).getId(), users.get(6).getId(), "Mary Acan", 5, "Great condition, fast delivery")
        );
        reviewRepo.saveAll(reviews);

        products.get(0).setReviewsCount(1);
        products.get(0).setRating(new BigDecimal("5"));
        products.get(6).setReviewsCount(1);
        products.get(6).setRating(new BigDecimal("4"));
        products.get(13).setReviewsCount(1);
        products.get(13).setRating(new BigDecimal("5"));
        productRepo.saveAll(List.of(products.get(0), products.get(6), products.get(13)));

        System.out.println("Database seeded with " + users.size() + " users, " + products.size() + " products, " + orders.size() + " orders");
    }

    private User createUser(String email, String password, String name, String role, String sellerType, String campus) {
        User u = new User(email, password, name, role);
        u.setSellerType(sellerType);
        u.setCampus(campus);
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        u.setBalance(BigDecimal.ZERO);
        u.setPendingBalance(BigDecimal.ZERO);
        u.setTotalEarned(BigDecimal.ZERO);
        u.setRating(BigDecimal.ZERO);
        u.setReviewsCount(0);
        u.setProductsCount(0);
        u.setSalesCount(0);
        u.setVerified(false);
        u.setIsActive(true);
        return u;
    }

    private Product createProduct(String title, String desc, BigDecimal price, String category, String campus,
                                   String sellerName, String sellerType, BigDecimal deliveryFee) {
        Product p = new Product();
        p.setTitle(title);
        p.setDescription(desc);
        p.setPrice(price);
        p.setOriginalPrice(price);
        p.setDeliveryFee(deliveryFee);
        p.setDeliveryZones("[\"makerere\",\"kyambogo\",\"muk\",\"ucu\",\"gu\",\"must\"]");
        p.setCategory(category);
        p.setCampus(campus);
        p.setCondition("new");
        p.setSellerName(sellerName);
        p.setSellerType(sellerType);
        p.setRating(BigDecimal.ZERO);
        p.setReviewsCount(0);
        p.setSalesCount(0);
        p.setIsActive(true);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return p;
    }

    private Order createOrder(Long buyerId, Long sellerId, Long productId, int quantity, BigDecimal total,
                               BigDecimal deliveryFee, String status, String buyerName, String sellerName,
                               String productTitle, String productImage) {
        Order o = new Order();
        o.setBuyerId(buyerId);
        o.setSellerId(sellerId);
        o.setProductId(productId);
        o.setQuantity(quantity);
        o.setTotal(total);
        o.setDeliveryFee(deliveryFee);
        o.setStatus(status);
        o.setDeliveryAddress("Hall 4, Room 12");
        o.setDeliveryCampus("makerere");
        o.setPaymentMethod("mobile_money");
        o.setBuyerName(buyerName);
        o.setSellerName(sellerName);
        o.setProductTitle(productTitle);
        o.setProductImage(productImage);
        o.setCreatedAt(LocalDateTime.now());
        o.setUpdatedAt(LocalDateTime.now());
        return o;
    }

    private Dispute createDispute(Long orderId, Long raisedBy, String reason, String description) {
        Dispute d = new Dispute();
        d.setOrderId(orderId);
        d.setRaisedBy(raisedBy);
        d.setReason(reason);
        d.setDescription(description);
        d.setStatus("open");
        d.setCreatedAt(LocalDateTime.now());
        d.setUpdatedAt(LocalDateTime.now());
        return d;
    }

    private Verification createVerification(Long userId, String userName, String documentType) {
        Verification v = new Verification();
        v.setUserId(userId);
        v.setUserName(userName);
        v.setDocumentType(documentType);
        v.setDocumentUrl("/docs/" + documentType + "_" + userId + ".pdf");
        v.setStatus("pending");
        v.setCreatedAt(LocalDateTime.now());
        v.setUpdatedAt(LocalDateTime.now());
        return v;
    }

    private Review createReview(Long productId, Long userId, String userName, int rating, String comment) {
        Review r = new Review();
        r.setProductId(productId);
        r.setUserId(userId);
        r.setUserName(userName);
        r.setRating(rating);
        r.setComment(comment);
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }
}

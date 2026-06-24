class User {
  final int id;
  final String name;
  final String email;
  final String role;
  final String? image;
  final String? campus;
  final bool verified;
  final String? shopName;
  final String? shopDescription;
  final String sellerType;
  final double balance;
  final double pending;
  final double totalEarned;

  User({
    required this.id,
    required this.name,
    required this.email,
    required this.role,
    this.image,
    this.campus,
    this.verified = false,
    this.shopName,
    this.shopDescription,
    this.sellerType = 'individual',
    this.balance = 0,
    this.pending = 0,
    this.totalEarned = 0,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] ?? 0,
      name: json['name'] ?? '',
      email: json['email'] ?? '',
      role: json['role'] ?? 'buyer',
      image: json['image'],
      campus: json['campus'],
      verified: json['verified'] ?? false,
      shopName: json['shop_name'],
      shopDescription: json['shop_description'],
      sellerType: json['seller_type'] ?? 'individual',
      balance: (json['balance'] ?? 0).toDouble(),
      pending: (json['pending'] ?? 0).toDouble(),
      totalEarned: (json['total_earned'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'name': name,
    'email': email,
    'role': role,
    'image': image,
    'campus': campus,
    'seller_type': sellerType,
  };

  bool get isSeller => role == 'seller';
  bool get isAdmin => role == 'admin';
  bool get isShop => sellerType == 'shop';
}

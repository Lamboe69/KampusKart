class User {
  final String id;
  final String name;
  final String email;
  final String role;
  final String? phone;
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
    this.phone,
    this.image,
    this.campus,
    this.verified = false,
    this.shopName,
    this.shopDescription,
    this.sellerType = 'individual',
    this.balance = 0,
    this.pending = 0,
    this.totalEarned = 0,
  }) : role = sellerType == 'shop' ? 'seller' : sellerType == 'admin' ? 'admin' : sellerType;

  factory User.fromJson(Map<String, dynamic> json) {
    final type = json['type'] ?? json['role'] ?? 'buyer';
    return User(
      id: json['id']?.toString() ?? '',
      name: json['name'] ?? '',
      email: json['email'] ?? '',
      phone: json['phone'],
      image: json['image'] ?? json['avatar'],
      campus: json['campus'],
      verified: json['verified'] == true || json['verified'] == 1,
      shopName: json['shop_name'],
      shopDescription: json['shop_description'],
      sellerType: json['seller_type'] ?? type == 'shop' ? 'shop' : 'individual',
      balance: (json['wallet_balance'] ?? json['balance'] ?? 0).toDouble(),
      pending: (json['wallet_pending'] ?? json['pending'] ?? 0).toDouble(),
      totalEarned: (json['wallet_total_earned'] ?? json['total_earned'] ?? 0).toDouble(),
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

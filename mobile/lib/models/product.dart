import 'dart:convert';

class Product {
  final int id;
  final String title;
  final double price;
  final double? originalPrice;
  final String category;
  final String condition;
  final String description;
  final String image;
  final String seller;
  final String sellerType;
  final String campus;
  final double rating;
  final int reviews;
  final int salesCount;
  final String status;
  final String? badge;
  final double deliveryFee;
  final Map<String, dynamic> deliveryFees;
  final List<String> deliveryZones;
  final String returnPolicy;
  final String createdAt;

  Product({
    required this.id,
    required this.title,
    required this.price,
    this.originalPrice,
    required this.category,
    required this.condition,
    required this.description,
    required this.image,
    required this.seller,
    this.sellerType = 'individual',
    required this.campus,
    this.rating = 0,
    this.reviews = 0,
    this.salesCount = 0,
    this.status = 'active',
    this.badge,
    this.deliveryFee = 0,
    this.deliveryFees = const {},
    this.deliveryZones = const [],
    this.returnPolicy = 'no-returns',
    required this.createdAt,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    List<String> parseZones(dynamic z) {
      if (z is List) return z.cast<String>();
      if (z is String) {
        try {
          final parsed = List<dynamic>.from(jsonDecode(z) as List);
          return parsed.cast<String>();
        } catch (_) {
          return z.split(',').map((e) => e.trim()).toList();
        }
      }
      return [];
    }

    Map<String, dynamic> parseFees(dynamic f) {
      if (f is Map) return f.cast<String, dynamic>();
      if (f is String) {
        try {
          return Map<String, dynamic>.from(jsonDecode(f) as Map);
        } catch (_) {}
      }
      return {};
    }

    return Product(
      id: json['id'] ?? 0,
      title: json['title'] ?? '',
      price: (json['price'] ?? 0).toDouble(),
      originalPrice: json['original_price']?.toDouble() ?? json['originalPrice']?.toDouble(),
      category: json['category'] ?? '',
      condition: json['condition'] ?? '',
      description: json['description'] ?? '',
      image: json['image'] ?? '',
      seller: json['seller'] ?? json['seller_name'] ?? json['sellerName'] ?? '',
      sellerType: json['seller_type'] ?? json['sellerType'] ?? 'individual',
      campus: json['campus'] ?? '',
      rating: (json['rating'] ?? 0).toDouble(),
      reviews: json['reviews'] ?? json['reviews_count'] ?? 0,
      salesCount: json['sales_count'] ?? json['salesCount'] ?? 0,
      status: json['status'] ?? 'active',
      badge: json['badge'],
      deliveryFee: (json['delivery_fee'] ?? json['deliveryFee'] ?? 0).toDouble(),
      deliveryFees: parseFees(json['delivery_fees'] ?? json['deliveryFees'] ?? {}),
      deliveryZones: parseZones(json['delivery_zones'] ?? json['deliveryZones'] ?? []),
      returnPolicy: json['return_policy'] ?? json['returnPolicy'] ?? 'no-returns',
      createdAt: json['created_at'] ?? json['createdAt'] ?? '',
    );
  }

  bool get hasDiscount => originalPrice != null && price < originalPrice!;
  int get discountPercent => hasDiscount
      ? ((1 - price / originalPrice!) * 100).round()
      : 0;
}

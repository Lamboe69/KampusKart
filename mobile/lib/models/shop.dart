class Shop {
  final int id;
  final String name;
  final String description;
  final String image;
  final String type;
  final String campus;
  final double rating;
  final int reviews;
  final int products;
  final int sales;
  final bool verified;
  final String since;

  Shop({
    required this.id,
    required this.name,
    required this.description,
    required this.image,
    this.type = 'individual',
    required this.campus,
    this.rating = 0,
    this.reviews = 0,
    this.products = 0,
    this.sales = 0,
    this.verified = false,
    this.since = '',
  });

  factory Shop.fromJson(Map<String, dynamic> json) {
    return Shop(
      id: json['id'] ?? 0,
      name: json['name'] ?? '',
      description: json['description'] ?? '',
      image: json['image'] ?? '',
      type: json['type'] ?? json['seller_type'] ?? 'individual',
      campus: json['campus'] ?? '',
      rating: (json['rating'] ?? 0).toDouble(),
      reviews: json['reviews'] ?? 0,
      products: json['products'] ?? 0,
      sales: json['sales'] ?? 0,
      verified: json['verified'] ?? false,
      since: json['since'] ?? '',
    );
  }
}

class Order {
  final int id;
  final int userId;
  final int productId;
  final String productTitle;
  final String productImage;
  final double price;
  final int quantity;
  final double total;
  final double deliveryFee;
  final String status;
  final String paymentMethod;
  final String deliveryCampus;
  final String sellerName;
  final String buyerName;
  final String? buyerPhone;
  final String createdAt;
  final String? deliveredAt;

  Order({
    required this.id,
    required this.userId,
    required this.productId,
    required this.productTitle,
    required this.productImage,
    required this.price,
    required this.quantity,
    required this.total,
    this.deliveryFee = 0,
    this.status = 'pending',
    this.paymentMethod = 'mobile_money',
    this.deliveryCampus = '',
    this.sellerName = '',
    this.buyerName = '',
    this.buyerPhone,
    required this.createdAt,
    this.deliveredAt,
  });

  factory Order.fromJson(Map<String, dynamic> json) {
    return Order(
      id: json['id'] ?? 0,
      userId: json['user_id'] ?? json['userId'] ?? 0,
      productId: json['product_id'] ?? json['productId'] ?? 0,
      productTitle: json['product_title'] ?? json['productTitle'] ?? '',
      productImage: json['product_image'] ?? json['productImage'] ?? '',
      price: (json['price'] ?? 0).toDouble(),
      quantity: json['quantity'] ?? 1,
      total: (json['total'] ?? 0).toDouble(),
      deliveryFee: (json['delivery_fee'] ?? json['deliveryFee'] ?? 0).toDouble(),
      status: json['status'] ?? 'pending',
      paymentMethod: json['payment_method'] ?? json['paymentMethod'] ?? 'mobile_money',
      deliveryCampus: json['delivery_campus'] ?? json['deliveryCampus'] ?? '',
      sellerName: json['seller_name'] ?? json['sellerName'] ?? '',
      buyerName: json['buyer_name'] ?? json['buyerName'] ?? '',
      buyerPhone: json['buyer_phone'] ?? json['buyerPhone'],
      createdAt: json['created_at'] ?? json['createdAt'] ?? '',
      deliveredAt: json['delivered_at'] ?? json['deliveredAt'],
    );
  }

  bool get isPending => status == 'pending';
  bool get isShipped => status == 'shipped';
  bool get isDelivered => status == 'delivered';
  bool get isDisputed => status == 'disputed';
  bool get isCompleted => status == 'completed';
  bool get isCancelled => status == 'cancelled';

  String get statusLabel {
    switch (status) {
      case 'pending': return 'Pending';
      case 'shipped': return 'Shipped';
      case 'delivered': return 'Delivered';
      case 'disputed': return 'Disputed';
      case 'completed': return 'Completed';
      case 'cancelled': return 'Cancelled';
      default: return status;
    }
  }
}

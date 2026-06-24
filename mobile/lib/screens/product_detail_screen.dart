import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../models/product.dart';
import '../providers/app_provider.dart';
import '../theme/app_theme.dart';
import 'cart_screen.dart';

class ProductDetailScreen extends StatelessWidget {
  final Product product;
  const ProductDetailScreen({super.key, required this.product});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    final inCart = provider.cart.any((p) => p.id == product.id);

    return Scaffold(
      appBar: AppBar(title: Text(product.title)),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            AspectRatio(
              aspectRatio: 1,
              child: Container(
                color: AppTheme.bgElevated,
                child: Image.network(
                  product.image,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) => const Center(child: Text('📦', style: TextStyle(fontSize: 80))),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      if (product.badge != null)
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(color: Colors.green, borderRadius: BorderRadius.circular(8)),
                          child: Text(product.badge!, style: const TextStyle(fontSize: 11, color: Colors.white, fontWeight: FontWeight.bold)),
                        ),
                      if (product.badge != null) const SizedBox(width: 8),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(color: product.hasDiscount ? Colors.red : Colors.transparent, borderRadius: BorderRadius.circular(8)),
                        child: Text(product.condition, style: TextStyle(fontSize: 11, color: product.hasDiscount ? Colors.white : AppTheme.textSecondary)),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Text(product.title, style: Theme.of(context).textTheme.headlineMedium?.copyWith(fontSize: 22)),
                  const SizedBox(height: 8),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Text('UGX ${NumberFormat('#,###').format(product.price.toInt())}',
                        style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold, color: AppTheme.accent)),
                      if (product.hasDiscount) ...[
                        const SizedBox(width: 8),
                        Text('UGX ${NumberFormat('#,###').format(product.originalPrice!.toInt())}',
                          style: const TextStyle(fontSize: 16, color: AppTheme.textTertiary, decoration: TextDecoration.lineThrough)),
                      ],
                    ],
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      const Icon(Icons.star, size: 16, color: Colors.amber),
                      const SizedBox(width: 4),
                      Text('${product.rating} (${product.reviews} reviews)', style: const TextStyle(color: AppTheme.textSecondary)),
                      const SizedBox(width: 16),
                      Text('Sold: ${product.salesCount}', style: const TextStyle(color: AppTheme.textTertiary)),
                    ],
                  ),
                  const SizedBox(height: 16),
                  const Divider(color: AppTheme.borderColor),
                  const SizedBox(height: 16),
                  Text('Description', style: Theme.of(context).textTheme.titleMedium),
                  const SizedBox(height: 8),
                  Text(product.description, style: const TextStyle(color: AppTheme.textSecondary, height: 1.5)),
                  const SizedBox(height: 16),
                  const Divider(color: AppTheme.borderColor),
                  const SizedBox(height: 8),
                  _infoRow(Icons.person_outline, 'Seller', product.seller),
                  _infoRow(Icons.location_on_outlined, 'Campus', product.campus),
                  _infoRow(Icons.local_shipping_outlined, 'Delivery', 'UGX ${NumberFormat('#,###').format(product.deliveryFee.toInt())}'),
                  _infoRow(Icons.replay_outlined, 'Returns', product.returnPolicy == 'returns-accepted' ? 'Accepted' : 'No returns'),
                ],
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: ElevatedButton.icon(
            onPressed: inCart
                ? () => Navigator.push(context, MaterialPageRoute(builder: (_) => const CartScreen()))
                : () => provider.addToCart(product),
            icon: Icon(inCart ? Icons.shopping_cart : Icons.add_shopping_cart),
            label: Text(inCart ? 'View in Cart' : 'Add to Cart'),
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(double.infinity, 52),
            ),
          ),
        ),
      ),
    );
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Icon(icon, size: 18, color: AppTheme.textTertiary),
          const SizedBox(width: 12),
          Text('$label: ', style: const TextStyle(color: AppTheme.textTertiary)),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }
}

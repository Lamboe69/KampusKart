import 'package:flutter/material.dart';
import '../models/shop.dart';
import '../theme/app_theme.dart';

class ShopCard extends StatelessWidget {
  final Shop shop;
  final VoidCallback onTap;

  const ShopCard({super.key, required this.shop, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: AppTheme.bgCard,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Row(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(12),
              child: Image.network(
                shop.image,
                width: 64, height: 64,
                fit: BoxFit.cover,
                errorBuilder: (_, __, ___) => Container(
                  width: 64, height: 64,
                  color: AppTheme.bgElevated,
                  child: const Center(child: Text('🏪', style: TextStyle(fontSize: 28))),
                ),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(shop.name, style: const TextStyle(fontWeight: FontWeight.w600)),
                      if (shop.verified) ...[
                        const SizedBox(width: 6),
                        const Icon(Icons.verified, size: 14, color: Colors.blue),
                      ],
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(shop.type == 'shop' ? 'Registered Shop' : 'Individual Seller',
                    style: const TextStyle(fontSize: 12, color: AppTheme.textSecondary)),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      const Icon(Icons.star, size: 12, color: Colors.amber),
                      const SizedBox(width: 2),
                      Text('${shop.rating}', style: const TextStyle(fontSize: 12)),
                      const SizedBox(width: 12),
                      Text('${shop.reviews} reviews', style: const TextStyle(fontSize: 12, color: AppTheme.textTertiary)),
                    ],
                  ),
                ],
              ),
            ),
            Column(
              children: [
                Text('${shop.products}', style: const TextStyle(fontWeight: FontWeight.bold, color: AppTheme.accent)),
                const Text('Products', style: TextStyle(fontSize: 10, color: AppTheme.textTertiary)),
                const SizedBox(height: 8),
                Text('${shop.sales}', style: const TextStyle(fontWeight: FontWeight.bold)),
                const Text('Sales', style: TextStyle(fontSize: 10, color: AppTheme.textTertiary)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

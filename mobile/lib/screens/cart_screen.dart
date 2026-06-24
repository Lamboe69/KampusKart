import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../providers/app_provider.dart';
import '../theme/app_theme.dart';
import 'checkout_screen.dart';

class CartScreen extends StatelessWidget {
  const CartScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    return Scaffold(
      appBar: AppBar(title: const Text('Shopping Cart')),
      body: provider.cart.isEmpty
          ? const Center(child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text('🛒', style: TextStyle(fontSize: 64)),
                SizedBox(height: 16),
                Text('Your cart is empty', style: TextStyle(color: AppTheme.textSecondary)),
              ],
            ))
          : ListView.separated(
              padding: const EdgeInsets.all(16),
              itemCount: provider.cart.length,
              separatorBuilder: (_, __) => const Divider(color: AppTheme.borderColor, height: 1),
              itemBuilder: (context, i) {
                final item = provider.cart[i];
                return ListTile(
                  leading: ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Image.network(item.image, width: 56, height: 56, fit: BoxFit.cover,
                      errorBuilder: (_, __, ___) => Container(width: 56, height: 56, color: AppTheme.bgElevated, child: const Center(child: Text('📦')))),
                  ),
                  title: Text(item.title, maxLines: 1, overflow: TextOverflow.ellipsis),
                  subtitle: Text('UGX ${NumberFormat('#,###').format(item.price.toInt())}',
                    style: const TextStyle(color: AppTheme.accent)),
                  trailing: IconButton(
                    icon: const Icon(Icons.delete_outline, color: Colors.red),
                    onPressed: () => provider.removeFromCart(item.id),
                  ),
                );
              },
            ),
      bottomNavigationBar: provider.cart.isEmpty
          ? null
          : SafeArea(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Total:', style: TextStyle(fontSize: 18)),
                        Text('UGX ${NumberFormat('#,###').format(provider.cartTotal.toInt())}',
                          style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: AppTheme.accent)),
                      ],
                    ),
                    const SizedBox(height: 12),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const CheckoutScreen())),
                        icon: const Icon(Icons.shopping_bag_outlined),
                        label: const Text('Proceed to Checkout'),
                        style: ElevatedButton.styleFrom(minimumSize: const Size(double.infinity, 52)),
                      ),
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}

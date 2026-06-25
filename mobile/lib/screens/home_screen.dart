import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/product.dart';
import '../providers/app_provider.dart';
import '../theme/app_theme.dart';
import '../widgets/product_card.dart';
import 'product_detail_screen.dart';
import 'cart_screen.dart';
import 'shops_screen.dart';
import 'orders_screen.dart';
import 'wallet_screen.dart';
import 'sell_screen.dart';
import 'messages_screen.dart';
import 'auth_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _currentIndex = 0;

  late final List<Widget> _pages;

  @override
  void initState() {
    super.initState();
    _pages = [
      _HomeTab(onBrowseTap: () => _openBrowse(context)),
      const _ShopsTab(),
      const _OrdersTab(),
      const _ProfileTab(),
      const WalletScreen(),
    ];
  }

  void _openBrowse(BuildContext context) {
    Navigator.push(context, MaterialPageRoute(builder: (_) => const _ProductsTab()));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (i) => setState(() => _currentIndex = i),
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home_outlined), activeIcon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(icon: Icon(Icons.store_outlined), activeIcon: Icon(Icons.store), label: 'Shops'),
          BottomNavigationBarItem(icon: Icon(Icons.receipt_outlined), activeIcon: Icon(Icons.receipt), label: 'Orders'),
          BottomNavigationBarItem(icon: Icon(Icons.person_outline), activeIcon: Icon(Icons.person), label: 'Profile'),
          BottomNavigationBarItem(icon: Icon(Icons.account_balance_wallet_outlined), activeIcon: Icon(Icons.account_balance_wallet), label: 'Wallet'),
        ],
      ),
    );
  }
}

class _HomeTab extends StatelessWidget {
  final VoidCallback onBrowseTap;
  const _HomeTab({required this.onBrowseTap});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    final displayProducts = provider.filteredProducts;
    final cats = provider.categories.where((c) => c.id != 'all').toList();
    return SafeArea(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 8),
            Center(
              child: Column(
                children: [
                  Text('KampusKart', style: Theme.of(context).textTheme.headlineLarge),
                  const SizedBox(height: 4),
                  Text('Uganda Campus Marketplace', style: TextStyle(color: AppTheme.textSecondary, fontSize: 13)),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                ...cats.map((cat) {
                  final isSelected = provider.selectedCategory == cat.id;
                  return ActionChip(
                    avatar: Text(cat.icon, style: const TextStyle(fontSize: 14)),
                    label: Text(cat.name, style: const TextStyle(fontSize: 12)),
                    onPressed: () => provider.setCategory(isSelected ? null : cat.id),
                    backgroundColor: isSelected ? AppTheme.accent : AppTheme.bgCard,
                    side: isSelected ? BorderSide.none : BorderSide(color: AppTheme.borderColor),
                  );
                }),
                ActionChip(
                  avatar: const Icon(Icons.search, size: 16, color: AppTheme.textSecondary),
                  label: const Text('Browse', style: TextStyle(fontSize: 12)),
                  onPressed: onBrowseTap,
                  backgroundColor: AppTheme.bgElevated,
                ),
              ],
            ),
            const SizedBox(height: 24),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Featured Products', style: Theme.of(context).textTheme.headlineMedium?.copyWith(fontSize: 20)),
                TextButton(onPressed: onBrowseTap, child: const Text('View All')),
              ],
            ),
            const SizedBox(height: 12),
            if (displayProducts.isEmpty)
              Center(
                child: Text('No products in this category', style: TextStyle(color: AppTheme.textSecondary)),
              )
            else ...[
              _FeaturedCarousel(products: displayProducts.take(8).toList()),
              if (displayProducts.length > 8) ...[
                const SizedBox(height: 24),
                Text('More Products', style: Theme.of(context).textTheme.headlineMedium?.copyWith(fontSize: 20)),
                const SizedBox(height: 12),
                GridView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    childAspectRatio: 0.7,
                    crossAxisSpacing: 12,
                    mainAxisSpacing: 12,
                  ),
                  itemCount: displayProducts.length - 8,
                  itemBuilder: (context, i) {
                    final product = displayProducts.skip(8).toList()[i];
                    return ProductCard(
                      product: product,
                      onTap: () => Navigator.push(context, MaterialPageRoute(
                        builder: (_) => ProductDetailScreen(product: product),
                      )),
                    );
                  },
                ),
              ],
            ],
          ],
        ),
      ),
    );
  }
}

class _ProductsTab extends StatelessWidget {
  const _ProductsTab();

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    return Scaffold(
      appBar: AppBar(title: const Text('Browse Products'), centerTitle: true),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Search products...',
                prefixIcon: const Icon(Icons.search, color: AppTheme.textTertiary),
                suffixIcon: IconButton(
                  icon: const Icon(Icons.tune, color: AppTheme.textTertiary, size: 20),
                  onPressed: () {},
                ),
              ),
            ),
          ),
          Expanded(
            child: GridView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                childAspectRatio: 0.7,
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
              ),
              itemCount: provider.products.length,
              itemBuilder: (context, i) {
                final product = provider.products[i];
                return ProductCard(
                  product: product,
                  onTap: () => Navigator.push(context, MaterialPageRoute(
                    builder: (_) => ProductDetailScreen(product: product),
                  )),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _FeaturedCarousel extends StatefulWidget {
  final List<Product> products;
  const _FeaturedCarousel({required this.products});

  @override
  State<_FeaturedCarousel> createState() => _FeaturedCarouselState();
}

class _FeaturedCarouselState extends State<_FeaturedCarousel> {
  late PageController _controller;
  late Timer _timer;
  int _currentPage = 0;

  int get _pageCount => (widget.products.length / 2).ceil();

  @override
  void initState() {
    super.initState();
    _controller = PageController();
    _timer = Timer.periodic(const Duration(seconds: 7), (_) {
      if (!mounted) return;
      final next = (_currentPage + 1) % _pageCount;
      _controller.animateToPage(
        next,
        duration: const Duration(milliseconds: 800),
        curve: Curves.easeInOut,
      );
    });
  }

  @override
  void dispose() {
    _timer.cancel();
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.products.isEmpty) return const SizedBox.shrink();
    return Column(
      children: [
        SizedBox(
          height: 240,
          child: PageView.builder(
            controller: _controller,
            onPageChanged: (i) => setState(() => _currentPage = i),
            itemCount: _pageCount,
            itemBuilder: (context, pageIndex) {
              final start = pageIndex * 2;
              return Row(
                children: [
                  Expanded(
                    child: ProductCard(
                      product: widget.products[start],
                      onTap: () => Navigator.push(context, MaterialPageRoute(
                        builder: (_) => ProductDetailScreen(product: widget.products[start]),
                      )),
                    ),
                  ),
                  if (start + 1 < widget.products.length) ...[
                    const SizedBox(width: 12),
                    Expanded(
                      child: ProductCard(
                        product: widget.products[start + 1],
                        onTap: () => Navigator.push(context, MaterialPageRoute(
                          builder: (_) => ProductDetailScreen(product: widget.products[start + 1]),
                        )),
                      ),
                    ),
                  ],
                ],
              );
            },
          ),
        ),
        const SizedBox(height: 8),
        if (_pageCount > 1)
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: List.generate(_pageCount, (i) {
              return AnimatedContainer(
                duration: const Duration(milliseconds: 300),
                margin: const EdgeInsets.symmetric(horizontal: 3),
                width: _currentPage == i ? 20 : 8,
                height: 8,
                decoration: BoxDecoration(
                  color: _currentPage == i ? AppTheme.accent : AppTheme.borderColor,
                  borderRadius: BorderRadius.circular(4),
                ),
              );
            }),
          ),
      ],
    );
  }
}

class _ShopsTab extends StatelessWidget {
  const _ShopsTab();
  @override
  Widget build(BuildContext context) {
    return const ShopsScreen();
  }
}

class _OrdersTab extends StatelessWidget {
  const _OrdersTab();
  @override
  Widget build(BuildContext context) {
    return const OrdersScreen();
  }
}

class _ProfileTab extends StatelessWidget {
  const _ProfileTab();
  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    return SafeArea(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: provider.isLoggedIn ? _buildProfile(context, provider) : _buildGuest(context),
      ),
    );
  }

  Widget _buildGuest(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        const SizedBox(height: 60),
        const Text('👤', style: TextStyle(fontSize: 64)),
        const SizedBox(height: 16),
        const Text('Sign in to access your profile', style: TextStyle(color: AppTheme.textSecondary)),
        const SizedBox(height: 24),
        ElevatedButton(
          onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const AuthScreen())),
          child: const Text('Sign In'),
        ),
      ],
    );
  }

  Widget _buildProfile(BuildContext context, AppProvider provider) {
    final u = provider.user!;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(height: 16),
        Row(
          children: [
            CircleAvatar(
              radius: 32,
              backgroundImage: u.image != null ? NetworkImage(u.image!) : null,
              child: u.image == null ? Text(u.name[0].toUpperCase()) : null,
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(u.name, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  Text(u.email, style: const TextStyle(color: AppTheme.textSecondary)),
                ],
              ),
            ),
          ],
        ),
        const SizedBox(height: 24),
        if (u.isSeller) ...[
          _menuItem(context, Icons.add_circle_outline, 'Sell a Product', () => Navigator.push(context, MaterialPageRoute(builder: (_) => const SellScreen()))),
          _menuItem(context, Icons.account_balance_wallet_outlined, 'Wallet', () => Navigator.push(context, MaterialPageRoute(builder: (_) => const WalletScreen()))),
        ],
        _menuItem(context, Icons.chat_outlined, 'Messages', () => Navigator.push(context, MaterialPageRoute(builder: (_) => const MessagesScreen()))),
        _menuItem(context, Icons.shopping_cart_outlined, 'Cart (${provider.cartCount})', () => Navigator.push(context, MaterialPageRoute(builder: (_) => const CartScreen()))),
        _menuItem(context, Icons.receipt_outlined, 'Orders', null),
        const SizedBox(height: 24),
        SizedBox(
          width: double.infinity,
          child: OutlinedButton(
            onPressed: () async {
              await provider.logout();
            },
            style: OutlinedButton.styleFrom(foregroundColor: Colors.red[400]),
            child: const Text('Sign Out'),
          ),
        ),
      ],
    );
  }

  Widget _menuItem(BuildContext context, IconData icon, String label, VoidCallback? onTap) {
    return ListTile(
      leading: Icon(icon, color: AppTheme.textSecondary),
      title: Text(label),
      trailing: const Icon(Icons.chevron_right, color: AppTheme.textTertiary),
      onTap: onTap,
    );
  }
}

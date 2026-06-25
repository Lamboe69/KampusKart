import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../providers/app_provider.dart';
import '../theme/app_theme.dart';

class WalletScreen extends StatelessWidget {
  const WalletScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    if (!provider.isLoggedIn) {
      return Scaffold(
        appBar: AppBar(title: const Text('Wallet')),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Text('🔒', style: TextStyle(fontSize: 64)),
              const SizedBox(height: 16),
              const Text('Sign in to access your wallet', style: TextStyle(color: AppTheme.textSecondary)),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () => Navigator.pushNamed(context, '/auth'),
                child: const Text('Sign In'),
              ),
            ],
          ),
        ),
      );
    }

    final u = provider.user!;
    return Scaffold(
      appBar: AppBar(title: const Text('Wallet')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                gradient: const LinearGradient(colors: [AppTheme.accent, Color(0xFF6D28D9)]),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                children: [
                  const Text('Balance', style: TextStyle(color: Colors.white70, fontSize: 14)),
                  const SizedBox(height: 8),
                  Text('UGX ${NumberFormat('#,###').format(u.balance.toInt())}',
                    style: const TextStyle(fontSize: 36, fontWeight: FontWeight.bold, color: Colors.white)),
                  const SizedBox(height: 16),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _stat('Pending', u.pending.toInt()),
                      _stat('Total Earned', u.totalEarned.toInt()),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            const Text('Coming soon: Withdraw to Mobile Money',
              style: TextStyle(color: AppTheme.textTertiary)),
          ],
        ),
      ),
    );
  }

  Widget _stat(String label, int amount) {
    return Column(
      children: [
        Text('UGX ${NumberFormat('#,###').format(amount)}',
          style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white, fontSize: 16)),
        Text(label, style: const TextStyle(color: Colors.white70, fontSize: 12)),
      ],
    );
  }
}

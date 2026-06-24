import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/app_provider.dart';
import '../theme/app_theme.dart';

class MessagesScreen extends StatelessWidget {
  const MessagesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    return Scaffold(
      appBar: AppBar(title: const Text('Messages')),
      body: provider.isLoggedIn
          ? const Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text('💬', style: TextStyle(fontSize: 64)),
                  SizedBox(height: 16),
                  Text('No conversations yet', style: TextStyle(color: AppTheme.textSecondary)),
                  SizedBox(height: 8),
                  Text('Chat with sellers about their products', style: TextStyle(color: AppTheme.textTertiary, fontSize: 12)),
                ],
              ),
            )
          : const Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text('🔒', style: TextStyle(fontSize: 64)),
                  SizedBox(height: 16),
                  Text('Sign in to view messages', style: TextStyle(color: AppTheme.textSecondary)),
                ],
              ),
            ),
    );
  }
}

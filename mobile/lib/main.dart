import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'app.dart';
import 'providers/app_provider.dart';
import 'services/chat_service.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AppProvider()..init()),
        ChangeNotifierProvider(create: (_) => ChatService()),
      ],
      child: const KampusKartApp(),
    ),
  );
}

import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;

class ApiConfig {
  static String get baseUrl {
    if (kIsWeb || !Platform.isAndroid) {
      return 'http://localhost:3001/api';
    }
    return 'http://10.0.2.2:3001/api';
  }

  static const String login = '/auth/login';
  static const String register = '/auth/register';
  static const String me = '/auth/me';
  static const String products = '/products';
  static const String shops = '/shops';
  static const String orders = '/orders';
  static const String wallet = '/wallet';
  static const String notifications = '/notifications';
  static const String categories = '/categories';
  static const String campuses = '/campuses';
  static const String upload = '/upload';
  static const String searchSuggestions = '/search/suggestions';
  static const String reviews = '/reviews';
  static const String messages = '/messages';
}

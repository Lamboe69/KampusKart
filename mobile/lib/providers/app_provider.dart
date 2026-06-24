import 'package:flutter/material.dart';
import '../models/user.dart';
import '../models/product.dart';
import '../models/order.dart';
import '../models/shop.dart';
import '../models/category.dart';
import '../models/campus.dart';
import '../services/api_service.dart';
import '../services/auth_service.dart';
import '../config/api_config.dart';

class AppProvider extends ChangeNotifier {
  final AuthService _auth = AuthService();
  final ApiService _api = ApiService();

  User? _user;
  List<Product> _products = [];
  List<Shop> _shops = [];
  List<Order> _orders = [];
  List<Category> _categories = [];
  List<Campus> _campuses = [];
  List<Product> _cart = [];
  String? _selectedCategory;
  bool _loading = false;
  String? _error;

  User? get user => _user;
  List<Product> get products => _products;
  List<Shop> get shops => _shops;
  List<Order> get orders => _orders;
  List<Category> get categories => _categories;
  List<Campus> get campuses => _campuses;
  List<Product> get cart => _cart;
  String? get selectedCategory => _selectedCategory;
  List<Product> get filteredProducts => _selectedCategory == null || _selectedCategory == 'all'
      ? _products
      : _products.where((p) => p.category == _selectedCategory).toList();
  bool get loading => _loading;
  String? get error => _error;
  bool get isLoggedIn => _user != null;

  int get cartCount => _cart.fold(0, (sum, item) => sum + 1);

  double get cartTotal =>
      _cart.fold(0.0, (sum, item) => sum + item.price);

  Future<void> init() async {
    _loading = true;
    notifyListeners();
    try {
      await Future.wait([
        fetchCategories(),
        fetchCampuses(),
        _tryAutoLogin(),
      ]);
      if (_user != null) {
        await Future.wait([
          fetchProducts(),
          fetchShops(),
          fetchOrders(),
        ]);
      } else {
        await fetchProducts();
        await fetchShops();
      }
    } catch (e) {
      _error = e.toString();
    }
    _loading = false;
    notifyListeners();
  }

  Future<void> _tryAutoLogin() async {
    final loggedIn = await _auth.isLoggedIn();
    if (loggedIn) {
      try {
        _user = await _auth.getMe();
      } catch (_) {
        await _auth.logout();
      }
    }
  }

  Future<void> login(String email, String password) async {
    _loading = true;
    notifyListeners();
    try {
      _user = await _auth.login(email, password);
      await Future.wait([fetchProducts(), fetchShops(), fetchOrders()]);
    } catch (e) {
      rethrow;
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> register({
    required String name,
    required String email,
    required String password,
    String role = 'buyer',
    String? campus,
  }) async {
    _loading = true;
    notifyListeners();
    try {
      _user = await _auth.register(
        name: name, email: email, password: password,
        role: role, campus: campus,
      );
      await Future.wait([fetchProducts(), fetchShops(), fetchOrders()]);
    } catch (e) {
      rethrow;
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  void setCategory(String? category) {
    _selectedCategory = category;
    notifyListeners();
  }

  Future<void> logout() async {
    await _auth.logout();
    _user = null;
    _orders = [];
    _cart = [];
    notifyListeners();
  }

  Future<void> fetchProducts() async {
    try {
      final data = await _api.get(ApiConfig.products);
      _products = (data['products'] as List)
          .map((e) => Product.fromJson(e))
          .toList();
      notifyListeners();
    } catch (_) {}
  }

  Future<void> fetchShops() async {
    try {
      final data = await _api.get(ApiConfig.shops);
      _shops = (data['shops'] as List)
          .map((e) => Shop.fromJson(e))
          .toList();
      notifyListeners();
    } catch (_) {}
  }

  Future<void> fetchOrders() async {
    if (_user == null) return;
    try {
      final data = await _api.get(ApiConfig.orders);
      _orders = (data['orders'] as List)
          .map((e) => Order.fromJson(e))
          .toList();
      notifyListeners();
    } catch (_) {}
  }

  Future<void> fetchCategories() async {
    try {
      final data = await _api.get(ApiConfig.categories);
      _categories = (data['categories'] as List)
          .map((e) => Category.fromJson(e))
          .toList();
      notifyListeners();
    } catch (_) {}
  }

  Future<void> fetchCampuses() async {
    try {
      final data = await _api.get(ApiConfig.campuses);
      _campuses = (data['campuses'] as List)
          .map((e) => Campus.fromJson(e))
          .toList();
      notifyListeners();
    } catch (_) {}
  }

  void addToCart(Product product) {
    if (!_cart.any((p) => p.id == product.id)) {
      _cart.add(product);
      notifyListeners();
    }
  }

  void removeFromCart(int productId) {
    _cart.removeWhere((p) => p.id == productId);
    notifyListeners();
  }

  void clearCart() {
    _cart.clear();
    notifyListeners();
  }
}

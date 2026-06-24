import '../config/api_config.dart';
import '../models/user.dart';
import 'api_service.dart';

class AuthService {
  final ApiService _api = ApiService();

  Future<User> login(String email, String password) async {
    final data = await _api.post(ApiConfig.login, {
      'email': email,
      'password': password,
    });
    await _api.setToken(data['token']);
    return User.fromJson(data['user']);
  }

  Future<User> register({
    required String name,
    required String email,
    required String password,
    String role = 'buyer',
    String? campus,
    String sellerType = 'individual',
  }) async {
    final data = await _api.post(ApiConfig.register, {
      'name': name,
      'email': email,
      'password': password,
      'role': role,
      if (campus != null) 'campus': campus,
      'seller_type': sellerType,
    });
    await _api.setToken(data['token']);
    return User.fromJson(data['user']);
  }

  Future<User> getMe() async {
    final data = await _api.get(ApiConfig.me);
    return User.fromJson(data['user']);
  }

  Future<void> logout() async {
    await _api.setToken(null);
  }

  Future<bool> isLoggedIn() async {
    final token = await _api.getToken();
    return token != null && token.isNotEmpty;
  }
}

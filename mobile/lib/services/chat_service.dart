import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import '../config/api_config.dart';
import 'api_service.dart';

class ChatMessage {
  final int? id;
  final String senderId;
  final String receiverId;
  final String message;
  final String? image;
  final bool read;
  final String? createdAt;

  ChatMessage({
    this.id,
    required this.senderId,
    required this.receiverId,
    required this.message,
    this.image,
    this.read = false,
    this.createdAt,
  });

  factory ChatMessage.fromJson(Map<String, dynamic> json) {
    return ChatMessage(
      id: json['id'] is int ? json['id'] : int.tryParse('${json['id']}'),
      senderId: '${json['senderId'] ?? json['sender_id'] ?? ''}',
      receiverId: '${json['receiverId'] ?? json['receiver_id'] ?? ''}',
      message: json['message'] ?? '',
      image: json['image'],
      read: json['read'] == true || json['read'] == 1,
      createdAt: json['createdAt'] ?? json['created_at'],
    );
  }
}

class Conversation {
  final String userId;
  final String name;
  final String? image;
  final String lastMessage;
  final String? lastMessageTime;
  final int unreadCount;

  Conversation({
    required this.userId,
    required this.name,
    this.image,
    this.lastMessage = '',
    this.lastMessageTime,
    this.unreadCount = 0,
  });

  factory Conversation.fromJson(Map<String, dynamic> json) {
    return Conversation(
      userId: '${json['userId'] ?? json['user_id'] ?? ''}',
      name: json['name'] ?? '',
      image: json['image'],
      lastMessage: json['lastMessage'] ?? json['last_message'] ?? '',
      lastMessageTime: json['lastMessageTime'] ?? json['last_message_time'],
      unreadCount: json['unreadCount'] ?? 0,
    );
  }
}

class ChatService extends ChangeNotifier {
  final ApiService _api = ApiService();
  WebSocketChannel? _channel;
  String? _currentUserId;
  bool _isConnected = false;

  List<Conversation> conversations = [];
  List<ChatMessage> currentMessages = [];
  String? activeChatPartnerId;
  bool _typing = false;
  String? _typingUserId;

  bool get isConnected => _isConnected;
  bool get isPartnerTyping => _typing;
  String? get typingUserId => _typingUserId;

  final _messageController = StreamController<ChatMessage>.broadcast();
  Stream<ChatMessage> get messageStream => _messageController.stream;

  void connect(String token, String userId) {
    _currentUserId = userId;
    final wsUrl = '${ApiConfig.wsUrl}?token=$token';
    _channel = WebSocketChannel.connect(Uri.parse(wsUrl));

    _channel!.stream.listen(
      (data) {
        final json = jsonDecode(data);
        _handleMessage(json);
      },
      onDone: () {
        _isConnected = false;
        notifyListeners();
        _reconnect(token, userId);
      },
      onError: (error) {
        _isConnected = false;
        notifyListeners();
      },
    );
    _isConnected = true;
  }

  void _reconnect(String token, String userId) {
    Future.delayed(const Duration(seconds: 3), () {
      if (!_isConnected) {
        connect(token, userId);
      }
    });
  }

  void disconnect() {
    _channel?.sink.close();
    _channel = null;
    _isConnected = false;
    _currentUserId = null;
    activeChatPartnerId = null;
  }

  void _handleMessage(Map<String, dynamic> json) {
    switch (json['type']) {
      case 'new_message':
        final msg = ChatMessage.fromJson(json);
        if (activeChatPartnerId != null &&
            (msg.senderId == activeChatPartnerId || msg.receiverId == activeChatPartnerId)) {
          currentMessages.add(msg);
          notifyListeners();
        }
        _messageController.add(msg);
        break;
      case 'sent':
        break;
      case 'typing':
        _typing = true;
        _typingUserId = json['senderId'];
        notifyListeners();
        Future.delayed(const Duration(seconds: 2), () {
          if (_typingUserId == json['senderId']) {
            _typing = false;
            _typingUserId = null;
            notifyListeners();
          }
        });
        break;
      case 'read_receipt':
        for (var m in currentMessages) {
          if (m.receiverId == _currentUserId) {
            m.read = true;
          }
        }
        notifyListeners();
        break;
      case 'notification':
        break;
    }
  }

  void sendMessage(String receiverId, String text, {String? image}) {
    if (_channel == null || !_isConnected) return;
    final msg = {
      'type': 'message',
      'receiver_id': receiverId,
      'message': text,
      if (image != null) 'image': image,
    };
    _channel!.sink.add(jsonEncode(msg));

    final localMsg = ChatMessage(
      senderId: _currentUserId ?? '',
      receiverId: receiverId,
      message: text,
      image: image,
      createdAt: DateTime.now().toIso8601String(),
    );
    currentMessages.add(localMsg);
    notifyListeners();
  }

  void sendTyping(String receiverId) {
    if (_channel == null || !_isConnected) return;
    _channel!.sink.add(jsonEncode({'type': 'typing', 'receiver_id': receiverId}));
  }

  void sendReadReceipt(String senderId) {
    if (_channel == null || !_isConnected) return;
    _channel!.sink.add(jsonEncode({'type': 'read', 'sender_id': senderId}));
  }

  Future<void> loadConversations() async {
    try {
      final data = await _api.get(ApiConfig.conversations);
      final list = (data as List).map((c) => Conversation.fromJson(c)).toList();
      conversations = list;
      notifyListeners();
    } catch (e) {
      debugPrint('Failed to load conversations: $e');
    }
  }

  Future<void> loadMessages(String otherUserId) async {
    activeChatPartnerId = otherUserId;
    try {
      final data = await _api.get('${ApiConfig.messages}/$otherUserId');
      final list = (data as List).map((m) => ChatMessage.fromJson(m)).toList();
      currentMessages = list;
      notifyListeners();
      sendReadReceipt(otherUserId);
    } catch (e) {
      debugPrint('Failed to load messages: $e');
    }
  }

  Future<String?> uploadImage(String filePath) async {
    try {
      final result = await _api.upload(
        ApiConfig.upload,
        filePath: filePath,
      );
      return result['url'];
    } catch (e) {
      debugPrint('Image upload failed: $e');
      return null;
    }
  }
}

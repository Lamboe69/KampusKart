import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:image_picker/image_picker.dart';
import 'package:intl/intl.dart';
import '../config/api_config.dart';
import '../providers/app_provider.dart';
import '../services/chat_service.dart';
import '../theme/app_theme.dart';

class MessagesScreen extends StatefulWidget {
  const MessagesScreen({super.key});

  @override
  State<MessagesScreen> createState() => _MessagesScreenState();
}

class _MessagesScreenState extends State<MessagesScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _initChat();
    });
  }

  void _initChat() {
    final provider = context.read<AppProvider>();
    if (provider.isLoggedIn && provider.user != null) {
      final chatService = context.read<ChatService>();
      final api = ApiService();
      api.getToken().then((token) {
        if (token != null) {
          chatService.connect(token, provider.user!.id);
          chatService.loadConversations();
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    final chat = context.watch<ChatService>();

    if (!provider.isLoggedIn) {
      return Scaffold(
        appBar: AppBar(title: const Text('Messages')),
        body: const Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(Icons.lock_outline, size: 64, color: AppTheme.textTertiary),
              SizedBox(height: 16),
              Text('Sign in to view messages', style: TextStyle(color: AppTheme.textSecondary)),
            ],
          ),
        ),
      );
    }

    if (chat.activeChatPartnerId != null) {
      return _ChatView(chat: chat, provider: provider);
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Messages'),
        actions: [
          if (chat.isConnected)
            const Padding(
              padding: EdgeInsets.only(right: 12),
              child: Icon(Icons.circle, size: 8, color: Colors.greenAccent),
            ),
        ],
      ),
      body: chat.conversations.isEmpty
          ? const Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(Icons.chat_bubble_outline, size: 64, color: AppTheme.textTertiary),
                  SizedBox(height: 16),
                  Text('No conversations yet', style: TextStyle(color: AppTheme.textSecondary)),
                  SizedBox(height: 8),
                  Text('Chat with sellers about their products', style: TextStyle(color: AppTheme.textTertiary, fontSize: 12)),
                ],
              ),
            )
          : RefreshIndicator(
              onRefresh: () => chat.loadConversations(),
              child: ListView.builder(
                itemCount: chat.conversations.length,
                itemBuilder: (context, index) {
                  final conv = chat.conversations[index];
                  return _ConversationTile(
                    conversation: conv,
                    onTap: () {
                      chat.loadMessages(conv.userId);
                    },
                  );
                },
              ),
            ),
    );
  }
}

class _ConversationTile extends StatelessWidget {
  final Conversation conversation;
  final VoidCallback onTap;

  const _ConversationTile({required this.conversation, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final hasUnread = conversation.unreadCount > 0;
    return ListTile(
      onTap: onTap,
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      leading: CircleAvatar(
        radius: 24,
        backgroundColor: AppTheme.accent.withAlpha(40),
        child: Text(
          conversation.name.isNotEmpty ? conversation.name[0].toUpperCase() : '?',
          style: const TextStyle(color: AppTheme.accent, fontWeight: FontWeight.bold, fontSize: 18),
        ),
      ),
      title: Text(
        conversation.name,
        style: TextStyle(
          color: AppTheme.textPrimary,
          fontWeight: hasUnread ? FontWeight.bold : FontWeight.normal,
        ),
      ),
      subtitle: Text(
        conversation.lastMessage,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
        style: TextStyle(
          color: hasUnread ? AppTheme.textPrimary : AppTheme.textTertiary,
          fontWeight: hasUnread ? FontWeight.w500 : FontWeight.normal,
        ),
      ),
      trailing: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          if (conversation.lastMessageTime != null)
            Text(
              _formatTime(conversation.lastMessageTime!),
              style: TextStyle(
                fontSize: 11,
                color: hasUnread ? AppTheme.accent : AppTheme.textTertiary,
              ),
            ),
          if (hasUnread) ...[
            const SizedBox(height: 4),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
              decoration: BoxDecoration(
                color: AppTheme.accent,
                borderRadius: BorderRadius.circular(10),
              ),
              child: Text(
                '${conversation.unreadCount}',
                style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ],
      ),
    );
  }

  String _formatTime(String iso) {
    try {
      final dt = DateTime.parse(iso);
      final now = DateTime.now();
      if (dt.day == now.day && dt.month == now.month && dt.year == now.year) {
        return DateFormat.Hm().format(dt);
      }
      if (dt.difference(now).inDays > -7) {
        return DateFormat.E().format(dt);
      }
      return DateFormat.MMMd().format(dt);
    } catch (_) {
      return '';
    }
  }
}

class _ChatView extends StatefulWidget {
  final ChatService chat;
  final AppProvider provider;

  const _ChatView({required this.chat, required this.provider});

  @override
  State<_ChatView> createState() => _ChatViewState();
}

class _ChatViewState extends State<_ChatView> {
  final TextEditingController _controller = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  final ImagePicker _picker = ImagePicker();
  String? _partnerName;

  @override
  void initState() {
    super.initState();
    final partnerId = widget.chat.activeChatPartnerId;
    if (partnerId != null) {
      final conv = widget.chat.conversations.where((c) => c.userId == partnerId);
      if (conv.isNotEmpty) {
        _partnerName = conv.first.name;
      }
    }
    WidgetsBinding.instance.addPostFrameCallback((_) => _scrollToBottom());
  }

  void _scrollToBottom() {
    Future.delayed(const Duration(milliseconds: 100), () {
      if (_scrollController.hasClients) {
        _scrollController.jumpTo(_scrollController.position.maxScrollExtent);
      }
    });
  }

  void _send() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;
    final partnerId = widget.chat.activeChatPartnerId;
    if (partnerId == null) return;
    widget.chat.sendMessage(partnerId, text);
    _controller.clear();
    Future.delayed(const Duration(milliseconds: 100), () => _scrollToBottom());
  }

  void _sendImage() async {
    final XFile? image = await _picker.pickImage(source: ImageSource.gallery, imageQuality: 80);
    if (image == null) return;
    final partnerId = widget.chat.activeChatPartnerId;
    if (partnerId == null) return;

    final url = await widget.chat.uploadImage(image.path);
    if (url != null) {
      widget.chat.sendMessage(partnerId, 'Image', image: url);
      Future.delayed(const Duration(milliseconds: 100), () => _scrollToBottom());
    }
  }

  @override
  Widget build(BuildContext context) {
    final partnerId = widget.chat.activeChatPartnerId ?? '';
    final myId = widget.provider.user?.id ?? '';

    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            widget.chat.activeChatPartnerId = null;
            widget.chat.currentMessages = [];
            widget.chat.loadConversations();
          },
        ),
        title: Text(_partnerName ?? 'Chat'),
        actions: [
          if (widget.chat.isPartnerTyping)
            const Padding(
              padding: EdgeInsets.only(right: 12),
              child: Text('typing...', style: TextStyle(color: AppTheme.accent, fontSize: 12)),
            ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              itemCount: widget.chat.currentMessages.length,
              itemBuilder: (context, index) {
                final msg = widget.chat.currentMessages[index];
                final isMe = msg.senderId == myId;
                return _MessageBubble(message: msg, isMe: isMe);
              },
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
            decoration: const BoxDecoration(
              color: AppTheme.bgNav,
              border: Border(top: BorderSide(color: AppTheme.borderColor)),
            ),
            child: SafeArea(
              child: Row(
                children: [
                  IconButton(
                    onPressed: _sendImage,
                    icon: const Icon(Icons.image_outlined, color: AppTheme.textSecondary),
                  ),
                  Expanded(
                    child: TextField(
                      controller: _controller,
                      style: const TextStyle(color: AppTheme.textPrimary),
                      decoration: InputDecoration(
                        hintText: 'Type a message...',
                        hintStyle: const TextStyle(color: AppTheme.textTertiary),
                        filled: true,
                        fillColor: AppTheme.bgElevated,
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(24),
                          borderSide: BorderSide.none,
                        ),
                        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                      ),
                      onChanged: (text) {
                        widget.chat.sendTyping(partnerId);
                      },
                      onSubmitted: (_) => _send(),
                    ),
                  ),
                  const SizedBox(width: 4),
                  IconButton(
                    onPressed: _send,
                    icon: const Icon(Icons.send, color: AppTheme.accent),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _MessageBubble extends StatelessWidget {
  final ChatMessage message;
  final bool isMe;

  const _MessageBubble({required this.message, required this.isMe});

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 4),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.75,
        ),
        child: Column(
          crossAxisAlignment: isMe ? CrossAxisAlignment.end : CrossAxisAlignment.start,
          children: [
            if (message.image != null && message.image!.isNotEmpty)
              ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Image.network(
                  '${ApiConfig.imageBaseUrl}${message.image}',
                  width: 200,
                  height: 200,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) => Container(
                    width: 200,
                    height: 120,
                    color: AppTheme.bgElevated,
                    child: const Icon(Icons.broken_image, color: AppTheme.textTertiary),
                  ),
                ),
              ),
            if (message.message.isNotEmpty && message.message != 'Image')
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                decoration: BoxDecoration(
                  color: isMe ? AppTheme.accent : AppTheme.bgElevated,
                  borderRadius: BorderRadius.only(
                    topLeft: const Radius.circular(16),
                    topRight: const Radius.circular(16),
                    bottomLeft: Radius.circular(isMe ? 16 : 4),
                    bottomRight: Radius.circular(isMe ? 4 : 16),
                  ),
                ),
                child: Text(
                  message.message,
                  style: TextStyle(
                    color: isMe ? Colors.white : AppTheme.textPrimary,
                    fontSize: 14,
                  ),
                ),
              ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    _formatTime(message.createdAt),
                    style: const TextStyle(fontSize: 10, color: AppTheme.textTertiary),
                  ),
                  if (isMe) ...[
                    const SizedBox(width: 4),
                    Icon(
                      message.read ? Icons.done_all : Icons.done,
                      size: 14,
                      color: message.read ? AppTheme.accent : AppTheme.textTertiary,
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _formatTime(String? iso) {
    if (iso == null) return '';
    try {
      final dt = DateTime.parse(iso);
      return DateFormat.Hm().format(dt);
    } catch (_) {
      return '';
    }
  }
}

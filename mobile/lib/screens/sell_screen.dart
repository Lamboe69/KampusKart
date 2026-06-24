import 'package:flutter/material.dart';
import '../config/api_config.dart';
import '../services/api_service.dart';

class SellScreen extends StatefulWidget {
  const SellScreen({super.key});

  @override
  State<SellScreen> createState() => _SellScreenState();
}

class _SellScreenState extends State<SellScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleCtrl = TextEditingController();
  final _priceCtrl = TextEditingController();
  final _descCtrl = TextEditingController();
  final _deliveryCtrl = TextEditingController();
  String _category = 'electronics';
  String _condition = 'Brand New';
  bool _submitting = false;
  String? _error;

  @override
  void dispose() {
    _titleCtrl.dispose();
    _priceCtrl.dispose();
    _descCtrl.dispose();
    _deliveryCtrl.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() { _submitting = true; _error = null; });
    try {
      await ApiService().post(ApiConfig.products, {
        'title': _titleCtrl.text.trim(),
        'price': int.parse(_priceCtrl.text.trim()),
        'category': _category,
        'condition': _condition,
        'description': _descCtrl.text.trim(),
        'delivery_fee': int.tryParse(_deliveryCtrl.text.trim()) ?? 0,
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Product listed successfully!')),
        );
        Navigator.pop(context);
      }
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Sell a Product')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              TextFormField(
                controller: _titleCtrl,
                decoration: const InputDecoration(labelText: 'Product Title', prefixIcon: Icon(Icons.label_outline)),
                validator: (v) => v == null || v.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _priceCtrl,
                decoration: const InputDecoration(labelText: 'Price (UGX)', prefixIcon: Icon(Icons.money)),
                keyboardType: TextInputType.number,
                validator: (v) => v == null || v.isEmpty ? 'Required' : null,
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _category,
                decoration: const InputDecoration(labelText: 'Category', prefixIcon: Icon(Icons.category_outlined)),
                items: const [
                  DropdownMenuItem(value: 'electronics', child: Text('📱 Electronics')),
                  DropdownMenuItem(value: 'fashion', child: Text('👕 Fashion')),
                  DropdownMenuItem(value: 'books', child: Text('📚 Books & Notes')),
                  DropdownMenuItem(value: 'food', child: Text('🍔 Food & Snacks')),
                  DropdownMenuItem(value: 'beauty', child: Text('💄 Beauty')),
                  DropdownMenuItem(value: 'services', child: Text('🔧 Services')),
                  DropdownMenuItem(value: 'furniture', child: Text('🪑 Furniture')),
                ],
                onChanged: (v) => setState(() => _category = v!),
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _condition,
                decoration: const InputDecoration(labelText: 'Condition', prefixIcon: Icon(Icons.info_outline)),
                items: ['Brand New', 'Like New', 'Good', 'Fair'].map((c) =>
                  DropdownMenuItem(value: c, child: Text(c))).toList(),
                onChanged: (v) => setState(() => _condition = v!),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _descCtrl,
                decoration: const InputDecoration(labelText: 'Description', prefixIcon: Icon(Icons.description_outlined), alignLabelWithHint: true),
                maxLines: 4,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _deliveryCtrl,
                decoration: const InputDecoration(labelText: 'Delivery Fee (UGX)', prefixIcon: Icon(Icons.local_shipping_outlined)),
                keyboardType: TextInputType.number,
              ),
              const SizedBox(height: 24),
              if (_error != null)
                Padding(
                  padding: const EdgeInsets.only(bottom: 16),
                  child: Text(_error!, style: const TextStyle(color: Colors.red)),
                ),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _submitting ? null : _submit,
                  child: _submitting
                      ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                      : const Text('List Product'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

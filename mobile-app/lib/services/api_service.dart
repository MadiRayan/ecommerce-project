import 'dart:convert';
import 'dart:io';

import 'package:http/http.dart' as http;

import '../config/woo_config.dart';
import '../models/product.dart';

class ApiService {
  Map<String, String> get _headers {
    final credentials = base64Encode(
      utf8.encode(
        '${WooConfig.consumerKey}:${WooConfig.consumerSecret}',
      ),
    );

    return {
      'Authorization': 'Basic $credentials',
      'Content-Type': 'application/json',
      'ngrok-skip-browser-warning': 'true',
    };
  }

  // ---------------------------------------------------------
  // GET PRODUCTS DIRECTLY FROM WOOCOMMERCE
  // ---------------------------------------------------------
  Future<List<Product>> fetchProducts() async {
    final uri = Uri.parse(
      '${WooConfig.baseUrl}/wp-json/wc/v3/products?per_page=100',
    );

    final response = await http.get(
      uri,
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data.map((json) {
        String imageUrl = '';

        if (json['images'] != null &&
            json['images'] is List &&
            (json['images'] as List).isNotEmpty) {
          imageUrl = json['images'][0]['src']?.toString() ?? '';

          // LocalWP image URLs normally contain blue.local.
          // Replace them so the physical phone can access the images.
          imageUrl = imageUrl
              .replaceFirst(
                'https://blue.local',
                WooConfig.baseUrl,
              )
              .replaceFirst(
                'http://blue.local',
                WooConfig.baseUrl,
              );
        }

        String category = 'General';

        if (json['categories'] != null &&
            json['categories'] is List &&
            (json['categories'] as List).isNotEmpty) {
          category =
              json['categories'][0]['name']?.toString() ?? 'General';
        }

        return Product.fromJson({
          'id': json['id'],
          'name': json['name'],
          'regular_price':
              json['regular_price'] ?? json['price'] ?? '0',
          'sale_price': json['sale_price'] ?? '0',
          'imageUrl': imageUrl,
          'category': category,
        });
      }).toList();
    }

    throw Exception(
      'Failed to load WooCommerce products. '
      'Status: ${response.statusCode}\n'
      '${response.body}',
    );
  }

  // ---------------------------------------------------------
  // IMAGE UPLOAD
  // ---------------------------------------------------------
  //
  // WooCommerce consumer keys authenticate WooCommerce itself,
  // but they do not directly upload files to the WordPress
  // Media Library.
  //
  // For now we return an empty URL so products can still be
  // created without an image.
  //
  // We can add proper WordPress image upload later.
  Future<String?> uploadImage(File imageFile) async {
    return '';
  }

  // ---------------------------------------------------------
  // FIND AN EXISTING WOOCOMMERCE CATEGORY
  // ---------------------------------------------------------
  Future<int?> _findCategoryId(String category) async {
    if (category.trim().isEmpty ||
        category.toLowerCase() == 'general') {
      return null;
    }

    final uri = Uri.parse(
      '${WooConfig.baseUrl}/wp-json/wc/v3/products/categories',
    ).replace(
      queryParameters: {
        'search': category,
        'per_page': '100',
      },
    );

    final response = await http.get(
      uri,
      headers: _headers,
    );

    if (response.statusCode != 200) {
      return null;
    }

    final List<dynamic> categories = jsonDecode(response.body);

    for (final item in categories) {
      final String name =
          item['name']?.toString().trim().toLowerCase() ?? '';

      if (name == category.trim().toLowerCase()) {
        final dynamic id = item['id'];

        if (id is int) {
          return id;
        }

        return int.tryParse(id.toString());
      }
    }

    return null;
  }

  // ---------------------------------------------------------
  // CREATE PRODUCT DIRECTLY IN WOOCOMMERCE
  // ---------------------------------------------------------
  Future<bool> createProduct({
    required String name,
    required double regularPrice,
    required double salePrice,
    required String imageUrl,
    required String category,
  }) async {
    final uri = Uri.parse(
      '${WooConfig.baseUrl}/wp-json/wc/v3/products',
    );

    final Map<String, dynamic> body = {
      'name': name,
      'type': 'simple',
      'status': 'publish',
      'regular_price': regularPrice.toStringAsFixed(2),
    };

    if (salePrice > 0) {
      body['sale_price'] = salePrice.toStringAsFixed(2);
    }

    if (imageUrl.trim().isNotEmpty) {
      body['images'] = [
        {
          'src': imageUrl.trim(),
        }
      ];
    }

    final categoryId = await _findCategoryId(category);

    if (categoryId != null) {
      body['categories'] = [
        {
          'id': categoryId,
        }
      ];
    }

    final response = await http.post(
      uri,
      headers: _headers,
      body: jsonEncode(body),
    );

    return response.statusCode == 200 ||
        response.statusCode == 201;
  }

  // ---------------------------------------------------------
  // CREATE ORDER DIRECTLY IN WOOCOMMERCE
  // ---------------------------------------------------------
  Future<bool> placeOrder({
    required String customerName,
    required String customerEmail,
    required String address,
    required List<Map<String, dynamic>> items,
    required double totalAmount,
  }) async {
    final uri = Uri.parse(
      '${WooConfig.baseUrl}/wp-json/wc/v3/orders',
    );

    final List<Map<String, dynamic>> lineItems = [];

    for (final item in items) {
      final dynamic rawId =
          item['product_id'] ?? item['productId'] ?? item['id'];

      final int? productId = rawId is int
          ? rawId
          : int.tryParse(rawId?.toString() ?? '');

      final dynamic rawQuantity = item['quantity'] ?? 1;

      final int quantity = rawQuantity is int
          ? rawQuantity
          : int.tryParse(rawQuantity.toString()) ?? 1;

      if (productId != null) {
        lineItems.add({
          'product_id': productId,
          'quantity': quantity,
        });
      }
    }

    if (lineItems.isEmpty) {
      return false;
    }

    final nameParts = customerName
        .trim()
        .split(RegExp(r'\s+'))
        .where((part) => part.isNotEmpty)
        .toList();

    final firstName =
        nameParts.isNotEmpty ? nameParts.first : customerName;

    final lastName =
        nameParts.length > 1 ? nameParts.sublist(1).join(' ') : '';

    final body = {
      'payment_method': 'cod',
      'payment_method_title': 'Cash on Delivery',
      'set_paid': false,
      'billing': {
        'first_name': firstName,
        'last_name': lastName,
        'email': customerEmail,
        'address_1': address,
      },
      'shipping': {
        'first_name': firstName,
        'last_name': lastName,
        'address_1': address,
      },
      'line_items': lineItems,
    };

    final response = await http.post(
      uri,
      headers: _headers,
      body: jsonEncode(body),
    );

    return response.statusCode == 200 ||
        response.statusCode == 201;
  }
}
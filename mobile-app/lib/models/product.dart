class Product {
  final int id;
  final String name;
  final double regularPrice;
  final double salePrice;
  final String imageUrl;
  final String category;

  Product({
    required this.id,
    required this.name,
    required this.regularPrice,
    required this.salePrice,
    required this.imageUrl,
    this.category = 'General',
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    double parsePrice(dynamic val) {
      if (val == null) return 0.0;
      if (val is num) return val.toDouble();
      return double.tryParse(val.toString()) ?? 0.0;
    }

    return Product(
      id: json['id'] is int ? json['id'] : int.tryParse(json['id']?.toString() ?? '0') ?? 0,
      name: json['name'] ?? 'Unnamed Product',
      regularPrice: parsePrice(json['regular_price'] ?? json['price']),
      salePrice: parsePrice(json['sale_price']),
      imageUrl: json['imageUrl'] ??
          (json['images'] != null && (json['images'] as List).isNotEmpty
              ? json['images'][0]['src']
              : ''),
      category: json['category'] ?? 'General',
    );
  }

  double get effectivePrice => salePrice > 0 ? salePrice : regularPrice;
  bool get isOnSale => salePrice > 0 && salePrice < regularPrice;
  int get discountPercentage =>
      isOnSale ? (((regularPrice - salePrice) / regularPrice) * 100).round() : 0;
}
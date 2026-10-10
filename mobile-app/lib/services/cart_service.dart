import '../models/cart_item.dart';
import '../models/product.dart';

class CartService {
  final List<CartItem> _items = [];

  List<CartItem> get items => _items;

  void addToCart(Product product, {int quantity = 1}) {
    int index = _items.indexWhere((i) => i.product.id == product.id);
    if (index >= 0) {
      _items[index].quantity += quantity;
    } else {
      _items.add(CartItem(product: product, quantity: quantity));
    }
  }

  void removeFromCart(int productId) {
    _items.removeWhere((item) => item.product.id == productId);
  }

  double get totalPrice {
    return _items.fold(0.0,
        (sum, item) => sum + (item.product.effectivePrice * item.quantity));
  }

  int get totalItemCount {
    return _items.fold(0, (sum, item) => sum + item.quantity);
  }

  void clear() {
    _items.clear();
  }
}

package com.example.cart

final case class Cart(items: Map[Product, Int]) {
  def addItem(product: Product, quantity: Int): Cart =
    Cart(items.updated(product, items.getOrElse(product, 0) + quantity))
}

object Cart {
  val empty: Cart = Cart(Map.empty)
}

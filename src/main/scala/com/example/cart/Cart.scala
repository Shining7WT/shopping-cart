package com.example.cart

import scala.math.BigDecimal.RoundingMode

final case class Cart(items: Map[Product, Int]) {
  def addItem(product: Product, quantity: Int): Cart =
    Cart(items.updated(product, items.getOrElse(product, 0) + quantity))

  def subtotal: BigDecimal =
    items.map { case (product, quantity) => product.price * quantity }.sum.setScale(2, RoundingMode.HALF_UP)

  def tax: BigDecimal =
    (subtotal * Cart.taxRate).setScale(2, RoundingMode.HALF_UP)

  def total: BigDecimal =
    subtotal + tax
}

object Cart {
  val empty: Cart = Cart(Map.empty)

  val taxRate: BigDecimal = BigDecimal("0.125")
}

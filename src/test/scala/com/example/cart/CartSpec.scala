package com.example.cart

class CartSpec extends munit.FunSuite {

  val bread: Product = Product("bread", BigDecimal("2.52"))
  val peanutButter: Product = Product("peanut-butter", BigDecimal("5.75"))

  test("empty cart has no items") {
    assertEquals(Cart.empty.items, Map.empty[Product, Int])
  }

  test("adding a product to an empty cart records its quantity") {
    val cart = Cart.empty.addItem(bread, 2)
    assertEquals(cart.items, Map(bread -> 2))
  }

  test("adding an already-present product accumulates the quantity") {
    val cart = Cart.empty.addItem(bread, 2).addItem(bread, 3)
    assertEquals(cart.items, Map(bread -> 5))
  }

  test("adding distinct products keeps them separate") {
    val cart = Cart.empty.addItem(bread, 2).addItem(peanutButter, 1)
    assertEquals(cart.items, Map(bread -> 2, peanutButter -> 1))
  }

  test("addItem is pure and does not mutate the original cart") {
    val original = Cart.empty.addItem(bread, 2)
    val updated = original.addItem(bread, 1)
    assertEquals(original.items, Map(bread -> 2))
    assertEquals(updated.items, Map(bread -> 3))
  }

  test("empty cart has zero subtotal, tax, and total") {
    assertEquals(Cart.empty.subtotal, BigDecimal("0.00"))
    assertEquals(Cart.empty.tax, BigDecimal("0.00"))
    assertEquals(Cart.empty.total, BigDecimal("0.00"))
  }

  test("subtotal sums price * quantity across all items") {
    val cart = Cart.empty.addItem(bread, 2).addItem(peanutButter, 1)
    assertEquals(cart.subtotal, BigDecimal("10.79"))
  }

  test("tax is 12.5% of the subtotal, rounded half-up to 2 decimal places") {
    val cart = Cart.empty.addItem(bread, 2).addItem(peanutButter, 1)
    assertEquals(cart.tax, BigDecimal("1.35"))
  }

  test("total is subtotal plus tax, matching the README's worked example") {
    val cart = Cart.empty.addItem(bread, 2).addItem(peanutButter, 1)
    assertEquals(cart.total, BigDecimal("12.14"))
  }
}

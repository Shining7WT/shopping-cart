package com.example.cart

trait PricingService[F[_]] {
  def priceOf(productName: String): F[Product]
}

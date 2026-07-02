package com.example.cart

import cats.effect.Concurrent
import cats.syntax.all._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.{EntityDecoder, Uri}
import org.http4s.circe.jsonOf
import org.http4s.client.Client

private final case class PricingResponse(title: String, price: BigDecimal)

private object PricingResponse {
  implicit val decoder: Decoder[PricingResponse] = deriveDecoder
}

final class Http4sPricingService[F[_]: Concurrent](client: Client[F], baseUri: Uri) extends PricingService[F] {

  implicit private val entityDecoder: EntityDecoder[F, PricingResponse] = jsonOf[F, PricingResponse]

  def priceOf(productName: String): F[Product] =
    client
      .expect[PricingResponse](baseUri / s"${productName.toLowerCase}.json")
      .map(response => Product(productName, response.price))
}

object Http4sPricingService {
  val defaultBaseUri: Uri =
    Uri.unsafeFromString("https://raw.githubusercontent.com/siriusxm/shopping-cart-test-data/main")
}

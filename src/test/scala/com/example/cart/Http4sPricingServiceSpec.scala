package com.example.cart

import cats.effect.IO
import io.circe.literal._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{HttpRoutes, Request, Uri}

class Http4sPricingServiceSpec extends munit.CatsEffectSuite {

  private val baseUri: Uri = uri"https://example.test/pricing"

  private def clientReturning(routes: HttpRoutes[IO]): Client[IO] =
    Client.fromHttpApp(routes.orNotFound)

  test("priceOf requests {name}.json under the base URI and decodes title/price into a Product") {
    var requestedPath: Option[String] = None

    val routes = HttpRoutes.of[IO] {
      case req @ GET -> Root / "pricing" / "bread.json" =>
        requestedPath = Some(req.uri.path.toString)
        Ok(json"""{"title": "Bread", "price": 2.52}""")
    }

    val service = new Http4sPricingService[IO](clientReturning(routes), baseUri)

    service.priceOf("bread").map { product =>
      assertEquals(product, Product("bread", BigDecimal("2.52")))
      assertEquals(requestedPath, Some("/pricing/bread.json"))
    }
  }

  test("priceOf lowercases the product name when building the request path") {
    val routes = HttpRoutes.of[IO] {
      case GET -> Root / "pricing" / "peanut-butter.json" =>
        Ok(json"""{"title": "Peanut Butter", "price": 5.75}""")
    }

    val service = new Http4sPricingService[IO](clientReturning(routes), baseUri)

    service.priceOf("Peanut-Butter").map { product =>
      assertEquals(product, Product("Peanut-Butter", BigDecimal("5.75")))
    }
  }

  test("priceOf fails when the server responds with a non-2xx status") {
    val routes = HttpRoutes.of[IO] { case GET -> Root / "pricing" / "unknown.json" =>
      NotFound()
    }

    val service = new Http4sPricingService[IO](clientReturning(routes), baseUri)

    service.priceOf("unknown").attempt.map { result =>
      assert(result.isLeft, s"expected a failure, got $result")
    }
  }

  test("priceOf fails when the response body is not valid pricing JSON") {
    val routes = HttpRoutes.of[IO] { case GET -> Root / "pricing" / "malformed.json" =>
      Ok(json"""{"unexpected": "shape"}""")
    }

    val service = new Http4sPricingService[IO](clientReturning(routes), baseUri)

    service.priceOf("malformed").attempt.map { result =>
      assert(result.isLeft, s"expected a decoding failure, got $result")
    }
  }
}

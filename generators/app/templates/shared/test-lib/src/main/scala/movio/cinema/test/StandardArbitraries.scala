package movio.cinema.test

import org.scalacheck.Arbitrary
import org.scalacheck.Gen

trait DateTimeArbitrary {
  import org.joda.time._

  implicit val arbLocalDateTime: Arbitrary[LocalDateTime] =
    Arbitrary(Gen.posNum[Long] map (new LocalDateTime(_)))

  implicit val arbDateTime: Arbitrary[DateTime] =
    Arbitrary(Gen.posNum[Long] map (new DateTime(_)))
}

trait PlayJsonArbitrary {
  import java.math.BigDecimal
  import play.api.libs.json._
  import Arbitrary.arbitrary
  import Gen._

  implicit val arbJsonObject: Arbitrary[JsObject] = Arbitrary(jsonObjectNode)
  implicit val arbJsonArray: Arbitrary[JsArray] = Arbitrary(jsonArrayNode)

  lazy val shortText: Gen[String] =
    for {
      n ← choose(0, 4)
      chars ← listOfN(n, alphaNumChar)
    } yield chars.mkString

  lazy val jsonWholeNumberNode: Gen[JsNumber] = arbitrary[Long] map (n ⇒ JsNumber(new BigDecimal(n)))
  lazy val jsonDecimalNumberNode: Gen[JsNumber]  = arbitrary[Double] map (n ⇒ JsNumber(new BigDecimal(n)))

  lazy val jsonNumberNode: Gen[JsNumber] = oneOf(jsonWholeNumberNode, jsonDecimalNumberNode)
  lazy val jsonStringNode: Gen[JsString] = shortText map JsString.apply
  lazy val jsonBoolNode: Gen[JsBoolean] = oneOf(true, false) map JsBoolean.apply
  lazy val jsonNullNode: Gen[JsNull.type] = const(JsNull)

  lazy val jsonValueNode: Gen[JsValue] =
    oneOf(jsonNumberNode, jsonStringNode, jsonBoolNode, jsonNullNode, jsonArrayNode, jsonObjectNode)

  lazy val jsonArrayNode: Gen[JsArray] =
    for {
      n ← choose(1, 4)
      values ← listOfN(n, jsonValueNode)
    } yield JsArray(values)

  lazy val jsonObjectNode: Gen[JsObject] =
    for {
      n ← choose(1, 4)
      keys ← listOfN(n, shortText)
      values ← listOfN(n, jsonValueNode)
    } yield JsObject(keys zip values)
}

object StandardArbitraries extends DateTimeArbitrary with PlayJsonArbitrary

/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import java.time.LocalDate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class CorporationTaxLiabilitySpec extends AnyWordSpec with Matchers {

  "CorporationTaxLiability" should {
    "convert to XML correctly" in {
      val periodEnd        = LocalDate.of(2024, 3, 31)
      val corpTaxLiability = CorporationTaxLiability(
        periodEnd = periodEnd,
        howMuchIncome = BigInt(50000),
        howMuchUnpaid = BigInt(10000),
        howMuchInterest = BigInt(500),
        penaltyRate = BigDecimal(12.75),
        penaltyRateReason = "Late payment"
      )

      val xml = corpTaxLiability.toXml

      xml.headOption.map(_.label)      shouldBe Some("corporationTaxLiability")
      (xml \ "periodEnd").text         shouldBe "2024-03-31"
      (xml \ "howMuchIncome").text     shouldBe "50000"
      (xml \ "howMuchUnpaid").text     shouldBe "10000"
      (xml \ "howMuchInterest").text   shouldBe "500"
      (xml \ "penaltyRate").text       shouldBe "12.75"
      (xml \ "penaltyRateReason").text shouldBe "Late payment"
    }

    "serialize and deserialize correctly" in {
      val periodEnd        = LocalDate.of(2024, 3, 31)
      val corpTaxLiability = CorporationTaxLiability(
        periodEnd = periodEnd,
        howMuchIncome = BigInt(50000),
        howMuchUnpaid = BigInt(10000),
        howMuchInterest = BigInt(500),
        penaltyRate = BigDecimal(12.75),
        penaltyRateReason = "Late payment"
      )

      val json   = Json.toJson(corpTaxLiability)
      val parsed = json.validate[CorporationTaxLiability]

      parsed                                  shouldBe JsSuccess(corpTaxLiability)
      (json \ "periodEnd").as[String]         shouldBe "2024-03-31"
      (json \ "howMuchIncome").as[BigInt]     shouldBe BigInt(50000)
      (json \ "howMuchUnpaid").as[BigInt]     shouldBe BigInt(10000)
      (json \ "howMuchInterest").as[BigInt]   shouldBe BigInt(500)
      (json \ "penaltyRate").as[BigDecimal]   shouldBe BigDecimal(12.75)
      (json \ "penaltyRateReason").as[String] shouldBe "Late payment"
    }
  }
}

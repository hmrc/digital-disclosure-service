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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class OnshoreTaxYearLiabilitiesSpec extends AnyWordSpec with Matchers {

  "OnshoreTaxYearLiabilities" should {
    "convert to XML correctly" when {
      "it has all fields populated" in {
        val model = OnshoreTaxYearLiabilities(
          nonBusinessIncome = Some(BigInt(1000)),
          businessIncome = Some(BigInt(2000)),
          lettingIncome = Some(BigInt(3000)),
          gains = Some(BigInt(4000)),
          unpaidTax = BigInt(5000),
          niContributions = BigInt(6000),
          interest = BigInt(7000),
          penaltyRate = BigDecimal(15.5),
          penaltyRateReason = "Test reason",
          undeclaredIncomeOrGain = Some("Test income source"),
          residentialTaxReduction = Some(true)
        )

        val xml = model.toXml

        (xml \ "nonBusinessIncome").text       shouldBe "1000"
        (xml \ "businessIncome").text          shouldBe "2000"
        (xml \ "lettingIncome").text           shouldBe "3000"
        (xml \ "gains").text                   shouldBe "4000"
        (xml \ "unpaidTax").text               shouldBe "5000"
        (xml \ "niContributions").text         shouldBe "6000"
        (xml \ "interest").text                shouldBe "7000"
        (xml \ "penaltyRate").text             shouldBe "15.5"
        (xml \ "penaltyRateReason").text       shouldBe "Test reason"
        (xml \ "undeclaredIncomeOrGain").text  shouldBe "Test income source"
        (xml \ "residentialTaxReduction").text shouldBe "true"
      }

      "it has only mandatory fields populated" in {
        val model = OnshoreTaxYearLiabilities(
          gains = None,
          unpaidTax = BigInt(5000),
          niContributions = BigInt(6000),
          interest = BigInt(7000),
          penaltyRate = BigDecimal(15.5),
          penaltyRateReason = "Test reason",
          residentialTaxReduction = None
        )

        val xml = model.toXml

        (xml \ "nonBusinessIncome").isEmpty       shouldBe true
        (xml \ "businessIncome").isEmpty          shouldBe true
        (xml \ "lettingIncome").isEmpty           shouldBe true
        (xml \ "gains").isEmpty                   shouldBe true
        (xml \ "unpaidTax").text                  shouldBe "5000"
        (xml \ "niContributions").text            shouldBe "6000"
        (xml \ "interest").text                   shouldBe "7000"
        (xml \ "penaltyRate").text                shouldBe "15.5"
        (xml \ "penaltyRateReason").text          shouldBe "Test reason"
        (xml \ "undeclaredIncomeOrGain").isEmpty  shouldBe true
        (xml \ "residentialTaxReduction").isEmpty shouldBe true
      }
    }

    "serialize and deserialize correctly" when {
      "it has all fields populated" in {
        val model = OnshoreTaxYearLiabilities(
          nonBusinessIncome = Some(BigInt(1000)),
          businessIncome = Some(BigInt(2000)),
          lettingIncome = Some(BigInt(3000)),
          gains = Some(BigInt(4000)),
          unpaidTax = BigInt(5000),
          niContributions = BigInt(6000),
          interest = BigInt(7000),
          penaltyRate = BigDecimal(15.5),
          penaltyRateReason = "Test reason",
          undeclaredIncomeOrGain = Some("Test income source"),
          residentialTaxReduction = Some(true)
        )

        val json   = Json.toJson(model)
        val parsed = json.validate[OnshoreTaxYearLiabilities]

        parsed                                         shouldBe JsSuccess(model)
        (json \ "nonBusinessIncome").as[BigInt]        shouldBe BigInt(1000)
        (json \ "businessIncome").as[BigInt]           shouldBe BigInt(2000)
        (json \ "lettingIncome").as[BigInt]            shouldBe BigInt(3000)
        (json \ "gains").as[BigInt]                    shouldBe BigInt(4000)
        (json \ "unpaidTax").as[BigInt]                shouldBe BigInt(5000)
        (json \ "niContributions").as[BigInt]          shouldBe BigInt(6000)
        (json \ "interest").as[BigInt]                 shouldBe BigInt(7000)
        (json \ "penaltyRate").as[BigDecimal]          shouldBe BigDecimal(15.5)
        (json \ "penaltyRateReason").as[String]        shouldBe "Test reason"
        (json \ "undeclaredIncomeOrGain").as[String]   shouldBe "Test income source"
        (json \ "residentialTaxReduction").as[Boolean] shouldBe true
      }

      "it has only mandatory fields populated" in {
        val model = OnshoreTaxYearLiabilities(
          gains = None,
          unpaidTax = BigInt(5000),
          niContributions = BigInt(6000),
          interest = BigInt(7000),
          penaltyRate = BigDecimal(15.5),
          penaltyRateReason = "Test reason",
          residentialTaxReduction = None
        )

        val json   = Json.toJson(model)
        val parsed = json.validate[OnshoreTaxYearLiabilities]

        parsed                                      shouldBe JsSuccess(model)
        (json \ "nonBusinessIncome").toOption       shouldBe None
        (json \ "businessIncome").toOption          shouldBe None
        (json \ "lettingIncome").toOption           shouldBe None
        (json \ "gains").toOption                   shouldBe None
        (json \ "unpaidTax").as[BigInt]             shouldBe BigInt(5000)
        (json \ "niContributions").as[BigInt]       shouldBe BigInt(6000)
        (json \ "interest").as[BigInt]              shouldBe BigInt(7000)
        (json \ "penaltyRate").as[BigDecimal]       shouldBe BigDecimal(15.5)
        (json \ "penaltyRateReason").as[String]     shouldBe "Test reason"
        (json \ "undeclaredIncomeOrGain").toOption  shouldBe None
        (json \ "residentialTaxReduction").toOption shouldBe None
      }
    }
  }
}

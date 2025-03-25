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

class OnshoreTaxYearWithLiabilitiesSpec extends AnyWordSpec with Matchers {

  "OnshoreTaxYearWithLiabilities" should {
    "convert to XML correctly" in {
      val taxYearWithLiabilities = OnshoreTaxYearWithLiabilities(
        taxYear = OnshoreYearStarting(2020),
        taxYearLiabilities = OnshoreTaxYearLiabilities(
          nonBusinessIncome = Some(BigInt(10000)),
          businessIncome = Some(BigInt(20000)),
          lettingIncome = None,
          gains = Some(BigInt(5000)),
          unpaidTax = BigInt(3000),
          niContributions = BigInt(1500),
          interest = BigInt(200),
          penaltyRate = BigDecimal(15.5),
          penaltyRateReason = "Late submission",
          undeclaredIncomeOrGain = Some("Investments"),
          residentialTaxReduction = Some(true)
        )
      )

      val xml = taxYearWithLiabilities.toXml

      xml.headOption.map(_.label) shouldBe Some("onshoreTaxYearWithLiabilities")
      (xml \ "taxYear").text      shouldBe "2020"

      val liabilitiesXml = (xml \ "taxYearLiabilities").headOption.map(_.child).getOrElse(Nil)
      (liabilitiesXml \ "nonBusinessIncome").text       shouldBe "10000"
      (liabilitiesXml \ "businessIncome").text          shouldBe "20000"
      (liabilitiesXml \ "lettingIncome").text           shouldBe ""
      (liabilitiesXml \ "gains").text                   shouldBe "5000"
      (liabilitiesXml \ "unpaidTax").text               shouldBe "3000"
      (liabilitiesXml \ "niContributions").text         shouldBe "1500"
      (liabilitiesXml \ "interest").text                shouldBe "200"
      (liabilitiesXml \ "penaltyRate").text             shouldBe "15.5"
      (liabilitiesXml \ "penaltyRateReason").text       shouldBe "Late submission"
      (liabilitiesXml \ "undeclaredIncomeOrGain").text  shouldBe "Investments"
      (liabilitiesXml \ "residentialTaxReduction").text shouldBe "true"
    }

    "serialize and deserialize correctly" in {
      val taxYearWithLiabilities = OnshoreTaxYearWithLiabilities(
        taxYear = OnshoreYearStarting(2020),
        taxYearLiabilities = OnshoreTaxYearLiabilities(
          nonBusinessIncome = Some(BigInt(10000)),
          businessIncome = Some(BigInt(20000)),
          lettingIncome = None,
          gains = Some(BigInt(5000)),
          unpaidTax = BigInt(3000),
          niContributions = BigInt(1500),
          interest = BigInt(200),
          penaltyRate = BigDecimal(15.5),
          penaltyRateReason = "Late submission",
          undeclaredIncomeOrGain = Some("Investments"),
          residentialTaxReduction = Some(true)
        )
      )

      val json   = Json.toJson(taxYearWithLiabilities)
      val parsed = json.validate[OnshoreTaxYearWithLiabilities]

      parsed                                                                   shouldBe JsSuccess(taxYearWithLiabilities)
      (json \ "taxYear" \ "startYear").as[Int]                                 shouldBe 2020
      (json \ "taxYearLiabilities" \ "nonBusinessIncome").asOpt[BigInt]        shouldBe Some(BigInt(10000))
      (json \ "taxYearLiabilities" \ "businessIncome").asOpt[BigInt]           shouldBe Some(BigInt(20000))
      (json \ "taxYearLiabilities" \ "lettingIncome").asOpt[BigInt]            shouldBe None
      (json \ "taxYearLiabilities" \ "gains").asOpt[BigInt]                    shouldBe Some(BigInt(5000))
      (json \ "taxYearLiabilities" \ "unpaidTax").as[BigInt]                   shouldBe BigInt(3000)
      (json \ "taxYearLiabilities" \ "niContributions").as[BigInt]             shouldBe BigInt(1500)
      (json \ "taxYearLiabilities" \ "interest").as[BigInt]                    shouldBe BigInt(200)
      (json \ "taxYearLiabilities" \ "penaltyRate").as[BigDecimal]             shouldBe BigDecimal(15.5)
      (json \ "taxYearLiabilities" \ "penaltyRateReason").as[String]           shouldBe "Late submission"
      (json \ "taxYearLiabilities" \ "undeclaredIncomeOrGain").asOpt[String]   shouldBe Some("Investments")
      (json \ "taxYearLiabilities" \ "residentialTaxReduction").asOpt[Boolean] shouldBe Some(true)
    }
  }
}

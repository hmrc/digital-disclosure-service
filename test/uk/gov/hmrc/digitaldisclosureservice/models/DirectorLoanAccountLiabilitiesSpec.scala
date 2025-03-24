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

class DirectorLoanAccountLiabilitiesSpec extends AnyWordSpec with Matchers {

  "DirectorLoanAccountLiabilities" should {
    "convert to XML correctly" in {
      val periodEnd = LocalDate.of(2023, 4, 5)
      val directorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
        name = "abc",
        periodEnd = periodEnd,
        overdrawn = BigInt(1000),
        unpaidTax = BigInt(200),
        interest = BigInt(50),
        penaltyRate = BigDecimal(15.5),
        penaltyRateReason = "123"
      )

      val xml = directorLoanAccountLiabilities.toXml

      xml.headOption.map(_.label) shouldBe Some("directorLoanAccountLiabilities")
      (xml \ "name").text shouldBe "abc"
      (xml \ "periodEnd").text shouldBe "2023-04-05"
      (xml \ "overdrawn").text shouldBe "1000"
      (xml \ "unpaidTax").text shouldBe "200"
      (xml \ "interest").text shouldBe "50"
      (xml \ "penaltyRate").text shouldBe "15.5"
      (xml \ "penaltyRateReason").text shouldBe "123"
    }

    "serialize and deserialize correctly" in {
      val periodEnd = LocalDate.of(2023, 4, 5)
      val directorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
        name = "abc",
        periodEnd = periodEnd,
        overdrawn = BigInt(1000),
        unpaidTax = BigInt(200),
        interest = BigInt(50),
        penaltyRate = BigDecimal(15.5),
        penaltyRateReason = "123"
      )

      val json = Json.toJson(directorLoanAccountLiabilities)
      val parsed = json.validate[DirectorLoanAccountLiabilities]

      parsed shouldBe JsSuccess(directorLoanAccountLiabilities)
      (json \ "name").as[String] shouldBe "abc"
      (json \ "periodEnd").as[String] shouldBe "2023-04-05"
      (json \ "overdrawn").as[BigInt] shouldBe BigInt(1000)
      (json \ "unpaidTax").as[BigInt] shouldBe BigInt(200)
      (json \ "interest").as[BigInt] shouldBe BigInt(50)
      (json \ "penaltyRate").as[BigDecimal] shouldBe BigDecimal(15.5)
      (json \ "penaltyRateReason").as[String] shouldBe "123"
    }
  }
}
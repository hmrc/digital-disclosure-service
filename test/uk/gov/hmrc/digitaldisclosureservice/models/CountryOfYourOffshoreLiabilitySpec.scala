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

class CountryOfYourOffshoreLiabilitySpec extends AnyWordSpec with Matchers {

  "CountryOfYourOffshoreLiability" should {
    "convert to XML correctly" in {
      val countryLiability = CountryOfYourOffshoreLiability(
        alpha3 = "FRA",
        name = "France"
      )

      val xml = countryLiability.toXml

      xml.headOption.map(_.label) shouldBe Some("countryOfYourOffshoreLiability")
      (xml \ "alpha3").text       shouldBe "FRA"
      (xml \ "name").text         shouldBe "France"
    }

    "serialize and deserialize correctly" in {
      val countryLiability = CountryOfYourOffshoreLiability(
        alpha3 = "FRA",
        name = "France"
      )

      val json   = Json.toJson(countryLiability)
      val parsed = json.validate[CountryOfYourOffshoreLiability]

      parsed                       shouldBe JsSuccess(countryLiability)
      (json \ "alpha3").as[String] shouldBe "FRA"
      (json \ "name").as[String]   shouldBe "France"
    }
  }
}

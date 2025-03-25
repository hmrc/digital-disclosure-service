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

package models.address

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class AddressSpec extends AnyWordSpec with Matchers {

  "Address" should {
    "convert to XML correctly" in {
      val address = Address(
        line1 = "123 Main Street",
        line2 = Some("Apartment 4B"),
        line3 = None,
        line4 = Some("London"),
        postcode = Some("SW1A 1AA"),
        country = Country("UK")
      )

      val xml = address.toXml

      xml.headOption.map(_.label) shouldBe Some("address")
      (xml \ "line1").text        shouldBe "123 Main Street"
      (xml \ "line2").text        shouldBe "Apartment 4B"
      (xml \ "line3").text        shouldBe ""
      (xml \ "line4").text        shouldBe "London"
      (xml \ "postcode").text     shouldBe "SW1A 1AA"
      (xml \ "country").text      shouldBe "UK"
    }

    "serialize and deserialize correctly" in {
      val address = Address(
        line1 = "123 Main Street",
        line2 = Some("Apartment 4B"),
        line3 = None,
        line4 = Some("London"),
        postcode = Some("SW1A 1AA"),
        country = Country("UK")
      )

      val json   = Json.toJson(address)
      val parsed = json.validate[Address]

      parsed                                 shouldBe JsSuccess(address)
      (json \ "line1").as[String]            shouldBe "123 Main Street"
      (json \ "line2").asOpt[String]         shouldBe Some("Apartment 4B")
      (json \ "line3").asOpt[String]         shouldBe None
      (json \ "line4").asOpt[String]         shouldBe Some("London")
      (json \ "postcode").asOpt[String]      shouldBe Some("SW1A 1AA")
      (json \ "country" \ "code").as[String] shouldBe "UK"
    }
  }
}

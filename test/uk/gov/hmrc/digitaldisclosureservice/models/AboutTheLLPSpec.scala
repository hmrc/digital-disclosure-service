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

package models.notification

import models.address.{Address, Country}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class AboutTheLLPSpec extends AnyWordSpec with Matchers {

  "AboutTheLLP" should {
    "convert to XML correctly with minimal fields" in {
      val aboutTheLLP = AboutTheLLP()

      val xml = aboutTheLLP.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutTheLLP")
      (xml \ "name").isEmpty shouldBe true
      (xml \ "address").isEmpty shouldBe true
    }

    "serialize and deserialize correctly with minimal fields" in {
      val aboutTheLLP = AboutTheLLP()

      val json = Json.toJson(aboutTheLLP)
      val parsed = json.validate[AboutTheLLP]

      parsed shouldBe JsSuccess(aboutTheLLP)
    }

    "convert to XML correctly with all fields populated" in {
      val address = Address(
        line1 = "Test Line 1",
        line2 = Some("Test Line 2"),
        line3 = None,
        line4 = None,
        postcode = Some("TE1 1ST"),
        country = Country("GB")
      )

      val aboutTheLLP = AboutTheLLP(
        name = Some("Test LLP Name"),
        address = Some(address)
      )

      val xml = aboutTheLLP.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutTheLLP")
      (xml \ "name").text shouldBe "Test LLP Name"
      (xml \ "address").nonEmpty shouldBe true
    }

    "serialize and deserialize correctly with all fields populated" in {
      val address = Address(
        line1 = "Test Line 1",
        line2 = Some("Test Line 2"),
        line3 = None,
        line4 = None,
        postcode = Some("TE1 1ST"),
        country = Country("GB")
      )

      val aboutTheLLP = AboutTheLLP(
        name = Some("Test LLP Name"),
        address = Some(address)
      )

      val json = Json.toJson(aboutTheLLP)
      val parsed = json.validate[AboutTheLLP]

      parsed shouldBe JsSuccess(aboutTheLLP)
    }
  }
}
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

class AboutTheCompanySpec extends AnyWordSpec with Matchers {

  "AboutTheCompany" should {
    "convert to XML correctly with minimal fields" in {
      val aboutTheCompany = AboutTheCompany()

      val xml = aboutTheCompany.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutTheCompany")
      (xml \ "name").isEmpty shouldBe true
      (xml \ "registrationNumber").isEmpty shouldBe true
      (xml \ "address").isEmpty shouldBe true
    }

    "serialize and deserialize correctly with minimal fields" in {
      val aboutTheCompany = AboutTheCompany()

      val json = Json.toJson(aboutTheCompany)
      val parsed = json.validate[AboutTheCompany]

      parsed shouldBe JsSuccess(aboutTheCompany)
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

      val aboutTheCompany = AboutTheCompany(
        name = Some("Test Company Name"),
        registrationNumber = Some("12345678"),
        address = Some(address)
      )

      val xml = aboutTheCompany.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutTheCompany")
      (xml \ "name").text shouldBe "Test Company Name"
      (xml \ "registrationNumber").text shouldBe "12345678"
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

      val aboutTheCompany = AboutTheCompany(
        name = Some("Test Company Name"),
        registrationNumber = Some("12345678"),
        address = Some(address)
      )

      val json = Json.toJson(aboutTheCompany)
      val parsed = json.validate[AboutTheCompany]

      parsed shouldBe JsSuccess(aboutTheCompany)
    }
  }
}
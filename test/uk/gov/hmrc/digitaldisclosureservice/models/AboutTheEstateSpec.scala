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

import models.YesNoOrUnsure
import models.address.{Address, Country}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDate

class AboutTheEstateSpec extends AnyWordSpec with Matchers {

  "AboutTheEstate" should {
    "convert to XML correctly with minimal fields" in {
      val aboutTheEstate = AboutTheEstate()

      val xml = aboutTheEstate.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutTheEstate")
      (xml \ "fullName").isEmpty shouldBe true
      (xml \ "dateOfBirth").isEmpty shouldBe true
      (xml \ "mainOccupation").isEmpty shouldBe true
      (xml \ "doTheyHaveANino").isEmpty shouldBe true
      (xml \ "nino").isEmpty shouldBe true
      (xml \ "registeredForVAT").isEmpty shouldBe true
      (xml \ "vatRegNumber").isEmpty shouldBe true
      (xml \ "registeredForSA").isEmpty shouldBe true
      (xml \ "sautr").isEmpty shouldBe true
      (xml \ "address").isEmpty shouldBe true
    }

    "serialize and deserialize correctly with minimal fields" in {
      val aboutTheEstate = AboutTheEstate()

      val json = Json.toJson(aboutTheEstate)
      val parsed = json.validate[AboutTheEstate]

      parsed shouldBe JsSuccess(aboutTheEstate)
    }

    "convert to XML correctly with all fields populated" in {
      val date = LocalDate.of(1980, 1, 1)
      val address = Address(
        line1 = "Test Line 1",
        line2 = Some("Test Line 2"),
        line3 = None,
        line4 = None,
        postcode = Some("TE1 1ST"),
        country = Country("GB")
      )

      val aboutTheEstate = AboutTheEstate(
        fullName = Some("Test Name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Test Occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("AA123456A"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("123456789"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("1234567890"),
        address = Some(address)
      )

      val xml = aboutTheEstate.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutTheEstate")
      (xml \ "fullName").text shouldBe "Test Name"
      (xml \ "dateOfBirth").text shouldBe "1980-01-01"
      (xml \ "mainOccupation").text shouldBe "Test Occupation"
      (xml \ "doTheyHaveANino").text.toLowerCase shouldBe "yes"
      (xml \ "nino").text shouldBe "AA123456A"
      (xml \ "registeredForVAT").text.toLowerCase shouldBe "yes"
      (xml \ "vatRegNumber").text shouldBe "123456789"
      (xml \ "registeredForSA").text.toLowerCase shouldBe "yes"
      (xml \ "sautr").text shouldBe "1234567890"
      (xml \ "address").nonEmpty shouldBe true
    }

    "serialize and deserialize correctly with all fields populated" in {
      val date = LocalDate.of(1980, 1, 1)
      val address = Address(
        line1 = "Test Line 1",
        line2 = Some("Test Line 2"),
        line3 = None,
        line4 = None,
        postcode = Some("TE1 1ST"),
        country = Country("GB")
      )

      val aboutTheEstate = AboutTheEstate(
        fullName = Some("Test Name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Test Occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("AA123456A"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("123456789"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("1234567890"),
        address = Some(address)
      )

      val json = Json.toJson(aboutTheEstate)
      val parsed = json.validate[AboutTheEstate]

      parsed shouldBe JsSuccess(aboutTheEstate)
    }
  }
}
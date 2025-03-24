/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}
import models.YesNoOrUnsure
import models.address.{Address, Country}
import java.time.LocalDate

class AboutYouSpec extends AnyWordSpec with Matchers {

  private val mockAddress = Address(
    line1 = "Test Line 1",
    line2 = Some("Test Line 2"),
    line3 = None,
    line4 = Some("Test City"),
    postcode = Some("XX1 1XX"),
    country = Country("GB")
  )

  private val fullAboutYou = AboutYou(
    fullName = Some("Test Name"),
    telephoneNumber = Some("00000000000"),
    emailAddress = Some("test@example.com"),
    dateOfBirth = Some(LocalDate.of(2000, 1, 1)),
    mainOccupation = Some("Test Occupation"),
    doYouHaveANino = Some(YesNoOrUnsure.Yes),
    nino = Some("XX000000X"),
    registeredForVAT = Some(YesNoOrUnsure.Yes),
    vatRegNumber = Some("000000000"),
    registeredForSA = Some(YesNoOrUnsure.Yes),
    sautr = Some("0000000000"),
    address = Some(mockAddress)
  )

  private val emptyAboutYou = AboutYou()

  "AboutYou" should {
    "serialize to JSON correctly" in {
      val json = Json.toJson(fullAboutYou)

      (json \ "fullName").asOpt[String] shouldBe Some("Test Name")
      (json \ "telephoneNumber").asOpt[String] shouldBe Some("00000000000")
      (json \ "emailAddress").asOpt[String] shouldBe Some("test@example.com")
      (json \ "dateOfBirth").asOpt[String] shouldBe Some("2000-01-01")
      (json \ "mainOccupation").asOpt[String] shouldBe Some("Test Occupation")
      (json \ "doYouHaveANino").asOpt[String] shouldBe Some("Yes")
      (json \ "nino").asOpt[String] shouldBe Some("XX000000X")
      (json \ "registeredForVAT").asOpt[String] shouldBe Some("Yes")
      (json \ "vatRegNumber").asOpt[String] shouldBe Some("000000000")
      (json \ "registeredForSA").asOpt[String] shouldBe Some("Yes")
      (json \ "sautr").asOpt[String] shouldBe Some("0000000000")
      (json \ "address").isDefined shouldBe true
    }

    "deserialize from JSON correctly" in {
      val json = Json.parse(
        """
          |{
          |  "fullName": "Test Name",
          |  "telephoneNumber": "00000000000",
          |  "emailAddress": "test@example.com",
          |  "dateOfBirth": "2000-01-01",
          |  "mainOccupation": "Test Occupation",
          |  "doYouHaveANino": "Yes",
          |  "nino": "XX000000X",
          |  "registeredForVAT": "Yes",
          |  "vatRegNumber": "000000000",
          |  "registeredForSA": "Yes",
          |  "sautr": "0000000000",
          |  "address": {
          |    "line1": "Test Line 1",
          |    "line2": "Test Line 2",
          |    "line4": "Test City",
          |    "postcode": "XX1 1XX",
          |    "country": {
          |      "code": "GB"
          |    }
          |  }
          |}
        """.stripMargin)

      val result = Json.fromJson[AboutYou](json)
      result shouldBe a[JsSuccess[_]]

      val aboutYou = result.get
      aboutYou.fullName shouldBe Some("Test Name")
      aboutYou.telephoneNumber shouldBe Some("00000000000")
      aboutYou.emailAddress shouldBe Some("test@example.com")
      aboutYou.dateOfBirth shouldBe Some(LocalDate.of(2000, 1, 1))
      aboutYou.mainOccupation shouldBe Some("Test Occupation")
    }

    "convert to XML correctly with all fields populated" in {
      val xml = fullAboutYou.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutYou")
      (xml \ "fullName").text shouldBe "Test Name"
      (xml \ "telephoneNumber").text shouldBe "00000000000"
      (xml \ "emailAddress").text shouldBe "test@example.com"
      (xml \ "dateOfBirth").text shouldBe "2000-01-01"
      (xml \ "mainOccupation").text shouldBe "Test Occupation"
      (xml \ "doYouHaveANino").nonEmpty shouldBe true
      (xml \ "nino").text shouldBe "XX000000X"
      (xml \ "registeredForVAT").nonEmpty shouldBe true
      (xml \ "vatRegNumber").text shouldBe "000000000"
      (xml \ "registeredForSA").nonEmpty shouldBe true
      (xml \ "sautr").text shouldBe "0000000000"
      (xml \ "address").nonEmpty shouldBe true
    }

    "convert to XML correctly with no fields populated" in {
      val xml = emptyAboutYou.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutYou")
      (xml \ "fullName").isEmpty shouldBe true
      (xml \ "telephoneNumber").isEmpty shouldBe true
      (xml \ "emailAddress").isEmpty shouldBe true
      (xml \ "dateOfBirth").isEmpty shouldBe true
      (xml \ "mainOccupation").isEmpty shouldBe true
      (xml \ "doYouHaveANino").isEmpty shouldBe true
      (xml \ "nino").isEmpty shouldBe true
      (xml \ "registeredForVAT").isEmpty shouldBe true
      (xml \ "vatRegNumber").isEmpty shouldBe true
      (xml \ "registeredForSA").isEmpty shouldBe true
      (xml \ "sautr").isEmpty shouldBe true
      (xml \ "address").isEmpty shouldBe true
    }

    "convert to XML correctly with partial fields populated" in {
      val partialAboutYou = AboutYou(
        fullName = Some("Test Name"),
        emailAddress = Some("test@example.com"),
        doYouHaveANino = Some(YesNoOrUnsure.No),
        registeredForVAT = Some(YesNoOrUnsure.Unsure)
      )

      val xml = partialAboutYou.toXml

      xml.headOption.map(_.label) shouldBe Some("aboutYou")
      (xml \ "fullName").text shouldBe "Test Name"
      (xml \ "telephoneNumber").isEmpty shouldBe true
      (xml \ "emailAddress").text shouldBe "test@example.com"
      (xml \ "dateOfBirth").isEmpty shouldBe true
      (xml \ "mainOccupation").isEmpty shouldBe true
      (xml \ "doYouHaveANino").nonEmpty shouldBe true
      (xml \ "nino").isEmpty shouldBe true
      (xml \ "registeredForVAT").nonEmpty shouldBe true
      (xml \ "vatRegNumber").isEmpty shouldBe true
      (xml \ "registeredForSA").isEmpty shouldBe true
      (xml \ "sautr").isEmpty shouldBe true
      (xml \ "address").isEmpty shouldBe true
    }
  }
}
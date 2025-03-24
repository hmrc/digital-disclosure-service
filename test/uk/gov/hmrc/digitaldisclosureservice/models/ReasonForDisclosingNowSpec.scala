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

package models.disclosure

import models._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class ReasonForDisclosingNowSpec extends AnyWordSpec with Matchers {

  "ReasonForDisclosingNow" should {
    "convert to XML correctly with minimal fields" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow()

      val xml = reasonForDisclosingNow.toXml

      xml.headOption.map(_.label)                shouldBe Some("reasonForDisclosingNow")
      (xml \ "reason").isEmpty                   shouldBe true
      (xml \ "otherReason").isEmpty              shouldBe true
      (xml \ "whyNotBeforeNow").isEmpty          shouldBe true
      (xml \ "receivedAdvice").isEmpty           shouldBe true
      (xml \ "personWhoGaveAdvice").isEmpty      shouldBe true
      (xml \ "adviceOnBehalfOfBusiness").isEmpty shouldBe true
      (xml \ "adviceBusinessName").isEmpty       shouldBe true
      (xml \ "personProfession").isEmpty         shouldBe true
      (xml \ "adviceGiven").isEmpty              shouldBe true
      (xml \ "whichEmail").isEmpty               shouldBe true
      (xml \ "whichPhone").isEmpty               shouldBe true
      (xml \ "email").isEmpty                    shouldBe true
      (xml \ "telephone").isEmpty                shouldBe true
    }

    "serialize and deserialize correctly with minimal fields" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow()

      val json   = Json.toJson(reasonForDisclosingNow)
      val parsed = json.validate[ReasonForDisclosingNow]

      parsed shouldBe JsSuccess(reasonForDisclosingNow)
    }

    "convert to XML correctly with some fields populated" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow(
        reason = Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance, WhyAreYouMakingADisclosure.News)),
        otherReason = Some("test other reason"),
        whyNotBeforeNow = Some("test why not before now"),
        receivedAdvice = Some(true),
        personWhoGaveAdvice = Some("test person"),
        adviceOnBehalfOfBusiness = Some(false),
        adviceBusinessName = Some("test business name"),
        personProfession = Some("test profession"),
        email = Some("test@example.com"),
        telephone = Some("01234567890")
      )

      val xml = reasonForDisclosingNow.toXml

      xml.headOption.map(_.label)             shouldBe Some("reasonForDisclosingNow")
      (xml \ "reason").nonEmpty               shouldBe true
      (xml \ "otherReason").text              shouldBe "test other reason"
      (xml \ "whyNotBeforeNow").text          shouldBe "test why not before now"
      (xml \ "receivedAdvice").text           shouldBe "true"
      (xml \ "personWhoGaveAdvice").text      shouldBe "test person"
      (xml \ "adviceOnBehalfOfBusiness").text shouldBe "false"
      (xml \ "adviceBusinessName").text       shouldBe "test business name"
      (xml \ "personProfession").text         shouldBe "test profession"
      (xml \ "email").text                    shouldBe "test@example.com"
      (xml \ "telephone").text                shouldBe "01234567890"
    }

    "serialize and deserialize correctly with some fields populated" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow(
        reason = Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance, WhyAreYouMakingADisclosure.News)),
        otherReason = Some("test other reason"),
        whyNotBeforeNow = Some("test why not before now"),
        receivedAdvice = Some(true),
        personWhoGaveAdvice = Some("test person"),
        adviceOnBehalfOfBusiness = Some(false),
        adviceBusinessName = Some("test business name"),
        personProfession = Some("test profession"),
        email = Some("test@example.com"),
        telephone = Some("01234567890")
      )

      val json   = Json.toJson(reasonForDisclosingNow)
      val parsed = json.validate[ReasonForDisclosingNow]

      parsed shouldBe JsSuccess(reasonForDisclosingNow)
    }

    "convert to XML correctly with all fields populated" in {
      val monthYear         = MonthYear(1, 2022)
      val contactPreference = AdviceContactPreference.Email
      val adviceGiven       = AdviceGiven(
        adviceGiven = "test advice",
        monthYear = monthYear,
        contactPreference = contactPreference
      )

      val reasonForDisclosingNow = ReasonForDisclosingNow(
        reason = Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance, WhyAreYouMakingADisclosure.News)),
        otherReason = Some("test other reason"),
        whyNotBeforeNow = Some("test why not before now"),
        receivedAdvice = Some(true),
        personWhoGaveAdvice = Some("test person"),
        adviceOnBehalfOfBusiness = Some(false),
        adviceBusinessName = Some("test business name"),
        personProfession = Some("test profession"),
        adviceGiven = Some(adviceGiven),
        whichEmail = Some(WhichEmailAddressCanWeContactYouWith.DifferentEmail),
        whichPhone = Some(WhichTelephoneNumberCanWeContactYouWith.DifferentNumber),
        email = Some("test@example.com"),
        telephone = Some("01234567890")
      )

      val xml = reasonForDisclosingNow.toXml

      xml.headOption.map(_.label)             shouldBe Some("reasonForDisclosingNow")
      (xml \ "reason").nonEmpty               shouldBe true
      (xml \ "otherReason").text              shouldBe "test other reason"
      (xml \ "whyNotBeforeNow").text          shouldBe "test why not before now"
      (xml \ "receivedAdvice").text           shouldBe "true"
      (xml \ "personWhoGaveAdvice").text      shouldBe "test person"
      (xml \ "adviceOnBehalfOfBusiness").text shouldBe "false"
      (xml \ "adviceBusinessName").text       shouldBe "test business name"
      (xml \ "personProfession").text         shouldBe "test profession"
      (xml \ "adviceGiven").nonEmpty          shouldBe true
      (xml \ "whichEmail").text               shouldBe "differentEmail"
      (xml \ "whichPhone").text               shouldBe "differentNumber"
      (xml \ "email").text                    shouldBe "test@example.com"
      (xml \ "telephone").text                shouldBe "01234567890"
    }

    "serialize and deserialize correctly with all fields populated" in {
      val monthYear         = MonthYear(1, 2022)
      val contactPreference = AdviceContactPreference.Email
      val adviceGiven       = AdviceGiven(
        adviceGiven = "test advice",
        monthYear = monthYear,
        contactPreference = contactPreference
      )

      val reasonForDisclosingNow = ReasonForDisclosingNow(
        reason = Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance, WhyAreYouMakingADisclosure.News)),
        otherReason = Some("test other reason"),
        whyNotBeforeNow = Some("test why not before now"),
        receivedAdvice = Some(true),
        personWhoGaveAdvice = Some("test person"),
        adviceOnBehalfOfBusiness = Some(false),
        adviceBusinessName = Some("test business name"),
        personProfession = Some("test profession"),
        adviceGiven = Some(adviceGiven),
        whichEmail = Some(WhichEmailAddressCanWeContactYouWith.DifferentEmail),
        whichPhone = Some(WhichTelephoneNumberCanWeContactYouWith.DifferentNumber),
        email = Some("test@example.com"),
        telephone = Some("01234567890")
      )

      val json   = Json.toJson(reasonForDisclosingNow)
      val parsed = json.validate[ReasonForDisclosingNow]

      parsed shouldBe JsSuccess(reasonForDisclosingNow)
    }
  }
}

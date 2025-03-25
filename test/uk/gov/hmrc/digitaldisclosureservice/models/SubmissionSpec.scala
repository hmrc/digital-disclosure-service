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
import java.time.{Instant, LocalDateTime}
import models.disclosure._
import models.notification._

class SubmissionSpec extends AnyWordSpec with Matchers {

  "Submission" should {

    val testInstant         = Instant.parse("2024-03-24T12:00:00Z")
    val testMetadata        = Metadata(Some("REF123"), Some(LocalDateTime.now()))
    val testPersonalDetails = PersonalDetails(Background(), AboutYou())

    "generate XML correctly for Notification" in {
      val notification = Notification(
        userId = "user123",
        submissionId = "sub456",
        lastUpdated = testInstant,
        metadata = testMetadata,
        personalDetails = testPersonalDetails,
        customerId = None
      )

      val xml = notification.toXml

      xml should include("<notification>")
      xml should include("<userId>user123</userId>")
      xml should include("<submissionId>sub456</submissionId>")
      xml should include(s"<lastUpdated>${testInstant.toString}</lastUpdated>")
      xml should include("<metadata>")
      xml should include("<reference>REF123</reference>")
      xml should include("<personalDetails>")
      xml should not include "<customerId>"
      xml should include("</notification>")
    }

    "include customerId in Notification XML when present" in {
      val notification = Notification(
        userId = "user123",
        submissionId = "sub456",
        lastUpdated = testInstant,
        metadata = testMetadata,
        personalDetails = testPersonalDetails,
        customerId = Some(NINO("AB123456C"))
      )

      val xml = notification.toXml

      xml should include("<nino>AB123456C</nino>")
    }

    "generate XML correctly for FullDisclosure" in {
      val disclosure = FullDisclosure(
        userId = "user123",
        submissionId = "sub789",
        lastUpdated = testInstant,
        metadata = testMetadata,
        caseReference = CaseReference(Some(true), Some("CASE123")),
        personalDetails = testPersonalDetails,
        onshoreLiabilities = None,
        offshoreLiabilities = OffshoreLiabilities(),
        otherLiabilities = OtherLiabilities(),
        reasonForDisclosingNow = ReasonForDisclosingNow(),
        customerId = None,
        offerAmount = None
      )

      val xml = disclosure.toXml

      xml should include("<fullDisclosure>")
      xml should include("<userId>user123</userId>")
      xml should include("<submissionId>sub789</submissionId>")
      xml should include(s"<lastUpdated>${testInstant.toString}</lastUpdated>")
      xml should include("<metadata>")
      xml should include("<reference>REF123</reference>")
      xml should include("<caseReference>")
      xml should include("<doYouHaveACaseReference>true</doYouHaveACaseReference>")
      xml should include("<whatIsTheCaseReference>CASE123</whatIsTheCaseReference>")
      xml should include("<personalDetails>")
      xml should include("</fullDisclosure>")
    }

    "include optional fields in FullDisclosure XML when present" in {
      val disclosure = FullDisclosure(
        userId = "user123",
        submissionId = "sub789",
        lastUpdated = testInstant,
        metadata = testMetadata,
        caseReference = CaseReference(Some(true), Some("CASE123")),
        personalDetails = testPersonalDetails,
        onshoreLiabilities = Some(OnshoreLiabilities()),
        offshoreLiabilities = OffshoreLiabilities(),
        otherLiabilities = OtherLiabilities(),
        reasonForDisclosingNow = ReasonForDisclosingNow(),
        customerId = Some(SAUTR("1234567890")),
        offerAmount = Some(BigInt(5000))
      )

      val xml = disclosure.toXml

      xml should include("<sautr>1234567890</sautr>")
      xml should include("<offerAmount>5000</offerAmount>")
    }

    "handle different CustomerId types correctly" in {
      val customerIds = List(
        NINO("AB123456C"),
        CAUTR("1234567890"),
        SAUTR("0987654321"),
        ARN("ARNX12345"),
        ExternalId("EXT-123-456")
      )

      customerIds.foreach { id =>
        val notification = Notification(
          userId = "user123",
          submissionId = "sub456",
          lastUpdated = testInstant,
          metadata = testMetadata,
          personalDetails = testPersonalDetails,
          customerId = Some(id)
        )

        val xml = notification.toXml

        id match {
          case NINO(value)       => xml should include(s"<nino>$value</nino>")
          case CAUTR(value)      => xml should include(s"<cautr>$value</cautr>")
          case SAUTR(value)      => xml should include(s"<sautr>$value</sautr>")
          case ARN(value)        => xml should include(s"<arn>$value</arn>")
          case ExternalId(value) => xml should include(s"<externalId>$value</externalId>")
        }
      }
    }

    "provide access to disclosingAboutThemselves property" in {
      val notification = Notification(
        userId = "user123",
        submissionId = "sub456",
        lastUpdated = testInstant,
        metadata = testMetadata,
        personalDetails = testPersonalDetails,
        customerId = None
      )

      notification.disclosingAboutThemselves shouldBe testPersonalDetails.disclosingAboutThemselves
    }
  }
}

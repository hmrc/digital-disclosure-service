/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.OptionValues
import play.api.i18n.Messages
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import models._

trait NotificationPdfServiceHelper extends AnyWordSpecLike
  with Matchers
  with OptionValues {

  implicit val messages: Messages 

  def baseNotificationTests(parsedText: String) = {
    parsedText should include("Digital Disclosure Service")
    parsedText should include("Notification of intent")

    parsedText should include("Your submission details")
    parsedText should include("Your reference number")
    parsedText should include("D61-ABCDE-ABC")
    parsedText should include("Your submission was sent on")
    parsedText should include("Monday 24th October 2022")

    parsedText should include(messages("notification.heading.background"))
    parsedText should include(messages("notification.background.haveYouReceivedALetter"))
    parsedText should include(messages("notification.background.offshoreLiabilities"))
    parsedText should include(messages("notification.background.onshoreLiabilities"))
    parsedText should include(messages("notification.background.disclosureEntity"))

    parsedText should include(messages("notification.heading.aboutYou"))

    parsedText should include(messages("notification.aboutYou.fullName"))
    parsedText should include(messages("notification.aboutYou.telephoneNumber"))
    parsedText should include(messages("notification.aboutYou.doYouHaveAEmailAddress"))
    parsedText should include(messages("notification.aboutYou.address"))

  }

  def testFullAboutYouQuestions(parsedText: String) = {
    parsedText should include(messages("notification.aboutYou.dateOfBirth"))
    parsedText should include(messages("notification.aboutYou.mainOccupation"))
    parsedText should include(messages("notification.aboutYou.doYouHaveANino"))
    parsedText should include(messages("notification.aboutYou.registeredForVAT"))
    parsedText should include(messages("notification.aboutYou.registeredForSA"))
  }

  def testNotFullAboutYouQuestions(parsedText: String) = {
    parsedText should not include(messages("notification.aboutYou.dateOfBirth"))
    parsedText should not include(messages("notification.aboutYou.mainOccupation"))
    parsedText should not include(messages("notification.aboutYou.doYouHaveANino"))
    parsedText should not include(messages("notification.aboutYou.registeredForVAT"))
    parsedText should not include(messages("notification.aboutYou.registeredForSA"))
  }

  def testIndividualQuestions(parsedText: String) = {
    parsedText should include(messages("notification.heading.aboutTheIndividual"))

    parsedText should include(messages("notification.aboutTheIndividual.fullName"))
    parsedText should include(messages("notification.aboutTheIndividual.dateOfBirth"))
    parsedText should include(messages("notification.aboutTheIndividual.mainOccupation"))
    parsedText should include(messages("notification.aboutTheIndividual.doYouHaveANino"))
    parsedText should include(messages("notification.aboutTheIndividual.registeredForVAT"))
    parsedText should include(messages("notification.aboutTheIndividual.registeredForSA"))
    parsedText should include(messages("notification.aboutTheIndividual.address"))
  }

  def testNotIndividualQuestions(parsedText: String) = {
    parsedText should not include(messages("notification.heading.aboutTheIndividual"))

    parsedText should not include(messages("notification.aboutTheIndividual.fullName"))
    parsedText should not include(messages("notification.aboutTheIndividual.dateOfBirth"))
    parsedText should not include(messages("notification.aboutTheIndividual.mainOccupation"))
    parsedText should not include(messages("notification.aboutTheIndividual.doYouHaveANino"))
    parsedText should not include(messages("notification.aboutTheIndividual.registeredForVAT"))
    parsedText should not include(messages("notification.aboutTheIndividual.registeredForSA"))
    parsedText should not include(messages("notification.aboutTheIndividual.address"))
  }

  def testCompanyQuestions(parsedText: String) = {
    parsedText should include(messages("notification.heading.aboutTheCompany"))

    parsedText should include(messages("notification.aboutTheCompany.name"))
    parsedText should include(messages("notification.aboutTheCompany.registrationNumber"))
    parsedText should include(messages("notification.aboutTheCompany.address"))
  }

  def testNotCompanyQuestions(parsedText: String) = {
    parsedText should not include(messages("notification.heading.aboutTheCompany"))

    parsedText should not include(messages("notification.aboutTheCompany.name"))
    parsedText should not include(messages("notification.aboutTheCompany.registrationNumber"))
    parsedText should not include(messages("notification.aboutTheCompany.address"))
  }

  def testTrustQuestions(parsedText: String) = {
    parsedText should include(messages("notification.heading.aboutTheTrust"))

    parsedText should include(messages("notification.aboutTheTrust.name"))
    parsedText should include(messages("notification.aboutTheTrust.address"))
  }

  def testNotTrustQuestions(parsedText: String) = {
    parsedText should not include(messages("notification.heading.aboutTheTrust"))

    parsedText should not include(messages("notification.aboutTheTrust.name"))
    parsedText should not include(messages("notification.aboutTheTrust.address"))
  }

  def testLLPQuestions(parsedText: String) = {
    parsedText should include(messages("notification.heading.aboutTheLLP"))

    parsedText should include(messages("notification.aboutTheLLP.name"))
    parsedText should include(messages("notification.aboutTheLLP.address"))
  }

  def testNotLLPQuestions(parsedText: String) = {
    parsedText should not include(messages("notification.heading.aboutTheLLP"))

    parsedText should not include(messages("notification.aboutTheLLP.name"))
    parsedText should not include(messages("notification.aboutTheLLP.address"))
  }

  def stripPDFToString(notification: Notification): String = {
    val testPdfGenerator = new NotificationPdfService
    val pdfStripper = new PDFTextStripper()
    val pdDoc = PDDocument.load(testPdfGenerator.createPdf(notification).toByteArray)
    pdfStripper.getText(pdDoc)
  }

}
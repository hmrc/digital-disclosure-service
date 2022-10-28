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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import models._
import uk.gov.hmrc.digitaldisclosureservice.views.html.NotificationView
import java.nio.file.{Files, Paths}

import java.util.Date

class OneOffTestPdf extends AnyWordSpecLike
  with Matchers
  with OptionValues
  with GuiceOneAppPerSuite
  with NotificationPdfServiceHelper {


  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  val testPdfService = app.injector.instanceOf[NotificationPdfService]

  val background = Background (
    haveYouReceivedALetter = Some(false),
    letterReferenceNumber = None,
    disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
    offshoreLiabilities = Some(false),
    onshoreLiabilities = Some(true)
  )
  val aboutYou = AboutYou(
    fullName = Some("Some name"),
    telephoneNumber = Some("+44 012345678"),
    doYouHaveAEmailAddress = Some(false),
    address = Some("Some address")
  )
  val aboutTheCompany = AboutTheCompany(
    name = Some("Some company name"),
    registrationNumber = Some("Some company registration number"),
    address = Some("Some company address")
  )
  val notification = Notification(Metadata(), background, aboutYou, aboutTheCompany = Some(aboutTheCompany))

  "PdfGenerationService" should {
    "generate a PDF" in {
      val result = testPdfService.createPdf(notification).toByteArray
      Files.write(Paths.get("submission2.pdf"), result)
    }
  }
  
}
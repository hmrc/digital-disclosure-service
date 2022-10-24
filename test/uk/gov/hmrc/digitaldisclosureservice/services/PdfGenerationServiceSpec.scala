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
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n
import play.api.i18n.{MessagesApi, MessagesImpl}
import play.api.mvc.{AnyContent, DefaultActionBuilder, DefaultMessagesControllerComponents, MessagesControllerComponents}
import play.api.test.Helpers.stubBodyParser
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.nio.file.{Paths, Files}

import java.io.File

import scala.concurrent.ExecutionContext

class PdfGenerationServiceSpec extends AnyWordSpecLike
  with Matchers
  with OptionValues
  with GuiceOneAppPerSuite {

  val testPdfGenerator = new PdfGenerationService
  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  "ErsReceiptPdfBuilderService" should {
    "generate the summary metadata" in {
      val pdfStripper = new PDFTextStripper()
      val pdDoc = PDDocument.load(testPdfGenerator.createPdf.toByteArray)
      val parsedText = pdfStripper.getText(pdDoc)

      parsedText should include("Digital Disclosure Service")
      parsedText should include("Notification of intent")

      parsedText should include("Your submission details")
      parsedText should include("Your reference number")
      parsedText should include("D61-ABCDE-ABC")
      parsedText should include("Your submission was sent on")
      parsedText should include("Monday 24th October 2022")

      parsedText should include("Background")
      parsedText should include("Are you making this disclosure because you have received a letter from HMRC?")
      parsedText should include("No")
      parsedText should include("What is the disclosure about?")
      parsedText should include("An individual")
      parsedText should include("Are you the individual this disclosure is about?")
      parsedText should include("Yes, I am the individual")
      parsedText should include("Do you want to disclose offshore liabilities?")
      parsedText should include("Yes")
      parsedText should include("Do you also want to disclose onshore liabilities?")
      parsedText should include("No")


    }
  }
}
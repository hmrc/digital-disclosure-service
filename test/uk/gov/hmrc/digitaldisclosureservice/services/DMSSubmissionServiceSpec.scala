/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future
import org.scalamock.handlers.{CallHandler1, CallHandler2}
import java.time.{Instant, LocalDateTime}
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import models.submission._
import models.notification._
import connectors.DMSSubmissionConnector
import services.SubmissionPdfService
import utils.MarkCalculator
import java.io.ByteArrayOutputStream
import scala.concurrent.ExecutionContext.Implicits.global
import models.PDF
import models.{Metadata, Notification, NINO}

class DMSSubmissionServiceSpec extends AnyWordSpec with Matchers 
    with MockFactory with ScalaFutures {

  val mockDmsConnector = mock[DMSSubmissionConnector]
  val mockPdfService = mock[SubmissionPdfService]
  val mockMarkCalculator = mock[MarkCalculator]

  val app = new GuiceApplicationBuilder().overrides(
    bind[DMSSubmissionConnector].toInstance(mockDmsConnector),
    bind[SubmissionPdfService].toInstance(mockPdfService),
    bind[MarkCalculator].toInstance(mockMarkCalculator)
  ).configure("create-internal-auth-token-on-start" -> false).build()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  val sut = new DMSSubmissionServiceImpl(mockDmsConnector, mockPdfService, mockMarkCalculator)

  def mockCreatePdf(notification: Notification)(
    response: PDF
  ): CallHandler2[Notification, Messages, PDF] =
    (mockPdfService
      .createPdf(_: Notification)(_: Messages))
      .expects(notification, *)
      .returning(response)

  def mockGetSfMark(xml: String)
  (response: String): CallHandler1[String, String] =
    (mockMarkCalculator
      .getSfMark(_: String))
      .expects(xml)
      .returning(response)

  def mockSubmit(submissionRequest: SubmissionRequest)(
    response: Future[SubmissionResponse]
  ): CallHandler2[SubmissionRequest, Array[Byte], Future[SubmissionResponse]] =
    (mockDmsConnector
      .submit(_: SubmissionRequest, _: Array[Byte]))
      .expects(submissionRequest, *)
      .returning(response)

  def mockSubmitAny(response: Future[SubmissionResponse]): CallHandler2[SubmissionRequest, Array[Byte], Future[SubmissionResponse]] =
    (mockDmsConnector
      .submit(_: SubmissionRequest, _: Array[Byte]))
      .expects(*, *)
      .returning(response)


  "submit" should {
    "create the pdf, generate a Mark, populate a request and send it to the connector" in {
      val stream = new ByteArrayOutputStream()
      val submissionTime = LocalDateTime.now()
      val notification = Notification(  
        userId = "userId",
        submissionId = "submissionId",
        lastUpdated = Instant.now,
        metadata = Metadata(reference = Some("1234-5678-ABCD"), submissionTime = Some(submissionTime)),
        personalDetails = PersonalDetails(
          background = Background(),
          aboutYou = AboutYou()
        ),
        customerId = Some(NINO("customerId123")))
      val submissionMark = "mark"

      val submissionMetadata = SubmissionMetadata(
        timeOfReceipt = submissionTime,
        customerId = "customerId123",
        submissionMark = submissionMark
      )
      val submissionRequest = SubmissionRequest(
        submissionReference = Some("12345678ABCD"),
        metadata = submissionMetadata
      )

      mockCreatePdf(notification)(PDF(stream))
      mockGetSfMark(notification.toXml)(submissionMark)
      mockSubmit(submissionRequest)(Future.successful(SubmissionResponse.Success("123")))

      val result = sut.submit(notification).futureValue
      result shouldEqual SubmissionResponse.Success("123")
    }

    "succeed when the time isnt supplied" in {
      val stream = new ByteArrayOutputStream()
      val notification = Notification(  
        userId = "userId",
        submissionId = "submissionId",
        lastUpdated = Instant.now,
        metadata = Metadata(submissionTime = None),
        personalDetails = PersonalDetails(
          background = Background(),
          aboutYou = AboutYou()
        ),
        customerId = None)
      val submissionMark = "mark"

      mockCreatePdf(notification)(PDF(stream))
      mockGetSfMark(notification.toXml)(submissionMark)
      mockSubmitAny(Future.successful(SubmissionResponse.Success("123")))

      val result = sut.submit(notification).futureValue
      result shouldEqual SubmissionResponse.Success("123")
    }

  }

}
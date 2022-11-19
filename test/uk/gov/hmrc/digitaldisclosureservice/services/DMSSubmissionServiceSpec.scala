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
import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.{Future, ExecutionContext}
import org.scalamock.handlers.{CallHandler1, CallHandler2, CallHandler3}
import java.time.{Instant, LocalDateTime}
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import models.submission._
import models.notification._
import connectors.DMSSubmissionConnector
import services.NotificationPdfService
import utils.MarkCalculator
import java.io.ByteArrayOutputStream
import scala.concurrent.ExecutionContext.Implicits.global
import models.PDF

class DMSSubmissionServiceSpec extends AnyWordSpec with Matchers 
    with MockFactory with ScalaFutures {

  val mockDmsConnector = mock[DMSSubmissionConnector]
  val mockPdfService = mock[NotificationPdfService]
  val mockMarkCalculator = mock[MarkCalculator]

  val app = new GuiceApplicationBuilder().overrides(
    bind[DMSSubmissionConnector].toInstance(mockDmsConnector),
    bind[NotificationPdfService].toInstance(mockPdfService),
    bind[MarkCalculator].toInstance(mockMarkCalculator)
  ).build()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  val sut = new DMSSubmissionServiceImpl(mockDmsConnector, mockPdfService, mockMarkCalculator)

  def mockCreatePdf(notification: Notification)(
    response: Future[PDF]
  ): CallHandler3[Notification, Messages, ExecutionContext, Future[PDF]] =
    (mockPdfService
      .createPdf(_: Notification)(_: Messages, _: ExecutionContext))
      .expects(notification, *, *)
      .returning(response)

  def mockGetSfMark(response: String): CallHandler1[Array[Byte], String] =
    (mockMarkCalculator
      .getSfMark(_: Array[Byte]))
      .expects(*)
      .returning(response)

  def mockSubmit(submissionRequest: SubmissionRequest)(
    response: Future[SubmissionResponse]
  ): CallHandler2[SubmissionRequest, Array[Byte], Future[SubmissionResponse]] =
    (mockDmsConnector
      .submit(_: SubmissionRequest, _: Array[Byte]))
      .expects(submissionRequest, *)
      .returning(response)

  "submitNotification" should {
    "create the pdf, generate a Mark, populate a request and send it to the connector" in {
      val stream = new ByteArrayOutputStream()
      val submissionTime = LocalDateTime.now()
      val notification = Notification(  
        userId = "userId",
        notificationId = "notificationId",
        lastUpdated = Instant.now,
        metadata = Metadata(submissionTime = Some(submissionTime)),
        background = Background(),
        aboutYou = AboutYou(),
        customerId = "customerId123")
      val submissionMark = "mark"

      val submissionMetadata = SubmissionMetadata(
        timeOfReceipt = submissionTime,
        numberOfPages = 3,
        customerId = "customerId123",
        submissionMark = submissionMark
      )
      val submissionRequest = SubmissionRequest(
        id = None,
        metadata = submissionMetadata
      )

      mockCreatePdf(notification)(Future.successful(PDF(stream, 3)))
      mockGetSfMark(submissionMark)
      mockSubmit(submissionRequest)(Future.successful(SubmissionResponse.Success("123")))

      val result = sut.submitNotification(notification).futureValue
      result shouldEqual SubmissionResponse.Success("123")
    }
  }

}
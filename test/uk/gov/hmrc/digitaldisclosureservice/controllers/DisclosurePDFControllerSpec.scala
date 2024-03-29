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

package controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.test.{DefaultAwaitTimeout, FakeHeaders, FakeRequest, Helpers}
import java.time.{LocalDateTime, ZoneOffset}
import play.api.libs.json.Json
import models.{FullDisclosure, Metadata}
import models.disclosure._
import models.notification._
import services.SubmissionPdfService
import utils.BaseSpec
import org.apache.pekko.util.ByteString
import org.apache.pekko.actor.ActorSystem
import play.api.i18n.{Messages, MessagesApi}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{any, refEq}
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers._
import play.api.i18n.DefaultMessagesApi
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import scala.concurrent.Future

class DisclosurePDFControllerSpec
    extends AnyWordSpec
    with Matchers
    with BaseSpec
    with DefaultAwaitTimeout
    with MockitoSugar {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  implicit val actorSystem        = ActorSystem()
  implicit val cc                 = Helpers.stubControllerComponents()

  val pdfService        = app.injector.instanceOf[SubmissionPdfService]
  val mockPdfService    = mock[SubmissionPdfService]
  val mockStubBehaviour = mock[StubBehaviour]
  val expectedPredicate = Predicate.Permission(
    Resource(ResourceType("digital-disclosure-service"), ResourceLocation("pdf")),
    IAAction("WRITE")
  )
  when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.unit)

  private val controller = new DisclosurePDFController(
    new DefaultMessagesApi(),
    mockPdfService,
    BackendAuthComponentsStub(mockStubBehaviour),
    Helpers.stubControllerComponents()
  )

  val instant        = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
  val testDisclosure = FullDisclosure(
    "123",
    "123",
    instant,
    Metadata(),
    CaseReference(),
    PersonalDetails(Background(), AboutYou()),
    None,
    OffshoreLiabilities(),
    OtherLiabilities(),
    ReasonForDisclosingNow()
  )

  "POST /disclosure/submit" should {
    "return 200 where the service returns a Success" in {
      val pdf = pdfService.generatePdfHtml(testDisclosure, false, "en")
      when(mockPdfService.generatePdfHtml(refEq(testDisclosure), any(), any())(any())) thenReturn pdf

      val fakeRequest = FakeRequest(
        method = "GET",
        uri = "/disclosure",
        headers = FakeHeaders(Seq("Authorization" -> "Token some-token")),
        body = Json.toJson(testDisclosure)
      )
      val result      = controller.generate()(fakeRequest)
      status(result)            shouldBe Status.OK
      contentAsBytes(result) shouldEqual ByteString(pdf.getBytes)
    }
  }

}

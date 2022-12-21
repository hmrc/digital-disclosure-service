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

package controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.test.{FakeRequest, Helpers, FakeHeaders, DefaultAwaitTimeout}
import java.time.{LocalDateTime, ZoneOffset}
import play.api.libs.json.Json
import models.notification._
import services.NotificationPdfService
import utils.BaseSpec
import akka.util.ByteString
import akka.actor.ActorSystem
import play.api.i18n.{MessagesApi, Messages}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{any, refEq}
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers._
import play.api.i18n.DefaultMessagesApi
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}
import scala.concurrent.Future

class NotificationPDFControllerSpec extends AnyWordSpec with Matchers with BaseSpec with DefaultAwaitTimeout with MockitoSugar {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  implicit val actorSystem = ActorSystem()
  implicit val cc = Helpers.stubControllerComponents()

  val pdfService = app.injector.instanceOf[NotificationPdfService]
  val mockPdfService = mock[NotificationPdfService]
  val mockStubBehaviour = mock[StubBehaviour]
  val expectedPredicate = Predicate.Permission(Resource(ResourceType("digital-disclosure-service"), ResourceLocation("pdf")), IAAction("WRITE"))
  when(mockStubBehaviour.stubAuth(Some(expectedPredicate), Retrieval.EmptyRetrieval)).thenReturn(Future.unit)

  private val controller = new NotificationPDFController(new DefaultMessagesApi(), mockPdfService, BackendAuthComponentsStub(mockStubBehaviour), Helpers.stubControllerComponents())

  val instant = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
  val testNotification = Notification("123", "123", instant, Metadata(), Background(), AboutYou())
  
  "POST /notification/submit" should {
    "return 200 where the service returns a Success" in {
      val pdf = pdfService.createPdf(testNotification)
      when(mockPdfService.createPdf(refEq(testNotification))(any())) thenReturn pdf

      val fakeRequest = FakeRequest(method = "GET", uri = "/notification", headers = FakeHeaders(Seq("Authorization" -> "Token some-token")), body = Json.toJson(testNotification))
      val result = controller.generate()(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsBytes(result) shouldEqual ByteString(pdf.byteArray)
    }
  }

}
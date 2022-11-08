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
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers, FakeHeaders}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import java.time.{LocalDateTime, ZoneOffset}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json.Json
import models.notification._
import play.api.mvc.Results.NoContent
import connectors.NotificationStoreConnector

class NotificationStoreControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach with MaterializerSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockNotificationStoreConnector)
  }

  val mockNotificationStoreConnector = mock[NotificationStoreConnector]
  private val controller = new NotificationStoreController(mockNotificationStoreConnector, Helpers.stubControllerComponents())

  val instant = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
  val testNotification = Notification("123", "123", instant, Metadata(), Background(), AboutYou())
  
  "GET /notification/user/:userId" should {
    "return 200" in {
      when(mockNotificationStoreConnector.getAllNotifications(refEq("123"))(any())) thenReturn Future.successful(Seq(testNotification))

      val fakeRequest = FakeRequest("GET", "/notification/user/123")
      val result = controller.getAll("123")(fakeRequest)
      status(result) shouldBe Status.OK
      val body = contentAsJson(result).as[Seq[Notification]]
      body shouldBe Seq(testNotification)
    }

    "return 404" in {
      when(mockNotificationStoreConnector.getAllNotifications(refEq("123"))(any())) thenReturn Future.successful(Nil)

      val fakeRequest = FakeRequest("GET", "/notification/123")
      val result = controller.getAll("123")(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
    }
  }


  "GET /notification/user/:userId/id/:id" should {
    "return 200" in {
      when(mockNotificationStoreConnector.getNotification(refEq("123"), refEq("456"))(any())) thenReturn Future.successful(Some(testNotification))

      val fakeRequest = FakeRequest("GET", "/notification/user/123/id/456")
      val result = controller.get("123", "456")(fakeRequest)
      status(result) shouldBe Status.OK
      val body = contentAsJson(result).as[Notification]
      body shouldBe testNotification
    }

    "return 404" in {
      when(mockNotificationStoreConnector.getNotification(refEq("123"), refEq("456"))(any())) thenReturn Future.successful(None)

      val fakeRequest = FakeRequest("GET", "/notification/user/123/id/456")
      val result = controller.get("123", "456")(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
    }
  }

  "PUT /notification/" should {
    "return 204" in {
      when(mockNotificationStoreConnector.setNotification(refEq(testNotification))(any())) thenReturn Future.successful(NoContent)

      val fakeRequest = FakeRequest(method = "GET", uri = "/notification", headers = FakeHeaders(Seq.empty), body = Json.toJson(testNotification))
      val result = controller.set()(fakeRequest)
      status(result) shouldBe Status.NO_CONTENT
    }
  }
}

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
import services.DMSSubmissionService
import models.submission.SubmissionResponse
import 
play.api.i18n.DefaultMessagesApi

class NotificationSubmissionControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfterEach with MaterializerSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockSubmissionService)
  }

  val mockSubmissionService = mock[DMSSubmissionService]
  private val controller = new NotificationSubmissionController(new DefaultMessagesApi(), mockSubmissionService, Helpers.stubControllerComponents())

  val instant = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
  val testNotification = Notification("123", "123", instant, Metadata(), Background(), AboutYou())
  
  "POST /notification/submit" should {
    "return 202 where the service returns a Success" in {
      when(mockSubmissionService.submitNotification(refEq(testNotification))(any())) thenReturn Future.successful(SubmissionResponse.Success("id"))

      val fakeRequest = FakeRequest(method = "GET", uri = "/notification", headers = FakeHeaders(Seq.empty), body = Json.toJson(testNotification))
      val result = controller.submit()(fakeRequest)
      status(result) shouldBe Status.ACCEPTED
      val body = contentAsJson(result).as[SubmissionResponse.Success]
      body shouldBe SubmissionResponse.Success("id")
    }
  }

  "POST /notification/submit" should {
    "return 500 where the service returns a Failure" in {
      when(mockSubmissionService.submitNotification(refEq(testNotification))(any())) thenReturn Future.successful(SubmissionResponse.Failure(Seq("error1", "error2")))

      val fakeRequest = FakeRequest(method = "GET", uri = "/notification", headers = FakeHeaders(Seq.empty), body = Json.toJson(testNotification))
      val result = controller.submit()(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      val body = contentAsJson(result).as[SubmissionResponse.Failure]
      body shouldBe SubmissionResponse.Failure(Seq("error1", "error2"))
    }
  }


}

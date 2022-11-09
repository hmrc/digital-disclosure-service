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
import play.api.test.{FakeRequest, Helpers, FakeHeaders}
import scala.concurrent.Future
import play.api.libs.json.{JsString, Json}
import models.notification._
import org.scalatest.concurrent.ScalaFutures
import java.time.Instant
import play.api.mvc.Results._

class BaseControllerSpec extends AnyWordSpec with Matchers with ScalaFutures  {

  object TestController extends BaseController(Helpers.stubControllerComponents())

  val testNotification = Notification("123", "123", Instant.now(), Metadata(), Background(), AboutYou())
  
  "withValidJson" should {
    "call f when valid json is passed in" in {
      implicit val fakeRequest = FakeRequest(method = "GET", uri = "/notification", headers = FakeHeaders(Seq.empty), body = Json.toJson(testNotification))
      val result = TestController.withValidJson[Notification] { _ => Future.successful(Ok("Succeeded")) }
      result.futureValue shouldEqual Ok("Succeeded")
    }

    "return a BadRequest where invalid json is passed in" in {
      implicit val fakeRequest = FakeRequest(method = "GET", uri = "/notification", headers = FakeHeaders(Seq.empty), body = JsString("1234"))
      val result = TestController.withValidJson[Notification] { _ => Future.successful(Ok("Succeeded")) }
      result.futureValue shouldEqual BadRequest("Invalid JSON")
    }
  }
  
}

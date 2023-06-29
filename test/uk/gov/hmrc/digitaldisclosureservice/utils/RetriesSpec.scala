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

package utils

import akka.actor.ActorSystem
import org.mockito.Mockito._
import uk.gov.hmrc.http.{HttpResponse, UpstreamErrorResponse}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import config.AppConfig
import scala.concurrent.ExecutionContext.Implicits.global

class RetriesSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {

  val actorSystem: ActorSystem = ActorSystem.create()
  val mockActorSystem          = mock[ActorSystem]
  val mockAppConfig            = mock[AppConfig]

  val retries = new Retries {
    override implicit def actorSystem: ActorSystem = mockActorSystem
    override implicit def ec: ExecutionContext     = ExecutionContext.Implicits.global
  }

  "retry" should {
    "not retry" when {
      "the request is successful" in {
        when(mockAppConfig.retryIntervals)
          .thenReturn(Nil)

        val expected = HttpResponse(202, "")
        val result   = retries.retry(Future(expected))(mockAppConfig)

        result.onComplete {
          case scala.util.Failure(exception) =>
            fail("the method returned failure when it should have succeeded")
          case scala.util.Success(value)     =>
            value shouldBe expected
        }

        verify(mockActorSystem, never()).scheduler
      }

      "no intervals are provided" in {
        when(mockAppConfig.retryIntervals)
          .thenReturn(Nil)

        val result = retries.retry(Future.failed(UpstreamErrorResponse("", 502, 503)))(mockAppConfig)

        result.onComplete {
          case scala.util.Failure(exception) =>
            exception shouldBe an[UpstreamErrorResponse]
          case scala.util.Success(value)     =>
            fail("the method returned successful when it should have failed")
        }

        verify(mockActorSystem, never()).scheduler
      }

      "the response is not a 5xx error" in {
        when(mockAppConfig.retryIntervals)
          .thenReturn(List(1.second))

        val result = retries.retry(Future.failed(UpstreamErrorResponse("", 400, 502)))(mockAppConfig)

        result.onComplete {
          case scala.util.Failure(exception) =>
            exception shouldBe an[UpstreamErrorResponse]
          case scala.util.Success(value)     =>
            fail("the method returned successful when it should have failed")
        }

        verify(mockActorSystem, never()).scheduler
      }

      "intervals have run out" in {
        when(mockAppConfig.retryIntervals)
          .thenReturn(List(1.millisecond))

        when(mockActorSystem.scheduler)
          .thenReturn(actorSystem.scheduler)

        val result = retries.retry(Future.failed(UpstreamErrorResponse("", 504, 503)))(mockAppConfig)

        whenReady(result.failed) {
          _ shouldBe an[UpstreamErrorResponse]
        }

        verify(mockActorSystem, times(1)).scheduler
      }
    }

    "retry" when {
      "the response is a 5xx error" in {
        when(mockAppConfig.retryIntervals)
          .thenReturn(List(1.millisecond, 2.milliseconds))

        when(mockActorSystem.scheduler)
          .thenReturn(actorSystem.scheduler)

        val result =
          retries.retry(Future.failed(UpstreamErrorResponse("Something went wrong", 502, 503)))(mockAppConfig)

        whenReady(result.failed) {
          _ shouldBe an[UpstreamErrorResponse]
        }

        verify(mockActorSystem, times(3)).scheduler
      }
    }
  }
}

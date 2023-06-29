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
import akka.pattern.after
import play.api.Logging
import config.AppConfig
import uk.gov.hmrc.http.UpstreamErrorResponse.Upstream5xxResponse

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait Retries extends Logging {

  implicit def actorSystem: ActorSystem

  implicit def ec: ExecutionContext

  def retry[A](f: => Future[A])(implicit appConfig: AppConfig): Future[A] = {

    def loop(remainingIntervals: Seq[FiniteDuration])(block: => Future[A]): Future[A] =
      remainingIntervals.toList match {
        case Nil          => block
        case head :: tail =>
          block.recoverWith { case Upstream5xxResponse(t) =>
            logger.warn(
              s"Retrying failed call with message: ${t.message} with status code: ${t.statusCode} response retrying after $head",
              t
            )
            after(head, actorSystem.scheduler)(loop(tail)(f))
          }
      }

    loop(appConfig.retryIntervals)(f)
  }
}

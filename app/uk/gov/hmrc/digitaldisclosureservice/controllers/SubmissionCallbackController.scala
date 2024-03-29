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

import play.api.Logging
import play.api.mvc.{Action, ControllerComponents}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import play.api.libs.json.JsValue
import play.api.i18n.I18nSupport
import models.callback.CallbackRequest

@Singleton()
class SubmissionCallbackController @Inject() (
  cc: ControllerComponents
) extends BaseController(cc)
    with I18nSupport
    with Logging {

  def callback: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[CallbackRequest] { request =>
      request.failureReason match {
        case Some(reason) =>
          logger.error(
            s"Callback failed for submission ${request.id} with status ${request.status}. Failure reason: $reason."
          )
        case None         => logger.info(s"Callback for submission ${request.id} with status ${request.status}.")
      }

      Future.successful(Ok("Received"))
    }
  }

}

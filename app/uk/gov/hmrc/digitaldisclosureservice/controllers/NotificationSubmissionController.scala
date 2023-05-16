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

package controllers

import play.api.mvc.{Action, ControllerComponents}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import play.api.libs.json.{Json, JsValue}
import models.Notification
import services.DMSSubmissionService
import play.api.i18n.{I18nSupport, MessagesApi}
import models.submission.SubmissionResponse
import uk.gov.hmrc.internalauth.client._
import controllers.Permissions.internalAuthPermission

@Singleton()
class NotificationSubmissionController @Inject()(
    override val messagesApi: MessagesApi,
    submissionService: DMSSubmissionService,
    val auth: BackendAuthComponents,
    cc: ControllerComponents
  )(implicit ec: ExecutionContext) extends BaseController(cc) with I18nSupport {

  def submit: Action[JsValue] = auth.authorizedAction(internalAuthPermission("submit")).async(parse.json) { implicit request =>
    withValidJson[Notification]{ notification =>
      submissionService.submit(notification, getLanguage).map(_ match {
        case SubmissionResponse.Success(id) => Accepted(Json.toJson(SubmissionResponse.Success(id)))
        case SubmissionResponse.Failure(errors) => InternalServerError(Json.toJson(SubmissionResponse.Failure(errors)))
      })
    }
  }

}

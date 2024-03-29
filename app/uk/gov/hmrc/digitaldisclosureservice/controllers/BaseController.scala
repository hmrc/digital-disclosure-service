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

import play.api.i18n.Lang

import scala.concurrent.Future
import play.api.mvc.{ControllerComponents, Request, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.libs.json.{JsSuccess, JsValue, Reads}

abstract class BaseController(cc: ControllerComponents) extends BackendController(cc) {

  def withValidJson[T](f: T => Future[Result])(implicit request: Request[JsValue], reads: Reads[T]): Future[Result] =
    request.body.validate[T] match {
      case JsSuccess(value, _) => f(value)
      case _                   => Future.successful(BadRequest("Invalid JSON"))
    }

  def getLanguage(implicit request: Request[JsValue]): String = {
    val acceptedLanguages: Seq[Lang]    = request.acceptLanguages
    val preferredLanguage: Option[Lang] = acceptedLanguages.headOption

    preferredLanguage match {
      case Some(lang) => lang.code
      case None       => "en"
    }
  }

}

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

import play.api.mvc.{Action, ControllerComponents}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue}
import models.notification.Notification
import services.NotificationPdfService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Result, ResponseHeader}
import play.api.http.HttpEntity
import akka.stream.scaladsl.Source
import akka.util.ByteString
import scala.concurrent.Future

@Singleton()
class NotificationPDFController @Inject()(
    override val messagesApi: MessagesApi,
    service: NotificationPdfService,
    cc: ControllerComponents
  ) extends BaseController(cc) with I18nSupport {

  def generate: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[Notification]{ notification =>
      val pdf = service.createPdf(notification).byteArray
      val contentLength = Some(pdf.length.toLong)
      
      Future.successful(Result(
        header = ResponseHeader(200, Map.empty),
        body = HttpEntity.Streamed(Source(Seq(ByteString(pdf))), contentLength, Some("application/pdf"))
      ))
    }
  }

}
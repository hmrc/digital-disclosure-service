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
import play.api.libs.json.{JsValue}
import models.notification.Notification
import services.NotificationPdfService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Result, ResponseHeader}
import play.api.http.HttpEntity
import akka.stream.scaladsl.Source
import akka.util.ByteString
import scala.concurrent.Future
import play.api.Logging
import uk.gov.hmrc.internalauth.client._
import controllers.Permissions.internalAuthPermission

import java.nio.file.Files
import java.io.File

@Singleton()
class NotificationPDFController @Inject()(
    override val messagesApi: MessagesApi,
    service: NotificationPdfService,
    val auth: BackendAuthComponents,
    cc: ControllerComponents
  ) extends BaseController(cc) with I18nSupport with Logging {

  def generate: Action[JsValue] = auth.authorizedAction(internalAuthPermission("pdf")).async(parse.json) { implicit request =>
    withValidJson[Notification]{ notification =>
      val pdf = service.createPdf(notification).byteArray
      val contentLength = Some(pdf.length.toLong)
      
      val tempFile: File = new File("test.pdf")
      Files.write(tempFile.toPath, pdf)

      logger.info(s"contentLength = $contentLength")
      Future.successful(Result(
        header = ResponseHeader(200, Map.empty),
        body = HttpEntity.Streamed(Source(Seq(ByteString(pdf))), contentLength, Some("application/octet-stream"))
      ))
    }
  }

}

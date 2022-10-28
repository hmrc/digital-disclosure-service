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

package uk.gov.hmrc.digitaldisclosureservice.controllers

import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import models._
import services.NotificationPdfService
import play.api.i18n.{MessagesApi, Messages}
import java.util.Date

@Singleton()
class NotificationPdfController @Inject()(
  override val messagesApi: MessagesApi,
  pdfGenerator: NotificationPdfService,
  cc: ControllerComponents)
    extends BackendController(cc) {

  def generate: Action[AnyContent] = Action.async { implicit request =>

    implicit lazy val messages: Messages = messagesApi.preferred(request)

    val metadata = Metadata(
      reference = Some("Some reference"),
      submissionTime = Some(new Date())
    )

    val background = Background (
      haveYouReceivedALetter = Some(false),
      letterReferenceNumber = None,
      disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
      offshoreLiabilities = Some(false),
      onshoreLiabilities = Some(true)
    )
    val aboutYou = AboutYou(
      fullName = Some("Some name"),
      telephoneNumber = Some("+44 012345678"),
      doYouHaveAEmailAddress = Some(false),
      address = Some("Some address")
    )
    val aboutTheCompany = AboutTheCompany(
      name = Some("Some company name"),
      registrationNumber = Some("Some company registration number"),
      address = Some("Some company address")
    )
    val notification = Notification(metadata, background, aboutYou, aboutTheCompany = Some(aboutTheCompany))

    val pdf = pdfGenerator.createPdf(notification).toByteArray
    Future.successful(Ok(pdf))
  }
}

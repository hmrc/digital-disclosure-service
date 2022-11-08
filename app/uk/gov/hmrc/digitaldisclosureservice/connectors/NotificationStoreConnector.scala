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

package connectors

import config.Service
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.{Json, JsSuccess, JsError}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace
import models.notification._
import play.api.mvc.Result
import play.api.mvc.Results.NoContent

@Singleton
class NotificationStoreConnector @Inject() (
                                httpClient: HttpClientV2,
                                configuration: Configuration
                              )(implicit ec: ExecutionContext) {

  private val service: Service = configuration.get[Service]("microservice.services.digital-disclosure-service-store")

  def getNotification(userId: String, notificationId: String)(implicit hc: HeaderCarrier): Future[Option[Notification]] = {
    httpClient
      .get(url"${service.baseUrl}/notification/userId/$userId/id/$notificationId")
      .execute
      .flatMap { response =>
        if (response.status == OK) {
            response.json.validate[Notification] match {
              case JsSuccess(notification, _) => Future.successful(Some(notification))
              case JsError(_) => Future.failed(NotificationStoreConnector.UnexpectedResponseException(response.status, response.body))
            }
        } else if (response.status == NOT_FOUND) {
          Future.successful(None)
        } else {
          Future.failed(NotificationStoreConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def getAllNotifications(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Notification]] = {
    httpClient
      .get(url"${service.baseUrl}/notification/userId/$userId")
      .execute
      .flatMap { response =>
        if (response.status == OK) {
            response.json.validate[Seq[Notification]] match {
              case JsSuccess(notifications, _) => Future.successful(notifications)
              case JsError(_) => Future.failed(NotificationStoreConnector.UnexpectedResponseException(response.status, response.body))
            }
        } else if (response.status == NOT_FOUND) {
          Future.successful(Nil)
        } else {
          Future.failed(NotificationStoreConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def setNotification(notification: Notification)(implicit hc: HeaderCarrier): Future[Result] = {
    httpClient
      .post(url"${service.baseUrl}/notification")
      .withBody(Json.toJson(notification))
      .execute
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(NoContent)
        } else {
          Future.failed(NotificationStoreConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }
}

object NotificationStoreConnector {

  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from DDS Store, status: $status, body: $body"
  }
}
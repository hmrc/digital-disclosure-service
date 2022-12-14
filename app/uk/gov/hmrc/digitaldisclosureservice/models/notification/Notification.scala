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

package models.notification

import play.api.libs.json.{Json, OFormat}
import java.time.Instant
import com.thoughtworks.xstream._
import com.thoughtworks.xstream.io.xml.DomDriver

final case class Notification (
  userId: String,
  notificationId: String,
  lastUpdated: Instant,
  metadata: Metadata,
  background: Background,
  aboutYou: AboutYou,
  aboutTheIndividual: Option[AboutTheIndividual] = None,
  aboutTheCompany: Option[AboutTheCompany] = None,
  aboutTheTrust: Option[AboutTheTrust] = None,
  aboutTheLLP: Option[AboutTheLLP] = None,
  aboutTheEstate: Option[AboutTheEstate] = None,
  customerId: Option[CustomerId] = None
) {
  def disclosingAboutThemselves: Boolean = background.disclosureEntity match {
    case Some(DisclosureEntity(Individual, Some(true))) => true
    case _ => false
  }

  def toXml: String = {
    val xstream = new XStream(new DomDriver)
    xstream.toXML(this)
  }
}

object Notification {
  implicit val format: OFormat[Notification] = Json.format[Notification]
}
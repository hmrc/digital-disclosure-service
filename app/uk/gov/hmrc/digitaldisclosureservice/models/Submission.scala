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

package models

import java.time.Instant
import models.notification._
import models.disclosure._
import play.api.libs.json.{Json, OFormat}
import com.thoughtworks.xstream._
import com.thoughtworks.xstream.io.xml.DomDriver

sealed trait Submission {
  def userId: String
  def submissionId: String
  def lastUpdated: Instant
  def metadata: Metadata
  def customerId: Option[CustomerId]

  def toXml: String = {
    val xstream = new XStream(new DomDriver)
    xstream.toXML(this)
  }
}

object Submission {
  implicit val format: OFormat[Submission] = Json.format[Submission]
}

final case class FullDisclosure (
  userId: String,
  submissionId: String,
  lastUpdated: Instant,
  metadata: Metadata,
  caseReference: CaseReference,
  personalDetails: PersonalDetails,
  offshoreLiabilities: OffshoreLiabilities,
  otherLiabilities: OtherLiabilities,
  reasonForDisclosingNow: ReasonForDisclosingNow,
  customerId: Option[CustomerId] = None
) extends Submission {
  def disclosingAboutThemselves = personalDetails.disclosingAboutThemselves
}


object FullDisclosure {
  implicit val format: OFormat[FullDisclosure] = Json.format[FullDisclosure]
}

final case class Notification (
  userId: String,
  submissionId: String,
  lastUpdated: Instant,
  metadata: Metadata,
  personalDetails: PersonalDetails,
  customerId: Option[CustomerId] = None
) extends Submission {
  def disclosingAboutThemselves = personalDetails.disclosingAboutThemselves
} 

object Notification {
  implicit val format: OFormat[Notification] = Json.format[Notification]
}
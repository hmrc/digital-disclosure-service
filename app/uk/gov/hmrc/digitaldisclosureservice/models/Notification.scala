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

package models

import java.util.Date


final case class Notification (
  metadata: Metadata,
  background: Background,
  aboutYou: AboutYou,
  aboutTheIndividual: Option[AboutTheIndividual] = None,
  aboutTheCompany: Option[AboutTheCompany] = None,
  aboutTheTrust: Option[AboutTheTrust] = None,
  aboutTheLLP: Option[AboutTheLLP] = None
) {
  def disclosingAboutThemselves: Boolean = background.disclosureEntity match {
    case Some(DisclosureEntity(Individual, Some(true))) => true
    case _ => false
  }
}

final case class Metadata (
  reference: Option[String] = None,
  submissionTime: Option[Date] = None
)

final case class Background (
  haveYouReceivedALetter: Option[Boolean] = None,
  letterReferenceNumber: Option[String] = None,
  disclosureEntity: Option[DisclosureEntity] = None,
  offshoreLiabilities: Option[Boolean] = None,
  onshoreLiabilities: Option[Boolean] = None  
)

final case class AboutYou (
  fullName: Option[String] = None,
  telephoneNumber: Option[String] = None,
  doYouHaveAEmailAddress: Option[Boolean] = None,
  emailAddress: Option[String] = None,
  dateOfBirth: Option[Date] = None,
  mainOccupation: Option[String] = None,
  doYouHaveANino: Option[YesNoOrUnsure] = None,
  nino: Option[String] = None,
  registeredForVAT: Option[YesNoOrUnsure] = None,
  vatRegNumber: Option[String] = None,
  registeredForSA: Option[YesNoOrUnsure] = None,
  sautr: Option[String] = None,
  address: Option[String] = None
)

final case class AboutTheIndividual (
  fullName: Option[String] = None,
  dateOfBirth: Option[Date] = None,
  mainOccupation: Option[String] = None,
  doYouHaveANino: Option[YesNoOrUnsure] = None,
  nino: Option[String] = None,
  registeredForVAT: Option[YesNoOrUnsure] = None,
  vatRegNumber: Option[String] = None,
  registeredForSA: Option[YesNoOrUnsure] = None,
  sautr: Option[String] = None,
  address: Option[String] = None
)

final case class AboutTheCompany (
  name: Option[String] = None,
  registrationNumber: Option[String] = None,
  address: Option[String] = None
)

final case class AboutTheTrust (
  name: Option[String] = None,
  address: Option[String] = None
)

final case class AboutTheLLP (
  name: Option[String] = None,
  address: Option[String] = None
)

final case class DisclosureEntity(entity: Entity, areYouTheEntity: Option[Boolean] = None)

sealed trait Entity
case object Individual extends Entity
case object Estate extends Entity
case object Company extends Entity
case object LLP extends Entity
case object Trust extends Entity

sealed trait YesNoOrUnsure
object YesNoOrUnsure {
  case object Yes extends YesNoOrUnsure
  case object No extends YesNoOrUnsure
  case object Unsure extends YesNoOrUnsure
}
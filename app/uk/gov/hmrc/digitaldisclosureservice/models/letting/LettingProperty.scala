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

package models

import models.address.Address
import java.time.LocalDate
import play.api.libs.json._
import scala.xml._

final case class LettingProperty(
                                  address: Option[Address] = None,
                                  dateFirstLetOut: Option[LocalDate] = None,
                                  stoppedBeingLetOut: Option[Boolean] = None,
                                  noLongerBeingLetOut: Option[NoLongerBeingLetOut] = None,
                                  fhl: Option[Boolean] = None,
                                  isJointOwnership: Option[Boolean] = None,
                                  isMortgageOnProperty: Option[Boolean] = None,
                                  percentageIncomeOnProperty: Option[Int] = None,
                                  wasFurnished: Option[Boolean] = None,
                                  typeOfMortgage: Option[TypeOfMortgageDidYouHave] = None,
                                  otherTypeOfMortgage: Option[String] = None,
                                  wasPropertyManagerByAgent: Option[Boolean] = None,
                                  didTheLettingAgentCollectRentOnYourBehalf: Option[Boolean] = None
                                ) {
  def toXml: NodeSeq = {
    <lettingProperty>
      {address.map(a => <address>{a.toXml}</address>).getOrElse(NodeSeq.Empty)}
      {dateFirstLetOut.map(date => <dateFirstLetOut>{date}</dateFirstLetOut>).getOrElse(NodeSeq.Empty)}
      {stoppedBeingLetOut.map(flag => <stoppedBeingLetOut>{flag}</stoppedBeingLetOut>).getOrElse(NodeSeq.Empty)}
      {noLongerBeingLetOut.map(nlb => <noLongerBeingLetOut>{nlb.toXml}</noLongerBeingLetOut>).getOrElse(NodeSeq.Empty)}
      {fhl.map(flag => <fhl>{flag}</fhl>).getOrElse(NodeSeq.Empty)}
      {isJointOwnership.map(flag => <isJointOwnership>{flag}</isJointOwnership>).getOrElse(NodeSeq.Empty)}
      {isMortgageOnProperty.map(flag => <isMortgageOnProperty>{flag}</isMortgageOnProperty>).getOrElse(NodeSeq.Empty)}
      {percentageIncomeOnProperty.map(p => <percentageIncomeOnProperty>{p}</percentageIncomeOnProperty>).getOrElse(NodeSeq.Empty)}
      {wasFurnished.map(flag => <wasFurnished>{flag}</wasFurnished>).getOrElse(NodeSeq.Empty)}
      {typeOfMortgage.map(t => <typeOfMortgage>{t}</typeOfMortgage>).getOrElse(NodeSeq.Empty)}
      {otherTypeOfMortgage.map(otm => <otherTypeOfMortgage>{otm}</otherTypeOfMortgage>).getOrElse(NodeSeq.Empty)}
      {wasPropertyManagerByAgent.map(flag => <wasPropertyManagerByAgent>{flag}</wasPropertyManagerByAgent>).getOrElse(NodeSeq.Empty)}
      {didTheLettingAgentCollectRentOnYourBehalf.map(flag => <didTheLettingAgentCollectRentOnYourBehalf>{flag}</didTheLettingAgentCollectRentOnYourBehalf>).getOrElse(NodeSeq.Empty)}
    </lettingProperty>
  }
}

object LettingProperty {
  implicit val format = Json.format[LettingProperty]
}

final case class NoLongerBeingLetOut(
                                      stopDate: LocalDate,
                                      whatHasHappenedToProperty: String
                                    ) {
  def toXml: NodeSeq = {
    <noLongerBeingLetOut>
      <stopDate>{stopDate}</stopDate>
      <whatHasHappenedToProperty>{whatHasHappenedToProperty}</whatHasHappenedToProperty>
    </noLongerBeingLetOut>
  }
}

object NoLongerBeingLetOut {
  implicit val noLongerBeingLetOutFormat: OFormat[NoLongerBeingLetOut] = Json.format[NoLongerBeingLetOut]
}

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

package models.notification

import play.api.libs.json.{Json, OFormat}
import models.AreYouTheEntity
import scala.xml._

final case class PersonalDetails(
                                  background: Background,
                                  aboutYou: AboutYou,
                                  aboutTheIndividual: Option[AboutTheIndividual] = None,
                                  aboutTheCompany: Option[AboutTheCompany] = None,
                                  aboutTheTrust: Option[AboutTheTrust] = None,
                                  aboutTheLLP: Option[AboutTheLLP] = None,
                                  aboutTheEstate: Option[AboutTheEstate] = None
                                ) {
  lazy val disclosingAboutThemselves: Boolean = background.disclosureEntity match {
    case Some(DisclosureEntity(Individual, Some(AreYouTheEntity.YesIAm))) => true
    case _                                                                => false
  }

  def toXml: NodeSeq = {
    <personalDetails>
      <background>{background.toXml}</background>
      <aboutYou>{aboutYou.toXml}</aboutYou>
      {aboutTheIndividual.map(i => <aboutTheIndividual>{i.toXml}</aboutTheIndividual>).getOrElse(NodeSeq.Empty)}
      {aboutTheCompany.map(c => <aboutTheCompany>{c.toXml}</aboutTheCompany>).getOrElse(NodeSeq.Empty)}
      {aboutTheTrust.map(t => <aboutTheTrust>{t.toXml}</aboutTheTrust>).getOrElse(NodeSeq.Empty)}
      {aboutTheLLP.map(l => <aboutTheLLP>{l.toXml}</aboutTheLLP>).getOrElse(NodeSeq.Empty)}
      {aboutTheEstate.map(e => <aboutTheEstate>{e.toXml}</aboutTheEstate>).getOrElse(NodeSeq.Empty)}
    </personalDetails>
  }
}

object PersonalDetails {
  implicit val format: OFormat[PersonalDetails] = Json.format[PersonalDetails]
}
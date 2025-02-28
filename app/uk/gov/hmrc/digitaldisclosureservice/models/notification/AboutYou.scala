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

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}
import models.YesNoOrUnsure
import models.address.Address
import uk.gov.hmrc.digitaldisclosureservice.utils.XmlHelper.extractChildNodes

import scala.xml._

final case class AboutYou(
                           fullName: Option[String] = None,
                           telephoneNumber: Option[String] = None,
                           emailAddress: Option[String] = None,
                           dateOfBirth: Option[LocalDate] = None,
                           mainOccupation: Option[String] = None,
                           doYouHaveANino: Option[YesNoOrUnsure] = None,
                           nino: Option[String] = None,
                           registeredForVAT: Option[YesNoOrUnsure] = None,
                           vatRegNumber: Option[String] = None,
                           registeredForSA: Option[YesNoOrUnsure] = None,
                           sautr: Option[String] = None,
                           address: Option[Address] = None
                         ) {
  def toXml: NodeSeq = {
    <aboutYou>
      {fullName.map(name => <fullName>{name}</fullName>).getOrElse(NodeSeq.Empty)}
      {telephoneNumber.map(phone => <telephoneNumber>{phone}</telephoneNumber>).getOrElse(NodeSeq.Empty)}
      {emailAddress.map(email => <emailAddress>{email}</emailAddress>).getOrElse(NodeSeq.Empty)}
      {dateOfBirth.map(dob => <dateOfBirth>{dob.toString}</dateOfBirth>).getOrElse(NodeSeq.Empty)}
      {mainOccupation.map(occupation => <mainOccupation>{occupation}</mainOccupation>).getOrElse(NodeSeq.Empty)}
      {doYouHaveANino.map(nino => <doYouHaveANino>{nino.toXml}</doYouHaveANino>).getOrElse(NodeSeq.Empty)}
      {nino.map(n => <nino>{n}</nino>).getOrElse(NodeSeq.Empty)}
      {registeredForVAT.map(vat => <registeredForVAT>{vat.toXml}</registeredForVAT>).getOrElse(NodeSeq.Empty)}
      {vatRegNumber.map(vrn => <vatRegNumber>{vrn}</vatRegNumber>).getOrElse(NodeSeq.Empty)}
      {registeredForSA.map(sa => <registeredForSA>{sa.toXml}</registeredForSA>).getOrElse(NodeSeq.Empty)}
      {sautr.map(s => <sautr>{s}</sautr>).getOrElse(NodeSeq.Empty)}
      {address.map(addr => <address>{extractChildNodes(addr.toXml)}</address>).getOrElse(NodeSeq.Empty)}
    </aboutYou>
  }
}

object AboutYou {
  implicit val format: OFormat[AboutYou] = Json.format[AboutYou]
}
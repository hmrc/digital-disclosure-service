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
import scala.xml._

final case class AboutTheEstate(
  fullName: Option[String] = None,
  dateOfBirth: Option[LocalDate] = None,
  mainOccupation: Option[String] = None,
  doTheyHaveANino: Option[YesNoOrUnsure] = None,
  nino: Option[String] = None,
  registeredForVAT: Option[YesNoOrUnsure] = None,
  vatRegNumber: Option[String] = None,
  registeredForSA: Option[YesNoOrUnsure] = None,
  sautr: Option[String] = None,
  address: Option[Address] = None
) {
  def toXml: NodeSeq =
    <aboutTheEstate>
      {fullName.map(name => <fullName>{name}</fullName>).getOrElse(NodeSeq.Empty)}
      {dateOfBirth.map(dob => <dateOfBirth>{dob}</dateOfBirth>).getOrElse(NodeSeq.Empty)}
      {mainOccupation.map(occupation => <mainOccupation>{occupation}</mainOccupation>).getOrElse(NodeSeq.Empty)}
      {doTheyHaveANino.map(ninoStatus => <doTheyHaveANino>{ninoStatus}</doTheyHaveANino>).getOrElse(NodeSeq.Empty)}
      {nino.map(n => <nino>{n}</nino>).getOrElse(NodeSeq.Empty)}
      {registeredForVAT.map(vatStatus => <registeredForVAT>{vatStatus}</registeredForVAT>).getOrElse(NodeSeq.Empty)}
      {vatRegNumber.map(vat => <vatRegNumber>{vat}</vatRegNumber>).getOrElse(NodeSeq.Empty)}
      {registeredForSA.map(saStatus => <registeredForSA>{saStatus}</registeredForSA>).getOrElse(NodeSeq.Empty)}
      {sautr.map(s => <sautr>{s}</sautr>).getOrElse(NodeSeq.Empty)}
      {address.map(addr => <address>{addr.toXml}</address>).getOrElse(NodeSeq.Empty)}
    </aboutTheEstate>
}

object AboutTheEstate {
  implicit val format: OFormat[AboutTheEstate] = Json.format[AboutTheEstate]
}

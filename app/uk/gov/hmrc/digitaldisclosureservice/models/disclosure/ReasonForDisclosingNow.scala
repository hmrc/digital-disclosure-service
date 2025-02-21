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

package models.disclosure

import play.api.libs.json.{Json, OFormat}
import models.{AdviceGiven, WhichEmailAddressCanWeContactYouWith, WhichTelephoneNumberCanWeContactYouWith, WhyAreYouMakingADisclosure}
import scala.xml._

final case class ReasonForDisclosingNow(
                                         reason: Option[Set[WhyAreYouMakingADisclosure]] = None,
                                         otherReason: Option[String] = None,
                                         whyNotBeforeNow: Option[String] = None,
                                         receivedAdvice: Option[Boolean] = None,
                                         personWhoGaveAdvice: Option[String] = None,
                                         adviceOnBehalfOfBusiness: Option[Boolean] = None,
                                         adviceBusinessName: Option[String] = None,
                                         personProfession: Option[String] = None,
                                         adviceGiven: Option[AdviceGiven] = None,
                                         whichEmail: Option[WhichEmailAddressCanWeContactYouWith] = None,
                                         whichPhone: Option[WhichTelephoneNumberCanWeContactYouWith] = None,
                                         email: Option[String] = None,
                                         telephone: Option[String] = None
                                       ) {
  def toXml: NodeSeq = {
    <reasonForDisclosingNow>
      {reason.map(r =>
      <reason>
        {r.map(reason => <disclosureReason>{reason.toXml}</disclosureReason>)}
      </reason>
    ).getOrElse(NodeSeq.Empty)}
      {otherReason.map(r => <otherReason>{r}</otherReason>).getOrElse(NodeSeq.Empty)}
      {whyNotBeforeNow.map(why => <whyNotBeforeNow>{why}</whyNotBeforeNow>).getOrElse(NodeSeq.Empty)}
      {receivedAdvice.map(received => <receivedAdvice>{received}</receivedAdvice>).getOrElse(NodeSeq.Empty)}
      {personWhoGaveAdvice.map(person => <personWhoGaveAdvice>{person}</personWhoGaveAdvice>).getOrElse(NodeSeq.Empty)}
      {adviceOnBehalfOfBusiness.map(onBehalf => <adviceOnBehalfOfBusiness>{onBehalf}</adviceOnBehalfOfBusiness>).getOrElse(NodeSeq.Empty)}
      {adviceBusinessName.map(name => <adviceBusinessName>{name}</adviceBusinessName>).getOrElse(NodeSeq.Empty)}
      {personProfession.map(profession => <personProfession>{profession}</personProfession>).getOrElse(NodeSeq.Empty)}
      {adviceGiven.map(advice => <adviceGiven>{advice.toXml}</adviceGiven>).getOrElse(NodeSeq.Empty)}
      {whichEmail.map(email => <whichEmail>{email.toXml}</whichEmail>).getOrElse(NodeSeq.Empty)}
      {whichPhone.map(phone => <whichPhone>{phone.toXml}</whichPhone>).getOrElse(NodeSeq.Empty)}
      {email.map(e => <email>{e}</email>).getOrElse(NodeSeq.Empty)}
      {telephone.map(t => <telephone>{t}</telephone>).getOrElse(NodeSeq.Empty)}
    </reasonForDisclosingNow>
  }
}

object ReasonForDisclosingNow {
  implicit val format: OFormat[ReasonForDisclosingNow] = Json.format[ReasonForDisclosingNow]
}
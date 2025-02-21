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

import models.IncomeOrGainSource
import play.api.libs.json.{Json, OFormat}
import scala.xml._

final case class Background(
                             haveYouReceivedALetter: Option[Boolean] = None,
                             letterReferenceNumber: Option[String] = None,
                             disclosureEntity: Option[DisclosureEntity] = None,
                             areYouRepresetingAnOrganisation: Option[Boolean] = None,
                             organisationName: Option[String] = None,
                             offshoreLiabilities: Option[Boolean] = None,
                             onshoreLiabilities: Option[Boolean] = None,
                             incomeSource: Option[Set[IncomeOrGainSource]] = None,
                             otherIncomeSource: Option[String] = None
                           ) {
  def toXml: NodeSeq = {
    <background>
      {haveYouReceivedALetter.map(answer => <haveYouReceivedALetter>{answer}</haveYouReceivedALetter>).getOrElse(NodeSeq.Empty)}
      {letterReferenceNumber.map(ref => <letterReferenceNumber>{ref}</letterReferenceNumber>).getOrElse(NodeSeq.Empty)}
      {disclosureEntity.map(entity => <disclosureEntity>{entity.toXml}</disclosureEntity>).getOrElse(NodeSeq.Empty)}
      {areYouRepresetingAnOrganisation.map(answer => <areYouRepresetingAnOrganisation>{answer}</areYouRepresetingAnOrganisation>).getOrElse(NodeSeq.Empty)}
      {organisationName.map(name => <organisationName>{name}</organisationName>).getOrElse(NodeSeq.Empty)}
      {offshoreLiabilities.map(answer => <offshoreLiabilities>{answer}</offshoreLiabilities>).getOrElse(NodeSeq.Empty)}
      {onshoreLiabilities.map(answer => <onshoreLiabilities>{answer}</onshoreLiabilities>).getOrElse(NodeSeq.Empty)}
      {incomeSource.map(sources =>
      <incomeSource>
        {sources.map(source => <source>{source}</source>)}
      </incomeSource>
    ).getOrElse(NodeSeq.Empty)}
      {otherIncomeSource.map(source => <otherIncomeSource>{source}</otherIncomeSource>).getOrElse(NodeSeq.Empty)}
    </background>
  }
}

object Background {
  implicit val format: OFormat[Background] = Json.format[Background]
}

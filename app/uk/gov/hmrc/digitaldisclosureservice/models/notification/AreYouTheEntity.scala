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

import scala.xml._

sealed trait AreYouTheEntity {
  def toXml: String = this match {
    case AreYouTheEntity.YesIAm                    => "yes"
    case AreYouTheEntity.IAmAnAccountantOrTaxAgent => "accountant"
    case AreYouTheEntity.IAmAFriend                => "friend"
    case AreYouTheEntity.VoluntaryOrganisation     => "voluntaryOrganisation"
    case AreYouTheEntity.PowerOfAttorney           => "powerOfAttorney"
  }
}

object AreYouTheEntity extends Enumerable.Implicits {

  case object YesIAm extends WithName("yes") with AreYouTheEntity
  case object IAmAnAccountantOrTaxAgent extends WithName("accountant") with AreYouTheEntity
  case object IAmAFriend extends WithName("friend") with AreYouTheEntity
  case object VoluntaryOrganisation extends WithName("voluntaryOrganisation") with AreYouTheEntity
  case object PowerOfAttorney extends WithName("powerOfAttorney") with AreYouTheEntity

  val values: Seq[AreYouTheEntity] = Seq(
    YesIAm,
    IAmAnAccountantOrTaxAgent,
    IAmAFriend,
    VoluntaryOrganisation,
    PowerOfAttorney
  )

  implicit val enumerable: Enumerable[AreYouTheEntity] =
    Enumerable(values.map(v => v.toString -> v): _*)

}

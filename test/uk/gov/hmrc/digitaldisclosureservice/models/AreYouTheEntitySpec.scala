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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AreYouTheEntitySpec extends AnyWordSpec with Matchers {

  "AreYouTheEntity" should {
    "convert to the correct string xml representation" when {
      "YesIAm" in {
        AreYouTheEntity.YesIAm.toXml shouldEqual "yes"
      }

      "IAmAnAccountantOrTaxAgent" in {
        AreYouTheEntity.IAmAnAccountantOrTaxAgent.toXml shouldEqual "accountant"
      }

      "IAmAFriend" in {
        AreYouTheEntity.IAmAFriend.toXml shouldEqual "friend"
      }

      "VoluntaryOrganisation" in {
        AreYouTheEntity.VoluntaryOrganisation.toXml shouldEqual "voluntaryOrganisation"
      }

      "PowerOfAttorney" in {
        AreYouTheEntity.PowerOfAttorney.toXml shouldEqual "powerOfAttorney"
      }
    }

    "contain the correct set of values" in {
      AreYouTheEntity.values should contain theSameElementsAs Seq(
        AreYouTheEntity.YesIAm,
        AreYouTheEntity.IAmAnAccountantOrTaxAgent,
        AreYouTheEntity.IAmAFriend,
        AreYouTheEntity.VoluntaryOrganisation,
        AreYouTheEntity.PowerOfAttorney
      )
    }

    "bind correctly from valid strings" in {
      val result1 = AreYouTheEntity.enumerable.withName("yes")
      result1 shouldBe Some(AreYouTheEntity.YesIAm)

      val result2 = AreYouTheEntity.enumerable.withName("accountant")
      result2 shouldBe Some(AreYouTheEntity.IAmAnAccountantOrTaxAgent)

      val result3 = AreYouTheEntity.enumerable.withName("friend")
      result3 shouldBe Some(AreYouTheEntity.IAmAFriend)

      val result4 = AreYouTheEntity.enumerable.withName("voluntaryOrganisation")
      result4 shouldBe Some(AreYouTheEntity.VoluntaryOrganisation)

      val result5 = AreYouTheEntity.enumerable.withName("powerOfAttorney")
      result5 shouldBe Some(AreYouTheEntity.PowerOfAttorney)
    }

    "not bind from invalid strings" in {
      val result = AreYouTheEntity.enumerable.withName("invalidOption")
      result shouldBe None
    }

    "properly implement toString" in {
      AreYouTheEntity.YesIAm.toString shouldEqual "yes"
      AreYouTheEntity.IAmAnAccountantOrTaxAgent.toString shouldEqual "accountant"
      AreYouTheEntity.IAmAFriend.toString shouldEqual "friend"
      AreYouTheEntity.VoluntaryOrganisation.toString shouldEqual "voluntaryOrganisation"
      AreYouTheEntity.PowerOfAttorney.toString shouldEqual "powerOfAttorney"
    }
  }
}
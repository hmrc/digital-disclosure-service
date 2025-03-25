/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}

class WhereDidTheUndeclaredIncomeOrGainIncludedSpec extends AnyWordSpec with Matchers {

  "WhereDidTheUndeclaredIncomeOrGainIncluded" should {
    "encode correctly to strings" in {
      WhereDidTheUndeclaredIncomeOrGainIncluded.Dividends.toString               shouldBe "dividends"
      WhereDidTheUndeclaredIncomeOrGainIncluded.Interest.toString                shouldBe "interest"
      WhereDidTheUndeclaredIncomeOrGainIncluded.PropertyIncome.toString          shouldBe "propertyIncome"
      WhereDidTheUndeclaredIncomeOrGainIncluded.ResidentialPropertyGain.toString shouldBe "residentialPropertyGain"
      WhereDidTheUndeclaredIncomeOrGainIncluded.SelfEmploymentIncome.toString    shouldBe "selfEmploymentIncome"
      WhereDidTheUndeclaredIncomeOrGainIncluded.OtherGains.toString              shouldBe "otherGains"
      WhereDidTheUndeclaredIncomeOrGainIncluded.SomewhereElse.toString           shouldBe "somewhereElse"
    }

    "contain all expected values" in {
      WhereDidTheUndeclaredIncomeOrGainIncluded.values.size shouldBe 7
      WhereDidTheUndeclaredIncomeOrGainIncluded.values        should contain(
        WhereDidTheUndeclaredIncomeOrGainIncluded.Dividends
      )
      WhereDidTheUndeclaredIncomeOrGainIncluded.values        should contain(
        WhereDidTheUndeclaredIncomeOrGainIncluded.Interest
      )
      WhereDidTheUndeclaredIncomeOrGainIncluded.values        should contain(
        WhereDidTheUndeclaredIncomeOrGainIncluded.PropertyIncome
      )
      WhereDidTheUndeclaredIncomeOrGainIncluded.values        should contain(
        WhereDidTheUndeclaredIncomeOrGainIncluded.ResidentialPropertyGain
      )
      WhereDidTheUndeclaredIncomeOrGainIncluded.values        should contain(
        WhereDidTheUndeclaredIncomeOrGainIncluded.SelfEmploymentIncome
      )
      WhereDidTheUndeclaredIncomeOrGainIncluded.values        should contain(
        WhereDidTheUndeclaredIncomeOrGainIncluded.OtherGains
      )
      WhereDidTheUndeclaredIncomeOrGainIncluded.values        should contain(
        WhereDidTheUndeclaredIncomeOrGainIncluded.SomewhereElse
      )
    }
  }
}

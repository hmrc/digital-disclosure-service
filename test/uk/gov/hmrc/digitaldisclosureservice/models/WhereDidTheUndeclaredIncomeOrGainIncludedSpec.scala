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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class WhereDidTheUndeclaredIncomeOrGainIncludedSpec extends AnyFreeSpec with Matchers {

  "WhereDidTheUndeclaredIncomeOrGainIncluded" - {

    "must return correct string values for each case" in {
      WhereDidTheUndeclaredIncomeOrGainIncluded.Dividends.toString mustBe "dividends"
      WhereDidTheUndeclaredIncomeOrGainIncluded.Interest.toString mustBe "interest"
      WhereDidTheUndeclaredIncomeOrGainIncluded.PropertyIncome.toString mustBe "propertyIncome"
      WhereDidTheUndeclaredIncomeOrGainIncluded.ResidentialPropertyGain.toString mustBe "residentialPropertyGain"
      WhereDidTheUndeclaredIncomeOrGainIncluded.SelfEmploymentIncome.toString mustBe "selfEmploymentIncome"
      WhereDidTheUndeclaredIncomeOrGainIncluded.OtherGains.toString mustBe "otherGains"
      WhereDidTheUndeclaredIncomeOrGainIncluded.SomewhereElse.toString mustBe "somewhereElse"
    }

    "must contain all expected values in the values sequence" in {
      WhereDidTheUndeclaredIncomeOrGainIncluded.values must contain theSameElementsAs Seq(
        WhereDidTheUndeclaredIncomeOrGainIncluded.Dividends,
        WhereDidTheUndeclaredIncomeOrGainIncluded.Interest,
        WhereDidTheUndeclaredIncomeOrGainIncluded.PropertyIncome,
        WhereDidTheUndeclaredIncomeOrGainIncluded.ResidentialPropertyGain,
        WhereDidTheUndeclaredIncomeOrGainIncluded.SelfEmploymentIncome,
        WhereDidTheUndeclaredIncomeOrGainIncluded.OtherGains,
        WhereDidTheUndeclaredIncomeOrGainIncluded.SomewhereElse
      )
    }

    "must serialize and deserialize correctly" in {
      val enumerable = WhereDidTheUndeclaredIncomeOrGainIncluded.enumerable
      WhereDidTheUndeclaredIncomeOrGainIncluded.values.foreach { value =>
        enumerable.withName(value.toString) mustBe Some(value)
      }
    }
  }
}
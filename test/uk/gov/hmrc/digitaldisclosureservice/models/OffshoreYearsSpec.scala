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

package uk.gov.hmrc.digitaldisclosureservice.models

import models.{CarelessPriorTo, DeliberatePriorTo, OffshoreYears, ReasonableExcusePriorTo}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, JsString, JsSuccess, Json}

class OffshoreYearsSpec extends AnyWordSpec with Matchers {

  "reads" should {
    "convert reasonableExcusePriorTo" in {
      JsString("reasonableExcusePriorTo").validate[OffshoreYears] shouldEqual JsSuccess(ReasonableExcusePriorTo)
    }

    "convert carelessPriorTo" in {
      JsString("carelessPriorTo").validate[OffshoreYears] shouldEqual JsSuccess(CarelessPriorTo)
    }

    "convert deliberatePriorTo" in {
      JsString("deliberatePriorTo").validate[OffshoreYears] shouldEqual JsSuccess(DeliberatePriorTo)
    }

    "convert unknown" in {
      JsString("random").validate[OffshoreYears] shouldEqual JsError("error.invalid")
    }
  }

  "writes" should {
    "convert ReasonableExcusePriorTo" in {
      Json
        .toJson[OffshoreYears](ReasonableExcusePriorTo)(OffshoreYears.writes)
        .asOpt[String]
        .getOrElse("") shouldEqual ReasonableExcusePriorTo.toString
    }

    "convert CarelessPriorTo" in {
      Json
        .toJson[OffshoreYears](CarelessPriorTo)(OffshoreYears.writes)
        .asOpt[String]
        .getOrElse("") shouldEqual CarelessPriorTo.toString
    }

    "convert DeliberatePriorTo" in {
      Json
        .toJson[OffshoreYears](DeliberatePriorTo)(OffshoreYears.writes)
        .asOpt[String]
        .getOrElse("") shouldEqual DeliberatePriorTo.toString
    }
  }
}

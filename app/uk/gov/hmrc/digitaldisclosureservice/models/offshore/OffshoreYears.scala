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

import play.api.libs.json._
import scala.util.Try
import scala.xml._

sealed trait OffshoreYears {
  def toXml: NodeSeq
}

final case class TaxYearStarting(startYear: Int) extends OffshoreYears with Ordered[TaxYearStarting] {
  override def toString: String = startYear.toString

  def compare(that: TaxYearStarting): Int = that.startYear - this.startYear

  override def toXml: NodeSeq = <taxYearStarting>{startYear}</taxYearStarting>
}

object TaxYearStarting {
  def findMissingYears(yearList: List[TaxYearStarting]): List[TaxYearStarting] =
    yearList.sorted(Ordering[TaxYearStarting].reverse).map(_.startYear) match {
      case (head :: tail) :+ last =>
        val yearsBetweenFirstAndLast = Range(head, last)
        val missingYearsAsInts       = yearsBetweenFirstAndLast.filterNot(int => yearList.contains(TaxYearStarting(int)))
        missingYearsAsInts.map(TaxYearStarting(_)).toList
      case _                      => Nil
    }

  implicit val format: Format[TaxYearStarting] = Json.format[TaxYearStarting]
}

case object ReasonableExcusePriorTo extends OffshoreYears {
  override def toString: String = "reasonableExcusePriorTo"
  override def toXml: NodeSeq   = <offshoreYears>reasonableExcusePriorTo</offshoreYears>
}

case object CarelessPriorTo extends OffshoreYears {
  override def toString: String = "carelessPriorTo"
  override def toXml: NodeSeq   = <offshoreYears>carelessPriorTo</offshoreYears>
}

case object DeliberatePriorTo extends OffshoreYears {
  override def toString: String = "deliberatePriorTo"
  override def toXml: NodeSeq   = <offshoreYears>deliberatePriorTo</offshoreYears>
}

object RawOffshoreYears {
  def unapply(s: String): Option[Int] = Try(s.toInt).toOption
}

object OffshoreYears {
  def fromString(str: String): Option[OffshoreYears] =
    str match {
      case "reasonableExcusePriorTo" => Some(ReasonableExcusePriorTo)
      case "carelessPriorTo"         => Some(CarelessPriorTo)
      case "deliberatePriorTo"       => Some(DeliberatePriorTo)
      case RawOffshoreYears(year)    => Some(TaxYearStarting(year))
      case _                         => None
    }

  implicit def reads: Reads[OffshoreYears] = Reads {
    case JsString(str) =>
      fromString(str)
        .map { s =>
          JsSuccess(s)
        }
        .getOrElse(JsError("error.invalid"))
    case _             =>
      JsError("error.invalid")
  }

  implicit val writes: Writes[OffshoreYears] = Writes[OffshoreYears] { value =>
    JsString(value.toString)
  }

  implicit val format: Format[OffshoreYears] = Format(reads, writes)

  def toXml(years: List[OffshoreYears]): NodeSeq =
    <offshoreYears>
      {years.map(_.toXml)}
    </offshoreYears>
}

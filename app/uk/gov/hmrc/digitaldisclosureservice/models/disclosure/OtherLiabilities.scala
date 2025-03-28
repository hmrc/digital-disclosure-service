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
import models.OtherLiabilityIssues
import scala.xml._

final case class OtherLiabilities(
  issues: Option[Set[OtherLiabilityIssues]] = None,
  inheritanceGift: Option[String] = None,
  other: Option[String] = None,
  taxCreditsReceived: Option[Boolean] = None
) {
  def toXml: NodeSeq =
    <otherLiabilities>
      {
      issues
        .map(i => <issues>
        {i.map(issue => <issue>{issue.toXml}</issue>)}
      </issues>)
        .getOrElse(NodeSeq.Empty)
    }
      {inheritanceGift.map(gift => <inheritanceGift>{gift}</inheritanceGift>).getOrElse(NodeSeq.Empty)}
      {other.map(o => <other>{o}</other>).getOrElse(NodeSeq.Empty)}
      {taxCreditsReceived.map(received => <taxCreditsReceived>{received}</taxCreditsReceived>).getOrElse(NodeSeq.Empty)}
    </otherLiabilities>
}

object OtherLiabilities {
  implicit val format: OFormat[OtherLiabilities] = Json.format[OtherLiabilities]
}

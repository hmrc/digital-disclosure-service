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

import java.time.LocalDateTime
import play.api.libs.json.{Json, OFormat}
import scala.xml._

final case class Metadata(
  reference: Option[String] = None,
  submissionTime: Option[LocalDateTime] = None
) {
  def toXml: NodeSeq =
    <metadata>
      {reference.map(ref => <reference>{ref}</reference>).getOrElse(NodeSeq.Empty)}
      {submissionTime.map(time => <submissionTime>{time.toString}</submissionTime>).getOrElse(NodeSeq.Empty)}
    </metadata>
}

object Metadata {
  implicit val format: OFormat[Metadata] = Json.format[Metadata]
}

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

package models.submission

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

final case class SubmissionMetadata(
  timeOfReceipt: LocalDateTime,
  customerId: String,
  submissionMark: String
) {
  val store              = true
  val formId             = "DO4SUB"
  val businessArea       = "EC"
  val classificationType = "EC-CCO-Digital Disclosure Serv"
  val casKey             = ""
}

object SubmissionMetadata {
  implicit lazy val format: OFormat[SubmissionMetadata] = Json.format
}

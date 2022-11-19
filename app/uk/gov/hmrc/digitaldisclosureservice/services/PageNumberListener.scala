/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import scala.concurrent.Promise
import com.openhtmltopdf.pdfboxout.{PDFCreationListener, PdfBoxRenderer}

class PageNumberListener extends PDFCreationListener {

  val countP = Promise[Int]()
  val countFuture = countP.future

  def preWrite(renderer: PdfBoxRenderer, pageCount: Int): Unit = {
    countP.success(pageCount)
  }
  def onClose(renderer: PdfBoxRenderer): Unit = ()
  def preOpen(renderer: PdfBoxRenderer): Unit = ()

}

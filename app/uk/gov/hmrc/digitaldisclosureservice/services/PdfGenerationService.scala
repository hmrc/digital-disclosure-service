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

package services

import java.io.{ByteArrayOutputStream, File}
import com.openhtmltopdf.pdfboxout.{PdfBoxRenderer, PdfRendererBuilder}
import com.openhtmltopdf.svgsupport.BatikSVGDrawer
import org.apache.commons.io.IOUtils
import models.PDF

trait PdfGenerationService {

  def buildPdf(html: String): PDF = {

    val os                       = new ByteArrayOutputStream()
    val builder                  = new PdfRendererBuilder
    val renderer: PdfBoxRenderer = builder
      .useColorProfile(IOUtils.toByteArray(getClass.getResourceAsStream("/resources/sRGB-Color-Space-Profile.icm")))
      .useFont(new File(getClass.getResource("/resources/ArialMT.ttf").toURI), "arial")
      .usePdfUaAccessbility(true)
      .withHtmlContent(html, "/")
      .useFastMode
      .useSVGDrawer(new BatikSVGDrawer())
      .toStream(os)
      .buildPdfRenderer()
    renderer.createPDF()
    renderer.close()

    PDF(os)

  }

}

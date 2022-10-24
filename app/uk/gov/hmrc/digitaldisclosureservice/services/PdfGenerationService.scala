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

import play.api.Logging
import play.api.i18n.Messages
import java.io.{ByteArrayOutputStream, File}

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.svgsupport.BatikSVGDrawer
import com.openhtmltopdf.util.XRLog

import javax.inject.{Inject, Singleton}
import org.apache.commons.io.IOUtils

import scala.collection.mutable.ListBuffer
import scala.io.Source

@Singleton
class PdfGenerationService extends Logging {

  def createPdf(implicit messages: Messages): ByteArrayOutputStream = {

    val startBoilerplate =
      s"""
         |<!DOCTYPE html>
         |<html lang="${messages.lang.code}">
         |<head>
         |<title>Digital </title>
         |<meta name="Lang" content="${messages.lang.code}"/>
         |<meta name="subject" content="Test subject"/>
         |<meta name="about" content="HMRC PDFA Document"/>
         |<style>
         |  body {margin: 0; font-family: 'arial'; font-size: 16px;}
         |  header {margin: 0; font-family: 'arial'; font-size: 16px;}
         |  h1 {font-size: 36pt; letter-spacing: 0.04em;}
         |  h2 {font-size: 24pt;}
         |  h3 {font-size: 1em;}
         |</style>
         |</head>
         |<body>
         |${pdfHeader}
         |""".stripMargin

    val endBoilerplate = "</body></html>"
    val html = startBoilerplate + addMetadata + addContent + endBoilerplate
    buildPdf(html)
  }

  def addMetadata(implicit messages: Messages): String = {
      s"""
       |<h1 style="padding-top: 1em;">Notification of intent</h1>
       |<h2 style="padding-top: 1em;">Your submission details</h2>
       |
       |<div style="display: block;">
       |
       |<h3 style="margin-bottom: 0em;">Your reference number</h3>
       |<p style="margin-top: 0.3em; padding-left: 0.05em">D61-ABCDE-ABC</p>
       |
       |<h3 style="margin-bottom: 0em;">Your submission was sent on</h3>
       |<p style="margin-top: 0.3em; padding-left: 0.05em">Monday 24th October 2022</p>
       |
       |</div>
       |""".stripMargin
  }

  def addContent(implicit messages: Messages): String = {
      s"""
       |<h2 style="padding-top: 1em;">Background</h2>
       |
       |<div style="display: block;">
       |
       |<h3 style="margin-bottom: 0em;">Are you making this disclosure because you have received a letter from HMRC?</h3>
       |<p style="margin-top: 0.3em; padding-left: 0.05em">No</p>
       |
       |<h3 style="margin-bottom: 0em;">What is the disclosure about?</h3>
       |<p style="margin-top: 0.3em; padding-left: 0.05em">An individual</p>
       |
       |<h3 style="margin-bottom: 0em;">Are you the individual this disclosure is about?</h3>
       |<p style="margin-top: 0.3em; padding-left: 0.05em">Yes, I am the individual</p>
       |
       |<h3 style="margin-bottom: 0em;">Do you want to disclose offshore liabilities?</h3>
       |<p style="margin-top: 0.3em; padding-left: 0.05em">Yes</p>
       |
       |<h3 style="margin-bottom: 0em;">Do you also want to disclose onshore liabilities?</h3>
       |<p style="margin-top: 0.3em; padding-left: 0.05em">No</p>
       |
       |</div>
       |""".stripMargin
  }

  def pdfHeader(implicit messages: Messages): String = {
    val crownIcon = Source.fromFile(getClass.getResource("/resources/crown.svg").toURI)
    val startHtml = s"""<div style="padding-bottom: 0.3em; margin-top: -1em;">"""
    val endHtml =
      s"""<p style="padding-left: 0.5em; display: inline-block; font-size: 16pt; vertical-align: middle;">Digital Disclosure Service</p>
            </div><hr/>"""
    val headingHtml = startHtml ++ crownIcon.getLines().mkString ++ endHtml
    crownIcon.close()
    headingHtml
  }

  def buildPdf(html: String)(implicit messages: Messages): ByteArrayOutputStream = {
    val os = new ByteArrayOutputStream()
    val builder = new PdfRendererBuilder
    builder
      .useColorProfile(IOUtils.toByteArray(getClass.getResourceAsStream("/resources/sRGB-Color-Space-Profile.icm")))
      .useFont(new File(getClass.getResource("/resources/ArialMT.ttf").toURI), "arial")
      .usePdfUaAccessbility(true)
      .usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_B)
      .withHtmlContent(html , "/")
      .useFastMode
      .useSVGDrawer(new BatikSVGDrawer())
      .toStream(os)
      .run()
    os
  }

}
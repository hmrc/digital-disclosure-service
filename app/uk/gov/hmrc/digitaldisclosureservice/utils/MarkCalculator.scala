/*
 * Copyright 2023 HM Revenue & Customs
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

package utils

import com.google.inject.{Singleton, ImplementedBy}
import org.apache.commons.codec.binary.Base64
import java.io.{ByteArrayInputStream, InputStream}
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import org.w3c.dom.Document
import java.security.MessageDigest
import com.sun.org.apache.xml.internal.security.transforms.Transforms
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput
import com.sun.org.apache.xml.internal.security.Init

@Singleton
class MarkCalculatorImpl extends MarkCalculator {

  def getSfMark(xml: String): String = {
    createMark(new ByteArrayInputStream(xml.getBytes))
  }

  final val DEFAULT_SEC_HASH_ALGORITHM: String = "SHA1"

  private def createMark(in: InputStream): String = {
    return toBase64(getMarkBytes(in))
  }

  def getAlgorithm: String = "<?xml version='1.0'?>\n" +
    "<dsig:Transforms xmlns:dsig='http://www.w3.org/2000/09/xmldsig#' xmlns:xdp='http://ns.adobe.com/xdp/'>\n" +
    "<dsig:Transform Algorithm='http://www.w3.org/TR/1999/REC-xpath-19991116'>\n" +
    "<dsig:XPath>count(ancestor-or-self::node()|/xdp:xdp)=count(ancestor-or-self::node())</dsig:XPath>\n" +
    "</dsig:Transform>\n" +
    "<dsig:Transform Algorithm='http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments'/>\n" +
    "</dsig:Transforms>"

  private def getMarkBytes(in: InputStream): Array[Byte] = {

    Init.init()

    // Parse the transform details to create a document
    val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance
    dbf.setNamespaceAware(true)
    val db: DocumentBuilder = dbf.newDocumentBuilder
    val doc: Document = db.parse(new ByteArrayInputStream(getAlgorithm.getBytes))

    // Construct a Apache security Transforms object from that document
    val transforms: Transforms = new Transforms(doc.getDocumentElement, null)

    // Now perform the transform on the input to get the results.
    val input: XMLSignatureInput = new XMLSignatureInput(in)
    val result: XMLSignatureInput = transforms.performTransforms(input)

    val md: MessageDigest = MessageDigest.getInstance(DEFAULT_SEC_HASH_ALGORITHM)
    md.update(result.getBytes)
    return md.digest
  }

  private def toBase64(irMarkBytes: Array[Byte]): String = {
    return new String(Base64.encodeBase64(irMarkBytes))
  }

}

@ImplementedBy(classOf[MarkCalculatorImpl])
trait MarkCalculator {
  def getSfMark(xml: String): String
}
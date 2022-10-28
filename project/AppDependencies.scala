import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.8.0"
  
  private val pdfboxVersion = "2.0.26"
  private val openHtmlVersion = "1.0.10"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % bootstrapVersion,
    "org.apache.pdfbox"      %     "pdfbox"                     % pdfboxVersion,
    "org.apache.pdfbox"      %     "xmpbox"                     % pdfboxVersion,
    "org.apache.xmlgraphics" %     "batik-transcoder"           % "1.14",
    "org.apache.xmlgraphics" %     "batik-codec"                % "1.14",
    "com.openhtmltopdf"      %     "openhtmltopdf-core"         % openHtmlVersion,
    "com.openhtmltopdf"      %     "openhtmltopdf-pdfbox"       % openHtmlVersion,
    "com.openhtmltopdf"      %     "openhtmltopdf-svg-support"  % openHtmlVersion,
    "uk.gov.hmrc"            %% "play-frontend-hmrc"            % "3.32.0-play-28"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion            % "test, it",
    
  )
}

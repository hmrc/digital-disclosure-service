import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.8.0"
  private val pdfboxVersion = "2.0.26"
  private val openHtmlVersion = "1.0.10"
  private val playV = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"              %%  s"bootstrap-backend-$playV"     % bootstrapVersion,
    "uk.gov.hmrc"              %%  s"internal-auth-client-$playV"  % "3.0.0",
    "uk.gov.hmrc.mongo"        %%  s"hmrc-mongo-$playV"            % "2.5.0",
    "uk.gov.hmrc"              %%  s"play-frontend-hmrc-$playV"    % "11.11.0",
    "org.apache.pdfbox"        %   "pdfbox"                        % pdfboxVersion,
    "org.apache.pdfbox"        %   "xmpbox"                        % pdfboxVersion,
    "org.apache.xmlgraphics"   %   "batik-transcoder"              % "1.17",
    "org.apache.xmlgraphics"   %   "batik-codec"                   % "1.17",
    "com.openhtmltopdf"        %   "openhtmltopdf-core"            % openHtmlVersion,
    "com.openhtmltopdf"        %   "openhtmltopdf-pdfbox"          % openHtmlVersion,
    "com.openhtmltopdf"        %   "openhtmltopdf-svg-support"     % openHtmlVersion,
    "org.typelevel"            %%  "cats-core"                     % "2.10.0",
    "com.github.pathikrit"     %%  "better-files"                  % "3.9.2",
    "commons-codec"            %   "commons-codec"                 % "1.16.0",
    "org.scala-lang.modules"   %%  "scala-xml"                     % "2.1.0",
    "uk.gov.hmrc"              %%  "tax-year"                      % "5.0.0",
    "org.apache.santuario"     %   "xmlsec"                        % "3.0.3",
    "commons-io"               %   "commons-io"                    % "2.14.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test-$playV"     % bootstrapVersion            % "test, it",
    "org.scalamock"           %% "scalamock"                  % "5.2.0"                     % "test, it",
    "org.scalatestplus"       %% "scalacheck-1-15"            % "3.2.11.0"                  % "test, it",
    "org.scalacheck"          %% "scalacheck"                 % "1.17.0"                    % "test, it",
    "org.apache.pekko"        %% "pekko-testkit"              % "1.0.3"                     % Test
  )
}
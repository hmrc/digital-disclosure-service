import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.4.0"
  private val pdfboxVersion = "2.0.26"
  private val openHtmlVersion = "1.0.10"
  private val playV = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"              %%  s"bootstrap-backend-$playV"     % bootstrapVersion,
    "uk.gov.hmrc"              %%  s"internal-auth-client-$playV"  % "1.10.0",
    "uk.gov.hmrc.mongo"        %%  s"hmrc-mongo-$playV"            % "1.7.0",
    "uk.gov.hmrc"              %%  s"play-frontend-hmrc-$playV"    % "8.5.0",
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
    "com.thoughtworks.xstream" %   "xstream"                       % "1.4.20",
    "uk.gov.hmrc"              %%  "tax-year"                      % "3.2.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test-$playV"     % bootstrapVersion            % "test, it",
    "org.scalamock"           %% "scalamock"                  % "5.2.0"                     % "test, it",
    "org.scalatestplus"       %% "scalacheck-1-15"            % "3.2.11.0"                  % "test, it",
    "org.scalacheck"          %% "scalacheck"                 % "1.17.0"                    % "test, it",
    "org.apache.pekko"        %% "pekko-testkit"              % "1.0.2"                     % Test
  )
}

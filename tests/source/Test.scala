package uk.org.lidalia.exampleapp.tests.functional

import db.changelog.FastLiquibase
import uk.org.lidalia.exampleapp.local.EnvironmentDefinition
import uk.org.lidalia.exampleapp.system.HasLogger
import uk.org.lidalia.exampleapp.system.logging.JulConfigurer.sendJulToSlf4j
import uk.org.lidalia.exampleapp.tests.logging.Slf4jTestLoggerFactory

object Test extends HasLogger {

  sendJulToSlf4j()
  FastLiquibase()

  def main(args: Array[String]) {

    val start = System.currentTimeMillis()
    EnvironmentDefinition(List(None), Slf4jTestLoggerFactory()).using { environment =>
      log.info(s"Running a test against server on port ${environment.servers.head.localPort} $environment")
    }
    println("Done in "+(System.currentTimeMillis() - start))
  }
}

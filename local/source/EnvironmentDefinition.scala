package uk.org.lidalia
package exampleapp
package local

import org.slf4j.Logger
import server.application.ApplicationConfig
import server.services.profiles.userProfileTableCreation
import server.web.{ServerDefinition, ServerConfig}
import system.db.changelog.Migrator.changeLog
import system.db.hsqldb.{HsqlDatabase, HsqlDatabaseDefinition}
import system.blockUntilShutdown
import net.Port

import scalalang.ResourceFactory
import ResourceFactory.usingAll
import system.logging.{StaticLoggerFactory, LoggerFactory}

import uk.org.lidalia.stubhttp.StubHttpServerFactory

object EnvironmentDefinition {

  def apply(
    ports: List[?[Port]] = List(None),
    loggerFactory: LoggerFactory[Logger] = StaticLoggerFactory,
    stub1Definition: StubHttpServerFactory = StubHttpServerFactory(),
    databaseDefinition: ResourceFactory[HsqlDatabase] = HsqlDatabaseDefinition(changeLog(userProfileTableCreation))
  ) = {
    new EnvironmentDefinition(
      ports,
      loggerFactory,
      stub1Definition,
      databaseDefinition
    )
  }
}

class EnvironmentDefinition private (
  ports: List[?[Port]],
  loggerFactory: LoggerFactory[Logger],
  stub1Definition: StubHttpServerFactory,
  databaseDefinition: ResourceFactory[HsqlDatabase]
) extends ResourceFactory[Environment] {

  def runUntilShutdown(): Unit = {
    using(blockUntilShutdown)
  }

  override def using[T](work: (Environment) => T): T = {

    usingAll(
      stub1Definition,
      databaseDefinition
    ) { (stub1, database) =>

      database.update()

      val appConfig = ApplicationConfig(
        sendGridUrl = stub1.localAddress,
        sendGridToken = "secret_token",
        jdbcConfig = database.jdbcConfig
      )

      val serverDefinitions = ports.map(port => ServerDefinition(ServerConfig(appConfig, port), loggerFactory))

      usingAll(serverDefinitions:_*) { servers =>
        work(Environment(
          stub1,
          database,
          servers.toList
        ))
      }
    }
  }
}

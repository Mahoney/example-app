package uk.org.lidalia
package exampleapp.tests

import ch.qos.logback.classic.Level.INFO
import scalalang.{PoolFactory, ResourceFactory}
import support.{TestEnvironment, TestEnvironmentDefinition, TestSuites}
import uk.org.lidalia.exampleapp.tests.functional.RegisterTests

class FunctionalTests(
  envDefinition: ?[ResourceFactory[TestEnvironment]]
) extends TestSuites(envDefinition) {

  def this() = this(None)

  override def nestedSuites(factory: ResourceFactory[TestEnvironment]) = List(
    new RegisterTests(factory),
    new RegisterTests(factory)
  )

  override def metaFactory = PoolFactory(TestEnvironmentDefinition())

  override def logLevels = List(
    "uk.org.lidalia" -> INFO
  )
}

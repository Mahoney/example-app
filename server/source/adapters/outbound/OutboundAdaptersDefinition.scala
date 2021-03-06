package uk.org.lidalia
package exampleapp.server.adapters.outbound

import scalalang.{PoolFactory, ResourceFactory}
import uk.org.lidalia.exampleapp.server.adapters.outbound.email.HttpEmailService
import uk.org.lidalia.exampleapp.server.adapters.outbound.profiles.DbUserProfileRepository
import uk.org.lidalia.exampleapp.server.domain.OutboundAdapters
import uk.org.lidalia.exampleapp.system.db.Database

class OutboundAdaptersDefinition(config: OutboundAdaptersConfig) extends ResourceFactory[OutboundAdapters] {

  override def using[T](work: (OutboundAdapters) => T): T = {

    val database = Database(config.jdbcConfig)

    PoolFactory(database).using { database =>
      val adapters = OutboundAdapters(
        HttpEmailService(config.sendGridUrl, config.sendGridToken),
        DbUserProfileRepository(database)
      )
      work(adapters)
    }
  }

}

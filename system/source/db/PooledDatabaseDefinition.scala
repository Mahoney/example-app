package uk.org.lidalia
package exampleapp
package system.db

import com.zaxxer.hikari.HikariDataSource
import scalalang.ResourceFactory
import uk.org.lidalia.scalalang.TryFinally._try

object PooledDatabaseDefinition {

  def apply(jdbcConfig: JdbcConfig) = new PooledDatabaseDefinition(jdbcConfig)

}

class PooledDatabaseDefinition private (
  jdbcConfig: JdbcConfig
) extends ResourceFactory[Database] {

  override def using[T](work: (Database) => T): T = {

    val ds = new HikariDataSource()
    ds.setJdbcUrl(jdbcConfig.jdbcUrl.toString)
    ds.setUsername(jdbcConfig.username)
    ds.setPassword(jdbcConfig.password)
    ds.setConnectionTestQuery(jdbcConfig.checkQuery)

    val db = Database(ds)

    _try {
      work(db)
    } _finally {
      ds.close()
    }
  }
}

package config

import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.MySQLProfile.api._

object DBConfig {
  private val config: Config = ConfigFactory.parseResources("application.conf").resolve()
  //println(config.root().render())

  if (!config.hasPath("db")) {
    throw new RuntimeException("Error: DB config missing in application conf")
  }

  val db = Database.forConfig("db", config)

  /*val db = Database.forURL(
    url = config.getString("url"),
    user = config.getString("user"),
    password = config.getString("password"),
    driver = config.getString("driver")
  )*/
}

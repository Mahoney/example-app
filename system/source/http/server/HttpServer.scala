package uk.org.lidalia
package exampleapp.system.http.server

import uk.org.lidalia.http.core.{Http, Request, Response}

trait HttpServer[+Result[_]] extends Http[Result] {
  def executeServer[T](request: Request[_, T]): Result[_]
}
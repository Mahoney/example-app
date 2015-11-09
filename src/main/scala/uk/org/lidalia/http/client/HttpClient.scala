package uk.org.lidalia.http.client

import uk.org.lidalia.http.core.Request
import uk.org.lidalia.net2.Url

object HttpClient {
  def apply(baseUrl: Url) = ExpectedEntityHttpClient(baseUrl)
}

trait HttpClient[+Result[_]] {

  def execute[T](request: Request[T, _]): Result[T]
}

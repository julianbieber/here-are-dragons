package controllers

import com.twitter.finagle.http.Request
import dao.UserDAO
import javax.inject.Inject

import scala.concurrent.ExecutionContext

@Inject
class ExampleLoggedInController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext

  post("/example") { request: Request =>
    withUser(request) { userId =>
      response.ok(s"Do something wih user $userId")
    }
  }
}

package dao

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import javax.inject.Inject
import scalikejdbc._

class PositionDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def getPostion(userId:Int): Option[DAOPosition] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT long, lat FROM public.users WHERE lat = $name".map { col =>
        DAOPosition()
      }.first().apply()
    }
  }

  def setPosition(name: String): Unit = {

  }


  }

}


case class DAOPosition(user:userDAO) {
  private var lat: String;
  private var long: String;
}
object PositionDAO {
  private val getPosition = scala.collection.mutable.Map[Float, Float]()
}
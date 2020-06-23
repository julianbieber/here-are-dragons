package dao

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import io.github.nremond.SecureHash
import javax.inject.Inject
import model.Account.LoginResponse
import scalikejdbc._

import scala.collection.immutable.List

class UserDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def getListOfEveryUserId(): Seq[Int] = {
    withSession(pool) { implicit session =>
      val photoNodes: List[Int] =
      sql"""SELECT id FROM public.users """.map(rs =>
        rs.int("id")
      ).list.apply()
      photoNodes
    }
  }


  def createUser(name: String, password: String): Option[Int] = {
    withSession(pool) { implicit session =>
      val hash = SecureHash.createHash(password)
      sql"INSERT INTO public.users (name, hash) VALUES ($name, $hash)".executeUpdate().apply()
      sql"SELECT id FROM public.users WHERE name = $name".map { col =>
        col.int("id")
      }.first().apply()
    }
  }

  def getUser(name: String): Option[DAOUser] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, name, hash FROM public.users WHERE name = $name".map { col =>
        DAOUser(col.int("id"), col.string("name"), col.string("hash"))
      }.first().apply()
    }
  }

  def deleteUser(name: String): Unit = {
    withSession(pool) { implicit session =>
      sql"DELETE FROM public.users WHERE name = $name".execute().apply()
    }
  }

  def login(name: String, password: String): Option[LoginResponse] = {
    getUser(name).flatMap { user =>
      if (SecureHash.validatePassword(password, user.passwordHash)) {
        val token = UUID.randomUUID().toString + generateSecureCookie()
        UserDAO.loggedInUsers.synchronized {
          UserDAO.loggedInUsers.put(user.id, token)
        }
        Option(LoginResponse(id = user.id, token = token))
      } else {
        None
      }
    }
  }

  def logout(id: Int, token: String): Unit = {
    UserDAO.loggedInUsers.synchronized {
      if (isLoggedIn(id, token)) {
        UserDAO.loggedInUsers.remove(id)
      }
    }
  }

  def isLoggedIn(userId: Int, token: String): Boolean = {
    println(userId,token)
    println(UserDAO.loggedInUsers)
    UserDAO.loggedInUsers.get(userId).contains(token)
  }

  // #########################################################

  private val random = SecureRandom.getInstance("SHA1PRNG")

  private def generateSecureCookie(): String = {
    val bytes = Array.fill(32)(0.byteValue)
    random.nextBytes(bytes)
    sha1(bytes)
  }

  private def sha1(bytes: Array[Byte]): String = digest(bytes, MessageDigest.getInstance("SHA1"))

  private def digest(bytes: Array[Byte], md: MessageDigest): String = {
    md.update(bytes)
    hexify(md.digest)
  }

  private def hexify(bytes: Array[Byte]): String = {
    val hex = "0123456789ABCDEF"
    val builder = new java.lang.StringBuilder(bytes.length * 2)
    bytes.foreach { byte => builder.append(hex.charAt((byte & 0xF0) >> 4)).append(hex.charAt(byte & 0xF)) }
    builder.toString
  }

}

object UserDAO {
  private val loggedInUsers = scala.collection.mutable.Map[Int, String]()
  loggedInUsers.put(0, "DEBUG")
}

case class DAOUser(id: Int, name: String, passwordHash: String)
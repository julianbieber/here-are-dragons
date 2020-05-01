package dao

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import io.github.nremond.SecureHash
import scalikejdbc._

class UserDAO(val pool: ConnectionPool) extends SQLUtil {

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

  def login(name: String, password: String): Option[String] = {
    getUser(name).flatMap { user =>
      if (SecureHash.validatePassword(password, user.passwordHash)) {
        val token = UUID.randomUUID().toString + generateSecureCookie()
        loggedInUsers.synchronized {
          loggedInUsers.put(name, token)
        }
        Option(token)
      } else {
        None
      }
    }
  }

  def logout(name: String, token: String): Unit = {
    loggedInUsers.synchronized {
      if (isLoggedIn(name, token)) {
        loggedInUsers.remove(name)
      }
    }
  }

  def isLoggedIn(name: String, token: String): Boolean = loggedInUsers.get(name).contains(token)

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

  private val loggedInUsers = scala.collection.mutable.Map[String, String]()

}

case class DAOUser(id: Int, name: String, passwordHash: String)
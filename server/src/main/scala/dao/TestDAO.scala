package dao
import scalikejdbc._

class TestDAO(val session: AutoSession) {
  def insert(lat: Float, long: Float): Unit = {
    sql"insert into public.gps (lat, long) values ($lat, $long)".update().apply()(session)
  }
}

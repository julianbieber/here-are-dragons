import background.{BackgroundExecutor, Experience, QuestUpdater}
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}
import com.google.inject.Module
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import controllers.{ActivityController, CharacterController, DungeonController, ExampleLoggedInController, GroupController, PositionController, QuestController, UserController}
import dao.{ActivityDAO, ExperienceDAO,QuestDAO,PositionDAO,UserDAO,PoIDAO}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext


object Api extends HttpServer {
  val dataSource: DataSource = {
    val ds = new HikariDataSource()
    ds.setDataSourceClassName("org.postgresql.ds.PGPoolingDataSource")
    ds.addDataSourceProperty("url", "jdbc:postgresql://db:5435/postgres")
    ds.addDataSourceProperty("user", "postgres")
    ds.addDataSourceProperty("password", "example")
    ds
  }
  val pool = new DataSourceConnectionPool(dataSource)
  override val modules: Seq[Module] = Seq(new ScalaModule {
    override def configure(): Unit = {
      bind[ExecutionContext].toInstance(ExecutionContext.global)
      bind[ConnectionPool].toInstance(pool)
    }
  })

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[UserController]
      .add[ExampleLoggedInController]
      .add[GroupController]
      .add[ActivityController]
      .add[PositionController]
      .add[QuestController]
      .add[CharacterController]
      .add[DungeonController]
  }

  val experienceJob = new BackgroundExecutor(new Experience(new ActivityDAO(pool), new ExperienceDAO(pool)))
  val QuestJob = new BackgroundExecutor(new QuestUpdater(new PoIDAO(pool),new QuestDAO(pool), new PositionDAO(pool),new UserDAO(pool)))
}
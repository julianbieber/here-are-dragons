import background.character.Activity
import background.{BackgroundExecutor, PoIUpdater, QuestUpdater}
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}
import com.google.inject.Module
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import controllers.{ActivityController, CharacterController, DifficultyController, DungeonController, ExampleLoggedInController, GroupController, PositionController, QuestController, TalentController, UserController}
import dao.{ActivityDAO, CalisthenicsDAO, ExperienceDAO, PoIDAO, PositionDAO, QuestDAO, TalentDAO, TalentUnlockDAO, UserDAO}
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
      bind[String].annotatedWithName("classifierUrl").toInstance("calisthenis:5001/classify")
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
      .add[TalentController]
      .add[DifficultyController]
  }

  /*val experienceJob = new BackgroundExecutor(new Activity(new ActivityDAO(pool), new ExperienceDAO(pool), new PositionDAO(pool), new TalentUnlockDAO(pool), new TalentDAO(pool), new CalisthenicsDAO(pool)),10000)
  val QuestJob = new BackgroundExecutor(new QuestUpdater(new PoIDAO(pool),new QuestDAO(pool), new PositionDAO(pool),new UserDAO(pool)),10000)
  val PoIJob = new BackgroundExecutor(new PoIUpdater(new PoIDAO(pool),new QuestDAO(pool), new PositionDAO(pool),new UserDAO(pool)),10000)*/
}
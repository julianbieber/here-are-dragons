package service

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import dao.{CalisthenicsDAO, CalisthenicsRow}
import javax.inject.{Inject, Named}
import model.Activity.ClassifierResult
import sttp.client._
import sttp.model.Uri.QuerySegment
import util.TimeUtil

class CalisthenicsService @Inject() (calisthenicsDAO: CalisthenicsDAO, @Named("classifierUrl") classifierUrl: String) {

  private implicit val classifierResultCodec = JsonCodecMaker.make[ClassifierResult]

  private implicit val backend = HttpURLConnectionBackend()

  def record(user: Int, vector: Seq[Float]): Unit = {
    classify(vector).foreach{ calisthenicsType =>
      calisthenicsDAO.store(CalisthenicsRow(
        user,
        calisthenicsType,
        vector,
        TimeUtil.now,
        false
      ))
    }
  }

  private def classify(vector: Seq[Float]): Option[Int] = {
    if (vector.length != 90) {
      None
    } else {
      val request = basicRequest.get(uri"$classifierUrl".querySegment(QuerySegment.KeyValue("data", vector.mkString(","))))
      request.send().body match {
        case Left(value) =>
          println(s"error occured during classification of calisthenics $value")
          None
        case Right(value) =>
          val classifierResult: ClassifierResult = readFromString(value)

          if (classifierResult.push > 0.75) {
            Option(CalisthenicsService.push)
          } else if (classifierResult.pull > 0.75) {
            Option(CalisthenicsService.pull)
          } else {
            None
          }
      }

    }
  }

}

object CalisthenicsService {
  val push = 3
  val pull = 4
}


package background

import javax.inject.Inject


class QuestUpdater @Inject()(val poIDAO: dao.PoIDAO, val questdao: dao.QuestDAO, val positiondao: dao.PositionDAO, val userdao: dao.UserDAO) extends Background {


  override def run(): Unit = {

    val listOfUsers: Seq[Int] = userdao.getListOfEveryUserId()

    for (user <- listOfUsers) {
      positiondao.getPosition(user).map {
        pos => {
          val poisCloseToUser = poIDAO.getPoIs(pos.longitude, pos.latitude)
          questdao.fillDatabaseFromPoIs(poisCloseToUser, user)
        }
      }
    }
  }
}


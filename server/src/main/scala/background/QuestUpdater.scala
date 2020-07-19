package background

import javax.inject.Inject


class QuestUpdater @Inject()(val poIDAO: dao.PoIDAO, val questdao: dao.QuestDAO, val positiondao: dao.PositionDAO, val userdao: dao.UserDAO) extends Background {

  var lastExchange : Int = 0

  override def run(): Unit = {

    val listOfUsers: Seq[Int] = userdao.getListOfEveryUserId()

    for (user <- listOfUsers) {

      positiondao.getPosition(user).map {
        pos => {
          val oldest = questdao.getOldest(user)
          if(lastExchange == 100 && oldest.isDefined){
            questdao.deleteQuest(oldest.get)
            lastExchange = 0
          }
          val poisCloseToUser = poIDAO.getPoIs(pos.longitude, pos.latitude)
          questdao.fillDatabaseFromPoIs(poisCloseToUser, user)
        }
      }
    }
    lastExchange += 1
  }
}


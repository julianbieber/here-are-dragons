package service

case class Status(
  var burning: Int,
  var wet: Int,
  var stunned:Int,
  var shocked: Int,
  var knockedDown: Int
) {
  def countDown(): Unit = {
    burning = math.max(burning-1, 0)
    wet = math.max(wet-1, 0)
    stunned = math.max(stunned-1, 0)
    shocked = math.max(shocked-1, 0)
    knockedDown = math.max(knockedDown-1, 0)
  }

  def add(status: Status): Unit = {
    if (status.burning > 0) {
      setOnFire(status.burning)
    }
    if (status.wet > 0) {
      drowse(status.wet)
    }
    if (status.stunned > 0) {
      stun(status.stunned)
    }
    if (status.shocked > 0) {
      shock(status.shocked)
    }
    if (status.knockedDown > 0) {
      knockDown(status.knockedDown)
    }
  }

  private def setOnFire(duration: Int): Unit = {
    burning = math.max(burning, duration)
    wet = 0
  }

  private def drowse(duration: Int): Unit = {
    wet = math.max(wet, duration)
    burning = 0
  }

  private def stun(duration: Int): Unit = {
    stunned = math.max(stunned, duration)
    shocked = 0
  }

  private def shock(duration: Int): Unit = {
    if (shocked > 0 || wet > 0) {
      stunned = math.max(math.max(stunned, duration), shocked)
    } else {
      shocked = duration
    }
  }

  private def knockDown(duration: Int): Unit = {
    knockedDown = math.max(knockedDown, duration)
  }

  def calculateAPGain(apGain: Int): Int = {
    if (stunned > 0 || knockedDown > 0) {
      -1000
    } else if (shocked > 0) {
      apGain / 2
    } else {
      apGain
    }
  }

  def locationBased: Status = copy(stunned = 0, shocked = 0, knockedDown = 0)

}

object Status {
  def empty: Status = Status(0, 0, 0, 0, 0)
}
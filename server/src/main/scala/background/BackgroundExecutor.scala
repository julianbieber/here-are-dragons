package background

import scala.util.control.NonFatal

class BackgroundExecutor(background: Background, interval : Long) {
  val t = new Thread(() => {
    while (true) {
      Thread.sleep(interval)
      try {
        background.run()
      } catch {
        case NonFatal(e) =>
          e.printStackTrace()
      }
    }
  })

  t.setDaemon(true)
  t.start()
}

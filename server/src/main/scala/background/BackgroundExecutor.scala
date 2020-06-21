package background

import scala.util.control.NonFatal

class BackgroundExecutor(background: Background) {
  val t = new Thread(() => {
    while (true) {
      Thread.sleep(10000)
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

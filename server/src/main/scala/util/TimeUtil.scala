package util

import org.joda.time.{DateTime, DateTimeZone}

object TimeUtil {
  def now: DateTime = DateTime.now(DateTimeZone.UTC)
}

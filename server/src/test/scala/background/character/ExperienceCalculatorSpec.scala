package background.character

import org.joda.time.Period
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import util.TimeUtil

class ExperienceCalculatorSpec extends AnyFlatSpec with Matchers with MockFactory  {
  "ExperienceCalculator" must "" in {
    ExperienceCalculator.forActivity(2, new Period(TimeUtil.now, TimeUtil.now.plusMinutes(2)), 10) must be(12)

  }
}

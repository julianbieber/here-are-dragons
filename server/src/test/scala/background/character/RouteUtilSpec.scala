package background.character

import dao.DAOPosition
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class RouteUtilSpec extends AnyFlatSpec with Matchers with MockFactory  {
  "RouteUtil" must "" in {
    val dateFormat = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss")
    val data = Seq(
    DAOPosition(2, 8.627042770385742.toFloat, 49.66950225830078.toFloat,DateTime.parse("2020-09-14 16:53:28", dateFormat)),
    DAOPosition(2, 8.627069473266602.toFloat, 49.66945266723633.toFloat,DateTime.parse("2020-09-14 16:53:30", dateFormat)),
    DAOPosition(2, 8.627062797546387.toFloat, 49.66944885253906.toFloat,DateTime.parse("2020-09-14 16:53:31", dateFormat)),
    DAOPosition(2, 8.627042770385742.toFloat, 49.66946792602539.toFloat,DateTime.parse("2020-09-14 16:53:33", dateFormat)),
    DAOPosition(2, 8.627023696899414.toFloat, 49.669490814208984.toFloat,DateTime.parse("2020-09-14 16:53:34", dateFormat)),
    DAOPosition(2, 8.626998901367188.toFloat, 49.669525146484375.toFloat,DateTime.parse("2020-09-14 16:53:35", dateFormat)),
    DAOPosition(2, 8.626973152160645.toFloat, 49.669559478759766.toFloat,DateTime.parse("2020-09-14 16:53:36", dateFormat)),
    DAOPosition(2, 8.626940727233887.toFloat, 49.66960525512695.toFloat,DateTime.parse("2020-09-14 16:53:37", dateFormat)),
    DAOPosition(2, 8.626879692077637.toFloat, 49.6696891784668.toFloat,DateTime.parse("2020-09-14 16:53:39", dateFormat)),
    DAOPosition(2, 8.626625061035156.toFloat, 49.66999816894531.toFloat,DateTime.parse("2020-09-14 16:53:40", dateFormat)),
    DAOPosition(2, 8.6265287399292.toFloat, 49.67010498046875.toFloat,DateTime.parse("2020-09-14 16:53:41", dateFormat)),
    DAOPosition(2, 8.62645149230957.toFloat, 49.670188903808594.toFloat,DateTime.parse("2020-09-14 16:53:42", dateFormat)),
    DAOPosition(2, 8.626388549804688.toFloat, 49.67026138305664.toFloat,DateTime.parse("2020-09-14 16:53:43", dateFormat)),
    DAOPosition(2, 8.626272201538086.toFloat, 49.670379638671875.toFloat,DateTime.parse("2020-09-14 16:53:45", dateFormat)),
    DAOPosition(2, 8.626174926757812.toFloat, 49.670494079589844.toFloat,DateTime.parse("2020-09-14 16:53:47", dateFormat)),
    DAOPosition(2, 8.626128196716309.toFloat, 49.67054748535156.toFloat,DateTime.parse("2020-09-14 16:53:48", dateFormat)),
    DAOPosition(2, 8.626086235046387.toFloat, 49.670597076416016.toFloat,DateTime.parse("2020-09-14 16:53:49", dateFormat)),
    DAOPosition(2, 8.6260404586792.toFloat, 49.670650482177734.toFloat,DateTime.parse("2020-09-14 16:53:50", dateFormat)),
    DAOPosition(2, 8.625935554504395.toFloat, 49.67076110839844.toFloat,DateTime.parse("2020-09-14 16:53:52", dateFormat)),
    DAOPosition(2, 8.625883102416992.toFloat, 49.67081832885742.toFloat,DateTime.parse("2020-09-14 16:53:53", dateFormat)),
    DAOPosition(2, 8.62583065032959.toFloat, 49.67087173461914.toFloat,DateTime.parse("2020-09-14 16:53:54", dateFormat)),
    DAOPosition(2, 8.625785827636719.toFloat, 49.67092514038086.toFloat,DateTime.parse("2020-09-14 16:53:55", dateFormat)),
    DAOPosition(2, 8.625697135925293.toFloat, 49.671043395996094.toFloat,DateTime.parse("2020-09-14 16:53:57", dateFormat)),
    DAOPosition(2, 8.625658988952637.toFloat, 49.67109680175781.toFloat,DateTime.parse("2020-09-14 16:53:58", dateFormat)),
    DAOPosition(2, 8.625597953796387.toFloat, 49.67117691040039.toFloat,DateTime.parse("2020-09-14 16:54:00", dateFormat)),
    DAOPosition(2, 8.625568389892578.toFloat, 49.67121887207031.toFloat,DateTime.parse("2020-09-14 16:54:01", dateFormat)),
    DAOPosition(2, 8.625506401062012.toFloat, 49.671302795410156.toFloat,DateTime.parse("2020-09-14 16:54:03", dateFormat)),
    DAOPosition(2, 8.625480651855469.toFloat, 49.671348571777344.toFloat,DateTime.parse("2020-09-14 16:54:04", dateFormat)),
    DAOPosition(2, 8.6254301071167.toFloat, 49.67143249511719.toFloat,DateTime.parse("2020-09-14 16:54:06", dateFormat)),
    DAOPosition(2, 8.625377655029297.toFloat, 49.6715202331543.toFloat,DateTime.parse("2020-09-14 16:54:08", dateFormat)),
    DAOPosition(2, 8.625349998474121.toFloat, 49.67156982421875.toFloat,DateTime.parse("2020-09-14 16:54:09", dateFormat)),
    DAOPosition(2, 8.62531852722168.toFloat, 49.67161560058594.toFloat,DateTime.parse("2020-09-14 16:54:10", dateFormat)),
    DAOPosition(2, 8.62525463104248.toFloat, 49.67171096801758.toFloat,DateTime.parse("2020-09-14 16:54:12", dateFormat)),
    DAOPosition(2, 8.625204086303711.toFloat, 49.671810150146484.toFloat,DateTime.parse("2020-09-14 16:54:14", dateFormat)),
    DAOPosition(2, 8.625149726867676.toFloat, 49.67190170288086.toFloat,DateTime.parse("2020-09-14 16:54:16", dateFormat)),
    DAOPosition(2, 8.625102043151855.toFloat, 49.6719970703125.toFloat,DateTime.parse("2020-09-14 16:54:18", dateFormat)),
    DAOPosition(2, 8.625053405761719.toFloat, 49.672096252441406.toFloat,DateTime.parse("2020-09-14 16:54:20", dateFormat)),
    DAOPosition(2, 8.624996185302734.toFloat, 49.672210693359375.toFloat,DateTime.parse("2020-09-14 16:54:22", dateFormat)),
    DAOPosition(2, 8.62493896484375.toFloat, 49.67232131958008.toFloat,DateTime.parse("2020-09-14 16:54:24", dateFormat)),
    DAOPosition(2, 8.624911308288574.toFloat, 49.67238235473633.toFloat,DateTime.parse("2020-09-14 16:54:25", dateFormat)),
    DAOPosition(2, 8.624887466430664.toFloat, 49.67243957519531.toFloat,DateTime.parse("2020-09-14 16:54:26", dateFormat)),
    DAOPosition(2, 8.624835968017578.toFloat, 49.672542572021484.toFloat,DateTime.parse("2020-09-14 16:54:28", dateFormat)),
    DAOPosition(2, 8.624809265136719.toFloat, 49.67259979248047.toFloat,DateTime.parse("2020-09-14 16:54:29", dateFormat)),
    DAOPosition(2, 8.624783515930176.toFloat, 49.67264938354492.toFloat,DateTime.parse("2020-09-14 16:54:30", dateFormat)),
    DAOPosition(2, 8.62474536895752.toFloat, 49.6727409362793.toFloat,DateTime.parse("2020-09-14 16:54:32", dateFormat)),
    DAOPosition(2, 8.624688148498535.toFloat, 49.672828674316406.toFloat,DateTime.parse("2020-09-14 16:54:34", dateFormat)),
    DAOPosition(2, 8.624651908874512.toFloat, 49.67288589477539.toFloat,DateTime.parse("2020-09-14 16:54:35", dateFormat)),
    DAOPosition(2, 8.624588966369629.toFloat, 49.67298889160156.toFloat,DateTime.parse("2020-09-14 16:54:37", dateFormat)),
    DAOPosition(2, 8.624536514282227.toFloat, 49.6730842590332.toFloat,DateTime.parse("2020-09-14 16:54:39", dateFormat)),
    DAOPosition(2, 8.624485969543457.toFloat, 49.673179626464844.toFloat,DateTime.parse("2020-09-14 16:54:41", dateFormat)),
    DAOPosition(2, 8.624471664428711.toFloat, 49.6732063293457.toFloat,DateTime.parse("2020-09-14 16:54:42", dateFormat)),
    DAOPosition(2, 8.62445068359375.toFloat, 49.67325210571289.toFloat,DateTime.parse("2020-09-14 16:54:43", dateFormat)),
    DAOPosition(2, 8.62437915802002.toFloat, 49.67334747314453.toFloat,DateTime.parse("2020-09-14 16:54:45", dateFormat)),
    DAOPosition(2, 8.624341011047363.toFloat, 49.67340087890625.toFloat,DateTime.parse("2020-09-14 16:54:46", dateFormat)),
    DAOPosition(2, 8.624293327331543.toFloat, 49.673492431640625.toFloat,DateTime.parse("2020-09-14 16:54:48", dateFormat)),
    DAOPosition(2, 8.624237060546875.toFloat, 49.673587799072266.toFloat,DateTime.parse("2020-09-14 16:54:50", dateFormat)),
    DAOPosition(2, 8.62416934967041.toFloat, 49.67369079589844.toFloat,DateTime.parse("2020-09-14 16:54:52", dateFormat)),
    DAOPosition(2, 8.624135971069336.toFloat, 49.673744201660156.toFloat,DateTime.parse("2020-09-14 16:54:53", dateFormat)),
    DAOPosition(2, 8.62409782409668.toFloat, 49.67379379272461.toFloat,DateTime.parse("2020-09-14 16:54:54", dateFormat)),
    DAOPosition(2, 8.623980522155762.toFloat, 49.673946380615234.toFloat,DateTime.parse("2020-09-14 16:54:56", dateFormat)),
    DAOPosition(2, 8.62392807006836.toFloat, 49.67401885986328.toFloat,DateTime.parse("2020-09-14 16:54:58", dateFormat)),
    DAOPosition(2, 8.62389087677002.toFloat, 49.67406463623047.toFloat,DateTime.parse("2020-09-14 16:54:59", dateFormat)),
    DAOPosition(2, 8.62381649017334.toFloat, 49.67417526245117.toFloat,DateTime.parse("2020-09-14 16:55:01", dateFormat)),
    DAOPosition(2, 8.623772621154785.toFloat, 49.67422866821289.toFloat,DateTime.parse("2020-09-14 16:55:02", dateFormat)),
    DAOPosition(2, 8.623730659484863.toFloat, 49.67428207397461.toFloat,DateTime.parse("2020-09-14 16:55:03", dateFormat)),
    DAOPosition(2, 8.62364673614502.toFloat, 49.67438888549805.toFloat,DateTime.parse("2020-09-14 16:55:05", dateFormat)),
    DAOPosition(2, 8.623605728149414.toFloat, 49.674434661865234.toFloat,DateTime.parse("2020-09-14 16:55:06", dateFormat)),
    DAOPosition(2, 8.623568534851074.toFloat, 49.67448806762695.toFloat,DateTime.parse("2020-09-14 16:55:07", dateFormat)),
    DAOPosition(2, 8.623489379882812.toFloat, 49.67458724975586.toFloat,DateTime.parse("2020-09-14 16:55:09", dateFormat)),
    DAOPosition(2, 8.623452186584473.toFloat, 49.67464065551758.toFloat,DateTime.parse("2020-09-14 16:55:10", dateFormat)),
    DAOPosition(2, 8.623406410217285.toFloat, 49.67469024658203.toFloat,DateTime.parse("2020-09-14 16:55:11", dateFormat)),
    DAOPosition(2, 8.623316764831543.toFloat, 49.67478942871094.toFloat,DateTime.parse("2020-09-14 16:55:13", dateFormat)),
    DAOPosition(2, 8.623272895812988.toFloat, 49.67483901977539.toFloat,DateTime.parse("2020-09-14 16:55:14", dateFormat)),
    DAOPosition(2, 8.623186111450195.toFloat, 49.67493438720703.toFloat,DateTime.parse("2020-09-14 16:55:16", dateFormat)),
    DAOPosition(2, 8.623146057128906.toFloat, 49.67498779296875.toFloat,DateTime.parse("2020-09-14 16:55:17", dateFormat)),
    DAOPosition(2, 8.62310791015625.toFloat, 49.6750373840332.toFloat,DateTime.parse("2020-09-14 16:55:18", dateFormat)),
    DAOPosition(2, 8.623022079467773.toFloat, 49.675140380859375.toFloat,DateTime.parse("2020-09-14 16:55:20", dateFormat)),
    DAOPosition(2, 8.622977256774902.toFloat, 49.675193786621094.toFloat,DateTime.parse("2020-09-14 16:55:21", dateFormat)),
    DAOPosition(2, 8.622933387756348.toFloat, 49.67524719238281.toFloat,DateTime.parse("2020-09-14 16:55:22", dateFormat)),
    DAOPosition(2, 8.622901916503906.toFloat, 49.67530059814453.toFloat,DateTime.parse("2020-09-14 16:55:23", dateFormat)),
    DAOPosition(2, 8.622840881347656.toFloat, 49.67540740966797.toFloat,DateTime.parse("2020-09-14 16:55:25", dateFormat)),
    DAOPosition(2, 8.622785568237305.toFloat, 49.67551803588867.toFloat,DateTime.parse("2020-09-14 16:55:27", dateFormat)),
    DAOPosition(2, 8.62274169921875.toFloat, 49.67564010620117.toFloat,DateTime.parse("2020-09-14 16:55:29", dateFormat))
    )
    RouteUtil.averageSpeed(data) must be >= 22.0
    RouteUtil.averageSpeed(data) must be <= 23.0
    RouteUtil.getMaxSpeedForDistance(100, data) must be >= 53.0
    RouteUtil.getMaxSpeedForDistance(100, data) must be <= 54.0

  }
}

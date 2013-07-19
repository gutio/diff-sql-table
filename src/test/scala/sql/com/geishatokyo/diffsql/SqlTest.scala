import com.geishatokyo.diffsql

import com.geishatokyo.diffsql.SqlParser

import org.scalatest._
import org.scalatest.matchers._

import scala.slick.driver.MySQLDriver.simple._

object Coffees extends Table[(String, Int, Double)]("COFFEES") {
  def name = column[String]("COF_NAME", O.PrimaryKey)
  def supID = column[Int]("SUP_ID")
  def price = column[Double]("PRICE")
  def * = name ~ supID ~ price
}

object Fake extends Table[(String, String, Float)]("COFFEES") {
  def name = column[String]("COFNAME", O.PrimaryKey)
  def supID = column[String]("SUP_ID")
  def price = column[Float]("PRICE")
  def * = name ~ supID ~ price
}

class ParserSpec extends FlatSpec with ShouldMatchers with Samples { self =>

  val mysql = """CREATE TABLE `coffees` (
  `COF_NAME` varchar(254) NOT NULL,
  `SUP_ID` int(11) NOT NULL,
  `PRICE` double NOT NULL,
  PRIMARY KEY (`COF_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1"""

  val sqlite = """CREATE TABLE t1(
t  TEXT,
nu NUMERIC,
i  INTEGER,
r  REAL,
no BLOB
)"""

  val slick = Coffees.ddl.createStatements.mkString

  val fake = Fake.ddl.createStatements.mkString

  "parser" should "succeed in parsing sql" in {
    def assert(sql: String) = {
      val result = SqlParser.parseSql(sql)
      self.assert(result.successful, result)
    }
    assert(slick)
    assert(mysql)
    assert(sqlite)
    assert(sample1)
    assert(sample2)
    assert(sample3)
    assert(sample4)
    assert(sample5)
  }

  "difference of sqls" should "be only option" in {
    val result = SqlParser.diff(slick, mysql)
    assert(result.isSuccess, result)
    assert(result.get.add.isEmpty, result)
    assert(result.get.drop.isEmpty, result)
    import SqlParser.TableOption._
    assert(result.get.options.contains(Engine.Value("InnoDB")), result)
    assert(result.get.options.contains(Charset.Value("latin1")), result)
  }

  "difference of sqls" should "be name and supid" in {
    val result = SqlParser.diff(slick, fake)
    assert(result.isSuccess, result)
    assert(result.get.add.size === 3, result)
    assert(result.get.drop.size === 3, result)
  }

}

trait Samples {

  val sample1 = """CREATE TABLE `musicinfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `musicHash` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
  `artist` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
  `duration` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `KEY_MusicInfo_musicHash` (`musicHash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8"""

  val sample2 = """CREATE TABLE `friendlink` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ownerId` bigint(20) NOT NULL,
  `friendId` bigint(20) NOT NULL,
  `status` int(11) NOT NULL,
  `friendship` int(11) NOT NULL,
  `updated` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8
"""

  val sample3 = """CREATE TABLE `itemdata` (
`itemId` bigint(20) NOT NULL,
`itemType` int(11) NOT NULL,
`name` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
`thumbnail` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
`explanation` longtext CHARACTER SET utf8mb4 NOT NULL,
`created` datetime NOT NULL,
`updated` datetime NOT NULL,
PRIMARY KEY (`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8"""

  val sample4 = """
CREATE TABLE `friendlink` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`ownerId` bigint(20) NOT NULL,
`friendId` bigint(20) NOT NULL,
`status` int(11) NOT NULL,
`friendship` int(11) NOT NULL,
`updated` datetime NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8
"""

  val sample5 = """CREATE TABLE `musicinfo` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`musicHash` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
`title` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
`artist` varchar(100) CHARACTER SET utf8mb4 NOT NULL,
`duration` double NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `KEY_MusicInfo_musicHash` (`musicHash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
"""

}

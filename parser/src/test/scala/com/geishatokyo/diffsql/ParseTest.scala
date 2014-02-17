package com.geishatokyo.diffsql

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

/**
 * Created by takeshita on 14/02/18.
 */
class ParseTest extends FlatSpec with ShouldMatchers {

  "test" should "be" in {

    val builder = new DiffSQLBuilder()
    val sqlDiff = builder.build()

    val sqls = sqlDiff.diff(afterSQL,beforeSQL)

    println(sqls)

    /*assert(sqls === List("""CREATE TABLE IF NOT EXISTS UserDesc (
  id INT ,
  aaa INT
);""",
      """ALTER TABLE User ADD COLUMN name VARCHAR(100) ;""",
      """ALTER TABLE User DROP COLUMN age;""",
      """ALTER TABLE User ADD  KEY a  (name,gender) ;"""))*/
  }

  val afterSQL =
    """
      |CREATE TABLE User(
      |  id INT AUTO_INCREMENT PRIMARY KEY,
      |  name VARCHAR(100),
      |  gender Int
      |);
      |
      |CREATE INDEX a on user (name,gender);
      |
      |CREATE TABLE UserDesc(
      |  id INT,
      |  aaa Int
      |);
      |
    """.stripMargin

  val beforeSQL =
    """
      |
      |CREATE TABLE User(
      |  id INT AUTO_INCREMENT PRIMARY KEY,
      |  age INT,
      |  gender Int
      |);
      |
      |
      |CREATE TABLE Hoge(
      |  id INT
      |);
      |
      |
    """.stripMargin

}
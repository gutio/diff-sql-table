package com.geishatokyo.diffsql

import scala.language.implicitConversions

/**
 * Created by takeshita on 14/02/17.
 */

case class Name(name: String) {
  override def equals(x: Any) = x match {
    case n: Name => name.equalsIgnoreCase(n.name)
    case str : String => name.equalsIgnoreCase(str)
    case _ => false
  }
  override def hashCode = name.toLowerCase.hashCode
  override def toString = name

}
object Name {
  implicit def nameToString(name: Name): String = name.name
  implicit def stringToName(string: String): Name = Name(string)
}

trait Definition

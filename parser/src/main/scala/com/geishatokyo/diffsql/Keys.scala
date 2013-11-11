package com.geishatokyo.diffsql

trait Keys { self: SqlParser =>

  abstract class Key(name: String, columns: Seq[Name], index: Option[Name] = None) extends Definition {
    override def toString =
      name + " " + index.getOrElse("") + " " + columns.mkString(" ")
  }
  
  case class CreateKey(tableName : String,indexName : String ,columns : Seq[Name],unique : Boolean) extends CreateDefinition{
    def toKeyInTableDef = if(unique){
      Key.Unique(Some(indexName),columns)
    }else{
      Key.Index(Some(indexName),columns)
    }
  
  }

  object Key {

    abstract class Parser(p: self.Parser[Any]) extends SelfParser[Key] {
      val parser = p ~> opt(value) ~ Apply(repsep(value, ",".r)) ^^ {
        case name ~ columns =>
          apply(name.map(Name.apply), columns.map(Name.apply).toSeq)
      }
      def apply(index: Option[Name], columns: Seq[Name]): Key
    }
    

    case class Primary(columns: Seq[Name]) extends Key("PRIMARY KEY", columns)
    case object Primary extends Parser("PRIMARY".i ~ "KEY".i) {
      def apply(index: Option[Name], columns: Seq[Name]) = Primary(columns)
    }

    case class Unique(index: Option[Name], columns: Seq[Name]) extends Key("UNIQUE KEY", columns, index)
    case object Unique extends Parser("UNIQUE".i ~ "KEY".i)

    case class Index(index: Option[Name], columns: Seq[Name]) extends Key("KEY", columns, index)
    case object Index extends Parser("KEY".i | "INDEX".i)

  }
  
  object CreateKey{
    object CreateIndex extends SelfParser[CreateKey] {
      val parser = ("CREATE".i ~> opt("UNIQUE".i) <~ "INDEX".i) ~ value ~ ("ON".i ~> value) ~ ("(" ~> repsep(value,",") <~ ")") <~ opt(";") ^^ {
        case isUnique ~ indexName ~ tableName ~ columns => {
          CreateKey(tableName,indexName,columns.map(Name.apply),isUnique.isDefined)
        }
      }
    }
    
  }

}

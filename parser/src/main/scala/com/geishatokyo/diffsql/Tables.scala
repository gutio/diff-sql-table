package com.geishatokyo.diffsql

trait Tables { self: SqlParser =>

  case class Table(name: Name, columns: Set[Definition], options: Set[TableOption]) { self =>
    def create = new Result {
      val name = self.name
      override def toString = columns.mkString("CREATE TABLE " + name + " ( \n  ", ",\n  ", "\n);")
    }
    def drop = new Result {
      val name = self.name
      override def toString = "DROP TABLE " + name + ";"
    }
  }

  object Table extends SelfParser[Table] {
    def expand(definitions: Set[Definition]) = definitions.flatMap {
      case column@Column(name, _, options) =>
        options.collect {
          case key: ColumnOption.Key => key.create(name)
        } + column.copy(options = options.filter {
          case _: ColumnOption.Key => false
          case _ => true
        })
      case definition =>
        Set(definition)
    }
    val parser =
      "CREATE".i ~ "TABLE".i ~ opt("IF".i ~ "NOT".i ~ "EXISTS".i) ~>
      value ~ Apply(repsep(dinition, ",".r)) ~ rep(tableOption) <~
      opt(";".r) ^^ {
        case name ~ defs ~ opts => Table(name, expand(defs.toSet), opts.toSet)
      }
  }

}
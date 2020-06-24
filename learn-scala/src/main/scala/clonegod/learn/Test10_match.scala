package clonegod.learn

object Test10_match {

  def main(args: Array[String]): Unit = {
    val tp = (1, 2, "abc")
    tp.productIterator.foreach(testMatch)

    println(partialSwitch("a"))
    println(partialSwitch("b"))
    println(partialSwitch("c"))
  }

  // switch case, 匹配到一个之后就结束，_匹配所有情况
  def testMatch(v: Any) {
    v match {
      case 1 => println("value is 1")
      case i: Int => println(s"type is Int, value is $i")
      case _ => println("not match")
    }
  }

  // 偏函数，只能对同一种类型进行检测，并返回结果
  def partialSwitch: PartialFunction[String, Int] = {
    case "a" => 1
    case "b" => 2
    case _ => -1
  }

}
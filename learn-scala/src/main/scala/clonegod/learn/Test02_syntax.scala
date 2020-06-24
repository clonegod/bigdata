package clonegod.learn

object Test02_syntax {
  def main(args: Array[String]): Unit = {
    testIf(10)

    testFor()


  }

  def testIf(n: Int): Unit = {
    println("=========== test if else ===================")
    if (n < 10) {
      println("n < 10")
    } else if (n == 10) {
      println("n == 10")
    } else {
      println("n > 10")
    }
  }

  def testFor(): Unit = {
    println("=========== test loop ===================")

    // 1.to(10)
    val r1 = 1 until 10
    println(r1)

    // 1.until(10)
    val r2 = 1 to 10
    println(r2)

    // 1到10，步长为2
    for (i <- 1.to(10, 2)) {
      print(i + ",")
    }
    println

    for (i <- 1 until 10) {
      for (j <- 1 to i) {
        // s"$i"：引用变量
        print(s"$i*$j=" + i * j)
        print("\t")
      }
      println
    }

    // 向量集
    val v = for (i <- 1 to 10; if i % 2 == 0) yield i
    println(v)

    var i = 0
    var flag = true
    do {
      while (i < 3) {
        print(s"i=$i\t")
        i += 1
      }
      flag = false
    } while (flag)

  }


}
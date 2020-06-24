package clonegod.learn

import scala.collection.mutable.ArrayBuffer

object Test04_Array {
  def main(args: Array[String]): Unit = {
    // 不可变
    val arr1 = Array[String]("hello", "world");
    println(arr1.length)
    arr1.foreach(println)

    // 可变
    var arr2 = new ArrayBuffer[String]()
    arr2.append("hello", "scala")
    println(arr2.length)
    arr2.foreach(println)


  }
}
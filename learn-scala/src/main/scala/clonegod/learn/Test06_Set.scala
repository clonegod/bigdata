package clonegod.learn

import scala.collection.immutable
import scala.collection.mutable

object Test06_Set {
  def main(args: Array[String]): Unit = {
    
    // 不可变Set
    val set1 = immutable.Set[Int](1,2,3,4,1,2,3)
    val set2 = Set[Int](1,2,3)
    
    set1.filter(e => e<3).foreach(println)
    // 交集
    println("交集")
    val set3 = set1 & set2
    set3.foreach(println)
    
    // 差集
    println("差集")
    val set4 = set1 &~ set2
    set4.foreach(println)
    
    // 可变Set
    println("可变Set")
    var s = mutable.Set[Int](1,2,3)
    s.+=(5,6,6)
    s.foreach(println)
    
  }
}
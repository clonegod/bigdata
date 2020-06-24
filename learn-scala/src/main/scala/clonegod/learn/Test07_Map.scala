package clonegod.learn

import scala.collection.immutable
import scala.collection.mutable

object Test07_Map {
  def main(args: Array[String]): Unit = {
    
    // 不可变
    println("不可变的map")
    val map1 = immutable.Map[String,Int]("id"->1, "age"->2, ("score", 100))
    
    map1.foreach(println)
    
    // 遍历map的key
    map1.keys.foreach(k => {
      println(s"$k="+map1.get(k).get)
    })
    
    println("过滤map的key,value")
    map1.filter(tp => {
      val key = tp._1
      val vlaue = tp._2
      ! key.equals("id")
    }).foreach(println)
    
    
    // 可变
    println("可变的map")
    var map2 = mutable.Map[String, Any]( ("name", "alice") )
    map2.put("name", "bob");
    map2.put("age", 20);
    map2.foreach(println)
    
    
  }
}
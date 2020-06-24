package clonegod.learn

import scala.collection.mutable.ListBuffer

object Test04_List {
  def main(args: Array[String]): Unit = {
    
    // 不可变List：没有修改相关的API
    val list = List[String]("Hello scala", "Hello spark", "Hello flink")
    
    // map: 对集合中每个元素进行转化，映射为另一种类型的结果
    var rs1: List[Array[String]] = list.map(e => e.split("\\s+"))
    rs1.foreach(println)
    
    // flatMap : 先执行map,再分别对每个map转化后的结果进行扁平化合并
    var rs2:List[String] = list.flatMap(e => e.split("\\s+"))
    rs2.foreach(e => print(e+","))
    println
    
    // 可变List: 有追加，删除，修改等API可以调用
    var l = new ListBuffer[Int]()
    l.append(0,1,2)
    l.filter(i => i > 0).foreach(println)
    
    
  }
  
  
  
}
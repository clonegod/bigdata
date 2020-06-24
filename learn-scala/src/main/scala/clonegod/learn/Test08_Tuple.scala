package clonegod.learn

import java.util.Date

object Test08_Tuple {
  def main(args: Array[String]): Unit = {
    
    // 元组可以不new，直接写()
    val tp1 = new Tuple1(1)
  		val tp2 = Tuple2(2, "tuple2")
  		val tp3 = (3, "tuple3", new Date())
  		
  		// 元组最多支持22个元素
    val tp22 = (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22)
    
    // ._ 按下标位置获取元组中的元素
    println(tp1._1)
    
    // 直接输出元组
    println(tp2)
    // 2元组比较特殊，可以进行反转
    println(tp2.swap)
    
    // 遍历元组
    tp3.productIterator.foreach(println)
    
  }
}
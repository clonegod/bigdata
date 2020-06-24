package clonegod.learn

// trait里的方法，可以实现，也可以不实现
trait MyTrait {
  def isEqual(o:Any):Boolean
  
  def isNotEqual(o:Any):Boolean = !isEqual(o)
  
}

trait ToStrTrait {
  def toStr(p:Point) = {
    println("x="+p.x+", y="+p.y)
  }
}

// 继承trait，首先用extends，有更多trait需要继承，需要使用with
class Point(_x:Int, _y:Int) extends MyTrait with ToStrTrait {
  val x = _x
  val y = _y
  
  override def isEqual(o:Any):Boolean = {
    o.isInstanceOf[Point] && o.asInstanceOf[Point].x == this.x
  }
  
}

object Test09_Trait {

  def main(args: Array[String]): Unit = {
    val p1 = new Point(1,2)
    val p2 = new Point(1,3)
    val p3 = new Point(2,3)
    
    println("p1==p2: " + p1.isEqual(p2))
    println("p1!=p2: " + p1.isNotEqual(p2))
    println("p1==p3: " + p1.isEqual(p3))
    
    p1.toStr(p1)
    
    
  }
}

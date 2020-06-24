package clonegod.learn

object Test11_Implicit {
  
  /**
   * scala 隐式转化: 
   * 	当参数或调用的方法不存在时，scala会在作用域下继续查找是否存在implicit定义的相关隐式转化参数/函数/类
   * 	隐式转化会尝试寻找哪些地方：当前作用域、伴生对象、伴生类、包对象。
   * 
   *  隐式转化的应用：spark RDD中，将一些本身没有的方法，通过隐式转化让其它地方能够方便的调用到这些方法。
   * 
   * 【参数的隐式转化】
   * 1、同一个作用域下，对一种类型而言，只能定义一个同类型的隐式变量（多余一个将无法判断该绑定哪个）
   * 2、多个参数的情况下，隐式参数必需放到柯里化的参数的第二个括号中
   * 
   * 【函数的隐式转化】
   * 3、函数的隐式转化和参数的隐士转化类似。在同一个作用域内，只能定义一个同类型的转化函数，与转化函数的名称无关。
   * 
   * 【类的隐式转化】
   * 4、隐式类必需定义在类，伴生对象，包对象中。
   * 隐式类的构造参数只能有一个参数。
   * 同一个类，伴生对象，包对象中不能出现同类型构造的隐式类。
   */
  def hello(age:Int)(implicit name:String /**隐式参数*/) {
    println(s"$name age is " + age)
  }
  
  class Animal(name:String) {
    def fly() {
      println(s"$name can fly")
    }
    
    def eat() {
      println(s"$name can eat")
    }
  }
  
  class Pig(_name: String) {
    val name = _name
  }
    
  def main(args: Array[String]): Unit = {
    
    // 隐式值
    implicit val arg1 = "alice"
    
    // hello 方法将在执行上下文中寻找类型为String的隐式变量，对隐式变量完成自动绑定
    hello(6)
    
    // 隐式函数
    implicit def pigToAnimal(pig:Pig):Animal = {
      new Animal("_"+pig.name)
    }
    
    var pig = new Pig("little pig")
    // pig本身没有fly方法，通过隐式函数pigToAnimal将pig间接转化为Animal类型，这样就可以调用Animal的fly方法
    pig.fly()
    
    
    // 隐式类
    implicit class Trans(pig:Pig) {
      def showName(){
        println(s"${pig.name} is pig")
      }
    }
    
    pig.showName()
    
    
  }
  
}
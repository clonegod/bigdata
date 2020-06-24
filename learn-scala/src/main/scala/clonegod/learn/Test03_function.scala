package clonegod.learn

import java.util.Date

object Test03_function {
  
  def main(args: Array[String]): Unit = {
    
  }
  
  // 方法体的返回值可以使用return进行返回，如果通过return返回结果，则方法必需设置返回值的类型。
  def max(a: Int, b: Int):Int = {
    if(a > b) return a
    return b
  }
  println( max(1,2) )
  
  
  
  // 如果方法体中没有return，默认将方法最后一行的计算结果作为方法的返回值，方法定义可以省略返回值类型，scala进行自动类型推断。
  def min(a: Int, b: Int) = {
    if(a < b) a else b
  }
  println( min(1,2) )
  
  // 如果方法体可以简化为一行，那么方法体的花括号可以省略
  def sum(a:Int, b:Int) = a + b
  println( sum(1,2) )
    
  
  // 如果省略了返回值类型和"=",无论方法体最后一行的计算结果是什么，都返回Unit
  def show() {
    println("show...")
  }
  show()
  
  // 递归：递归方法必需显示声明函数的返回值类型
  def recursive(num:Int):Int = {
    if(num == 1) {
      1
    } else {
      num * recursive(num -1)
    }
  }
  println( recursive(5) )
  
  // 方法参数可以指定默认值
  def default_arg(a:Int=1, b:Int=1):Int = {
    a + b
  }
  println( default_arg() ) // 使用参数的默认值计算
  println( default_arg(100) ) // 指定第一个参数的值
  println( default_arg(b=(-1)) ) // 指定第二参数的值
  println( default_arg(1,2) ) // 不使用默认值
  
  
  // 可变参数
  def varArgs(args:String*) {
    args.foreach(println)
  }
  varArgs("alice", "bob")
  
  // 匿名函数
  def anfn = (a:Int, b:Int) =>{
    a + b
  }
  
  println( anfn(1,2) )
  
  // 偏应用函数: 简化相同参数的传递
  // 一些情况下，方法传递的参数非常多，而且要频繁调用，但是大部分参数的值都是相同的，则可以定义偏函数来简化函数的调用
  def printLog(userId:String, date:Date, data:String) {
    println(s"uid=$userId: $date, data=$data")
  }
  
  val uid = "123"
  val printLog0 = printLog(uid, _:Date, _:String) // 固定某些参数的传入，之后只需要传递其它变化的参数
  printLog0(new Date(), "password correct")
  printLog0(new Date(), "visit main page")
  printLog0(new Date(), "visit user page")
  
  // 高阶函数的三种类型：
  // 1、方法的参数是函数
  // 2、方法的返回值是函数
  // 3、方法的参数和返回值都是函数
  def fn(f:(Int,Int)=>Int, s:String):String = {
    val r = f(100, 200) // 数据处理逻辑由外部传入
    r + "#" + s
  }
  
  // 1、定义一个函数，用来传入另一个函数
  val fn0 = (a:Int,b:Int) => a + b
  val fn1 = (a:Int,b:Int) => a - b
  println( fn(fn0, "scala") )
  println( fn(fn1, "spark") )
  
  
  // 2、方法返回值类型是函数
  def func(s:String) = {
    // 内部定义的函数，使用了外部传入的参数
    def func0(s1:Int, s2:String) = {
      s.reverse + "-" + s2 + ":" + s1
    }
    // _ 则可以省略最外层方法的返回值类型："(Int, String)=>String"
    func0 _
  }
  
  // CBA-1:x
  println( func("ABC")(1, "x") )
  
  // 3、方法参数和返回值类型都是函数
  def m(f:(Int, Int)=>Int) = {
    val r = f(1,2)
    def m0(s1:String, s2:String) = {
      r + "@" + s1 + s2
    }
    m0 _
  }
  
  println( m( (a,b)=>a+b )("hello ", "world") )
  
  // 柯里化函数(高阶函数的简化--应用：与隐式转化相关)
  def klh(n1:Int,n2:Int)(s1:String,s2:String) = {
    (n1 + n2) +  s1 + s2
  }
  
  println(klh(1,2)(" scala=>", "spark"))
  
}
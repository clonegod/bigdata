package clonegod.learn

/**
 * 1、object 相当于Java中的单例类，object中定义的都是静态的，相当于Java中的工具类。
 * 2、常量定义使用val，变量定义使用var。常量和变量的类型可以不写，由scala自动进行类型推断。
 * 3、scala中每行后面的分号结束符可以不写。
 * 4、class在定义的时候可以传递参数，相当于提供了一个构造函数，类中的属性默认具有getter/setter的功能
 * 5、在同一个scala文件中，class类名和object名称一样时，这个类叫做该同名对象的伴生类，这个对象叫做该同名类的伴生对象，它们之间可以互相访问彼此的私有属性
 */
object Person {
  
  def apply(version: String):Unit = {
    println("Person object version=" + version)
  }
  
  def main(args: Array[String]): Unit = {
    // object 传参将调用apply方法
    Person("V1.0")
    
    // 常量
    val PI = 3.14
    println("PI=" + PI)
    
    // 构造函数1
    var p1 = new Person("alice", 20)
    
    // 构造函数2
    var p2 = new Person("bob", 30, "男", new Address("China", "beijing"))
   
    sayHello(p1)
    
    sayHello(p2)
    
    // 对象可以访问伴生类的私有属性
    println("version=" + p1.version)
  }
  
  def sayHello(person: Person) : Unit = {
    println("Hello: " + person.name)
    person.desc;
  }
  
}

class Person(xname:String, xage: Int) {
  var name = xname
  var age = xage
  var sex = "Male"
  var addr = new Address("","")
  private val version = "1.0"
  
  // 重载构造函数
  def this(_name:String, _age:Int, _sex:String, _addr:Address) {
    this(_name, _age)
    this.sex = _sex;
    this.addr = _addr
  }
  
  // 方法没有参数，可以省略后面的括号
  def desc:Unit = {
    println("name=%s,age=%s,sex=%s,addr=%s".format(this.name, this.age, this.sex, this.addr))
  }
  
}

// case 关键字修身class，会自动生成equals, toString等方法
case class Address(_country: String, _city: String) {
  val country = _country
  val city = _city
  
  // 重写父类方法
//  override def toString:String = {
//    return this.country + ": " + this.city;
//  }
  
}
#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
1、类名首字母大写风格, 后面的括号里写父类名称；
2、创建对象，直接类名+()；
3、类中的方法分3种：构造方法，实例方法(公共方法、私有方法)、静态方法/类方法；
4、构造方法：__init__(self, arg1, ...)，在创建对象时使用self给对象添加必需的属性；属性可以加"_"描述为私有；
4、普通方法：第一个参数都是:self，后面跟其它的形参；私有方法以"_"开头进行命名；
5、静态方法：方法参数不需要self参数，通过类名调用；
"""


# 定义一个Animal类，继承自object类
class Animal(object):
    # 类的静态属性
    nickName = "Animal"

    # 设置对象属性
    def __init__(self, name, age):
        self._name = name
        self._age = age

    # 公共方法，供外部调用
    def show(self, ext=""):
        info = self._show0()
        print(info, ext)

    # 私有方法，内部使用
    def _show0(self):
        return "name={}, age={}".format(self._name, self._age)

    # 静态方法，没有self参数
    @staticmethod
    def abc():
        print("Class attribute: ", Animal.nickName)
        print("static method have no 'self' arg")


class Cat(Animal):

    def __init__(self, name, age, hobby):
        super().__init__(name, age)  # 调用父类构造器，初始化父类属性
        self._hobby = hobby  # 子类新增属性

    def show(self, ext=""):
        # 子类可选择复用父类方法逻辑
        # super().show(ext)

        # 或重写方法逻辑
        print('%s name=%s, age=%s, hobby=%s' % (ext, self._hobby, self._name, self._age))


# run test
if __name__ == '__main__':
    Animal.abc()

    dog = Animal('dog', 1)
    dog.show("野狗")

    cat = Cat('cat', 1, 'finish')
    cat.show("黑猫")

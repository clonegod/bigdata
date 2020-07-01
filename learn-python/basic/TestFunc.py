#!/usr/bin/env python3
# -*- coding: utf-8 -*-


# 检查输入参数类型是否合法
def my_abs(x):
    if not isinstance(x, (int, float)):
        raise TypeError('only int or float acceptable')
    if x > 0:
        return x
    else:
        return -x


# 位置参数-固定参数
def power(x):
    return x * x


# 默认参数-当函数有多个参数时，把变化大的参数放前面，变化小的参数放后面。变化小的参数就可以作为默认参数。
# 有多个默认参数时，可以不按顺序提供默认参数的值，需要用name=value的方式来声明参数的绑定关系。
# 定义默认参数要牢记一点：默认参数必须指向不变对象！
def power2(x, n=2):
    r = 1
    while n > 0:
        n = n - 1
        r = r * x
    return r


# 可变参数-传入的参数个数是可变的，这些关键字参数在函数内部自动组装为一个tuple
def sum(*numbers):
    r = 0
    for n in numbers:
        r += n
    return r


# 命名参数-允许你传入0个或任意个含参数名的参数，这些关键字参数在函数内部自动组装为一个dict
def person(name, age, **kw):
    print('name:', name, 'age:', age, 'other:', kw)


# 命名关键字参数-在命名参数的基础上，限定传入的key-value
# 命名关键字参数需要一个特殊分隔符*，*后面的参数被视为命名关键字参数
# 关键字参数只接收city和job
# 命名关键字参数可以有缺省值
def person2(name, age, *, city='Beijing', job):
    print(name, age, city, job)


# 另一种情况：函数定义中已经有了一个可变参数，后面跟着的命名关键字参数就不再需要一个特殊分隔符*了
def person3(name, age, *args, city, job):
    print(name, age, args, city, job)


# 参数组合
# 可以用必选参数、默认参数、可变参数、关键字参数和命名关键字参数，这5种参数都可以组合使用。
# 参数定义的顺序必须是：必选参数、默认参数、可变参数、命名关键字参数和关键字参数。
# 虽然可以组合多达5种参数，但不要同时使用太多的组合，否则函数接口的可理解性很差。
def f1(a, b, c=0, *args, **kw):
    print('a =', a, 'b =', b, 'c =', c, 'args =', args, 'kw =', kw)


def f2(a, b, c=0, *, d, **kw):
    print('a =', a, 'b =', b, 'c =', c, 'd =', d, 'kw =', kw)


if __name__ == "__main__":
    print(power(2))
    print(power2(2, 3))

    print(sum())
    print(sum(1, 2, 3))
    # 在list或tuple前面加一个*号，把list或tuple的元素变成可变参数传进去
    print(sum(*[1, 2, 3]))

    # **把dict的所有key-value用关键字参数传入到函数的**kw参数
    person('alice', 20, **{'job':'engineer', 'city':'beijing'})
    person2('bob', 20, **{'job':'engineer'})
    person3('chalis', 20, *('pilot', 'builder'), **{'job':'engineer', 'city':'beijing'})

    # 对于任意函数，都可以通过类似func(*args, **kw)的形式调用它，无论它的参数是如何定义的。
    f1(1, 2)
    f1(1, 2, 3)
    f1(1, 2, 3, 4)
    f1(1, 2, 3, 4, 5, name="alice")
    f1(1, 2, 3, 4, 5, **{"name":"alice", "age": 20})

    f2(1, 2, d="d1")
    f2(1, 2, d="d2", name="alice")
    f2(1, 2, d="d2", **{"name":"alice", "age": 20})

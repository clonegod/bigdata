#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import functools


# 装饰器函数-很强大
# @functools.wraps: 把原始函数的__name__等属性复制到wrapper()函数中，确保func.__name__取得的函数名称是原始函数名
def log(text):
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kw):
            print('%s call %s():' % (text, func.__name__))
            return func(*args, **kw)
        return wrapper
    return decorator


# 被@标识的函数，就是被装饰过的函数。外部调用时，将先调用装饰函数，在装饰函数内部再对真正对函数进行调用
# 同名的fn1函数变量指向了新的函数
@log("debug")
def fn1():
    # 真正的函数逻辑最后执行
    print('fn1 invoked!\n')


@log("info")
def fn2():
    # 真正的函数逻辑最后执行
    print('fn2 invoked!\n')


if __name__ == '__main__':
    fn1()

    fn2()

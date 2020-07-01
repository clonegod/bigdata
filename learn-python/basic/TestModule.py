#!/usr/bin/env python3
# -*- coding: utf-8 -*-

""" a test module """

__author__ = ''

import sys


def test():
    # sys.argv变量，用list存储了命令行的所有参数
    # argv至少有一个元素，因为第一个参数永远是该.py文件的名称
    args = sys.argv
    print('script path:', args[0])

    if len(args) == 1:
        print('Hello, world!')
    elif len(args) == 2:
        print('Hello, %s!' % args[1])
    else:
        print('Too many arguments!')


# 当我们在命令行运行hello模块文件时，Python解释器把一个特殊变量__name__置为__main__
# 而如果在其他地方导入该hello模块时，if判断将失败
# 因此，这种if测试可以让一个模块通过命令行运行时执行一些额外的代码，最常见的就是运行测试。

# 直接运行本python文件时条件成立，test()将被执行。本模块被导入其它模块中使用，条件不成立，test()不会被执行。
if __name__ == '__main__':
    test()

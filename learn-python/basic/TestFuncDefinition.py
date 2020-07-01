#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
python编程风格，有两种方式：
1、模块中都是函数，以比较松散的方式组织代码（绝大部分是这种风格）；
2、面向对象设计，封装逻辑，面向对象编程；

不管用那种方式，有两点比较重要的地方：
1、代码有注释，说明这个模块主要是要干什么的，要完成什么功能
2、模块内部代码逻辑要清晰，调用入口在哪儿最好能让人一眼就看到
3、对外提供调用的函数/方法定义为public，只有内部才会使用的函数/方法定义为private: _xxx()
"""


# 在模块里公开greeting()函数
def greetings():
    _open_connection()

    _do_work()

    _close_connection()


# 私有函数
def _open_connection():
    pass


# 私有函数
def _do_work():
    pass


# 私有函数
def _close_connection():
    pass


if __name__ == '__main__':
    greetings()

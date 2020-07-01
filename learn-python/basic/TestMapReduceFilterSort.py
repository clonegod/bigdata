#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from functools import reduce


def test_map():
    def f(x):
        return x * x

    # 把f(x)作用在list的每一个元素并把结果生成一个新的list
    r = map(f, list(range(1, 10)))
    # map返回的Iterator, Iterator是惰性序列，通过list()函数让它把整个序列都计算出来
    print(list(r))


def test_reduce():
    def add(x, y):
        return x * 10 + y

    r = reduce(add, [1, 3, 5, 7, 9])
    print(r)


def test_reduce_lambda():
    from functools import reduce
    r = reduce(lambda x, y: x * 10 + y, [1, 3, 5, 7, 9])
    print(r)


# 常量列表
DIGITS = {'0': 0, '1': 1, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9}


# 把字符串转化为整数的函数
def test_map_reduce_str2int(s):
    def char2num(s0):
        return DIGITS[s0]

    return reduce(lambda x, y: x * 10 + y, map(char2num, s))


# filter()的作用是从一个序列中筛出符合条件的元素。
# 由于filter()使用了惰性计算，所以只有在取filter()结果的时候，才会真正筛选并每次返回下一个筛出的元素。
def test_filter():
    def not_empty(s):
        return s and s.strip()

    # 把一个序列中的空字符串删掉
    rs_iter = filter(not_empty, ['A', '', 'B', None, 'C', '  '])
    # filter()返回的是一个Iterator，是一个惰性序列，需要用list()函数获得所有结果并返回list。
    print(list(rs_iter))


# 列表排序
def test_sort():
    print(sorted([36, 5, -12, 9, -21], key=abs))

    l1 = ['bob', 'about', 'Zoo', 'Credit']
    # 给sorted传入key函数, key函数指定的是排序规则
    l2 = sorted(l1, key=str.lower, reverse=True)
    print(l1)
    print(l2)


if __name__ == '__main__':
    test_map()
    test_reduce()
    test_reduce_lambda()
    print(123 == test_map_reduce_str2int("123"))

    test_filter()

    test_sort()

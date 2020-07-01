#!/usr/bin/env python3
# -*- coding: utf-8 -*-


def test_list():
    print("---list")
    l1 = list(range(10))
    print(l1)

    # 第1个
    print(l1[0])
    # 最后1个
    print(l1[-1])
    # 前3个
    print(l1[:3])
    # 后两个
    print(l1[-2:])

    # 复制列表
    l2 = l1[:]
    print(l2)


def test_tuple():
    print("---tuple")
    t1 = (range(5))
    print(t1)
    print(t1[0])
    print(t1[-1])


def test_set():
    print("---set")
    s1 = set([1, 2, 3, 2, 1])
    s1.add(5)
    s1.remove(3)
    print(s1)


def test_map():
    print("---map")
    m1 = {"name": "alice", "age": 20}
    m1.get('name')
    m1['sex'] = 1
    m1.pop('name')
    print(m1)


def list_generator():
    # 列表生成式
    print("---list_generator")
    l1 = [x for x in range(1, 11) if x % 2 == 0]
    print(l1)

    l2 = [x * x for x in range(1, 11) if x % 2 == 0]
    print(l2)

    L = ['Hello', 'World', 'IBM', 'Apple']
    l3 = [s.lower() for s in L]
    print(l3)

    l4 = [x if x % 2 == 0 else -x for x in range(1, 11)]
    print(l4)

    # os.listdir可以列出文件和目录
    import os
    dirs = [d for d in os.listdir('.')]
    print(dirs)


def iter_map():
    # 遍历map
    print("---iter_map")
    d = {'x': 'A', 'y': 'B', 'z': 'C'}
    for k, v in d.items():
        print(k, '->', v)

    l1 = [k + '=' + v for k, v in d.items()]
    print(l1)


if __name__ == "__main__":
    test_list()
    test_tuple()
    test_set()
    test_map()

    list_generator()
    iter_map()



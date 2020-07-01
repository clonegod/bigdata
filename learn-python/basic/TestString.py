#!/usr/bin/env python3
# -*- coding: utf-8 -*-

accounts = """861798520851508
1906287146139027
1045822342276196
"""

if __name__ == "__main__":
    print(accounts.replace('\n', ','))

    # 字符串格式化
    print('hello: %s' % 'alice')
    print('hello: %s %s' % ('alice', 'bob'))
    print('hello: {}, {}'.format('alice', 'bob'))


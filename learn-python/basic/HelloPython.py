#!/usr/bin/env python3
# -*- coding: utf-8 -*-


def run(_name=''):
    text = """Hello Python!
Welcome to python playground!"""
    print(text)

    print('Hello', _name)


if __name__ == "__main__":
    name = input('input your name:\n')
    run(name)

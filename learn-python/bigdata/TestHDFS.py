#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from contextlib import contextmanager
from hdfs3 import HDFileSystem


@contextmanager
def connect_hdfs(host='localhost', port=8020):
    try:
        conn = HDFileSystem(host, port)
        print(conn)
        yield conn
    finally:
        conn.disconnect()


if __name__ == "__main__":
    print('run...')

    hdfs = HDFileSystem('localhost', 8020)
    with hdfs.open("hdfs://localhost:8020/testdata/persons.txt"):
        print(f.read())


#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from hdfs.client import Client


# 读取hdfs文件内容,将每行存入数组返回
def read_hdfs_file(client, filename):
    lines = []
    with client.read(filename, encoding='utf-8', delimiter='\n') as reader:
        for line in reader:
            lines.append(line.strip())
    return lines


if __name__ == '__main__':
    client = Client("http://127.0.0.1:50070/")
    print("hdfs中的目录为:", client.list(hdfs_path="/user", status=True))
    with client.read("/user/hive/warehouse/test.db/t_tmp1/tmp1.txt", length=200, encoding='utf-8') as obj:
        for i in obj:
            print(i)
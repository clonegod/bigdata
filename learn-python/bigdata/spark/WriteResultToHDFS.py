from pyspark import SparkConf, SparkContext

import os
import datetime as dt

# 明确设置spark使用的python版本
PYTHON3_PATH = "/usr/local/bin/python3.7"
os.environ['PYSPARK_PYTHON'] = PYTHON3_PATH

# 1、构建上下文
conf = SparkConf() \
    .setMaster("local[*]") \
    .setAppName("hello spark word count")

sc = SparkContext.getOrCreate(conf)

## 读取hdfs文件
hdfs_file = "hdfs://localhost:8020/test/wc.txt"

print("process file: %s" % hdfs_file)
rdd = sc.textFile(hdfs_file)
rdd1 = rdd.flatMap(lambda line: line.split(" ")).map(lambda word: (word, 1))
rdd2 = rdd1.reduceByKey(lambda a, b: a + b)

# 将计算结果保存到hdfs
output_file = "hdfs://localhost:8020/test/{date}/word_counts/".format(date=dt.datetime.now().strftime("%Y-%m-%d"))
# repartition(1) 将结果合并到一个分区文件中存放
rdd2.repartition(1).saveAsTextFile(output_file)



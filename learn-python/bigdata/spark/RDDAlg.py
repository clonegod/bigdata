from pyspark import SparkConf, SparkContext

import os

# 明确设置spark使用的python版本
PYTHON3_PATH = "/usr/local/bin/python3.7"
os.environ['PYSPARK_PYTHON'] = PYTHON3_PATH


# 1、构建上下文
conf = SparkConf() \
    .setMaster("local[*]") \
    .setAppName("hello spark word count")

sc = SparkContext.getOrCreate(conf)


"""
RDD Transformation 转换算子
map, flatMap, fliter, distinct, sample, sortBy
"""
numberRDD = sc.parrallelism()



"""
PairRDD 相关到算子
groupByKey, reduceByKey, sortByKy, aggregateByKey, smapleByKey
"""


"""
RDD Action 算子
collect
"""
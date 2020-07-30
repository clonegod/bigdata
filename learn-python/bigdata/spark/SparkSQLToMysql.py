from pyspark.sql import SparkSession
import os

# 明确设置spark使用的python版本
PYTHON3_PATH = "/usr/local/bin/python3.7"
os.environ['PYSPARK_PYTHON'] = PYTHON3_PATH

spark = SparkSession.builder \
    .master("local[*]") \
    .appName("sparkSQL1") \
    .getOrCreate()

sc = spark.sparkContext

# 声明schema
from pyspark.sql.types import *

schema = StructType([
    StructField("name", StringType()),
    StructField("age", IntegerType())
])

# rdd转换为dataFrame

rdd3 = sc.textFile("/test/students.txt") \
    .map(lambda line: line.split(",")) \
    .map(lambda arr: (arr[0], int(arr[1])))

df3 = spark.createDataFrame(rdd3, schema)
df3.show()

# 将结果写入mysql
mysql_url = "jdbc:mysql://{host}:{port}/{database}".format(host="127.0.0.1", port=3306, database="test")
write_mode = "overwrite"
table_name = "t_persons"
mysql_auth = {'user': 'root', 'password': '123456', "useSSL": "False"}


df3.write.jdbc(mysql_url, table_name, write_mode, mysql_auth)

"""
默认为SaveMode.ErrorIfExists模式，该模式下，如果数据库中已经存在该表，则会直接报异常，导致数据不能存入数据库.
另外三种模式如下：
SaveMode.Append 如果表已经存在，则追加在该表中；若该表不存在，则会先创建表，再插入数据；
SaveMode.Overwrite 重写模式，其实质是先将已有的表及其数据全都删除，再重新创建该表，最后插入新的数据；
SaveMode.Ignore 若表不存在，则创建表，并存入数据；在表存在的情况下，直接跳过数据的存储，不会报错。
"""
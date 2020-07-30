from pyspark.sql import SparkSession
import os

# 明确设置spark使用的python版本
PYTHON3_PATH = "/usr/local/bin/python3.7"
os.environ['PYSPARK_PYTHON'] = PYTHON3_PATH

spark = SparkSession.builder \
        .master("local[*]") \
        .appName("sparkSQL1") \
        .getOrCreate()

# 声明schema
from pyspark.sql.types import *
schema = StructType([
    StructField("name", StringType()),
    StructField("age", IntegerType())
])

# 读取数据DataFrame
df = spark.read.schema(schema).json("hdfs://localhost:8020/test/person.json")
df.show()


# DSL语法使用
df. printSchema()
df.select("name").show()
df.select(df['name'].alias("name1"), df['age'] + 1).toDF("name2", "age2").show()
df.filter(df['name'] == 'alice').show()
df.groupBy("age").count().show()
df.agg({"age": "avg"}).show()

# 创建临时表(将df的结果映射到一张二维表中，之后在该表上写SQL进行查询)
df.createTempView("t_person")
# 如果临时表名冲突，则replace掉以前的。
df.createOrReplaceTempView("t_person")

spark.sql("select * from t_person").show()


# 全局临时视图(可跨多个session会话存在)
df.createOrReplaceGlobalTempView("t_persons")
spark1 = spark.newSession()
spark1.sql("select name glb_temp_name, age glb_temp_age from global_temp.t_persons").show()


## RDD 与 DataFrame 的转换
# rdd转换为dataFrame，第1种方式
sc = spark.sparkContext
rdd = sc.textFile("/test/students.txt")\
        .map(lambda line: line.split(","))\
        .map(lambda arr: (arr[0], int(arr[1])))
print(rdd.collect())

df = rdd.toDF(["name", "age"])
df.show()
# df.createTempView("t_students")
# spark.sql("select * from t_students where age = 20").show()


# rdd转换为dataFrame，第2种方式
rdd2 = sc.textFile("/test/students.txt")\
        .map(lambda line: line.split(","))

from pyspark import Row
stuRdd = rdd2.map(lambda arr: Row(name=arr[0], age=arr[1]))
df2 = spark.createDataFrame(stuRdd)
df2.show()

df2.createTempView("t_students")
spark.sql("select * from t_students where age = 20").show()

# rdd转换为dataFrame，第3种方式
rdd3 = sc.textFile("/test/students.txt")\
        .map(lambda line: line.split(","))\
        .map(lambda arr: (arr[0], int(arr[1])))

df3 = spark.createDataFrame(rdd3, schema)
df3.show()

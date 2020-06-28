
## spark伪集群环境搭建

#### 下载安装包
注意：

spark版本和本地开发程序版本一致，Hadoop版本和已有Hadoop集群版本一致

从aliyun镜像下载速度很快。

下载地址：

https://mirrors.aliyun.com/apache/spark/spark-2.3.4/spark-2.3.4-bin-hadoop2.6.tgz


#### 配置
> 小技巧：
> :r! /usr/local/...
> for i in {1..8}; do scp -r ./spark node-$i:/$PWD;done


```
/Users/huangqihang/bigdata/spark-2.3.4-bin-hadoop2.6

# 配置Master
# vi conf/spark-env.sh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home
export SPARK_MASTER_HOST=127.0.0.1
export SPARK_MASTER_PORT=7077
export SPARK_WORKER_CORES=8
export SPARK_WORKER_MEMORY=1G

# 配置Worker节点，每个Worker起一行
# vi conf/slaves
localhost
```

#### 启动

```
cd /Users/huangqihang/bigdata/spark-2.3.4-bin-hadoop2.6
sh sbin/start-all.sh
sh sbin/stop-all.sh

# 修改webUI的端口
vi sbin/start-master.sh
if [ "$SPARK_MASTER_WEBUI_PORT" = "" ]; then
  # => 18080
  SPARK_MASTER_WEBUI_PORT=8080 
fi
```

#### 验证
```
> jps
    Mater
    Worker
```

访问spark后台管理页面：

http://127.0.0.1:8080/


#### Spark Submit
```
bin/spark-submit \
--master spark://localhost:7077 \
--class org.apache.spark.examples.SparkPi \
--executor-memory 1024m --total-executor-cores 8 \
examples/jars/spark-examples_2.11-2.3.4.jar 10


executor-memory : 给每个执行器分配多大的运行内存
total-executor-cores : 所有Worker节点一共最多可以使用的core数
```

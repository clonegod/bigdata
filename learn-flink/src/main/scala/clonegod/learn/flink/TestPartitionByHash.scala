package clonegod.learn.flink

object TestPartitionByHash {
  def main(args: Array[String]): Unit = {
    val data = Map("id"->100)

    val maxParallel = 16
    val parallel = 4

    val hash = Math.abs(data.get("id").hashCode())
    // 计算数据要发送到哪个分区上，确保范围在 0-maxParallel之间
    // 基于hashcode的分区算法
    val range = (hash % maxParallel) * parallel / maxParallel
    print(range)
  }
}

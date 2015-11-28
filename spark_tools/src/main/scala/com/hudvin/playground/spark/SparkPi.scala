//import org.apache.spark.sql.SQLContext
//import org.apache.spark.sql.hive.HiveContext
//
//import scala.collection.mutable.ArrayBuffer
//import scala.math.random
//
//import org.apache.spark._
//import org.apache.spark.SparkContext._
//import org.apache.spark.rdd.RDD
//
///** Computes an approximation to pi */
//object SparkPi {
//  def main(args: Array[String]) {
//
//    val conf = new SparkConf().setAppName("Spark Pi")
//    conf.setMaster("local[*]")
//   // val sparkHome = "/opt/spark/sparfk-1.5.1" //System.getenv("SPARK_HOME")
//   // conf.setSparkHome(sparkHome)
//    conf.setJars(SparkContext.jarOfClass(this.getClass).toList)
//
//    val sc = new SparkContext(conf)
//
//    val sqlSc = new SQLContext(sc)
//    val reddit = sqlSc.read.json("/media/3tb/data/datasets/reddit/json/reddit-small.json")
//    reddit.registerTempTable("reddit_table")
//    reddit.printSchema()
//
//    val dated_table =
//      sqlSc.sql(" SELECT *, to_date(from_unixtime(created_utc)) as date FROM reddit_table")//.collect.foreach(println)
//
//    dated_table.registerTempTable("dated_table")
//
//    sqlSc.sql("select date, from_unixtime(created_utc) from dated_table ORDER BY ups").take(100).foreach(println)
//
////    dated_table.sqlContext.sql("SELECT COUNT(*) as total, author,created " +
////      "FROM reddit_table  WHERE author!='[deleted]' GROUP BY author, from_unixtime(created_utc) as created order by total  ")
////      .collect.foreach(println)
//
//   // System.out.println(count)
//
//
//
//
//    sc.stop()
//  }
//}

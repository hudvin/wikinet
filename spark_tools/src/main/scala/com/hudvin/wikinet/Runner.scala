package com.hudvin.wikinet

import com.hudvin.wikinet.converter.ConverterRunner
import com.hudvin.wikinet.spark.SparkWikiRunner

object Runner {

  def main (args: Array[String]) = {
    args.toList match {
      case List("convert",in, out) => {
        ConverterRunner.main(Array(in, out))
        println(in, out)
      }
      case List("extract",in, out) => {
        SparkWikiRunner.main(Array(in, out))
        println(in, out)
      }
      case _=> println("wrong arguments")
    }
  }

}

package com.hudvin.wikinet.spark

import com.google.gson.JsonParser
import org.apache.spark.{SparkConf, SparkContext}

object SparkWikiRunner {

  def main(args: Array[String]): Unit = {
    //configuration
    val master = "local[*]" // or "spark://sanctum:7077"
    val outputDir = "/tmp/links_dir"
    //val dumpFile = "/media/3tb/data/datasets/wiki/1000.json"
    val dumpFile = getClass.getResource("/1000.json").getPath

    val conf = new SparkConf().setAppName("wiki")
    conf.setMaster(master)
    conf.setJars(SparkContext.jarOfClass(this.getClass).toSeq)
    conf.set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    val wikiFile = sc.textFile(dumpFile)
    val titles = wikiFile.flatMap(line => {
      val json = new JsonParser().parse(line).getAsJsonObject
      val text = json.getAsJsonObject("revision").get("text").getAsString
      val title = json.get("title").getAsString
      if (text.trim.nonEmpty) {
        val wikiParser = new WikiLoader(text)
        val links = wikiParser.getWikiExtractor.getInternalLinks
        //return pairs current_page->outgoing_page
        links.map(link => "\"%s\"->\"%s\"".format(title, link))
      } else None
    })
    titles.saveAsTextFile(outputDir)
  }

}

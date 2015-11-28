package com.hudvin.playground.spaxrk

import java.nio.file.{Files, Paths}

import com.google.gson.JsonParser
import com.hudvin.playground.spark.WikiLoader
import org.apache.spark.{SparkConf, SparkContext}

object SparkWiki {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("wiki")
    conf.setMaster("local[4]")
    // conf.setMaster("spark://sanctum:7077")
    conf.setJars(SparkContext.jarOfClass(this.getClass).toSeq)
    val sc = new SparkContext(conf)

    val wikiFile = sc.textFile("/media/3tb/data/datasets/wiki/1000.json")
    val titles = wikiFile.flatMap(line => {
      val parser = new JsonParser()
      val json = parser.parse(line).getAsJsonObject
      val text = json.getAsJsonObject("revision").get("text").getAsString
      val title = json.get("title").getAsString
      if (text.trim.nonEmpty) {
        val wikiParser = new WikiLoader
        wikiParser.load(text)
        val links = wikiParser.getWikiExtractor.getInternalLinks
        links.map(link=>"\"%s\"->\"%s\";".format(title,link))
        //links
      } else None
    })
    titles.saveAsTextFile("/tmp/links_dir")
  }

}

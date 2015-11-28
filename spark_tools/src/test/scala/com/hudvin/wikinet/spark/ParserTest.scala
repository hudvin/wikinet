package com.hudvin.wikinet.spark

import com.google.gson.JsonParser
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.io.Source

class ParserTest extends FunSuite with BeforeAndAfter {

  test("parse wiki json") {
    for (line <- Source.fromFile(getClass.getResource("/test_data/1000.json").getPath).getLines()) {
      val json = new JsonParser().parse(line).getAsJsonObject
      val text = json.getAsJsonObject("revision").get("text").getAsString
      val wikiLoader = new WikiLoader(line)
      for (link <- wikiLoader.getWikiExtractor.getInternalLinks) {
        System.out.println(link)
      }
    }
  }


}
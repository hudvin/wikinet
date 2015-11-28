package com.hudvin.wikinet.converter

import java.io.{BufferedInputStream, FileInputStream}

import com.hudvin.wikinet.converter.parsers.{JsonImporter, PageParser}
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

import scala.io.Source
import scala.xml.pull.XMLEventReader


object ConverterRunner {

  def main(args: Array[String]) {
    def bz2Reader(filePath: String) = new BZip2CompressorInputStream(
      new BufferedInputStream(new FileInputStream(filePath), 32768 * 100))

    //config
    val wikiFile = "/media/3tb/data/datasets/enwiki-20150602-pages-articles.xml.bz2"
    val outputFile = "/tmp/enwiki-20150602-pages-articles.json.bz2"

    val pageParser = new PageParser()
    val xmlReader = new XMLEventReader(Source.fromInputStream(bz2Reader(wikiFile)))
    val jsonImporter = new JsonImporter(outputFile)
    var counter = 0
    pageParser.parse(xmlReader, wiki => {
      jsonImporter.insert(wiki)
      counter += 1
      if (counter % 10000 == 0) println(counter)
    })
    jsonImporter.close()
  }


}
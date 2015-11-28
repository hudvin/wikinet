package com.hudvin.wikinet.converter

import java.io.{BufferedInputStream, FileInputStream}

import com.hudvin.wikinet.converter.parsers.{JsonImporter, PageParser}
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

import scala.io.Source
import scala.xml.pull.XMLEventReader


object ConverterRunner {

  private val devInputOutput = ( "/media/3tb/data/datasets/enwiki-20150602-pages-articles.xml.bz2",
    "/tmp/enwiki-20150602-pages-articles.json.bz2"
    )

  def bz2Reader(filePath: String) = new BZip2CompressorInputStream(
    new BufferedInputStream(new FileInputStream(filePath), 32768 * 100))

  def main(args: Array[String]) {
    val inputOutput = {
      if(args.isEmpty) devInputOutput else (args(0), args(1))
    }

    val pageParser = new PageParser()
    val xmlReader = new XMLEventReader(Source.fromInputStream(bz2Reader(inputOutput._1)))
    val jsonImporter = new JsonImporter(inputOutput._2)
    var counter = 0
    pageParser.parse(xmlReader, wiki => {
      jsonImporter.insert(wiki)
      counter += 1
      if (counter % 10000 == 0) println(counter)
    })
    jsonImporter.close()
  }


}
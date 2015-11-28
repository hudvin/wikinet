package com.hudvin.wikipro

import java.io.{BufferedInputStream, FileInputStream}

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

import scala.io.Source
import scala.xml.pull.XMLEventReader



object Runner {

  def main(args: Array[String]) {
    def bz2Reader(filePath:String ) =  new BZip2CompressorInputStream(
      new BufferedInputStream(new FileInputStream(filePath),32768*100))
    val wikiFile = "/media/3tb/data/datasets/enwiki-20150602-pages-articles.xml.bz2"
    val pageParser = new PageParser()
    val xmlReader =  new XMLEventReader(Source.fromInputStream(bz2Reader(wikiFile)))

    val outputFile = "/tmp/enwiki-20150602-pages-articles.json.bz2"
    val jsonImporter = new JsonImporter(outputFile)

    var counter = 0
    pageParser.parse(xmlReader,wiki=>{
      jsonImporter.insert(wiki)
      counter+=1
      if(counter % 10000 ==0) println(counter)
    })

    jsonImporter.close()

  }


}



//  jsonImporter.insert(wiki)
//      cassi.insert(wiki)
//      val gson = new Gson
//      val jsonTree =  gson.toJsonTree(wiki)
//
//      val extractor  = new Extractor
//      extractor.load(wiki.revision.text)
//      val ast = extractor.getAst()
//      println(ast)
//
//
//      jsonTree.getAsJsonObject().addProperty("ast", "");
//
//      val jsonString = gson.toJson(jsonTree)

//  def parseWiki(content:String): Unit ={
//    val extractor  = new Extractor(content)
//    extractor.getInternalLinks.foreach { println }
//  }

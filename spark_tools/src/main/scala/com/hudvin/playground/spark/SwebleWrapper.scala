package com.hudvin.playground.spark

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import de.fau.cs.osr.ptk.common.jxpath.AstNodePointerFactory
import org.apache.commons.jxpath.JXPathContext
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl
import org.sweble.wikitext.example.{SerializationMethod, Serializer}
import org.sweble.wikitext.parser.nodes.{WtNode, WtPageName}
import org.sweble.wikitext.parser.utils.NonExpandingParser

import scala.collection.mutable.ListBuffer

///**
//  * Created by kontiki on 26.11.15.
//  */
//class SwebleParser {
//
//  private val WARNINGS_ENABLED = false
//  private val GATHER_RTD: Boolean = true
//  private val AUTO_CORRECT: Boolean = false
//
//
//  def createContextFromWikiText(content: String): JXPathContext = {
//    JXPathContextReferenceImpl.addNodePointerFactory(new AstNodePointerFactory)
//    val parser = new NonExpandingParser(WARNINGS_ENABLED, GATHER_RTD, AUTO_CORRECT)
//    val ast = parser.parseArticle(content, "")
//    val ctx = JXPathContext.newContext(ast)
//    ctx
//  }
//
//}


object XParser {

  private val WARNINGS_ENABLED = false
  private val GATHER_RTD: Boolean = true
  private val AUTO_CORRECT: Boolean = false

  JXPathContextReferenceImpl.addNodePointerFactory(new AstNodePointerFactory)
  private val parser = new NonExpandingParser(WARNINGS_ENABLED, GATHER_RTD, AUTO_CORRECT)

  def getParser(): NonExpandingParser = parser

}

//class Extractor(content: String) {
//
//  private val ast = XParser.getParser().parseArticle(content, "")
//  private val ctx = JXPathContext.newContext(ast)
//
//  def getAst() = ast
//
//  def getInternalLinks: List[String] = {
//    val internalLinks = new ListBuffer[String]
//    val it = ctx.iterate("//WtInternalLink")
//    while (it.hasNext) {
//      val n: WtNode = it.next.asInstanceOf[WtNode].get(0)
//      val value = (n.asInstanceOf[WtPageName]).getAsString
//      internalLinks += value
//    }
//    internalLinks.toList
//
//  }
//
//
//}


abstract class BaseLoader {

  private var rootNode: WtNode = null

  protected final val method = SerializationMethod.JSON

  def load(content: String)

  protected def getSerializer: Serializer

  def getJson: String

  def getWikiExtractor: WikiExtractor = new WikiExtractor(getRootNode)

  def getRootNode: WtNode = rootNode

  def setRootNode(rootNode: WtNode) = this.rootNode = rootNode
}

class WikiLoader extends BaseLoader {

  XParser.getParser()

  private var serializer: Serializer = null

  def load(content: String) {
    try {
      serializer = new Serializer(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), "")
      serializer.setParserAutoCorrectEnabled(false)
      serializer.setParserWarningsEnabled(true)
      serializer.setParserRtdEnabled(true)
      serializer.setPpSimplifyAst(true)
      serializer.setPpStripLocations(false)
      serializer.setPpStripAllAttributes(false)
      serializer.setPpStripRtdAttributes(false)
      serializer.setQuiet(true)
      serializer.setParserWarningsEnabled(false)
      setRootNode(serializer.getAst)
    }
    catch {
      case t: Throwable => {
        throw new RuntimeException(t)
      }
    }
  }

  def getJson: String = {
    val serializer: Serializer = getSerializer
    serializer.serializeTo(method)
    val serialized: Array[Byte] = serializer.serializeTo(method)
    serializer.roundTrip(method)
    val json: String = new String(serialized, "UTF8")
    json
  }

  protected def getSerializer: Serializer = serializer
}

class JsonLoader extends BaseLoader {
  private var serializer: Serializer = null
  private var jsonContent: String = null

  def load(content: String) {
    try {
      jsonContent = content
      serializer = new Serializer(null, null)
      setRootNode(serializer.deserializeFrom(method, content.getBytes))
    }
    catch {
      case t: Throwable => {
        throw new RuntimeException(t)
      }
    }
  }

  def getJson: String = jsonContent


  protected def getSerializer: Serializer = serializer
}


//object XParser {
//
//  private val WARNINGS_ENABLED = false
//  private val GATHER_RTD: Boolean = true
//  private val AUTO_CORRECT: Boolean = false
//
//  JXPathContextReferenceImpl.addNodePointerFactory(new AstNodePointerFactory)
//  private val parser = new NonExpandingParser(WARNINGS_ENABLED, GATHER_RTD, AUTO_CORRECT)
//  def getParser(): NonExpandingParser = parser
//
//}

class WikiExtractor(rootNode: WtNode) {
//
//  private final val WARNINGS_ENABLED: Boolean = false
//  private final val GATHER_RTD: Boolean = true
//  private final val AUTO_CORRECT: Boolean = false
//
//  JXPathContextReferenceImpl.addNodePointerFactory(new AstNodePointerFactory)

  require(rootNode!=null)
 // println(rootNode)
  private  val  ctx = JXPathContext.newContext(rootNode)

  def getInternalLinks: List[String] = {
    val it = ctx.iterate("//WtInternalLink")
    val links = new ListBuffer[String]
    while (it.hasNext) {
      val wtNode = it.next.asInstanceOf[WtNode].get(0)
      val value = wtNode.asInstanceOf[WtPageName].getAsString
      links += value
    }
    links.toList
  }
}


object Runner {

  def main(args: Array[String]) {
    val wikiLoader = new WikiLoader
    val in = String.join("\n", Files.readAllLines(Paths.get("/tmp/1.wiki")))
    wikiLoader.load(in)
    val json: String = wikiLoader.getJson
    System.out.println(json)
    for (link <- wikiLoader.getWikiExtractor.getInternalLinks) {
      System.out.println(link)
    }


    val jsonLoader = new JsonLoader
    jsonLoader.load(json)
    val json2: String = jsonLoader.getJson
    System.out.println(json == json2)
    for (link <- jsonLoader.getWikiExtractor.getInternalLinks) {
      System.out.println(link)
    }
  }

}
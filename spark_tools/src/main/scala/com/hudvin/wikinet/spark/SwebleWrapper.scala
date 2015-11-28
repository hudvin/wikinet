package com.hudvin.wikinet.spark

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import de.fau.cs.osr.ptk.common.jxpath.AstNodePointerFactory
import org.apache.commons.jxpath.JXPathContext
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl
import org.sweble.wikitext.example.{SerializationMethod, Serializer}
import org.sweble.wikitext.parser.nodes.{WtNode, WtPageName}
import org.sweble.wikitext.parser.utils.NonExpandingParser


object XParser {

  private val WARNINGS_ENABLED = false
  private val GATHER_RTD = true
  private val AUTO_CORRECT = false

  JXPathContextReferenceImpl.addNodePointerFactory(new AstNodePointerFactory)
  private val parser = new NonExpandingParser(WARNINGS_ENABLED, GATHER_RTD, AUTO_CORRECT)

  def getParser: NonExpandingParser = parser

}


abstract class BaseLoader(content: String) {

  private var rootNode: WtNode = null

  load(content)

  protected var serializer: Serializer = null

  protected final val method = SerializationMethod.JSON

  protected def load(content: String)

  def getJson: String

  def getWikiExtractor = new WikiDateExtractor(getRootNode)

  def getRootNode: WtNode = rootNode

  def setRootNode(rootNode: WtNode) = this.rootNode = rootNode
}


class WikiLoader(content: String) extends BaseLoader(content) {

  //need this to init jxpacontext
  XParser.getParser

  override protected def load(content: String) {
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

  override def getJson: String = {
    serializer.serializeTo(method)
    val serialized = serializer.serializeTo(method)
    serializer.roundTrip(method)
    new String(serialized, "UTF8")
  }

}


class JsonLoader(content: String) extends BaseLoader(content) {

  private var jsonContent: String = null

  override protected def load(content: String) {
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

  override def getJson = jsonContent

}


class WikiDateExtractor(rootNode: WtNode) {

  private val ctx = JXPathContext.newContext(rootNode)

  def getInternalLinks: List[String] = {
    import scala.collection.JavaConversions._
    ctx.iterate("//WtInternalLink").map(item => {
      val wtNode = item.asInstanceOf[WtNode].get(0)
      val value = wtNode.asInstanceOf[WtPageName].getAsString
      value
    }).toList
  }
}
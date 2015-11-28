package com.hudvin.wikipro

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import de.fau.cs.osr.ptk.common.jxpath.AstNodePointerFactory
import org.apache.commons.jxpath.JXPathContext
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl
import org.sweble.wikitext.parser.nodes.{WtNode, WtPageName}
import org.sweble.wikitext.parser.utils.NonExpandingParser


import scala.collection.mutable.ListBuffer

/**
  * Created by kontiki on 08.11.15.
  */
object XParser {

  private val WARNINGS_ENABLED = false
  private val GATHER_RTD: Boolean = true
  private val AUTO_CORRECT: Boolean = false

  JXPathContextReferenceImpl.addNodePointerFactory(new AstNodePointerFactory)
  private val parser = new NonExpandingParser(WARNINGS_ENABLED, GATHER_RTD, AUTO_CORRECT)
  def getParser(): NonExpandingParser = parser

}

class Extractor(content:String){

  private val ast =  XParser.getParser().parseArticle(content,"")
  private val ctx = JXPathContext.newContext(ast)

  def getAst() = ast

  def getInternalLinks: List[String] ={
    val internalLinks = new ListBuffer[String]
    val it =  ctx.iterate("//WtInternalLink")
    while(it.hasNext){
      val n: WtNode = it.next.asInstanceOf[WtNode].get(0)
      val value = (n.asInstanceOf[WtPageName]).getAsString
      internalLinks+=value
    }
    internalLinks.toList
  }


}


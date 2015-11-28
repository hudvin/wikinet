package com.hudvin.wikipro

import java.io._

import com.datastax.driver.core.{BoundStatement, Cluster}
import com.google.gson.Gson
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream

trait Importer{

  def  insert(wikiPage: WikiPage)

  def close()

}


class JsonImporter(outputFile:String) extends  Importer{

  private val bz2Os =  new BZip2CompressorOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile),32768*100))
  private val bz5printWriter = new PrintWriter(bz2Os)


  override def insert(wikiPage: WikiPage): Unit = {
    val jsonString = new Gson().toJson(wikiPage)
    bz5printWriter.println(jsonString)
  }

  def close(): Unit ={
    bz5printWriter.close()
  }

}


class CassandraImporter(clusterHost:String) extends Importer{

  private val cluster = connect(clusterHost)
  private val session = cluster.connect()


  val statement = session.prepare(
    "INSERT INTO wiki.page( title, contributor_id, contributor_username, id, ns, redirect_title, revision_comment," +
      " revision_id, revision_minor, revision_parent_id, revision_sha1, revision_text, revision_timestamp)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")


  def connect(node:String):Cluster =  {
    val cluster = Cluster.builder()
      .addContactPoint(node)
      .build()
    cluster
  }

  override def insert(wikiPage: WikiPage): Unit ={
    try {
      import Predef.{intWrapper => _}
      val boundStatement = new BoundStatement(statement)
      val params = Seq(
        wikiPage.title,
        wikiPage.revision.contributor.id, wikiPage.revision.contributor.username,
        wikiPage.id, wikiPage.ns,
        wikiPage.redirectTitle, wikiPage.revision.comment, wikiPage.revision.id,
        wikiPage.revision.minor, wikiPage.revision.parentId,
        wikiPage.revision.sha1, wikiPage.revision.text, wikiPage.revision.timestamp)
        .map(i => i.asInstanceOf[Object])
      session.execute(boundStatement.bind(params: _*))
    }catch {
      case e: Throwable =>{
        println("********")
        e.printStackTrace()
        println(wikiPage)
        println("========")
      }
    }
  }

  def close() {
    cluster.close()
  }

}

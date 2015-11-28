package com.hudvin.wikinet.converter.parsers

import scala.collection.mutable.Stack
import scala.xml.pull.{EvElemEnd, EvElemStart, EvText, XMLEventReader}

/**
  * Created with IntelliJ IDEA.
  * User: hudvin
  * Date: 10/30/12
  * Time: 3:12 PM
  * To change this template use File | Settings | File Templates.
  */


trait Parser[T] {

  def parse(xmlReader: XMLEventReader, callback: (T) => Unit)

}


class PageParser extends Parser[WikiPage] {

  private val PAGE_PATH = List("page", "mediawiki")
  private val REVISION_PATH = List("revision", "page", "mediawiki")
  private val CONTRIBUTOR_PATH = List("contributor", "revision", "page", "mediawiki")

  private val PAGE_TAG: String = "page"
  private val REVISION_TAG = "revision_tag"
  private val PARENTID_TAG = "parentid"
  private val TIMESTAMP_TAG = "timestamp"
  private val TITLE_TAG = "title"
  private val NS_TAG = "ns"
  private val ID_TAG = "id"
  private val CONTRIBUTOR_TAG = "contributor"
  private val MINOR_TAG = "minor"
  private val COMMENT_TAG = "comment"
  private val SHA1_TAG = "sha1"
  private val TEXT_TAG = "text"
  private val REDIRECT_TAG: String = "redirect"
  private val TITLE_ATTR: String = "title"
  private val USERNAME_TAG = "username"

  private val path: scala.collection.mutable.Stack[String] = new Stack[String]


  def parse(xmlReader: XMLEventReader, callback: (WikiPage) => Unit) {
    var wikiPage: WikiPage = null
    var buffer: StringBuilder = null
    while (xmlReader.hasNext) {
      val v = xmlReader.next
      v match {
        case EvElemStart(_, PAGE_TAG, _, _) => {
          path.push(PAGE_TAG)
          wikiPage = new WikiPage
        }
        case EvElemStart(_, label, attr_, _) => {
          path.push(label)
          buffer = new StringBuilder
          label match {
            case REDIRECT_TAG => {
              val redirect = attr_.get(TITLE_ATTR).get.toString()
              if (attr_.get(TITLE_ATTR) != None) {
                wikiPage.redirectTitle = redirect
              }
            }
            case _ => {}
          }
        }
        case EvElemEnd(_, label) => {
          path.pop()
          label match {
            case PAGE_TAG => {
              callback(wikiPage)
            }
            case TITLE_TAG => {
              wikiPage.title = buffer.toString()
            }
            case NS_TAG => {
              wikiPage.ns = getIntValue(buffer.toString())
            }
            case ID_TAG => {
              path.toList match {
                case PAGE_PATH => {
                  wikiPage.id = getIntValue(buffer.toString())
                }
                case REVISION_PATH => {
                  wikiPage.revision.id = getIntValue(buffer.toString())
                }
                case CONTRIBUTOR_PATH => {
                  wikiPage.revision.contributor.id = getIntValue(buffer.toString())
                }
              }
            }
            //            case REDIRECT_TAG => {
            //              wikiPage.redirectTitle = buffer.toString()
            //            }
            case REVISION_TAG => {
            }
            case PARENTID_TAG =>
              wikiPage.revision.parentId = getIntValue(buffer.toString)

            case TIMESTAMP_TAG => {
              wikiPage.revision.timestamp = buffer.toString()
            }
            case USERNAME_TAG => {
              wikiPage.revision.contributor.username = buffer.toString()
            }
            case MINOR_TAG => {
              wikiPage.revision.minor = buffer.toString()
            }
            case COMMENT_TAG => {
              wikiPage.revision.comment = buffer.toString()
            }
            case SHA1_TAG => {
              wikiPage.revision.sha1 = buffer.toString()
            }
            case TEXT_TAG => {
              wikiPage.revision.text = buffer.toString()


            }
            case _ => {}
          }
        }
        case EvText(text) => {
          buffer.append(text)
        }
        case _ => {}
      }
    }
  }

  private def getIntValue(strValue: String): Int = strValue match {
    case "" => -1
    case _ => strValue.toInt
  }


}
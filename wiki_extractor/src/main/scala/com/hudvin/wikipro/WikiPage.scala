package com.hudvin.wikipro;

class WikiPage {

  var title: String = ""
  var ns: Int = -1
  var id: Long = -1
  var redirectTitle: String = ""
  val revision = new Revision()

  override def toString = "title: " + title + ", ns: " + ns + ", redirectTitle: " + redirectTitle + ", revision:" + revision.toString
}


class Revision {

  var id: Long = -1
  var parentId: Long = -1
  var timestamp: String = ""
  var comment: String = ""
  var sha1: String = ""
  var text: String = ""
  var minor: String = ""

  val contributor = new Contributor()

  override def toString = "id: " + id + ",parentId: " + parentId + ",timestamp: " +
    " " + timestamp + ", contributor: " + contributor.toString + ",comment: " +
    comment + ", sha1: " + sha1 + ", text: " + text + ",minor: " + minor

  class Contributor {

    var username: String = ""
    var id: Long = -1

    override def toString = "username: " + username + ",id: " + id
  }

}



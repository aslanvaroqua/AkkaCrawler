package com.wishclouds.extractors

import scala.util.matching.Regex
import com.wishclouds.engine.{AttributeTitleExtractorFeed, DefaultTitleExtractorFeed}
import com.wishclouds.engine.Protocol.QueryFailed
import org.jsoup.nodes.Document
import scala.Some


/**
 * User: aslanvaroqua
 * Date: 12/19/2013
 * Time: 9:38 AM
 */


object TitleExtractors {

  sealed trait TitleExtractor


  def query(d: Document, selector: String) = selector match {
    case s:String if d.select(s).isEmpty => QueryFailed("false")
    case s: String =>
      val a = d.select(s).first.text()
      a
      a
  }

  def attributeQuery(d: Document, selector: String, attribute: String): String = selector match {
    case s: String => d.select("meta[property=og:title:amount]").attr(attribute)
    case _ => "failed"
  }


  def matcher: Regex = "*".r

  case object defaultTitleExtractor extends TitleExtractor {

    def apply(titleExtractorFeed: DefaultTitleExtractorFeed): Option[String] = {

      titleExtractorFeed match {

        case DefaultTitleExtractorFeed(d: Document, metaTitleSelector: List[String], metaTitleExtractor: String) => {

          def findFirstSelector(d: Document, selectors: List[String]): Option[String] = {
            selectors match {
              case x :: xs => query(d, x) match {
                case element: String => Some(element)
                case _ => findFirstSelector(d, xs)
              }


            }
          }

          findFirstSelector(d, metaTitleSelector)


        }
      }

    }
  }

  case object attributeTitleExtractor extends TitleExtractor {

    def apply(attributeExtractorFeed: AttributeTitleExtractorFeed): Option[String] = {


      def grabTitle(titleRaw: Option[String]): Option[String] = {

        val finalTitle = titleRaw match {

          case Some(title) =>
            val matches = matcher.findAllMatchIn(title)
            matches.foldLeft("")(_ + _.matched) match {
              case "" => None
              case result => try
                Some(result.toDouble.toString())
              catch {
                case ex: Exception =>
                  println(titleRaw + " - " + result)
                  None
              }
            }
          case _ => None
        }
        finalTitle
      }

      attributeExtractorFeed match {

        case AttributeTitleExtractorFeed(d: Document, metaAttributeSelector: Option[String], metaTitleSelector: List[String], metaTitleExtractor: String) => {

          def findFirstSelector(d: Document, selectors: List[String], metaAttributeSelector: Option[String]): Option[String] = {
            (selectors, metaAttributeSelector) match {
              case (_, None) => throw new RuntimeException("Selector empty")
              case (x :: xs, Some(attr)) => attributeQuery(d, x, attr) match {
                case element: String => grabTitle(Some(element))
                case _ => findFirstSelector(d, xs, metaAttributeSelector)
              }
            }
          }
          findFirstSelector(d, metaTitleSelector, metaAttributeSelector)
        }
      }

    }
  }

}

package com.wishclouds.extractors

import scala.util.matching.Regex
import com.wishclouds.engine.{DefaultKeywordsExtractorFeed, ThumbnailImageExtractorFeed}
import com.wishclouds.engine.Protocol.QueryFailed
import org.jsoup.nodes.{Element, Document}
import scala.Some
import scalaz.std.map
import scala.collection.immutable.TreeMap


/**
 * User: aslanvaroqua
 * Date: 12/19/2013
 * Time: 9:38 AM
 */


object KeywordExtractors {

  sealed trait KeywordExtractor


  import scala.collection.JavaConversions._

  def defaultQuery(d: Document, selector: String) = { selector match{
    case selector:String if d.select(selector).isEmpty => "failed"
    case selector:String =>
      val a = d.select(selector).iterator.toList map {a => a.text()}
      a
      a
  }
  }

  def attributeQuery(d: Document, selector: String, attribute: String): String = selector match {
    case s: String => d.select(selector).attr(attribute)
    case _ => "failed"
  }


  case object defaultKeywordExtractor extends KeywordExtractor {

    def apply(defaultKeywordExtractorFeed: DefaultKeywordsExtractorFeed): Option[List[String]] = {


      defaultKeywordExtractorFeed match {

        case DefaultKeywordsExtractorFeed(d: Document, metaKeywordSelectors: List[String], metaKeywordExtractor: String) => {

          def findKeywords(d: Document, selectors: List[String]): Option[List[String]] = {


            //   val imageSelectors:List[String] = selectors.filter(a => !d.select(a).isEmpty)

            def findFirstSelector(d: Document, selectors: List[String]): List[String] = {
              selectors match {
                case x :: xs => defaultQuery(d, x) match {
                  case element: List[String] => element
                  case _ => findFirstSelector(d, xs)
                }


              }
            }

            Option(findFirstSelector(d, selectors))

          }
          findKeywords(d, metaKeywordSelectors)
        }
      }

    }
  }

}

package com.wishclouds.extractors

import scala.util.matching.Regex
import com.wishclouds.engine.{AttributeBrandExtractorFeed, DefaultBrandExtractorFeed}
import com.wishclouds.engine.Protocol.QueryFailed
import org.jsoup.nodes.Document
import scala.Some


/**
 * User: aslanvaroqua
 * Date: 12/19/2013
 * Time: 9:38 AM
 */


object BrandExtractors {

  sealed trait BrandExtractor


  def query(d: Document, selector: String) = selector match {
    case s:String if d.select(s).isEmpty => QueryFailed("false")
    case s: String =>   "Kate Spade"
  }

  def attributeQuery(d: Document, selector: String, attribute: String): String = selector match {
    case s: String => d.select(selector).attr(attribute)
    case _ => "failed"
  }


  def matcher: Regex = "*".r

  case object defaultBrandExtractor extends BrandExtractor {

    def apply(brandExtractorFeed: DefaultBrandExtractorFeed): Option[String] = {

      brandExtractorFeed match {

        case DefaultBrandExtractorFeed(d: Document, metaBrandSelector: List[String], metaBrandExtractor: String) => {

          Some(metaBrandExtractor.head.toString)

        }
      }

    }
  }

  case object attributeBrandExtractor extends BrandExtractor {

    def apply(attributeExtractorFeed: AttributeBrandExtractorFeed): Option[String] = {

      attributeExtractorFeed match {

        case AttributeBrandExtractorFeed(d: Document, metaAttributeSelector: Option[String], metaBrandSelector: List[String], metaBrandExtractor: String) => {

          def findFirstSelector(d: Document, selectors: List[String], metaAttributeSelector: Option[String]): Option[String] = {
            (selectors, metaAttributeSelector) match {
              case (_, None) => throw new RuntimeException("Selector empty")
              case (x :: xs, Some(attr)) => attributeQuery(d, x, attr) match {
                case element: String => Some(element)
                case _ => findFirstSelector(d, xs, metaAttributeSelector)
              }
            }
          }
          findFirstSelector(d, metaBrandSelector, metaAttributeSelector)
        }
      }

    }
  }

}

package com.wishclouds.extractors

import scala.util.matching.Regex
import com.wishclouds.engine.{DefaultPriceExtractorFeed, AttributePriceExtractorFeed}
import org.jsoup.nodes.Document
import org.jsoup.nodes.{Element, Document}
import org.jsoup.Jsoup
import scala.Some
import scala.collection.JavaConverters._
import com.wishclouds.engine.Protocol.QueryFailed

/**
 * User: aslanvaroqua
 * Date: 12/19/2013
 * Time: 9:38 AM
 */
object PriceExtractors {

  sealed trait PriceExtractor


  def query(d: Document, selector: String) = selector match {
    case s:String if d.select(s).isEmpty => QueryFailed("false")
    case s: String =>
      val a = d.select(s).first().toString()
      a

}


  def attributeQuery(d: Document, selector: String, attribute: String) = selector match {

    case s:String if d.select(s).isEmpty => QueryFailed("false")
    case s:String => d.select(s.toString()).attr(attribute)


  }


  def matcher: Regex = "[0-9.]*".r

  case object defaultPriceExtractor extends PriceExtractor {

    def apply(priceExtractorFeed: DefaultPriceExtractorFeed): Option[String] = {


      def grabPrice(priceRaw: Option[String]): Option[String] = {

        val finalPrice = priceRaw match {

          case Some(price) =>
            val matches = matcher.findAllMatchIn(price)
            matches.foldLeft("")(_ + _.matched) match {
              case "" => None
              case result => try
                Some(result.toDouble.toString())
              catch {
                case ex: Exception =>
                  println(priceRaw + " - " + result)
                  None
              }
            }
          case _ => None

        }

        finalPrice
      }

      priceExtractorFeed match {

        case DefaultPriceExtractorFeed(d: Document, metaPriceSelector: List[String], metaPriceExtractor: String) => {

          def findFirstSelector(d: Document, selectors: List[String]): Option[String] = {
            selectors match {
              case x :: xs => query(d, x) match {
                case QueryFailed(n) => findFirstSelector(d, xs)
                case element: String => grabPrice(Some(element));
                case _ => findFirstSelector(d, xs)
              }


            }
          }

          findFirstSelector(d, metaPriceSelector)

        }
      }

    }
  }

  case object attributePriceExtractor extends PriceExtractor {

    def apply(attributeExtractorFeed: AttributePriceExtractorFeed): Option[String] = {


      def grabPrice(priceRaw: Option[String]): Option[String] = {

        val finalPrice = priceRaw match {

          case Some(price) =>
            val matches = matcher.findAllMatchIn(price)
            matches.foldLeft("")(_ + _.matched) match {
              case "" => None
              case result => try
                Some(result.toDouble.toString())
              catch {
                case ex: Exception =>
                  println(priceRaw + " - " + result)
                  None
              }
            }
          case _ => None
        }
        finalPrice
      }

      attributeExtractorFeed match {

        case AttributePriceExtractorFeed(d: Document, metaAttributeSelector: Option[String], metaPriceSelector: List[String], metaPriceExtractor: String) => {

          def findFirstSelector(d: Document, selectors: List[String], metaAttributeSelector: Option[String]): Option[String] = {
            (selectors, metaAttributeSelector) match {
              case (_, None) => throw new RuntimeException("Selector empty")
              case (x :: xs, Some(attr)) => attributeQuery(d, x, attr) match {
                case QueryFailed(n) => findFirstSelector(d, xs, metaAttributeSelector)
                case element: String => grabPrice(Some(element))
                case _ => findFirstSelector(d, xs, metaAttributeSelector)
              }
            }
          }
          findFirstSelector(d, metaPriceSelector, metaAttributeSelector)
        }
      }

    }
  }

}

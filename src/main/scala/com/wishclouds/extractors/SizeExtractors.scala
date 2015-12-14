package com.wishclouds.extractors

import scala.util.matching.Regex
import com.wishclouds.engine.{AttributeSizeExtractorFeed}
import com.wishclouds.engine.Protocol.QueryFailed
import org.jsoup.nodes.Document
import scala.Some
import scala.util.matching.Regex
import com.wishclouds.engine.{DefaultCategoryExtractorFeed}
import org.jsoup.nodes.Document
import scala.Some
import scala.collection.JavaConversions._

/**
 * User: aslanvaroqua
 * Date: 12/19/2013
 * Time: 9:38 AM
 */


object SizeExtractors {

  sealed trait SizeExtractor


  def selectionQuery(d: Document, selector: String, attribute: String):String = d.select(selector).attr(attribute)

  def attributeQuery(d: Document, selector: String, attribute:String):List[String] = { selector match{
    case selector:String if d.select(selector).isEmpty => List()
    case selector:String => d.select(selector).iterator.toList map {a => a.attr(attribute)}
  }
  }


  def matcher: Regex = "*".r


  case object attributeSizeExtractor extends SizeExtractor {

    def apply(attributeExtractorFeed: AttributeSizeExtractorFeed): Option[List[String]] = {


      def grabSize(sizeRaw: Option[String]): Option[String] = {

        val finalSize = sizeRaw match {

          case Some(title) =>
            val matches = matcher.findAllMatchIn(title)
            matches.foldLeft("")(_ + _.matched) match {
              case "" => None
              case result => try
                Some(result.toDouble.toString())
              catch {
                case ex: Exception =>
                  println(sizeRaw + " - " + result)
                  None
              }
            }
          case _ => None
        }
        finalSize
      }

      attributeExtractorFeed match {

        case AttributeSizeExtractorFeed(d: Document, metaAttributeSelector: Option[String], metaSizeSelector: List[String], metaSizeExtractor: String) => {

          def findSizes(d: Document, selectors: List[String], metaAttributeSelector: Option[String]): Option[List[String]] = {

            val attribute = metaAttributeSelector match {
              case Some(s:String) => s
              case _ => "unavailable"
            }

            val sizeSelectors:List[String] = selectors.filter(a => !d.select(a).isEmpty)



            val sizes:List[String] = attributeQuery(d, sizeSelectors.head, attribute)

             sizes


            Option(sizes)

          }
          findSizes(d, metaSizeSelector, metaAttributeSelector)
        }
      }

    }
  }

}

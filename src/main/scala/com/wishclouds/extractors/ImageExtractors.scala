package com.wishclouds.extractors

import scala.util.matching.Regex
import com.wishclouds.engine.{ThumbnailImageExtractorFeed}
import com.wishclouds.engine.Protocol.QueryFailed
import org.jsoup.nodes.Document
import scala.Some


/**
 * User: aslanvaroqua
 * Date: 12/19/2013
 * Time: 9:38 AM
 */


object ImageExtractors {

  sealed trait ImageExtractor


  def thumbnailQuery(d: Document, selector: String, attribute: String) = {
     d.select(selector).attr(attribute)
  }

  def attributeQuery(d: Document, selector: String, attribute: String): String = selector match {
    case s: String => d.select("meta[property=og:title:amount]").attr(attribute)
    case _ => "failed"
  }


  def matcher: Regex = "*".r


  case object thumbnailImageExtractor extends ImageExtractor {

    def apply(thumbnailExtractorFeed: ThumbnailImageExtractorFeed): Option[List[String]] = {


      thumbnailExtractorFeed match {

        case ThumbnailImageExtractorFeed(d: Document, metaAttributeSelector: Option[String], metaImageSelector: List[String], metaImageExtractor: String) => {

          def findImages(d: Document, selectors: List[String], metaAttributeSelector: Option[String]): Option[List[String]] = {

            val attribute = metaAttributeSelector match {
              case Some(s:String) => s
              case _ => "unavailable"
            }

            val imageSelectors:List[String] = selectors.filter(a => !d.select(a).isEmpty)


            val images:List[String] = imageSelectors map {a => thumbnailQuery(d, a, attribute) }


            Option(images)

          }
         findImages(d, metaImageSelector, metaAttributeSelector)
        }
      }

    }
  }

}

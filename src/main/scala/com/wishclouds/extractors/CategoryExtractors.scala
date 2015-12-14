package com.wishclouds.extractors

import scala.util.matching.Regex
import com.wishclouds.engine.{DefaultCategoryExtractorFeed}
import org.jsoup.nodes.Document
import scala.Some



/**
 * User: aslanvaroqua
 * Date: 12/19/2013
 * Time: 9:38 AM
 */


object CategoryExtractors {

  sealed trait CategoryExtractor


  import scala.collection.JavaConversions._

  def defaultQuery(d: Document, selector: String) = { selector match{
    case selector:String if d.select(selector).isEmpty => "failed"
    case selector:String => d.select(selector).iterator.toList map {a => a.text()}
  }
  }

  def attributeQuery(d: Document, selector: String, attribute: String): String = selector match {
    case s: String => d.select(selector).attr(attribute)
    case _ => "failed"
  }




  case object defaultCategoryExtractor extends CategoryExtractor {

    def apply(defaultCategoryExtractorFeed: DefaultCategoryExtractorFeed): Option[List[String]] = {


      defaultCategoryExtractorFeed match {

        case DefaultCategoryExtractorFeed(d: Document, metaCategorySelectors: List[String], metaCategoryExtractor: String) => {

          def findImages(d: Document, selectors: List[String]): Option[List[String]] = {


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
          metaCategorySelectors
          findImages(d, metaCategorySelectors)
        }
      }

    }
  }

}

//package com.wishclouds.extractors
//
//import scala.util.matching.Regex
//import com.wishclouds.engine.{DefaultDescriptionExtractorFeed}
//import com.wishclouds.engine.Protocol.QueryFailed
//import org.jsoup.nodes.Document
//import scala.Some
//
//
///**
// * User: aslanvaroqua
// * Date: 12/19/2013
// * Time: 9:38 AM
// */
//
//
//object BulletsExtractors {
//
//  sealed trait DescriptionExtractor
//
//
//  def query(d: Document, selector: String) = selector match {
//    case s:String if d.select(s).isEmpty => QueryFailed("false")
//    case s: String =>   d.select(s).first.text().replace("Description", "").replace("Description ", "")
//  }
//
//  def attributeQuery(d: Document, selector: String, attribute: String): String = selector match {
//    case s: String => d.select(selector).attr(attribute)
//    case _ => "failed"
//  }
//
//
//  def matcher: Regex = "*".r
//
//  case object defaultDescriptionExtractor extends DescriptionExtractor {
//
//    def apply(descriptionExtractorFeed: DefaultDescriptionExtractorFeed): Option[String] = {
//
//      descriptionExtractorFeed match {
//
//        case DefaultDescriptionExtractorFeed(d: Document, metaDescriptionSelector: List[String], metaDescriptionExtractor: String) => {
//
//          def findFirstSelector(d: Document, selectors: List[String]): Option[String] = {
//            selectors match {
//              case x :: xs => query(d, x) match {
//                case QueryFailed(_) => findFirstSelector(d, xs)
//                case element: String => Some(element)
//                case _ => findFirstSelector(d, xs)
//              }
//
//
//            }
//          }
//          val a =
//          findFirstSelector(d, metaDescriptionSelector)
//a
//          a
//
//        }
//      }
//
//    }
//  }
//
////  case object attributeDescriptionExtractor extends DescriptionExtractor {
////
////    def apply(attributeExtractorFeed: AttributeDescriptionExtractorFeed): Option[String] = {
////
////
////      def grabDescription(descriptionRaw: Option[String]): Option[String] = {
////
////        val finalDescription = descriptionRaw match {
////
////          case Some(description) =>
////            val matches = matcher.findAllMatchIn(description)
////            matches.foldLeft("")(_ + _.matched) match {
////              case "" => None
////              case result => try
////                Some(result.toDouble.toString())
////              catch {
////                case ex: Exception =>
////                  println(descriptionRaw + " - " + result)
////                  None
////              }
////            }
////          case _ => None
////        }
////        finalDescription
////      }
////
////      attributeExtractorFeed match {
////
////        case AttributeDescriptionExtractorFeed(d: Document, metaAttributeSelector: Option[String], metaDescriptionSelector: List[String], metaDescriptionExtractor: String) => {
////
////          def findFirstSelector(d: Document, selectors: List[String], metaAttributeSelector: Option[String]): Option[String] = {
////            (selectors, metaAttributeSelector) match {
////              case (_, None) => throw new RuntimeException("Selector empty")
////              case (x :: xs, Some(attr)) => attributeQuery(d, x, attr) match {
////                case element: String => grabDescription(Some(element))
////                case _ => findFirstSelector(d, xs, metaAttributeSelector)
////              }
////            }
////          }
////          findFirstSelector(d, metaDescriptionSelector, metaAttributeSelector)
////        }
////      }
////
////    }
////  }
//
//}

package com.wishclouds.engine

import org.jsoup.nodes.Document
import com.wishclouds.models.Metadata
import play.api.libs.json.Json
import akka.actor.Address

/**
 * User: Miguel A. Iglesias
 * Date: 11/14/13
 * Time: 1:52 PM
 */
object Protocol {


  case class Link(url: String, text: String)

  case class LinkRedis(link: Link, history: Set[Link] = Set())

  object Link {
    implicit val jsonFormat = Json.format[Link]
  }

  object LinkRedis {
    implicit val jsonFormat = Json.format[LinkRedis]
  }

  case class Fetched(document: Document)

  case class SingleLinkMsg(link: LinkRedis, metadata: Metadata)

  //  case class RedisProcessedLinkMsg(link: String, metadata: Metadata)

  case class MultipleLinkMsg(links: Set[LinkRedis], metadata: Metadata)

  case class MongoDataMsg(colors:List[String], sizes:List[String], title: String, price: String, regular_price: String, brand:String,
                          description:String, catalogue_id: String, url: String, images: List[String])

  case class ProductToSave(retailer: String, identifier: String, link: LinkRedis)

  case class Start(url:String, metadata: Metadata, doc: Document)

  case class CountProducts(retailer: String)

  case class QueryFailed(message: String)

  case class Count(products: Int, product_links: Int)

  case class StringMsg(message: String)

  case class DocumentToProcess(link: LinkRedis, document: Document, metadata: Metadata)

  case class ProductDetail(identifier: String, links: Set[LinkRedis])

  object ProductDetail {
    implicit val jsonFormat = Json.format[ProductDetail]
  }

  case class FailedLink(link: LinkRedis, metadata: Metadata)

  case object SuccessFulLink

  case object FailedLinks

  case object ShutDown

  case object Started


}

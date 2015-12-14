package com.wishclouds.engine

import com.wishclouds.engine.Protocol._
import akka.actor._
import scala.concurrent._
import org.jsoup.nodes.Document
import ExecutionContext.Implicits.global
import dispatch.Future
import scala.util.{Failure, Success}
import com.wishclouds.models.Metadata
import scala.Some
import com.wishclouds.Worker.WorkComplete
import com.wishclouds.extractors.PriceExtractors.{attributePriceExtractor, defaultPriceExtractor}
import com.wishclouds.extractors.TitleExtractors.{attributeTitleExtractor, defaultTitleExtractor}
import com.wishclouds.extractors.ImageExtractors.thumbnailImageExtractor
import com.wishclouds.extractors.DescriptionExtractors.defaultDescriptionExtractor
import com.wishclouds.extractors.BrandExtractors.{attributeBrandExtractor, defaultBrandExtractor}
import com.wishclouds.extractors.ColorExtractors.defaultColorExtractor
import com.wishclouds.extractors.SizeExtractors.{attributeSizeExtractor}

/**
 * User: Aslan Varoqua
 * Date: 10/31/13
 * Time: 6:15 PM
 */

case class DefaultPriceExtractorFeed(d: Document, metaPriceSelector: List[String], metaPriceExtractor: String)

case class AttributePriceExtractorFeed(d: Document, metaPriceAttribute: Option[String], metaPriceSelector: List[String], metaPriceExtractor: String)

case class DefaultTitleExtractorFeed(d: Document, metaTitleSelector: List[String], metaTitleExtractor: String)

case class AttributeTitleExtractorFeed(d: Document, metaTitleAttribute: Option[String], metaTitleSelector: List[String], metaTitleExtractor: String)

case class AttributeBrandExtractorFeed(d: Document, metaBrandAttribute: Option[String], metaBrandSelector: List[String], metaBrandExtractor: String)

case class DefaultBrandExtractorFeed(d: Document, metaBrandSelector: List[String], metaBrandExtractor: String)

case class AttributeCategoryExtractorFeed(d: Document, metaCategoryAttribute: Option[String], metaCategorySelector: List[String], metaCategoryExtractor: String)

case class DefaultCategoryExtractorFeed(d: Document, metaCategorySelector: List[String], metaCategoryExtractor: String)

case class DefaultColorExtractorFeed(d: Document, metaColorSelector: List[String], metaColorExtractor: String)

case class DefaultSizeExtractorFeed(d: Document, metaSizeSelector: List[String], metaSizeExtractor: String)

case class  AttributeSizeExtractorFeed(d: Document, metaSizeAttribute: Option[String], metaSizeSelector: List[String], metaSizeExtractor: String)

case class DefaultBulletExtractorFeed(d: Document, metaBulletSelector: List[String], metaBulletExtractor: String)

case class AttributeKeywordsExtractorFeed(d: Document, metaKeywordsAttribute: Option[String], metaKeywordsSelector: List[String], metaKeywordsExtractor: String)

case class DefaultKeywordsExtractorFeed(d: Document, metaKeywordsSelector: List[String], metaKeywordsExtractor: String)

case class DefaultDescriptionExtractorFeed(d: Document, metaDescriptionSelector: List[String], metaDescriptionExtractor: String)

case class ThumbnailImageExtractorFeed(d:Document, metaImageAttribute:Option[String], metaImageSelectors:List[String], metaImageExtractor:String)



class LinksWorker(parent:ActorRef, master: ActorSelection) extends Actor {

  import scala.concurrent.duration._

  context.setReceiveTimeout(20 seconds)

  def findLinks(url:String, d: Document, metadata: Metadata): Future[Success[MongoDataMsg]] = {

    val metaPriceSelectors: List[String] = metadata.price.price_selectors
    val metaPriceExtractor: String = metadata.price.price_extractor
    val metaPriceAttribute: Option[String] = metadata.price.price_attribute
    val metaTitleExtractor: String = metadata.title.title_extractor
    val metaTitleAttribute: Option[String] = metadata.title.title_attribute
    val metaTitleSelectors: List[String] = metadata.title.title_selectors
    val metaImageExtractor: String = metadata.image.image_extractor
    val metaImageAttribute: Option[String] = metadata.image.image_attribute
    val metaImageSelectors: List[String] = metadata.image.image_selectors
    val metaDescriptionExtractor: String = metadata.description.description_extractor
    val metaDescriptionAttribute: Option[String] = metadata.description.description_attribute
    val metaDescriptionSelectors: List[String] = metadata.description.description_selectors
    val metaKeywordExtractor: String = metadata.Keywords.keyword_extractor
    val metaKeywordAttribute: Option[String] = metadata.Keywords.keyword_attribute
    val metaKeywordSelectors: List[String] = metadata.Keywords.keyword_selectors
    val metaBulletsExtractor: String = metadata.bullets.bullet_extractor
    val metaBulletAttribute: Option[String] = metadata.bullets.bullet_attribute
    val metaBulletSelectors: List[String] = metadata.bullets.bullet_selectors
    val metaCategoryExtractor: String = metadata.category.category_extractor
    val metaCategoryAttribute: Option[String] = metadata.category.category_attribute
    val metaCategorySelectors: List[String] = metadata.category.category_selectors
    val metaBrandExtractor: String = metadata.brand.brand_extractor
    val metaBrandAttribute: Option[String] = metadata.brand.brand_attribute
    val metaBrandSelectors: List[String] = metadata.brand.brand_selectors
    val metaColorExtractor: String = "defaultColorExtractor"
    val metaColorAttribute: Option[String] = metadata.color.color_attribute
    val metaColorSelectors: List[String] = metadata.color.color_selectors
    val metaSizeExtractor: String = metadata.size.size_extractor
    val metaSizeAttribute: Option[String] = metadata.size.size_attribute
    val metaSizeSelectors: List[String] = metadata.size.size_selectors


    val priceExtractorSelection = future{ metaPriceExtractor match {
      case "defaultPriceExtractor" => defaultPriceExtractor(DefaultPriceExtractorFeed(d, metaPriceSelectors, metaPriceExtractor))
      case "attributePriceExtractor" => attributePriceExtractor(AttributePriceExtractorFeed(d, metaPriceAttribute, metaPriceSelectors, metaPriceExtractor))
    }
    }

    val brandExtractorSelection = future{ metaPriceExtractor match {
      case "defaultPriceExtractor" => defaultBrandExtractor(DefaultBrandExtractorFeed(d, metaBrandSelectors, metaBrandExtractor))
      case "attributePriceExtractor" => attributeBrandExtractor(AttributeBrandExtractorFeed(d, metaBrandAttribute, metaBrandSelectors, metaBrandExtractor))
    }
    }

    val titleExtractorSelection = future {metaTitleExtractor match {
      case "defaultTitleExtractor" => defaultTitleExtractor(DefaultTitleExtractorFeed(d, metaTitleSelectors, metaTitleExtractor))
      // case "attributeTitleExtractor" => attributeTitleExtractor(AttributeTitleExtractorFeed(d, metaPriceAttribute, metaPriceSelector, metaPriceExtractor))///attributePriceExtractor(AttributeExtractorFeed(d, metaPriceAttribute, metaPriceSelector, metaPriceExtractor))
    }
    }

    val descriptionExtractorSelection = future {metaDescriptionExtractor match {
      case "defaultDescriptionExtractor" => defaultDescriptionExtractor(DefaultDescriptionExtractorFeed(d, metaDescriptionSelectors, metaDescriptionExtractor))
      // case "attributeTitleExtractor" => attributeTitleExtractor(AttributeTitleExtractorFeed(d, metaPriceAttribute, metaPriceSelector, metaPriceExtractor))///attributePriceExtractor(AttributeExtractorFeed(d, metaPriceAttribute, metaPriceSelector, metaPriceExtractor))
    }
    }

    val colorExtractorSelection = future {metaColorExtractor match {
      // case "defaultImageExtractor" => defaultTitleExtractor(DefaultTitleExtractorFeed(d, metaTitleSelector, metaTitleExtractor))
      case "defaultColorExtractor" => defaultColorExtractor(DefaultColorExtractorFeed(d, metaColorSelectors, metaColorExtractor))///attributePriceExtractor(AttributeExtractorFeed(d, metaPriceAttribute, metaPriceSelector, metaPriceExtractor))
    }
    }

    val sizeExtractorSelection = future {metaSizeExtractor match {
      // case "defaultImageExtractor" => defaultTitleExtractor(DefaultTitleExtractorFeed(d, metaTitleSelector, metaTitleExtractor))
      case "attributeSizeExtractor" => attributeSizeExtractor(AttributeSizeExtractorFeed(d, metaSizeAttribute, metaSizeSelectors, metaSizeExtractor))
    }
    }

    val imageExtractorSelection = future {metaImageExtractor match {
     // case "defaultImageExtractor" => defaultTitleExtractor(DefaultTitleExtractorFeed(d, metaTitleSelector, metaTitleExtractor))
       case "thumbnailImageExtractor" => thumbnailImageExtractor(ThumbnailImageExtractorFeed(d, metaImageAttribute, metaImageSelectors, metaImageExtractor))///attributePriceExtractor(AttributeExtractorFeed(d, metaPriceAttribute, metaPriceSelector, metaPriceExtractor))
    }
    }


   for {
     Some(title) <- titleExtractorSelection
     Some(colors) <- colorExtractorSelection
     Some(price) <- priceExtractorSelection
     Some(images) <- imageExtractorSelection
     Some(description) <- descriptionExtractorSelection
     Some(sizes) <- sizeExtractorSelection
    } yield {
      Success(MongoDataMsg(colors, sizes, title, price, price, "brand", description, "catalogueId", url, images))
    }




  }

import akka.pattern.pipe
  override def receive = {
    case ReceiveTimeout => self ! PoisonPill
    //gathers all the links on the document and sends them back to replyTo, which usually is the master node
    case Start(url:String, d: Metadata, e: Document) => {
      findLinks(url, e, d).recoverWith{
        case ex => Future.successful(Failure(ex))
      } pipeTo self
    }
    case s@Success(_) =>
      parent ! s
     context.stop(self)
    case f@Failure(_) => context.stop(self)

    case StringMsg(a) => sender ! StringMsg("success"); context.stop(self)
  }
}

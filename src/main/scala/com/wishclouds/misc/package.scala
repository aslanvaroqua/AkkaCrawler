package com.wishclouds.explorer

import com.mongodb.casbah.MongoConnection
import com.wishclouds.models._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports.MongoDBList
import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat._
import com.novus.salat.global._
import org.elasticsearch.cluster.metadata.MetaData

/**
 * User: Miguel A. Iglesias
 * Date: 11/26/13
 * Time: 12:43 PM
 */
package object server {

  object MetaModel {

    val connection: MongoConnection = MongoConnection("", 27017)
    val db = connection("explorer")


    implicit def dbListToString(dbList: Option[MongoDBList]): List[String] = dbList.map(_.toList).getOrElse(List("a")).map(_.toString)



    def findName(retailer: String): Option[Name] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Name(
          dbo.get("_id").toString,
          dbo.get("name").toString

        )

    }

    def findPrice(retailer: String): Option[Price] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Price(
          dbo.getAs[MongoDBList]("price_selectors"),
          dbo.getAs[String]("price_attribute"),
          dbo.get("price_extractor").toString

        )
    }

    def findTitle(retailer: String): Option[Title] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Title(
          dbo.getAs[MongoDBList]("title_selectors"),
          dbo.getAs[String]("title_attribute"),
          dbo.get("title_extractor").toString
        )
    }

    def findImage(retailer: String): Option[Image] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Image(
          dbo.get("image_extractor").toString,
          dbo.getAs[String]("image_attribute"),
          dbo.getAs[MongoDBList]("image_selectors")

        )
    }


    def findDescription(retailer: String): Option[Description] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Description(
          dbo.get("description_extractor").toString,
          dbo.getAs[String]("description_attribute"),
          dbo.getAs[MongoDBList]("description_selectors")

        )
    }





    def findColor(retailer: String): Option[Color] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Color(
          dbo.get("color_extractor").toString,
          dbo.getAs[String]("color_attribute"),
          dbo.getAs[MongoDBList]("color_selectors")

        )
    }



    def findCategory(retailer: String): Option[Category] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Category(

          dbo.get("category_extractor").toString,
          dbo.getAs[String]("category_attribute"),
          dbo.getAs[MongoDBList]("category_selectors")
        )
    }

    def findKeywords(retailer: String): Option[Keywords] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Keywords(

          dbo.get("keyword_extractor").toString,
          dbo.getAs[String]("keyword_attribute"),
          dbo.getAs[MongoDBList]("keyword_selectors")
        )
    }

    def findSize(retailer: String): Option[Size] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Size(


          dbo.get("size_extractor").toString,
          dbo.getAs[String]("size_attribute"),
          dbo.getAs[MongoDBList]("size_selectors")

        )
    }

    def findBrand(retailer: String): Option[Brand] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Brand(

          dbo.get("brand_extractor").toString,
          dbo.getAs[String]("brand_attribute"),
          dbo.getAs[MongoDBList]("brand_selectors")

        )
    }
    def findBullets(retailer: String): Option[Bullets] = db("meta").findOne(MongoDBObject("name" -> retailer)).map {
      dbo =>
        Bullets(
          dbo.get("bullet_extractor").toString,
          dbo.getAs[String]("bullet_attribute"),
          dbo.getAs[MongoDBList]("bullet_selectors")
        )
    }

  }

}

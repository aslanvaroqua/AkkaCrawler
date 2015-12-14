package com.wishclouds.models

/**
 * User: Aslan Varoqua
 * Date: 10/29/13
 * Time: 3:04 PM
 */

case class Metadata(price:Price, title:Title, image:Image, description:Description,
                    color:Color, Keywords:Keywords, category:Category, size:Size,
                    brand:Brand, bullets:Bullets)

case class Price(price_selectors: List[String],
                 price_attribute: Option[String],
                 price_extractor: String)

case class Title(title_selectors: List[String],
                 title_attribute: Option[String],
                 title_extractor: String)

case class Image(image_extractor: String,
                 image_attribute: Option[String],
                 image_selectors: List[String])

case class Description(description_extractor: String,
                 description_attribute: Option[String],
                 description_selectors: List[String])


case class Color(color_extractor: String,
                 color_attribute: Option[String],
                 color_selectors: List[String])

case class Keywords(keyword_extractor: String,
                    keyword_attribute: Option[String],
                    keyword_selectors: List[String])

case class Category(category_extractor: String,
                    category_attribute: Option[String],
                    category_selectors: List[String])

case class Size(size_extractor: String,
                size_attribute: Option[String],
                size_selectors: List[String])

case class Brand(brand_extractor: String,
                 brand_attribute: Option[String],
                 brand_selectors: List[String])

case class Bullets(bullet_extractor: String,
                   bullet_attribute: Option[String],
                   bullet_selectors: List[String])

case class Name(_id: String,
                name: String
                 )






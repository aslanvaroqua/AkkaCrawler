
package com.wishclouds

import akka.actor.{Props, ActorSystem}
import akka.testkit._
import org.specs2.mutable.SpecificationLike
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import org.specs2.time.NoTimeConversions
import org.specs2.specification.Scope
import com.wishclouds.engine.LinksWorker
import com.wishclouds.WorkExecutor
import com.wishclouds.explorer.server.MetaModel
import dispatch._
import scala.concurrent.Future
import org.jsoup.nodes.Document
import com.wishclouds.engine.Protocol.{StringMsg, MongoDataMsg, Start}
import scala.util.Failure
import scala.util.Success
import com.wishclouds.Worker.WorkComplete
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.TypeImports.MongoConnection
import com.wishclouds.models.Metadata



class MasterSpec extends TestKit(ActorSystem("test")) with ImplicitSender
with SpecificationLike with NoTimeConversions {

  val connection: MongoConnection = MongoConnection("54.204.16.176", 27017)
  val db = connection("explorer")
  val httpCall = new Http()
  httpCall.configure {
    builder =>
      builder.setRequestTimeoutInMs(200)
      builder.setMaxRequestRetry(3)
  }

  def fetchPage(link: String, times: Int = 3): Future[Document] = {
    if (times < 0)
      Future.failed(new NoSuchElementException)
    else {
      val svc = url(link)
      httpCall(svc setFollowRedirects true OK as.jsoup.Document) recoverWith {
        case e =>
          Future(blocking(Thread.sleep(3000))).flatMap {
            _ =>
            // log.error(s"Retrying link $link. $times times left", e)
              fetchPage(link, times - 1)
          }
      }

    }

  }



  //val toms_metadata = MetaModel.findMetadata("toms").get
  val n = "kate spade"
  val kate_spade_metadata:Metadata =   {
    Metadata(
      MetaModel.findPrice(n).get,
      MetaModel.findTitle(n).get,
      MetaModel.findImage(n).get,
      MetaModel.findDescription(n).get,
      MetaModel.findColor(n).get,
      MetaModel.findKeywords(n).get,
      MetaModel.findCategory(n).get,
      MetaModel.findSize(n).get,
      MetaModel.findBrand(n).get,
      MetaModel.findBullets(n).get)
  }


//  "The worker" should {
//    "replies with success within 60 seconds after its done" in new Actors {
//
//      fetchPage("http://www.toms.com/mens/shoes/paseo/movember-grey-suede-men-s-paseos/s") onComplete {
//        case Success(d) =>
//          val startMessage = StringMsg("message test")
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case StringMsg(a) => assert(a === "success"); println(s"Got: $a")
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//        }
//      }
//
//
//
//  }
//////
//////
//  "The worker" should {
//    "reply with the correct price with Default Price Extractor" in new Actors {
//
//      fetchPage("http://www.toms.com/mens/shoes/paseo/movember-grey-suede-men-s-paseos/s") onComplete {
//        case Success(d) =>
//          val startMessage = Start(toms_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          b
//          assert(b.toDouble === 74.00)
//          println(s"Got: $b") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//
//  }
////
////
////
////
//  "The worker" should {
//    "reply with the correct price with Attribute Price Extractor" in new Actors {
//
//      fetchPage("http://www.katespade.com/fortunes-resin-iphone-5-case/8ARU0296,en_US,pd.html?dwvar_8ARU0296_color=645&dwvar_8ARU0296_size=UNS&cgid=ks-accessories-tech#start=2&cgid=ks-accessories-tech") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g, h, i))=> {
//          println(b)
//          assert(b.toDouble === 40.00)
//          println(s"Got: $b") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//}

  "The worker" should {
    "reply with the correct color" in new Actors {
      val url = "http://www.katespade.com/addie-top/NJMU2099,en_US,pd.html?dwvar_NJMU2099_color=001&dwvar_NJMU2099_size=0&cgid=ks-clothing-tops#start=16&cgid=ks-clothing-tops"
      fetchPage(url) onComplete {
        case Success(d) =>
          val startMessage = Start(url, kate_spade_metadata, d)
          master.send(worker, startMessage)
        case Failure(e) =>
          master.send(worker, "failed")
      }


      master.receiveOne(60 seconds) match {
        case Success(MongoDataMsg(colors:List[String], sizes:List[String], title: String, price: String, regular_price: String, brand:String,
        description:String, catalogue_id: String, url: String, images: List[String]))=> {
          assert(colors === List("black", "cream"))
          assert(sizes === List("2", "8"))
          assert(title ===  "addie top")
          assert(price.toDouble === 178.00)
          assert(brand === "brand")
          assert(description === "a notched neckline and pleated front placket give this silk blouse its structural appeal. this soft silhouette is polished with a tweed pencil skirt and tights and looks just as luxe with dark denim and bold heels.")
          assert(images === List("http://s7d4.scene7.com/is/image/KateSpade/NJMU2099_001?$productgrid$"))
          println(colors, sizes, title, price , regular_price , brand , description , catalogue_id , url, images)
        }
          case e => throw new Error("Don't know what we got: " + e.toString)

      }
    }


  }

  "The worker" should {
    "reply with the correct color" in new Actors {
      val url = "http://www.katespade.com/addie-top/NJMU2099,en_US,pd.html?dwvar_NJMU2099_color=001&dwvar_NJMU2099_size=0&cgid=ks-clothing-tops#start=16&cgid=ks-clothing-tops"
      fetchPage(url) onComplete {
        case Success(d) =>
          val startMessage = Start(url, kate_spade_metadata, d)
          master.send(worker, startMessage)
        case Failure(e) =>
          master.send(worker, "failed")
      }


      master.receiveOne(60 seconds) match {
        case Success(MongoDataMsg(colors:List[String], sizes:List[String], title: String, price: String, regular_price: String, brand:String,
        description:String, catalogue_id: String, url: String, images: List[String]))=> {
          assert(colors === List("black", "cream"))
          assert(sizes === List("2", "8"))
          assert(title ===  "addie top")
          assert(price.toDouble === 178.00)
          assert(brand === "brand")
          assert(description === "a notched neckline and pleated front placket give this silk blouse its structural appeal. this soft silhouette is polished with a tweed pencil skirt and tights and looks just as luxe with dark denim and bold heels.")
          assert(images === List("http://s7d4.scene7.com/is/image/KateSpade/NJMU2099_001?$productgrid$"))
          println(colors, sizes, title, price , regular_price , brand , description , catalogue_id , url, images)
        }
        case e => throw new Error("Don't know what we got: " + e.toString)

      }
    }


  }



//  "The worker" should {
//    "reply with the correct description with Default Description Extractor for Kate Spade" in new Actors {
//
//      fetchPage("http://www.katespade.com/fortunes-resin-iphone-5-case/8ARU0296,en_US,pd.html?dwvar_8ARU0296_color=645&dwvar_8ARU0296_size=UNS&cgid=ks-accessories-tech#start=2&cgid=ks-accessories-tech") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          println(b)
//          assert(!d.isEmpty)
//          println(s"Got: $d") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//  }
//
//  "The worker" should {
//    "reply with the correct description with Attribut Description Extractor for Kate Spade" in new Actors {
//
//      fetchPage("http://www.katespade.com/fortunes-resin-iphone-5-case/8ARU0296,en_US,pd.html?dwvar_8ARU0296_color=645&dwvar_8ARU0296_size=UNS&cgid=ks-accessories-tech#start=2&cgid=ks-accessories-tech") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          println(b)
//          assert(!d.isEmpty)
//          println(s"Got: $d") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//  }
//
//  "The worker" should {
//    "reply with the correct description with the correct list of colors" in new Actors {
//
//      fetchPage("http://www.katespade.com/fortunes-resin-iphone-5-case/8ARU0296,en_US,pd.html?dwvar_8ARU0296_color=645&dwvar_8ARU0296_size=UNS&cgid=ks-accessories-tech#start=2&cgid=ks-accessories-tech") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          println(b)
//          assert(!d.isEmpty)
//          println(s"Got: $d") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//  }
//
//
//  "The worker" should {
//    "reply with the correct description with the correct list of sizes" in new Actors {
//
//      fetchPage("http://www.katespade.com/fortunes-resin-iphone-5-case/8ARU0296,en_US,pd.html?dwvar_8ARU0296_color=645&dwvar_8ARU0296_size=UNS&cgid=ks-accessories-tech#start=2&cgid=ks-accessories-tech") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          println(b)
//          assert(!d.isEmpty)
//          println(s"Got: $d") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//  }
//
//
//  "The worker" should {
//    "reply whether a color is unavailable" in new Actors {
//
//      fetchPage("http://www.katespade.com/fortunes-resin-iphone-5-case/8ARU0296,en_US,pd.html?dwvar_8ARU0296_color=645&dwvar_8ARU0296_size=UNS&cgid=ks-accessories-tech#start=2&cgid=ks-accessories-tech") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          println(b)
//          assert(!d.isEmpty)
//          println(s"Got: $d") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//  }
//
//
//  "The worker" should {
//    "reply with bullets for the Default Bullet Selector" in new Actors {
//
//      fetchPage("http://www.katespade.com/fortunes-resin-iphone-5-case/8ARU0296,en_US,pd.html?dwvar_8ARU0296_color=645&dwvar_8ARU0296_size=UNS&cgid=ks-accessories-tech#start=2&cgid=ks-accessories-tech") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          println(b)
//          assert(!d.isEmpty)
//          println(s"Got: $d") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//  }
////
////
////
////
//  "The worker" should {
//    "reply with the correct title with Default Title Extractor" in new Actors {
//
//      fetchPage("http://www.toms.com/mens/shoes/paseo/movember-grey-suede-men-s-paseos/s") onComplete {
//        case Success(d) =>
//          val startMessage = Start(toms_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          assert(a === "Movember Grey Suede Men's Paseos")
//          println(s"Got: $a") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//  }
////
////
////
//  "The worker" should  {
//    "reply with the three images with Thumbnail Extractor" in new Actors {
//
//      fetchPage("http://www.toms.com/mens/shoes/paseo/movember-grey-suede-men-s-paseos/s") onComplete {
//        case Success(d) =>
//          val startMessage = Start(toms_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          assert(!g.isEmpty)
//          println(s"Got: $f") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//  }
//
//  "The worker" should  {
//    "reply with the description" in new Actors {
//
//      fetchPage("http://www.toms.com/mens/shoes/paseo/movember-grey-suede-men-s-paseos/s") onComplete {
//        case Success(d) =>
//          val startMessage = Start(toms_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case Success(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          assert(!d.isEmpty)
//          println(s"Got: $d") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//  }


//  "The worker" should {
//    "reply with the correct title with Attribute Title Extractor" in new Actors {
//
//      fetchPage("http://www.toms.com/mens/shoes/paseo/movember-grey-suede-men-s-paseos/s") onComplete {
//        case Success(d) =>
//          val startMessage = Start(kate_spade_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case WorkComplete(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          b
//          assert(b.toDouble === 74.00)
//          println(s"Got: $a") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//
//  }

//  "The worker" should {
//    "enter the data into mongodb" in new Actors {
//
//      fetchPage("http://www.toms.com/mens/shoes/paseo/movember-grey-suede-men-s-paseos/s") onComplete {
//        case Success(d) =>
//          val startMessage = Start(toms_metadata, d)
//          master.send(worker, startMessage)
//        case Failure(e) =>
//          master.send(worker, "failed")
//      }
//
//
//      master.receiveOne(60 seconds) match {
//        case WorkComplete(MongoDataMsg(a, b, c, d, e, f, g))=> {
//          assert(a === "test_title")
//          println(s"Got: $a") }
//        case e => throw new Error("Don't know what we got: " + e.toString)
//
//      }
//    }
//
//
//
//  }


//
//  "The worker" should {
//    "persist to mongo" in new Actors {
//      val startMessage = Start(metadata, d)
//
//      master.send(worker, startMessage)
//      master.expectMsg()
//        case Worker.WorkComplete(result)=>
//          links.size should be > 0
//          log.debug(s"Got: $result")
//        case _ =>
//        case e => throw new Error("Don't know what we got: " + e.toString)
//      }
//    }


    trait Actors extends Scope {
    val master = TestProbe()
//    val worker = TestActorRef(new LinksWorker(system.actorSelection(master.ref.path)))
    //system.actorOf(Props(classOf[RecoverWorker], system.actorSelection(master.ref.path)))
    val worker = system.actorOf(Props(new LinksWorker(master.ref,system.actorSelection(master.ref.path))))
  }



}

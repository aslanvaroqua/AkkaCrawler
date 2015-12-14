package com.wishclouds

import akka.actor.{ActorSelection, Actor}
import akka.util.Timeout
import scala.concurrent.Await
import play.api.libs.json.{JsSuccess, Json}
import com.wishclouds.engine.Protocol.{Start, ProductDetail}
import akka.actor.Actor
import akka.actor._
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent._
import play.api.libs.json.{JsSuccess, Json}
import scala.util.{Failure, Success}
import com.redis.RedisClient
import org.jsoup.nodes.Document
import com.wishclouds.Worker.WorkComplete
import akka.routing.{SmallestMailboxRouter, FromConfig}
import java.util.UUID
import ExecutionContext.Implicits.global
import dispatch._
import play.api.libs.json.JsSuccess
import scala.util.Failure
import scala.Some
import scala.util.Success
import play.api.libs.json.JsSuccess
import scala.util.Failure
import scala.Some
import scala.util.Success
import play.api.libs.json.JsSuccess
import scala.util.Failure
import scala.Some
import scala.util.Success
import play.api.libs.json.JsSuccess
import dispatch.Future
import scala.util.Failure
import scala.Some
import scala.util.Success
import akka.actor.SupervisorStrategy.Restart
import akka.event.LoggingReceive
import play.api.libs.json.JsSuccess
import scala.util.Failure
import scala.Some
import akka.actor.OneForOneStrategy
import scala.util.Success
import akka.actor.Terminated


import scala.collection.immutable.Queue
import scala.Some
import akka.actor.OneForOneStrategy
import akka.actor.Terminated
import scala.Some
import akka.actor.OneForOneStrategy
import akka.actor.Terminated
import scala.Some
import akka.actor.OneForOneStrategy
import akka.actor.Terminated
import scala.io.Source
import com.wishclouds.explorer.server.MetaModel
import org.slf4j.LoggerFactory
import com.wishclouds.engine.{LinksWorker}
import com.wishclouds.models.Metadata

/**
 * User: max
 * Date: 10/22/13
 * Time: 11:48 AM
 */



class WorkExecutor(master: ActorSelection) extends Actor with Global {

  val clientRedis = RedisClient("localhost", 6379)

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

  def getPageWorker = context.actorOf(Props(new LinksWorker(self, master)), name = "linkWorker"+ UUID.randomUUID().toString )

  def getMetadata(n:String): Metadata = {
    Metadata(MetaModel.findPrice(n).get, MetaModel.findTitle(n).get,
      MetaModel.findImage(n).get,MetaModel.findDescription(n).get,
      MetaModel.findColor(n).get, MetaModel.findKeywords(n).get,
      MetaModel.findCategory(n).get, MetaModel.findSize(n).get,
      MetaModel.findBrand(n).get, MetaModel.findBullets(n).get)
  }

  def findProducts(n: String):Iterable[Option[ProductDetail]] = {
    implicit val timeout = Timeout(30 seconds)
    val hGetRetailer = Await.result(clientRedis.hgetall(n + "-products"), 30 seconds)

    val products = hGetRetailer.map {
      case (key, productString) =>
        val productJson = Json.parse(productString)
        Json.fromJson[ProductDetail](productJson) match {
          case JsSuccess(productDetail, _) => Some(productDetail)
          case _ => None

        }
    }

    products
  }

  def receive = {
    case n: String ⇒
      val n2 = "whatup"
      val result = s"$n * $n = $n2"
      log.debug(s"WorkExecutor started")

      val products = findProducts(n)
      val metadata = getMetadata(n)
      products foreach {
        case Some(y) => {
          val url: String = y.links.head.link.url
          fetchPage(url) onComplete {
            case Success(d) =>
              getPageWorker ! Start(url, metadata, d)
            case Failure(e) =>
              context.stop(self)
          }
        }
      }

      log.debug(s"WorkExecutor finished")
      master ! Worker.WorkComplete(result)


    case Worker.WorkComplete(result) => sender ! Worker.WorkComplete(result)


}

}
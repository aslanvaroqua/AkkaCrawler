//package com.wishclouds.explorer.engine
//
//import akka.actor.{Actor, ActorRef, Props, ActorSystem}
//import akka.testkit.{TestActorRef, TestProbe, ImplicitSender, TestKit}
//import org.specs2.mutable.{Before, SpecificationLike}
//import org.specs2.time.NoTimeConversions
//import com.typesafe.config.ConfigFactory
//import com.wishclouds.explorer.misc.coordinators.{MockClient, MockCoordinator, NodesCoordinator}
//import scala.concurrent.duration.Duration
//import scala.concurrent._
//import com.wishclouds.explorer.engine.Protocol._
//import scala.concurrent.duration._
//import com.wishclouds.explorer.models.MetaModel
//import akka.routing.SmallestMailboxRouter
//import org.specs2.specification.Scope
//import com.wishclouds.explorer.engine._
//import scala.Some
//import com.wishclouds.explorer.engine.Protocol.MultipleLinkMsg
//import com.wishclouds.explorer.engine.Protocol.Start
//import akka.cluster.ClusterEvent.ClusterDomainEvent
//import akka.cluster.Cluster
//
///**
// * User: Miguel A. Iglesias
// * Date: 11/27/13
// * Time: 10:01 AM
// */
//
//object MasterSpec {
//  val config = ConfigFactory.load()
//  val metadata = MetaModel.findOneMetadata("toms").get
//
//  def multiLink(count: Int): MultipleLinkMsg = MultipleLinkMsg(
//    (1 to count).map(l => LinkRedis(Link(l.toString,""))).toSet,
//    metadata
//  )
//
//  implicit def mockClientFinder(coordinator: NodesCoordinator) = new {
//    def mockClient = coordinator match {
//      case mock: MockCoordinator => mock.mockClient
//    }
//  }
//
//  class Dumb extends Actor {
//    def receive = {
//      case _ => ()
//    }
//  }
//
//}
//
//class MasterSpec extends TestKit(ActorSystem("ClusterSystem", ConfigFactory.parseString(
//  """
//    |akka {
//    |
//    |  actor {
//    |    provider = "akka.cluster.ClusterActorRefProvider"
//    |  }
//    |    remote {
//    |    log-remote-lifecycle-events = off
//    |    netty.tcp {
//    |      hostname = "127.0.0.1"
//    |      port = 2551
//    |    }
//    |  }
//    |
//    |  cluster {
//    |
//    |   seed-nodes = [
//    |      "akka.tcp://ClusterSystem@127.0.0.1:2551"]
//    |  }
//    |}
//  """.stripMargin))) with ImplicitSender
//with SpecificationLike with NoTimeConversions {
//
//
//  import MasterSpec._
//
//  implicit val exec  = system.dispatcher
//
//  def delay(t: Duration): Future[Unit] = Future {
//    blocking {
//      Thread.sleep(t.toMillis)
//    }
//  }
//
//  "The cluster" should {
//
//    "should not have 1 member at the beginning" in new Actors {
//      coordinator.mockClient.count should be equalTo 1
//      Thread.sleep(2000)
//      coordinator.mockClient.count should be equalTo 1
//    }
//
//    "create new instances when the master resizes" in new Actors {
//      (1 to 200) foreach {
//        msg =>
//          master ! MultipleLinkMsg(Set[LinkRedis](LinkRedis(Link(msg.toString,"-"))), metadata)
//      }
//      coordinator.mockClient.count should be greaterThanOrEqualTo 3
//    }
//
//    "send a Start(metadata) to the worker" in new Actors {
//      master ! List(metadata)
//      worker.expectMsg(5 seconds, Start(metadata))
//    }
//
//    "not send a Start(metadata) to the worker if that retailer is been processed" in new Actors {
//      master ! List(metadata)
//      worker.expectMsg(5 seconds, Start(metadata))
//      master ! List(metadata)
//      worker.expectNoMsg(5 seconds)
//    }
//
//    "be able to process new retailers after a while" in new Actors {
//      master ! List(metadata)
//      worker.expectMsg(5 seconds, Start(metadata))
//      Thread.sleep(15000)
//      master ! List(metadata)
//      worker.expectMsg(5 seconds, Start(metadata))
//    }
//
//    "send all the links to the workerRouter" in new Actors {
//      master ! MultipleLinkMsg((1 to 10).map(l => LinkRedis(Link(l.toString,"-"))).toSet, metadata)
//      worker.receiveN(10)
//    }
//
//  }
//
//
//  trait Actors extends Scope {
//
//
//  }
//
//}
//
//

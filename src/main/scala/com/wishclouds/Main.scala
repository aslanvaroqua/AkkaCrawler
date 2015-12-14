package com.wishclouds

import akka.actor._
import akka.contrib.pattern.ClusterClient
import scala.concurrent.duration._


/**
 * User: max
 * Date: 9/25/13
 * Time: 11:31 AM
 */
object Main extends App with Startup with Global {
  val env = args(0)
  val masterLocation = args(0) match {
    case x if x == "localhost" => "192.168.10.206"
    case _ => config.getString("cluster.master")
  }
  val contactAddress: akka.actor.Address = AddressFromURIString(s"akka.tcp://Workers@$masterLocation:2552")
  startWorker(contactAddress)
}

trait Startup {

  def systemName = "Workers"

  def workTimeout = 10.seconds

  def startWorker(contactAddress: akka.actor.Address): Unit = {

    val system = ActorSystem(systemName)

    val initialContacts = Set(
      system.actorSelection(RootActorPath(contactAddress) / "user" / "receptionist"))
    val clusterClient = system.actorOf(ClusterClient.props(initialContacts), "clusterClient")
    system.actorOf(Worker.props(clusterClient, Props[WorkExecutor]), "worker")
  }
}
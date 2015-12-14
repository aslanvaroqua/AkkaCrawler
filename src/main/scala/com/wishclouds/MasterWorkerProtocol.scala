package com.wishclouds

/**
 * User: max
 * Date: 9/18/13
 * Time: 5:23 PM
 */
object MasterWorkerProtocol {
  // Messages from Workers
  case class RegisterWorker(workerId: String, instanceId:String)
  case class WorkerRequestsWork(workerId: String)
  case class WorkIsDone(workerId: String, workId: String, result: Any)
  case class WorkFailed(workerId: String, workId: String)

  // Messages to Workers
  case object WorkIsReady
  case class Ack(id: String)
}
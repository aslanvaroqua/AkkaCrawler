package com.wishclouds

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

/**
 * User: max
 * Date: 10/22/13
 * Time: 11:48 AM
 */
trait Global {
  val config = ConfigFactory.load()
  val log = LoggerFactory.getLogger("min")
}

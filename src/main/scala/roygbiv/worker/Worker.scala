/**
Copyright [2011] [Henrik Engstroem, Mario Gonzalez]

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package roygbiv.worker

import akka.dispatch.Dispatchers
import roygbiv.scene.Scene
import akka.event.EventHandler
import akka.actor.{ActorRef, Actor}
import roygbiv.common.WorkResult
import roygbiv.integrator.UnidirectionalPathIntegrator
import collection.mutable.ArrayBuffer
import roygbiv.color.RGBColor
import roygbiv.math.{MersenneTwisterRNG, UniformRNG}
import roygbiv.sampler.LowDiscrepancySampler

case object Stop

case object ContinueQuestion

class Worker(aggregator: ActorRef) extends Actor {
  // TODO : Improve id!!!
  val id = "worker" + Thread.currentThread.getId
  var imageWidth = 0
  var imageHeight = 0
  var buffer = new ArrayBuffer[RGBColor]()
  val rng = new MersenneTwisterRNG
  val sampler = LowDiscrepancySampler(4, 4, rng)

  self.dispatcher = Worker.WorkerDispatcher

  var continue = true

  var scene: Option[Scene] = None
  def receive = {
    case s: Scene =>
      EventHandler.debug(this, "Starting to work on scene [%s]".format(scene))
      scene = Some(s)

      imageHeight = scene.get.camera.screenHeight
      imageWidth = scene.get.camera.screenWidth
      buffer = new ArrayBuffer[RGBColor]()
      buffer = buffer.padTo(imageWidth * imageHeight, RGBColor.Black)

      calculate
    case Stop => continue = false
    case ContinueQuestion => if (continue) calculate
    case unknown => EventHandler.warning(this, "Unknown message: " + unknown)
  }

  def calculate = {
    // This is where the magic happens
    val integrator = UnidirectionalPathIntegrator(scene.get, sampler)

    var i = 0
    for {
      y <- 0 until imageHeight
      x <- 0 until imageWidth
    } yield {
      val (xP, yP) = sampler.nextSample2D
      buffer(i) = integrator.l(x + xP, y + yP)
      i += 1
      // TODO : is there a better way to iterate than using a var i -
      // perhaps a nextPos.
    }

    aggregator ! WorkResult(id, buffer.toSeq)
    self ! ContinueQuestion
  }
}

object Worker {
  val availableProcessors = Runtime.getRuntime.availableProcessors

  // Create dedicated dispatcher for workers.
  // Since we want to minimize context switching in threads to maximize output from CPU during the
  // rendering of an image the number of available threads in the dispatcher = number of available processors
  lazy val WorkerDispatcher = Dispatchers.newExecutorBasedEventDrivenDispatcher("worker-dispatcher")
    .setCorePoolSize(availableProcessors)
    .build
}
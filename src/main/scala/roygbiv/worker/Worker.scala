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

case object Stop

case object ContinueQuestion

class Worker(aggregator: ActorRef) extends Actor {
  // TODO : Improve id!!!
  val id = "worker" + Thread.currentThread.getId

  self.dispatcher = Worker.WorkerDispatcher
  var continue = true
  var scene: Option[Scene] = None

  def receive = {
    case s: Scene =>
      EventHandler.debug(this, "Starting to work on scene [%s]".format(scene))
      scene = Some(s)
      calculate
    case Stop => continue = false
    case ContinueQuestion => if (continue) calculate
    case unknown => EventHandler.warning(this, "Unknown message: " + unknown)
  }

  def calculate = {
    val imageWidth = scene.get.camera.screenWidth
    val imageHeight = scene.get.camera.screenHeight
    val integrator = UnidirectionalPathIntegrator(scene.get)

    // This is where the magic happens
    val buffer = new ArrayBuffer[RGBColor](imageWidth * imageHeight)

    for {
      x <- 0 to imageWidth;
      y <- 0 until imageHeight
    } yield {
      buffer += integrator.l(x.asInstanceOf[Float], y.asInstanceOf[Float])
    }

    aggregator ! WorkResult(id,  buffer.toSeq)
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
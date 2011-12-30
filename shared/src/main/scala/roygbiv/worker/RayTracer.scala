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

import roygbiv.scene.Scene
import roygbiv.integrator.UnidirectionalPathIntegrator
import collection.mutable.ArrayBuffer
import roygbiv.color.RGBColor
import roygbiv.math.MersenneTwisterRNG
import roygbiv.sampler.LowDiscrepancySampler
import akka.actor.{ActorRef, Actor}
import roygbiv.{WorkResult, Stop, Pause, Start}

case object ProcessCheckPoint

class RayTracer extends Actor {
  val id = "RayTracer-" + Thread.currentThread.getId
  var imageWidth = 0
  var imageHeight = 0
  var buffer = new ArrayBuffer[RGBColor]()
  val rng = new MersenneTwisterRNG
  val sampler = LowDiscrepancySampler(4, 4, rng)
  var continue = true
  var scene: Option[Scene] = None
  var parentActor: Option[ActorRef] = None

  def receive = {
    case Work(s) =>
      //EventHandler.debug(this, "Starting to work on scene [%s]".format(scene))
      parentActor = Some(sender)
      scene = Some(s)
      imageHeight = scene.get.camera.screenHeight
      imageWidth = scene.get.camera.screenWidth
      buffer = new ArrayBuffer[RGBColor]()
      buffer = buffer.padTo(imageWidth * imageHeight, RGBColor.Black)
      calculate()
    case Stop =>
      continue = false
      reset()
    case Pause =>
      continue = false
    case Start =>
      continue = true
      calculate()
    case ProcessCheckPoint => if (continue) calculate()
    case unknown => //EventHandler.warning(this, "Unknown message: " + unknown)
  }

  def calculate() = {
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
    }

    // Send result to parent actor
    parentActor.get ! WorkResult(id, 0, buffer.toSeq)

    // Check if we should continue the work
    self ! ProcessCheckPoint
  }

  def reset() = {
    buffer = new ArrayBuffer[RGBColor]()
    scene = None
  }
}
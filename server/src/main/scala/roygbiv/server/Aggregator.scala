/**
 * Copyright [2011] [Henrik Engstroem, Mario Gonzalez]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package roygbiv.server

import collection.mutable.ArrayBuffer
import roygbiv.color.RGBColor
import java.util.concurrent.TimeUnit
import roygbiv.scene.Scene
import akka.util.Duration
import java.awt.image.{ BufferedImage ⇒ JBufferedImage }
import akka.actor.{ ActorRef, Cancellable, Actor }
import javax.swing.ImageIcon
import roygbiv.{ ClientRegistration, RenderingStatistics, WorkResult }

case object GenerateImage

class Aggregator extends Actor {
  var scheduled: Option[Cancellable] = None
  var previousLayers = 0L
  var buffer = new ArrayBuffer[RGBColor]()
  var scene: Option[Scene] = None
  var clients = Seq[ActorRef]()
  var startTime = 0L
  var rays = 0L
  var layers = 0
  var rayPayload = 0L

  override def preStart() {
    if (scheduled.isEmpty) {
      val cancellable =
        context.system.scheduler.schedule(
          Duration(context.system.settings.config.getMilliseconds("akka.raytracing.image.generation-frequency"), TimeUnit.MILLISECONDS),
          Duration(context.system.settings.config.getMilliseconds("akka.raytracing.image.generation-frequency"), TimeUnit.MILLISECONDS),
          self,
          GenerateImage)
      scheduled = Some(cancellable)
    }
  }

  override def postStop() {
    for (cancellable ← scheduled) cancellable.cancel()
  }

  def receive = {
    case s: Scene ⇒
      startTime = System.nanoTime
      scene = Some(s)
      rayPayload = s.camera.screenWidth * s.camera.screenHeight
    case result: WorkResult ⇒
      applyResult(result)
    case GenerateImage ⇒
      generateImage()
    case ClientRegistration ⇒
      clients = sender +: clients
  }

  def applyResult(result: WorkResult) = result match {
    case r: WorkResult ⇒
      var counter = 0
      if (buffer.isEmpty) {
        buffer = new ArrayBuffer[RGBColor](r.result.size)
        r.result.foreach({
          color ⇒
            buffer += color
            counter += 1
        })
      } else {
        r.result.foreach({
          color ⇒
            buffer(counter) = buffer(counter) + color
            counter += 1
        })
      }

      rays = rays + rayPayload
      layers = layers + 1

      val raysPerSecond = rays / ((System.nanoTime - startTime) / 1000000000)
      pushResult(RenderingStatistics(layers, rays, raysPerSecond))
  }

  def generateImage() {
    if (layers > previousLayers && buffer.size != 0) {
      previousLayers = layers
      val camera = scene.get.camera
      val scale = 1.0f / layers
      val image = new JBufferedImage(camera.screenWidth, camera.screenHeight, JBufferedImage.TYPE_INT_RGB)
      image.setRGB(0, 0, camera.screenWidth, camera.screenHeight, buffer.map(color ⇒ (color * scale).asInt).toArray, 0, camera.screenWidth)
      pushResult(new ImageIcon(image))
    }
  }

  def pushResult(result: Any) = {
    def pushToClient(client: ActorRef, result: Any) = {
      try {
        client ! result
      } catch {
        case e: Exception ⇒
          // Simplistic/naive error handing -
          // just remove from client from clients since we could not communicate with it.
          // Should be more forgiving in a real-app situation.
          println("*********************************************************")
          println("--> REMOVING CLIENT: " + client)
          println("--> EXCEPTION      : " + e.toString)
          println("*********************************************************")
          clients = clients.filterNot(_ == client)
      }
    }

    for (client ← clients) pushToClient(client, result)
  }
}
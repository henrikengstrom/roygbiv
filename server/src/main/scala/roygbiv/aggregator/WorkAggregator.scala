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
package roygbiv.aggregator

import roygbiv.common.WorkResult
import akka.actor._
import java.util.concurrent.{ScheduledFuture, TimeUnit}
import akka.event.EventHandler
import collection.mutable.ArrayBuffer
import roygbiv.color.RGBColor
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

case object GenerateImage

class WorkAggregator extends Actor {
  var scheduled: Option[ScheduledFuture[_]] = None
  var buffer = new ArrayBuffer[RGBColor]()
  val width = 800
  val height = 800

  var resultCounter = 0
  var previousResultNumber = 0

  override def preStart() {
    if (scheduled.isEmpty) {
      val scheduledFuture = Scheduler.schedule(self, GenerateImage, 10000, 10000, TimeUnit.MILLISECONDS)
      scheduled = Some(scheduledFuture)
    }
  }

  override def postStop() {
    for (scheduledFuture <- scheduled) scheduledFuture.cancel(false)
  }

  def receive = {
    case result: WorkResult => applyResult(result)
    case GenerateImage => generateImage()
  }

  def applyResult(workResult: WorkResult) {
    var counter = 0
    if (buffer.isEmpty) {
      buffer = new ArrayBuffer[RGBColor](workResult.result.size)
      workResult.result.foreach({
        color =>
          buffer += color
          counter += 1
      })
    } else {
      workResult.result.foreach({
        color =>
          buffer(counter) = buffer(counter) + color
          counter += 1
      })
    }

    resultCounter += 1
    EventHandler.debug(this, "Applying result from worker [%s], total calculated items: [%s]".format(resultCounter, workResult.workerId))
  }

  def generateImage() {
    if (resultCounter > previousResultNumber) {
      EventHandler.debug(this, "Writing image")
      previousResultNumber = resultCounter
      val scale = 1.0f/resultCounter
      val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      image.setRGB(0, 0, width, height, buffer.map(color => (color * scale).asInt).toArray, 0, width)
      val file = new File("img_" + System.currentTimeMillis + "_" + resultCounter + ".png")
      ImageIO.write(image, "png", file)
    }
  }
}
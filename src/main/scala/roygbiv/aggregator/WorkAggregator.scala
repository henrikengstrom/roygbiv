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

case object GenerateImage

class WorkAggregator extends Actor {
  var scheduled: Option[ScheduledFuture[_]] = None

  var resultCounter = 0

  override def preStart() {
    if (scheduled.isEmpty) {
      val scheduledFuture = Scheduler.schedule(self, GenerateImage, 3000, 3000, TimeUnit.MILLISECONDS)
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

  def applyResult(result: WorkResult) {
    resultCounter += 1
    EventHandler.debug(this, "Applying result from worker [%s], total calculated items: [%s]".format(resultCounter, result.workerId))
    // TODO : Apply this result to the existing result
  }

  def generateImage() {
    EventHandler.debug(this, "*** Generating image ***")
    // TODO : Persist image
  }
}
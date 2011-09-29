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

import akka.actor.Actor
import akka.dispatch.Dispatchers
import roygbiv.scene.Scene
import akka.event.EventHandler

case object Stop

class Worker extends Actor {
  self.dispatcher = Worker.WorkerDispatcher
  var continue = true

  def receive = {
    case scene: Scene =>
      EventHandler.debug(this, "Starting to work on scene [%s]".format(scene))
      calculate(scene)
    case Stop => continue = false
    case unknown => EventHandler.warning(this, "Unknown message: " + unknown)
  }

  def calculate(scene: Scene) = {
    // This is where the magic happens
  }
}

object Worker {
  // Create dedicated dispatcher for workers.
  // Since we want to minimize context switching in threads to maximize output from CPU during the
  // rendering of an image the number of available threads in the dispatcher = number of available processors
  lazy val WorkerDispatcher = Dispatchers.newExecutorBasedEventDrivenDispatcher("worker-dispatcher")
    .setCorePoolSize(Runtime.getRuntime.availableProcessors)
    .build
}
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
package roygbiv

import akka.actor.Actor
import akka.actor.Actor._
import akka.config.Config.config
import common.WorkResult
import worker.{RayTracer, Work}
import akka.event.EventHandler
import akka.dispatch.Dispatchers

class ClientWorker extends Actor {
  import ClientWorker._

  def receive = {
    case work @ Work => actorOf(new RayTracer(WorkerDispatcher)).start() ! work
    case result @ WorkResult => remote.actorFor(ServerAggregatorId, ServerHost, ServerPort) ! result
    case other => EventHandler.error(this, "Received unexpected message [%s]".format(other))
  }
}

object ClientWorker {
  val ServerHost = config.getString("akka.roygbiv.remote.hostname", "127.0.0.1")
  val ServerPort = config.getInt("akka.roygbiv.remote.port", 2552)
  val ServerAggregatorId = config.getString("akka.roygbiv.remote.aggregatorId", "aggregator")

  val availableProcessors = Runtime.getRuntime.availableProcessors

  // Create dedicated dispatcher for workers.
  // Since we want to minimize context switching in threads to maximize output from CPU during the
  // rendering of an image the number of available threads in the dispatcher = number of available processors
  lazy val WorkerDispatcher = Dispatchers.newExecutorBasedEventDrivenDispatcher("worker-dispatcher")
    .setCorePoolSize(availableProcessors)
    .build
}

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
package roygbiv.http

import akka.actor.Actor._
import akka.http.{RequestMethod, Get}
import roygbiv.scene.{SceneLoader, LoadScene, Scene}
import roygbiv.aggregator.WorkAggregator
import akka.actor.{ActorRef, Actor}
import roygbiv.worker.{Stop, Worker}

class RenderResource extends Actor {
  import RenderResource._

  val aggregator = actorOf[WorkAggregator]

  var workerActors: List[ActorRef] = List()

  var inProgress = false

  def receive = {
    case get: Get =>
      get.request.getPathInfo match {
        case StartPattern =>
          if (!inProgress) {
            aggregator.start()
            loadScene
            get.OK("Rendering started @ " + System.currentTimeMillis)
          } else {
            get.OK("Rendering already in progress - stop current job before invoking a new one.")
          }
        case StopPattern =>
          stopWorkers()
          aggregator.stop()
          get.OK("Rendering stopped @ " + System.currentTimeMillis)
        case StatusPattern => get.OK("status is ok")
        case other => get.BadRequest("Incorrect uri [%s]".format(get.request.getPathInfo))
      }
    case other: RequestMethod => other.NotAllowed("Unsupported HTTP method for endpoint.")

    case scene: Scene => distributeWork(scene)
  }

  def loadScene = {
    val loader = actorOf[SceneLoader].start()
    loader ! LoadScene
  }

  def distributeWork(scene: Scene) = {
    for (i <- 1 to Worker.availableProcessors) {
      val actor = actorOf(new Worker(aggregator)).start()
      workerActors = actor :: workerActors
      actor ! scene
    }
  }

  def stopWorkers() = {
    workerActors.foreach { worker =>
      worker ! Stop
      worker.stop()
    }

    workerActors = List[ActorRef]()
  }
}

object RenderResource {
  val StartPattern = EndpointURI.RenderServiceUri + "/start"
  val StopPattern = EndpointURI.RenderServiceUri + "/stop"
  val StatusPattern = EndpointURI.RenderServiceUri + "/status"
}
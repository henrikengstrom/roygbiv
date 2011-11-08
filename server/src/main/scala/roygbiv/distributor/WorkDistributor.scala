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
package roygbiv.distributor

import akka.actor.Actor
import akka.actor.Actor._
import roygbiv.worker.{Work, RegisterClient}
import roygbiv.scene.json.JsonSceneLoader
import roygbiv.scene.{SceneLoaderOrchestrator, LoadScene, Scene}
import akka.event.EventHandler

class WorkDistributor extends Actor {
  import WorkDistributor._

  override def preStart() {
    if (!scene.isDefined) loadScene()
  }

  override def postRestart(reason: Throwable) {
    if (!scene.isDefined) loadScene()
  }

  // TODO: This does contain a race condition that should be fixed -
  // if client registers for work before the scene has been loaded it will not work as planned...
  def receive = {
    case ClientInformation(id, host, port) => remote.actorFor(id, host, port) ! Work(AggregatorId, scene.get)
    case s: Scene =>
      EventHandler.info(this, "*** Scene loaded ***")
      scene = Some(s)
    case other => EventHandler.error(this, "Received unknown message: [%s]".format(other))
  }

  def loadScene() = {
    val loader = actorOf[SceneLoaderOrchestrator].start()
    // TODO : these parameters should, of course, be dynamic and chosen in the REST interface
    loader ! LoadScene(JsonSceneLoader.SceneType, "server/src/main/resources/SceneExample.lcj")
  }
}

object WorkDistributor {
  val AggregatorId = "aggregator"
  var scene: Option[Scene] = None
}
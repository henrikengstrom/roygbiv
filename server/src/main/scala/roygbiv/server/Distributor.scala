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
package roygbiv.server

import roygbiv.scene.json.JsonSceneLoader
import roygbiv.scene.{ SceneLoaderOrchestrator, LoadScene, Scene }
import akka.actor.{Props, Actor}
import roygbiv._

class Distributor extends Actor {
  import Distributor._

  // TODO : use Actors as state machine
  var state: Int = _

  override def preStart() {
    if (!scene.isDefined) loadScene()
  }

  override def postRestart(reason: Throwable) {
    if (!scene.isDefined) loadScene()
  }

  def receive = {
    case s: Scene ⇒
      scene = Some(s)
      context.actorOf(Props[Aggregator]) ! s
    case c: ClientRegistration ⇒
      clients = c +: clients
      context.actorFor(c.remoteAddress) !
        WorkInstruction(
          context.system.settings.config.getString("akka.raytracing.aggregator.address"),
          scene.get)
      if (state == Started) context.actorFor(c.remoteAddress) ! Start
    case Start ⇒
      state = Started
      for (c ← clients) context.actorFor(c.remoteAddress) ! Start
    case Pause ⇒
      state = Paused
      for (c ← clients) context.actorFor(c.remoteAddress) ! Pause
    case Stop ⇒
      state = Stopped
      for (c ← clients) context.actorFor(c.remoteAddress) ! Stop
  }

  private def loadScene() = {
    val loader = context.actorOf(Props[SceneLoaderOrchestrator])
    loader ! LoadScene(JsonSceneLoader.SceneType,
      context.system.settings.config.getString("akka.raytracing.scenedefinition"))
  }
}

object Distributor {
  var scene: Option[Scene] = None
  var clients: Vector[ClientRegistration] = Vector()
  final val Started = 1
  final val Paused = 2
  final val Stopped = 3
}
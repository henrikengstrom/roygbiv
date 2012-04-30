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

import roygbiv.scene.json.JsonSceneLoader
import roygbiv.scene.{ SceneLoaderOrchestrator, LoadScene, Scene }
import roygbiv._
import akka.actor.{ ActorRef, Props, Actor }

class Distributor extends Actor {
  var scene: Option[Scene] = None
  var clients: Vector[ActorRef] = Vector()
  final val Started = 1
  final val Stopped = 2

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
      context.actorFor("/user/aggregator") ! s
    case ClientRegistration ⇒
      clients = sender +: clients
      sender !
        WorkInstruction(
          context.system.settings.config.getString("akka.raytracing.aggregator.address"),
          scene.get)
      if (state == Started) sender ! Start
    case Start ⇒
      state = Started
      for (c ← clients) c ! Start
    case Stop ⇒
      state = Stopped
      for (c ← clients) c ! Stop
  }

  private def loadScene() = {
    val loader = context.actorOf(Props[SceneLoaderOrchestrator], "sceneLoaderOrchestrator")
    loader ! LoadScene(JsonSceneLoader.SceneType,
      context.system.settings.config.getString("akka.raytracing.scene-definition"))
  }
}
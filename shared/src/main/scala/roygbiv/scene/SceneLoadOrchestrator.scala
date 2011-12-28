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
package roygbiv.scene

import akka.actor.Actor
import json.JsonSceneLoader

case class LoadScene(sceneType: String, location: String)

class SceneLoaderOrchestrator extends Actor {
  def receive = {
    case LoadScene(JsonSceneLoader.SceneType, location) ⇒
      self.channel ! JsonSceneLoader(location, SceneLoaderOrchestrator.FileEncoding).loadScene
    case _ ⇒ println("Unknown scene type or message")
  }
}

object SceneLoaderOrchestrator {
  val FileEncoding = "UTF-8"
}
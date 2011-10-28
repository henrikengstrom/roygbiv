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
package roygbiv.scene

import json.JsonSceneLoader
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

@org.junit.runner.RunWith(classOf[org.scalatest.junit.JUnitRunner])
class JsonSceneLoaderSpec extends WordSpec with MustMatchers {
  "JsonSceneLoader" must {
    "load and parse JSON into case classes" in {
      val sceneLoader = JsonSceneLoader("src/main/resources/SceneExample.lcj", "UTF-8")
      val scene = sceneLoader.loadScene
      scene.name must equal ("TestScene")
    }
  }
}

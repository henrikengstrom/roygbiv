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

import roygbiv.acceleration.TrivialAccelerator
import roygbiv.camera.PinholeCamera
import roygbiv.math.Tuple3f
import roygbiv.material.DiffuseMaterial
import roygbiv.color.RGBColor
import roygbiv.shape.{DiscScatterer, SphereEmitter, SphereScatterer}

case class JsonSceneLoader(location: String, encoding: String) extends SceneLoader {
  def loadScene: Scene = {
    // TODO - load scene with SJSON
    /*
    val jsonString = scala.io.Source.fromFile(location, encoding).mkString
    */

    val accelerator =  new TrivialAccelerator
    val camera = PinholeCamera(Tuple3f(0.0f, 1.0f, 7.0f), Tuple3f(0.0f, 1.0f, -1.0f), Tuple3f(0.0f, 1.0f, 0.0f), 1.9f, 1024, 768)
    val scene = Scene("TestScene", accelerator, camera)

    val sphereMaterial = DiffuseMaterial("m1", "sphere_material", RGBColor(0.5f, 0.5f, 0.9f))
    val planeMaterial = DiffuseMaterial("m2", "plane_material", RGBColor(0.5f, 0.5f, 0.5f))
    val planeMaterial2 = DiffuseMaterial("m2", "plane_material", RGBColor(0.1f, 0.6f, 0.2f))

    val sphereScatterer = SphereScatterer(Tuple3f(0.0f, 0.5f, 0.0f), 0.5f, sphereMaterial)
    val discScatterer = DiscScatterer(Tuple3f(1.0f, 0.5f, 0.0f), 0.5f, Tuple3f(0.0f, 0.0f, 1.0f), planeMaterial)
    val discScatterer2 = DiscScatterer(Tuple3f(0.0f, 0.0f, 0.0f), 12.5f, Tuple3f(0.0f, 1.0f, 0.0f), planeMaterial2)
    val sphereEmitter = SphereEmitter(Tuple3f(0.0f, 1.5f, 0.0f), 0.5f, RGBColor(1.0f, 0.6f, 0.2f), 1.0f)

    scene.addShape(sphereScatterer)
    scene.addShape(discScatterer)
    scene.addShape(discScatterer2)
    scene.addEmitter(sphereEmitter)
    scene
  }
}

object JsonSceneLoader {
  val SceneType = "json"
}
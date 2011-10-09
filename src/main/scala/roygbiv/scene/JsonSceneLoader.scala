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

    val sphereMaterial = DiffuseMaterial("m1", "sphere_material", RGBColor(0.2f, 0.5f, 0.8f))
    val planeMaterial2 = DiffuseMaterial("m3", "plane_materia2", RGBColor(0.8f, 0.8f, 0.8f))
    val redMaterial = DiffuseMaterial("m3", "plane_materia2", RGBColor(0.8f, 0.01f, 0.01f))
    val greenMaterial = DiffuseMaterial("m3", "plane_materia2", RGBColor(0.01f, 0.8f, 0.01f))

    val sphereScatterer = SphereScatterer(Tuple3f(-0.5f, 0.5f, -1.0f), 0.5f, sphereMaterial)
    val sphereScatterer2 = SphereScatterer(Tuple3f(0.6f, 0.4f, 0.2f), 0.4f, planeMaterial2)
    val discScatterer2 = DiscScatterer(Tuple3f(0.0f, 0.0f, 0.0f), 12.5f, Tuple3f(0.0f, 1.0f, 0.0f), planeMaterial2)
    val discScatterer3 = DiscScatterer(Tuple3f(-1.5f, 0.0f, 0.0f), 12.5f, Tuple3f(1.0f, 0.0f, 0.0f), redMaterial)
    val discScatterer4 = DiscScatterer(Tuple3f(1.5f, 0.0f, 0.0f), 12.5f, Tuple3f(-1.0f, 0.0f, 0.0f), greenMaterial)
    val discScatterer5 = DiscScatterer(Tuple3f(0.0f, 0.0f, -2.0f), 12.5f, Tuple3f(0.0f, 0.0f, 1.0f), planeMaterial2)
    val discScatterer6 = DiscScatterer(Tuple3f(0.0f, 2.5f, 0.0f), 12.5f, Tuple3f(0.0f, -1.0f, 0.0f), planeMaterial2)
    val sphereEmitter = SphereEmitter(Tuple3f(0.0f, 2.5f, 0.0f), 0.5f, RGBColor(1.0f, 1.0f, 1.0f), 2.0f)

    scene.addShape(sphereScatterer)
    scene.addShape(sphereScatterer2)
    scene.addShape(discScatterer2)
    scene.addShape(discScatterer3)
    scene.addShape(discScatterer4)
    scene.addShape(discScatterer5)
    scene.addShape(discScatterer6)
    scene.addEmitter(sphereEmitter)
    scene
  }
}

object JsonSceneLoader {
  val SceneType = "json"
}
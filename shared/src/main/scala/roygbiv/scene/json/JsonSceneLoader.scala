package roygbiv.scene.json

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

import roygbiv.acceleration.TrivialAccelerator
import roygbiv.math.Tuple3f
import roygbiv.color.RGBColor
import reflect.BeanInfo
import sjson.json.JSONTypeHint
import annotation.target.field
import roygbiv.scene.{Scene, SceneLoader}
import roygbiv.camera.PinholeCamera
import roygbiv.material.{Material, DiffuseMaterial}
import roygbiv.shape._

case class JsonSceneLoader(location: String, encoding: String) extends SceneLoader {

  import JsonSceneLoader._

  def loadScene: Scene = {
    val json = scala.io.Source.fromFile(location, encoding).mkString
    val jsonScene = sjson.json.Serializer.SJSON.in[JsonScene](json).asInstanceOf[JsonScene]
    convert(jsonScene)

    // TODO : convert to JSON format instead of Scala code
    /*
    val accelerator =  new TrivialAccelerator
    val camera = PinholeCamera(Tuple3f(0.0f, 1.455f, 6.63f), Tuple3f(0.0f, 1.455f, -1.0f), Tuple3f(0.0f, 1.0f, 0.0f), 1.8f, 800, 800)
    val scene = Scene("TestScene", accelerator, camera)

    //val sphereMaterial = DiffuseMaterial("m1", "sphere_material", RGBColor(0.2f, 0.8f, 0.2f))
    val sphereMaterial = MirrorMaterial("m1", "sphere_material", RGBColor(1.0f, 1.0f, 1.0f))
    val planeMaterial2 = DiffuseMaterial("m3", "plane_materia2", RGBColor(0.8f, 0.8f, 0.8f))
    val redMaterial = DiffuseMaterial("m3", "plane_materia2", RGBColor(0.8f, 0.2f, 0.2f))
    val greenMaterial = DiffuseMaterial("m3", "plane_materia2", RGBColor(0.2f, 0.2f, 0.8f))

    val sphereScatterer = SphereScatterer(Tuple3f(-0.8f, 0.55f, -0.4f), 0.55f, sphereMaterial)
    val sphereScatterer2 = SphereScatterer(Tuple3f(0.7f, 0.55f, 0.35f), 0.55f, planeMaterial2)
    val discScatterer2 = DiscScatterer(Tuple3f(0.0f, 0.0f, 0.0f), 2.2f, Tuple3f(0.0f, 1.0f, 0.0f), planeMaterial2)
    val discScatterer3 = DiscScatterer(Tuple3f(-1.45f, 1.45f, 0.0f), 2.2f, Tuple3f(1.0f, 0.0f, 0.0f), redMaterial)
    val discScatterer4 = DiscScatterer(Tuple3f(1.45f, 1.45f, 0.0f), 2.2f, Tuple3f(-1.0f, 0.0f, 0.0f), greenMaterial)
    val discScatterer5 = DiscScatterer(Tuple3f(0.0f, 1.45f, -1.5f), 2.2f, Tuple3f(0.0f, 0.0f, 1.0f), planeMaterial2)
    val discScatterer6 = DiscScatterer(Tuple3f(0.0f, 2.9f, 0.0f), 2.2f, Tuple3f(0.0f, -1.0f, 0.0f), planeMaterial2)
    val discEmitter = DiscEmitter(Tuple3f(0.0f, 2.89f, 0.0f), Tuple3f(0.0f, -1.0f, 0.0f), 0.33f, RGBColor(1.0f, 1.0f, 1.0f), 50.0f)

    scene.addShape(sphereScatterer)
    scene.addShape(sphereScatterer2)
    scene.addShape(discScatterer2)
    scene.addShape(discScatterer3)
    scene.addShape(discScatterer4)
    scene.addShape(discScatterer5)
    scene.addShape(discScatterer6)
    scene.addEmitter(discEmitter)

    scene
    */
  }
}

object JsonSceneLoader {
  final val SceneType = "json"
  final val MaterialDiffuse = "diffuse"
  final val LightSpherical = "spherical"
  final val LightDisc = "disc"
  final val GeometrySphere = "sphere"
  final val GeometryDisc = "disc"

  def convert(jsonScene: JsonScene): Scene = {
    val accelerator = new TrivialAccelerator
    val camera = convert(jsonScene.scene.camera)
    val scene = Scene(jsonScene.scene.name, accelerator, camera)

    // Materials
    val materialMap = jsonScene.scene.materials map { m => convert(m) } map { m => (m.id, m) } toMap

    // Lights
    val lights = jsonScene.scene.lights map { l => convert(l) }

    // Geometry
    val geometry: List[Shape] = jsonScene.scene.geometry map { g => convert(g, materialMap) }

    // Now add the stuff to the scene
    lights.foreach { l => scene.addEmitter(l) }
    geometry.foreach { g => scene.addShape(g) }
    scene
  }

  def convert(jsonCamera: JsonCamera): PinholeCamera = {
    // TODO : remove hard coded values
    PinholeCamera(to3f(jsonCamera.loc), to3f(jsonCamera.target), to3f(jsonCamera.up), jsonCamera.viewplanedist, 800, 800)
  }

  def convert(jsonMaterial: JsonMaterial): Material = {
    jsonMaterial.mtype match {
      case MaterialDiffuse => DiffuseMaterial(jsonMaterial.id, jsonMaterial.name, RGBColor(to3f(jsonMaterial.color)))
      case _ => throw new RuntimeException("Unknown material: " + jsonMaterial.mtype + " - aborting json parsing")
    }
  }

  def convert(jsonLight: JsonLight): Emitter = {
    jsonLight.ltype match {
      case LightDisc => DiscEmitter(to3f(jsonLight.loc), to3f(jsonLight.normal), jsonLight.radius, RGBColor(to3f(jsonLight.color)), jsonLight.power)
      case LightSpherical => SphereEmitter(to3f(jsonLight.loc), jsonLight.radius, RGBColor(to3f(jsonLight.color)), jsonLight.power)
      case _ => throw new RuntimeException("Unknown light: " + jsonLight.ltype + " - aborting json parsing")
    }
  }

  def convert(jsonGeometry: JsonGeometry, materialMap: Map[String, Material]): Shape = {
    jsonGeometry.gtype match {
      case GeometryDisc => DiscScatterer(to3f(jsonGeometry.loc), jsonGeometry.radius, to3f(jsonGeometry.normal), materialMap(jsonGeometry.mid))
      case GeometrySphere => SphereScatterer(to3f(jsonGeometry.loc), jsonGeometry.radius, materialMap(jsonGeometry.mid))
      case _ => throw new RuntimeException("Unknown geomerty: " + jsonGeometry.gtype + " - aborting json parsing")
    }
  }

  def to3f(values: List[Float]): Tuple3f = {
    Tuple3f(values(0), values(1), values(2))
  }
}

@BeanInfo case class JsonScene(@(JSONTypeHint@field)(value = classOf[JsonSceneData]) scene: JsonSceneData) {
  private def this() = this (null)
}

@BeanInfo case class JsonSceneData(
  version: String,
  name: String,
  @(JSONTypeHint@field)(value = classOf[JsonImage]) image: JsonImage,
  @(JSONTypeHint@field)(value = classOf[JsonRenderer]) renderer: JsonRenderer,
  @(JSONTypeHint@field)(value = classOf[JsonCamera]) camera: JsonCamera,
  @(JSONTypeHint@field)(value = classOf[JsonMaterial]) materials: List[JsonMaterial],
  @(JSONTypeHint@field)(value = classOf[JsonLight]) lights: List[JsonLight],
  @(JSONTypeHint@field)(value = classOf[JsonGeometry]) geometry: List[JsonGeometry]) {
  private def this() = this (null, null, null, null, null, null, null, null)
}

@BeanInfo case class JsonImage(width: Int, height: Int) {
  private def this() = this (0, 0)
}

@BeanInfo case class JsonRenderer(rtype: String) {
  private def this() = this (null)
}

@BeanInfo case class JsonCamera(
  ctype: String,
  @(JSONTypeHint@field)(value = classOf[Float]) up: List[Float],
  @(JSONTypeHint@field)(value = classOf[Float]) loc: List[Float],
  @(JSONTypeHint@field)(value = classOf[Float]) target: List[Float],
  viewplanedist: Float) {
  private def this() = this (null, null, null, null, 0)
}

@BeanInfo case class JsonMaterial(
  mtype: String,
  id: String,
  name: String,
  @(JSONTypeHint@field)(value = classOf[Float]) color: List[Float]) {
  private def this() = this (null, null, null, null)
}

@BeanInfo case class JsonLight(
  ltype: String,
  name: String,
  radius: Float,
  @(JSONTypeHint@field)(value = classOf[Float]) loc: List[Float],
  @(JSONTypeHint@field)(value = classOf[Float]) normal: List[Float],
  @(JSONTypeHint@field)(value = classOf[Float]) color: List[Float],
  power: Float) {
  private def this() = this (null, null, 0, null, null, null, 0)
}

@BeanInfo case class JsonGeometry(
  gtype: String,
  name: String,
  radius: Float,
  @(JSONTypeHint@field)(value = classOf[Float]) loc: List[Float],
  @(JSONTypeHint@field)(value = classOf[Float]) normal: List[Float],
  mid: String) {
  private def this() = this (null, null, 0, null, null, null)
}
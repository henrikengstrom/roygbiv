package roygbiv.scene.json

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

import roygbiv.acceleration.TrivialAccelerator
import roygbiv.math.Tuple3f
import roygbiv.color.RGBColor
import reflect.BeanInfo
import sjson.json.JSONTypeHint
import annotation.target.field
import roygbiv.scene.{ Scene, SceneLoader }
import roygbiv.camera.PinholeCamera
import roygbiv.material.{ Material, DiffuseMaterial }
import roygbiv.shape._

case class JsonSceneLoader(location: String, encoding: String) extends SceneLoader {

  import JsonSceneLoader._

  def loadScene: Scene = {
    val json = scala.io.Source.fromFile(location, encoding).mkString
    val jsonScene = sjson.json.Serializer.SJSON.in[JsonScene](json).asInstanceOf[JsonScene]
    convert(jsonScene)
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
    val camera = convert(jsonScene.scene.camera, jsonScene.scene.image.width, jsonScene.scene.image.height)
    val scene = Scene(jsonScene.scene.name, accelerator, camera)

    // Materials
    val materialMap = jsonScene.scene.materials map { m ⇒ convert(m) } map { m ⇒ (m.id, m) } toMap

    // Lights
    val lights = jsonScene.scene.lights map { l ⇒ convert(l) }

    // Geometry
    val geometry: List[Shape] = jsonScene.scene.geometry map { g ⇒ convert(g, materialMap) }

    // Now add the stuff to the scene
    lights.foreach { l ⇒ scene.addEmitter(l) }
    geometry.foreach { g ⇒ scene.addShape(g) }
    scene
  }

  def convert(jsonCamera: JsonCamera, width: Int, height: Int): PinholeCamera = {
    PinholeCamera(to3f(jsonCamera.loc), to3f(jsonCamera.target), to3f(jsonCamera.up), jsonCamera.viewplanedist, width, height)
  }

  def convert(jsonMaterial: JsonMaterial): Material = {
    jsonMaterial.mtype match {
      case MaterialDiffuse ⇒ DiffuseMaterial(jsonMaterial.id, jsonMaterial.name, RGBColor(to3f(jsonMaterial.color)))
      case _               ⇒ throw new RuntimeException("Unknown material: " + jsonMaterial.mtype + " - aborting json parsing")
    }
  }

  def convert(jsonLight: JsonLight): Emitter = {
    jsonLight.ltype match {
      case LightDisc      ⇒ DiscEmitter(to3f(jsonLight.loc), to3f(jsonLight.normal), jsonLight.radius, RGBColor(to3f(jsonLight.color)), jsonLight.power)
      case LightSpherical ⇒ SphereEmitter(to3f(jsonLight.loc), jsonLight.radius, RGBColor(to3f(jsonLight.color)), jsonLight.power)
      case _              ⇒ throw new RuntimeException("Unknown light: " + jsonLight.ltype + " - aborting json parsing")
    }
  }

  def convert(jsonGeometry: JsonGeometry, materialMap: Map[String, Material]): Shape = {
    jsonGeometry.gtype match {
      case GeometryDisc   ⇒ DiscScatterer(to3f(jsonGeometry.loc), jsonGeometry.radius, to3f(jsonGeometry.normal), materialMap(jsonGeometry.mid))
      case GeometrySphere ⇒ SphereScatterer(to3f(jsonGeometry.loc), jsonGeometry.radius, materialMap(jsonGeometry.mid))
      case _              ⇒ throw new RuntimeException("Unknown geomerty: " + jsonGeometry.gtype + " - aborting json parsing")
    }
  }

  def to3f(values: List[Float]): Tuple3f = {
    Tuple3f(values(0), values(1), values(2))
  }
}

@BeanInfo case class JsonScene(@(JSONTypeHint @field)(value = classOf[JsonSceneData]) scene: JsonSceneData) {
  private def this() = this(null)
}

@BeanInfo case class JsonSceneData(
  version: String,
  name: String,
  @(JSONTypeHint @field)(value = classOf[JsonImage]) image: JsonImage,
  @(JSONTypeHint @field)(value = classOf[JsonRenderer]) renderer: JsonRenderer,
  @(JSONTypeHint @field)(value = classOf[JsonCamera]) camera: JsonCamera,
  @(JSONTypeHint @field)(value = classOf[JsonMaterial]) materials: List[JsonMaterial],
  @(JSONTypeHint @field)(value = classOf[JsonLight]) lights: List[JsonLight],
  @(JSONTypeHint @field)(value = classOf[JsonGeometry]) geometry: List[JsonGeometry]) {
  private def this() = this(null, null, null, null, null, null, null, null)
}

@BeanInfo case class JsonImage(width: Int, height: Int) {
  private def this() = this(0, 0)
}

@BeanInfo case class JsonRenderer(rtype: String) {
  private def this() = this(null)
}

@BeanInfo case class JsonCamera(
  ctype: String,
  @(JSONTypeHint @field)(value = classOf[Float]) up: List[Float],
  @(JSONTypeHint @field)(value = classOf[Float]) loc: List[Float],
  @(JSONTypeHint @field)(value = classOf[Float]) target: List[Float],
  viewplanedist: Float) {
  private def this() = this(null, null, null, null, 0)
}

@BeanInfo case class JsonMaterial(
  mtype: String,
  id: String,
  name: String,
  @(JSONTypeHint @field)(value = classOf[Float]) color: List[Float]) {
  private def this() = this(null, null, null, null)
}

@BeanInfo case class JsonLight(
  ltype: String,
  name: String,
  radius: Float,
  @(JSONTypeHint @field)(value = classOf[Float]) loc: List[Float],
  @(JSONTypeHint @field)(value = classOf[Float]) normal: List[Float],
  @(JSONTypeHint @field)(value = classOf[Float]) color: List[Float],
  power: Float) {
  private def this() = this(null, null, 0, null, null, null, 0)
}

@BeanInfo case class JsonGeometry(
  gtype: String,
  name: String,
  radius: Float,
  @(JSONTypeHint @field)(value = classOf[Float]) loc: List[Float],
  @(JSONTypeHint @field)(value = classOf[Float]) normal: List[Float],
  mid: String) {
  private def this() = this(null, null, 0, null, null, null)
}
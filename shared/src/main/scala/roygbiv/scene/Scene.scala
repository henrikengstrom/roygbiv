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

import roygbiv.acceleration.Accelerator
import roygbiv.math.Ray
import roygbiv.shape.{ Intersection, Emitter, Shape }
import roygbiv.camera.PinholeCamera

case class Scene(name: String, accelerator: Accelerator, camera: PinholeCamera) {
  var emitters = List[Emitter]()

  def addShape(shape: Shape) = {
    accelerator.addShape(shape)
  }

  def addEmitter(emitter: Emitter) = {
    accelerator.addShape(emitter)
    emitters = emitter :: emitters
  }

  def intersect(ray: Ray): Option[Intersection] = {
    accelerator.intersect(ray)
  }

}
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
package roygbiv.shape

import roygbiv.material.Material
import roygbiv.math.{Tuple3f, Ray}

/**
 * Class representing an intersection of a ray with a shape
 */
case class Intersection(t: Float, point: Tuple3f = null, shape: Shape = null) {
  def normal: Tuple3f = {
    shape.getNormalAtPoint(point)
  }
}

/**
 * Base class for shapes
 */
trait Shape {
  def area: Float
  def intersect(ray: Ray): Option[Intersection]
  def getNormalAtPoint(point: Tuple3f): Tuple3f
}


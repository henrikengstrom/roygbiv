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
package roygbiv.acceleration

import roygbiv.shape.{Shape, Intersection}
import roygbiv.math.{MathUtils, Ray}

class TrivialAccelerator extends Accelerator {
  var shapes = List[Shape]()

  def addShape(shape: Shape) = {
    shapes = shape :: shapes
  }

  def intersect(ray: Ray): Option[Intersection] = {
    var intersection: Intersection = Intersection(MathUtils.MaxDistance)

    for (shape <- shapes) {
      shape.intersect(ray) match {
        case Some(i) => if (i.t < intersection.t) intersection = i
        case None =>
      }
    }

    if (intersection.t == MathUtils.MaxDistance) None else Some(intersection)
  }
}

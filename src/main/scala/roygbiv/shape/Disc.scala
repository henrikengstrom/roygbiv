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

import roygbiv.math.{MathUtils, Ray, Tuple3f}

trait Disc extends Shape {
  lazy val radiusSquared = radius * radius
  lazy val area = (scala.math.Pi * radiusSquared).asInstanceOf[Float]
  def radius: Float
  def center: Tuple3f
  def normal: Tuple3f

  def intersect(ray: Ray): Option[Intersection] = {
    val incidence = ray.direction.dot(normal)
    val oc = center - ray.origin
    val t = oc.dot(normal) / incidence

    if (t > MathUtils.Epsilon) {
      val hitPoint = ray.getPointAtT(t)

      if (hitPoint.distanceSquared(center) < radiusSquared) {
        Some(Intersection(t, hitPoint, this))
      }
    }

    None
  }

  def getNormalAtPoint(point: Tuple3f): Tuple3f = normal
}
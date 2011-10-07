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
import roygbiv.math.{MathUtils, Ray, Vector3f, Point3f}

case class Sphere(center: Point3f, radius: Float, material: Material) extends Shape {
  private val radiusSquared = radius * radius
  private val area = (4.0D * radiusSquared * scala.math.Pi).asInstanceOf[Float]

  def getArea: Float = area

  def getMaterial: Material = material

  def intersect(ray: Ray): (Boolean, Intersection) = {
    val v = Vector3f(ray.origin - center)
    val b = -v.dot(ray.direction)
    var discriminant = (b * b) - v.dot(v) + radiusSquared

    if (discriminant > 0.0f) {
      discriminant = scala.math.sqrt(discriminant).asInstanceOf[Float]
      val i1 = b - discriminant
      val i2 = b + discriminant

      if (i2 > 0.0f) {
        if (i1 < 0.0f) {
          if (i2 < MathUtils.MaxDistance) {
            (true, Intersection(i2, ray.getPointAtT(i2), this))
          }
        } else {
          if (i1 < MathUtils.MaxDistance) {
            (true, Intersection(i1, ray.getPointAtT(i1), this))
          }
        }
      }
    }

    (false, null)
  }

  def getNormalAtPoint(point: Point3f): Vector3f = {
    Vector3f(point - center).normalize()
  }
}
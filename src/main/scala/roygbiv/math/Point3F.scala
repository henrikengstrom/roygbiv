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
package roygbiv.math

/**
 * Immutable implementation of a point.
 */
case class Point3f(x: Float, y: Float, z: Float) {

  def distance(other: Point3f): Float = {
    scala.math.sqrt(distanceSquared(other)).asInstanceOf[Float]
  }

  def distanceSquared(other: Point3f): Float = {
    val dx = x - other.x
    val dy = y - other.y
    val dz = z - other.z
    (dx * dx) + (dy * dy) + (dz * dz)
  }

  def +(p: Point3f): Point3f = {
    Point3f(x + p.x, y + p.y, z + p.z)
  }

  def +(v: Vector3f): Point3f = {
    Point3f(x + v.x, y + v.y, z + v.z)
  }

  def -(p: Point3f): Point3f = {
    Point3f(x - p.x, y - p.y, z - p.z)
  }

  def ==(p: Point3f): Boolean = {
    x == p.x && y == p.y && z == p.z
  }
}
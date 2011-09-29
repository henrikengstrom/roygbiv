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
 * Immutable implementation of a vector.
 */
case class Vector3f(x: Float, y: Float, z: Float) {

  def length(): Float = {
    scala.math.sqrt(lengthSquared).asInstanceOf[Float]
  }

  def lengthSquared(): Float = {
    (x * x) + (y * y) + (z * z)
  }

  def dot(v: Vector3f): Float = {
    (x * v.x) + (y * v.y) + (z * v.z)
  }

  def normalize(): Vector3f = {
    val n = (1.0 / scala.math.sqrt((x * x) + (y * y) + (z * z))).asInstanceOf[Float]
    Vector3f(x * n, y * n, z * n)
  }

  def +(v: Vector3f): Vector3f = {
    Vector3f(x + v.x, y + v.y, z + v.z)
  }

  def -(v: Vector3f): Vector3f = {
    Vector3f(x - v.x, y - v.y, z - v.z)
  }

  def *(f: Float): Vector3f = {
    Vector3f(f * x, f * y, f * z)
  }

  def ==(v: Vector3f): Boolean = {
    x == v.x && y == v.y && z == v.z
  }

}

object Vector3f {

  def cross(v1: Vector3f, v2: Vector3f): Vector3f = {
    val x = (v1.y * v2.z) - (v1.z * v2.y)
    val y = (v2.x * v1.z) - (v2.z * v1.x)
    val z = (v1.x * v2.y) - (v1.y*v2.x)
    Vector3f(x, y, z)
  }

  def normalize(v: Vector3f): Vector3f = {
    val n = (1.0 / scala.math.sqrt((v.x * v.x) + (v.y * v.y) + (v.z * v.z))).asInstanceOf[Float]
    Vector3f(v.x * n, v.y * n, v.z * n)
  }

}
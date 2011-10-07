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

case class Tuple3f(x: Float, y: Float, z: Float) {

  def this(other: Tuple3f) {
    this(other.x, other.y, other.z)
  }

  def +(t: Tuple3f): Tuple3f = {
    Tuple3f(x + t.x, y + t.y, z + t.z)
  }

  def -(t: Tuple3f): Tuple3f = {
    Tuple3f(x - t.x, y - t.y, z - t.z)
  }

  def unary_- : Tuple3f = {
    Tuple3f(-x, -y, -z)
  }

  def *(f: Float): Tuple3f = {
    Tuple3f(f * x, f * y, f * z)
  }

  def ==(t: Tuple3f): Boolean = {
    x == t.x && y == t.y && z == t.z
  }

  def distance(other: Tuple3f): Float = {
    scala.math.sqrt(distanceSquared(other)).asInstanceOf[Float]
  }

  def distanceSquared(other: Tuple3f): Float = {
    val dx = x - other.x
    val dy = y - other.y
    val dz = z - other.z
    (dx * dx) + (dy * dy) + (dz * dz)
  }

  def length(): Float = {
    scala.math.sqrt(lengthSquared).asInstanceOf[Float]
  }

  def lengthSquared(): Float = {
    (x * x) + (y * y) + (z * z)
  }

  def dot(t: Tuple3f): Float = {
    (x * t.x) + (y * t.y) + (z * t.z)
  }

  def normalize(): Tuple3f = {
    val n = (1.0 / scala.math.sqrt((x * x) + (y * y) + (z * z))).asInstanceOf[Float]
    Tuple3f(x * n, y * n, z * n)
  }
}

object Tuple3f {
  def apply(other: Tuple3f) = new Tuple3f(other)

   def cross(t1: Tuple3f, t2: Tuple3f): Tuple3f = {
    val x = (t1.y * t2.z) - (t1.z * t2.y)
    val y = (t2.x * t1.z) - (t2.z * t1.x)
    val z = (t1.x * t2.y) - (t1.y * t2.x)
    Tuple3f(x, y, z)
  }

  def normalize(t: Tuple3f): Tuple3f = {
    val n = (1.0 / scala.math.sqrt((t.x * t.x) + (t.y * t.y) + (t.z * t.z))).asInstanceOf[Float]
    Tuple3f(t.x * n, t.y * n, t.z * n)
  }
}

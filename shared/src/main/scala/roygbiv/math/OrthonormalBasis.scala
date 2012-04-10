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
package roygbiv.math

case class OrthonormalBasis(u: Tuple3f, v: Tuple3f, w: Tuple3f) {

  def transform(t: Tuple3f): Tuple3f = {
    Tuple3f((t.x * u.x) + (t.y * v.x) + (t.z * w.x),
      (t.x * u.y) + (t.y * v.y) + (t.z * w.y),
      (t.x * u.z) + (t.y * v.z) + (t.z * w.z))
  }
}

object OrthonormalBasis {
  def makeFromW(w: Tuple3f): OrthonormalBasis = {
    var vx = 0.0f
    var vy = 0.0f
    var vz = 0.0f

    if (scala.math.abs(w.x) < scala.math.abs(w.y) && scala.math.abs(w.x) < scala.math.abs(w.z)) {
      vy = w.z
      vz = -w.y
    } else if (scala.math.abs(w.y) < scala.math.abs(w.z)) {
      vx = w.z
      vz = -w.x
    } else {
      vx = w.y
      vy = -w.x
    }

    val v = Tuple3f(vx, vy, vz).normalize
    OrthonormalBasis(Tuple3f.cross(v, w), v, w)
  }
}
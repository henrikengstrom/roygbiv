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

object MathUtils {
  import roygbiv.math.Tuple3f._

  private val up = Tuple3f(0.004239838f, 0.9999618f, 0.007639708f)
  val InvPi = (1.0D / scala.math.Pi).asInstanceOf[Float]
  val TwoPi = (2.0D * scala.math.Pi).asInstanceOf[Float]
  val InvTwoPi = (1.0D / (2.0D * scala.math.Pi)).asInstanceOf[Float]
  val Epsilon = 1.0000000000000001E-005f
  val MaxDistance = 100000000.0f

  def cosineSampleHemisphere(normal: Tuple3f, u1: Float, u2: Float): Tuple3f = {
    val sinPolar = scala.math.sqrt(1.0D - u1)
    val cosPolar = scala.math.sqrt(u2)
    val azimuth = scala.math.Pi * 2.0D * u1
    val cosAzimuth = scala.math.cos(azimuth)
    val sinAzimuth = scala.math.sin(azimuth)

    val u = cross(up, normal).normalize()
    val v = cross(normal, u)
    val w = Tuple3f((cosAzimuth * sinPolar).asInstanceOf[Float],
                     (sinAzimuth * sinPolar).asInstanceOf[Float],
                     cosPolar.asInstanceOf[Float])

    Tuple3f((w.x * u.x) + (w.y * v.x) + (w.z * normal.x),
            (w.x * u.y) + (w.y * v.y) + (w.z * normal.y),
            (w.x * u.z) + (w.y * v.z) + (w.z * normal.z)).normalize()
  }
}
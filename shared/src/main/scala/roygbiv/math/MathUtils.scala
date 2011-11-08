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

import collection.mutable.ArrayBuffer

object MathUtils {
  import roygbiv.math.Tuple3f._

  private val up = Tuple3f(0.004239838f, 0.9999618f, 0.007639708f)
  val InvPi = (1.0D / scala.math.Pi).asInstanceOf[Float]
  val TwoPi = (2.0D * scala.math.Pi).asInstanceOf[Float]
  val InvTwoPi = (1.0D / (2.0D * scala.math.Pi)).asInstanceOf[Float]
  val Epsilon = 1.0000000000000001E-004f
  val MaxDistance = 100000000.0f

  def cosineSampleHemisphere(normal: Tuple3f, u1: Float, u2: Float): Tuple3f = {
    val sinPolar = scala.math.sqrt(1.0D - u2)
    val cosPolar = scala.math.sqrt(u2)
    val azimuth = scala.math.Pi * 2.0D * u1
    val cosAzimuth = scala.math.cos(azimuth)
    val sinAzimuth = scala.math.sin(azimuth)

    val u = cross(up, normal).normalize
    val v = cross(normal, u)
    val w = Tuple3f((cosAzimuth * sinPolar).asInstanceOf[Float],
                     (sinAzimuth * sinPolar).asInstanceOf[Float],
                     cosPolar.asInstanceOf[Float])

    Tuple3f((w.x * u.x) + (w.y * v.x) + (w.z * normal.x),
            (w.x * u.y) + (w.y * v.y) + (w.z * normal.y),
            (w.x * u.z) + (w.y * v.z) + (w.z * normal.z)).normalize
  }

  def powerHeuristic(pdf1: Float, pdf2: Float): Float = {
    val pdf1sq = pdf1 * pdf1
    (pdf1sq) / (pdf1sq + pdf2 * pdf2)
  }

  def getRandomVectorInUnitSphere(u1: Float, u2: Float): Tuple3f = {
    val sqrt = scala.math.sqrt(u2 * (1.0f - u2))
    val twoPi = 2.0D * scala.math.Pi * u1
    val x = (2.0D * scala.math.cos(twoPi) * sqrt).asInstanceOf[Float]
    val y = (2.0D * scala.math.sin(twoPi) * sqrt).asInstanceOf[Float]
    val z = 1.0f - (2.0f * u2)

    Tuple3f(x, y, z).normalize
  }

  def vanDerCorput(m: Int, scramble: Int): Float = {
    var n = m
    n = (n << 16) | (n >>> 16)
    n = ((n & 0x00ff00ff) << 8) | ((n & 0xff00ff00) >>> 8)
    n = ((n & 0x0f0f0f0f) << 4) | ((n & 0xf0f0f0f0) >>> 4)
    n = ((n & 0x33333333) << 2) | ((n & 0xcccccccc) >>> 2)
    n = ((n & 0x55555555) << 1) | ((n & 0xaaaaaaaa) >>> 1)
    n ^= scramble
    ((n >>> 8) & 0xffffff) / (1 << 24).asInstanceOf[Float]
  }

  def sobol2(m: Int, s: Int): Float = {
    var n = m
    var scramble = s
    var v = 1 << 31

    while (n != 0) {
      if ((n & 1) != 0)
        scramble ^= v

      n >>>= 1
      v ^= v >>> 1
    }

    (((scramble >>> 8) & 0xffffff)) / (1 << 24).asInstanceOf[Float]
  }

  def shuffle[T](array: ArrayBuffer[T], count: Int,  dims: Int, rng: RandomNumberGenerator) {
    for (i <- 0 until count) {
      val other = i + scala.math.abs(rng.nextInt % (count - i))

      for (j <- 0 until  dims) {
        val pos1 = dims * i + j
        val pos2 = dims * other + j
        val temp = array(pos1)
        array(pos1) = array(pos2)
        array(pos2) = temp
      }

    }
  }
}
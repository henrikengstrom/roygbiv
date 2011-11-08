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

import roygbiv.color.RGBColor
import roygbiv.math.{MathUtils, OrthonormalBasis, Tuple3f}

case class DiscEmitter(center: Tuple3f, normal: Tuple3f, radius: Float, color: RGBColor, power: Float) extends Disc with Emitter {
  val radiance = color * power
  lazy val pdf = 1.0f / area
  lazy val basis = OrthonormalBasis.makeFromW(normal)

  def le: RGBColor = radiance

  def getSample(u1: Float, u2: Float): LightSample = {
    LightSample(randomPointOnSurface(u1, u2), normal, radiance, pdf)
  }

  def randomPointOnSurface(u1: Float, u2: Float): Tuple3f = {
    val theta =  u2 * MathUtils.TwoPi
    val sqrtFactor = radius * scala.math.sqrt(u1)
    val x = (sqrtFactor * scala.math.cos(theta)).asInstanceOf[Float]
    val y = (sqrtFactor * scala.math.sin(theta)).asInstanceOf[Float]
    basis.transform(Tuple3f(x, y, 0.0f)) + center
  }
}
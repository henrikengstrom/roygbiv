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

import roygbiv.math.Tuple3f
import roygbiv.color.RGBColor

case class DiscEmitter(center: Tuple3f, normal: Tuple3f, radius: Float, color: RGBColor, power: Float) extends Disc with Emitter {
  val radiance = color * power

  lazy val pdf = 1.0f / area

  def le: RGBColor = radiance

  def getSample(u1: Float, u2: Float): LightSample = {
    // TODO - implement
    /*
    var point: Point3f = getRandomPointOnSurface(r1, r2)
    var normalAtPoint: Vector3f = getNormalAtPoint(point)
    var color: Spectrum = new Spectrum(_radiance)
    var pdf: Float = 1.0f / getArea
    return new LightSample(point, normalAtPoint, color, pdf)
    */
    null
  }
}
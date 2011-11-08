
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
package roygbiv.bxdf

import roygbiv.math.Tuple3f
import roygbiv.color.RGBColor
import roygbiv.fresnel.Fresnel

case class SpecularReflectionBSDF(color: RGBColor, fresnel: Fresnel) extends BSDF {

  def sampleF(wi: Tuple3f, normal: Tuple3f, u1: Float, u2: Float): BSDFSample = {
    val dot = normal.dot(wi)
    val wo = -((normal * (2.0f * -dot) + wi).normalize)
    val color = fresnel.evaluate(dot) * (1.0f / dot)
    BSDFSample(wo, 1.0f, color)
  }

  def f(wi: Tuple3f, wo: Tuple3f, normal: Tuple3f): RGBColor = RGBColor.Black

  def rho: RGBColor = color

  def pdf(wi: Tuple3f, wo: Tuple3f, normal: Tuple3f): Float = 1.0f

  override def hasDeltaDistribution: Boolean = true
}
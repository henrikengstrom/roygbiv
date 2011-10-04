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

import roygbiv.color.RGBColor
import roygbiv.math.{MathUtils, Vector3f}

case class LambertianBSDF(color: RGBColor) extends BSDF {
  private final val preScaledColor = color * MathUtils.InvPi

  def sampleF(wi: Vector3f, normal: Vector3f, u1: Float, u2: Float): BSDFSample = {
    val wo = MathUtils.cosineSampleHemisphere(normal, u1, u2)
    BSDFSample(wo, pdf(wi, wo, normal), preScaledColor)
  }

  def f(wi: Vector3f, wo: Vector3f, normal: Vector3f): RGBColor = preScaledColor

  def rho: RGBColor = color

  def pdf(wi: Vector3f, wo: Vector3f, normal: Vector3f): Float = {
    normal.dot(wo) * MathUtils.InvPi
  }
}
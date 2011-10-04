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

import roygbiv.math.Vector3f
import roygbiv.color.RGBColor

case class BSDFSample(wo: Vector3f, pdf: Float, color: RGBColor)

trait BSDF {
  def sampleF(wi: Vector3f, normal: Vector3f, u1: Float, u2: Float): BSDFSample
  def f(wi: Vector3f, wo: Vector3f, normal: Vector3f): RGBColor
  def rho: RGBColor
  def hasDeltaDistribution: Boolean = false
  def pdf(wi: Vector3f, wo: Vector3f, normal: Vector3f): Float
}
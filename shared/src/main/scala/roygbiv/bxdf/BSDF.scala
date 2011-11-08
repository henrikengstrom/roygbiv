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

case class BSDFSample(wo: Tuple3f, pdf: Float, color: RGBColor)

trait BSDF {
  def sampleF(wi: Tuple3f, normal: Tuple3f, u1: Float, u2: Float): BSDFSample
  def f(wi: Tuple3f, wo: Tuple3f, normal: Tuple3f): RGBColor
  def rho: RGBColor
  def hasDeltaDistribution: Boolean = false
  def pdf(wi: Tuple3f, wo: Tuple3f, normal: Tuple3f): Float
}
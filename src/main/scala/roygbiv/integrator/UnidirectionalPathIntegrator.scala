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
package roygbiv.integrator

import roygbiv.color.RGBColor
import roygbiv.scene.Scene
import roygbiv.shape.{Shape, Emitter, Scatterer}
import roygbiv.bxdf.BSDF
import roygbiv.math.{Ray, MathUtils, RandomNumberGenerator, Tuple3f}

case class UnidirectionalPathIntegrator(scene: Scene) {
  val camera = scene.camera

  def l(pixelX: Float, pixelY: Float, rng: RandomNumberGenerator): RGBColor = {
    val color = tracePixel(pixelX, pixelY, rng)
    if (!color.isNaN) color else RGBColor.Black
  }

  def tracePixel(pixelX: Float, pixelY: Float, rng: RandomNumberGenerator): RGBColor = {
    val ray = camera.getRayForPixel(pixelX, pixelY)

    scene.intersect(ray) match {
      case Some(i) => i.shape match {
        case s: Scatterer => {
          estimateRadiance(s, i.point, getAdjustedNormal(s.getNormalAtPoint(i.point), ray, s), -ray.direction, s.material.bsdf, 1, rng)
        }
        case e: Emitter => {
          if (e.getNormalAtPoint(i.point).dot(ray.direction) < 0.0f) e.le else RGBColor.Black
        }
      }
      case None => RGBColor.Black
    }
  }

  def estimateRadiance(shape: Shape, intersection: Tuple3f, normal: Tuple3f, wi: Tuple3f, bsdf: BSDF, traceDepth: Int, rng: RandomNumberGenerator): RGBColor = {
    if (traceDepth <= UnidirectionalPathIntegrator.MaxTraceDepth)
      estimateIndirectIllumination(shape, intersection, normal, wi, bsdf, traceDepth, rng)
    else
      RGBColor.Black
  }

  def estimateIndirectIllumination(shape: Shape, intersection: Tuple3f, normal: Tuple3f, wi: Tuple3f, bsdf: BSDF, traceDepth: Int, rng: RandomNumberGenerator): RGBColor = {
    val sample = bsdf.sampleF(wi, normal, rng.nextRandom, rng.nextRandom)

    if (sample.pdf == 0.0f)
      return RGBColor.Black

    val contrib = sample.color * (scala.math.abs(normal.dot(sample.wo)) / sample.pdf)
    val absorbance = if (traceDepth > UnidirectionalPathIntegrator.MinBounces) scala.math.min(0.9f, contrib.max) else 1.0f

    if (absorbance > rng.nextRandom) {
      var estimatedRadiance = RGBColor.Black
      val ray = Ray(normal * MathUtils.Epsilon + intersection, sample.wo)

      scene.intersect(ray) match {
        case Some(i) => i.shape match {
          case s: Scatterer => {
            estimatedRadiance = estimateRadiance(s, i.point, getAdjustedNormal(s.getNormalAtPoint(i.point), ray, s), -ray.direction, s.material.bsdf, traceDepth + 1, rng)
          }
          case e: Emitter => {
            if (e.getNormalAtPoint(i.point).dot(ray.direction) < 0.0f)
              estimatedRadiance = e.le
          }
        }
        case None => RGBColor.Black
      }

      if (!estimatedRadiance.isBlack) {
        estimatedRadiance = estimatedRadiance * contrib

        if (traceDepth > UnidirectionalPathIntegrator.MinBounces) {
          estimatedRadiance = estimatedRadiance * (1.0f / absorbance)
        }
      }

      estimatedRadiance
    } else {
      RGBColor.Black
    }
  }

  def getAdjustedNormal(normal: Tuple3f, ray: Ray, scatterer: Scatterer): Tuple3f = {
    if (normal.dot(ray.direction) > 0.0f && !scatterer.material.bsdf.hasDeltaDistribution)
       -normal
    else
      normal
  }
}

object UnidirectionalPathIntegrator {
  val MaxTraceDepth = 10
  val MinBounces = 3
}
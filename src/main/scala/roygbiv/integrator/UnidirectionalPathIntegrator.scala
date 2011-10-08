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
import roygbiv.shape.{Emitter, Scatterer}

case class UnidirectionalPathIntegrator(scene: Scene) {
  val camera = scene.camera

  def l(pixelX: Float, pixelY: Float): RGBColor = {
    val ray = camera.getRayForPixel(pixelX, pixelY)

    scene.intersect(ray) match {
      case Some(i) => i.shape match {
        case s: Scatterer =>
          s.material.getBSDF.rho
        case e: Emitter =>
          e.le
      }
      case None => RGBColor.Black
    }
  }

}
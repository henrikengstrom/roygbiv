/**
 * Copyright [2011] [Henrik Engstroem, Mario Gonzalez]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package roygbiv.camera

import roygbiv.math.{ Ray, OrthonormalBasis, Tuple3f }

case class PinholeCamera(eyeLocation: Tuple3f, target: Tuple3f, upVector: Tuple3f, viewPlaneDistance: Float, screenWidth: Int, screenHeight: Int) extends Camera {
  val invScreenWidth = 1.0f / screenWidth
  val invScreenHeight = 1.0f / screenHeight
  var viewPlaneWidth = 1.0f
  var viewPlaneHeight = 1.0f

  if (screenWidth > screenHeight) {
    viewPlaneHeight = screenHeight.asInstanceOf[Float] / screenWidth
  } else if (screenHeight > screenWidth) {
    viewPlaneWidth = screenWidth.asInstanceOf[Float] / screenHeight
  }

  val bu = viewPlaneWidth * 0.5f
  val bv = viewPlaneHeight * 0.5f
  val au = -bu
  val av = -bv
  val uScale = (bu - au) * invScreenWidth
  val vScale = (bv - av) * invScreenHeight
  val basis = PinholeCamera.createBasis(target, eyeLocation, upVector)

  def getRayForPixel(pixelX: Float, pixelY: Float): Ray = {
    val xv = au + uScale * pixelX
    val yv = av + vScale * (screenHeight - pixelY)
    val zv = -viewPlaneDistance
    val direction = basis.transform(Tuple3f(xv, yv, zv)).normalize
    Ray(eyeLocation, direction)
  }
}

object PinholeCamera {
  def createBasis(target: Tuple3f, eyeLocation: Tuple3f, upVector: Tuple3f): OrthonormalBasis = {
    val w = -((target - eyeLocation).normalize)
    val u = Tuple3f.cross(upVector, w)
    val v = Tuple3f.cross(w, u)
    OrthonormalBasis(u, v, w)
  }
}

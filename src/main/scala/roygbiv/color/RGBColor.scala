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
package roygbiv.color

/**
 * Immutable implementation of an RGB color.
 */
case class RGBColor(red: Float, green: Float, blue: Float) extends Color {
  import RGBColor._

  def this(color: Float) = {
    this(color, color, color)
  }

  def +(other: RGBColor): RGBColor = {
    RGBColor(red + other.red, green + other.green, blue + other.blue)
  }

  def -(other: RGBColor): RGBColor = {
    RGBColor(red - other.red, green - other.green, blue - other.blue)
  }

  def *(other: RGBColor): RGBColor = {
    RGBColor(red * other.red, green * other.green, blue * other.blue)
  }

  def *(scalar: Float): RGBColor = {
    RGBColor(red * scalar, green * scalar, blue * scalar)
  }

  def luminance: Float = {
    (0.212671f * red) + (0.715160f * green) + (0.072169f * blue)
  }

  def max: Float = {
    scala.math.max(red, scala.math.max(green, blue))
  }

  def asInt: Int = {
    val r = scala.math.max(gammaCompress(red) * 255f, 0.0f).asInstanceOf[Int]
    val g = scala.math.max(gammaCompress(green) * 255f, 0.0f).asInstanceOf[Int]
    val b = scala.math.max(gammaCompress(blue) * 255f, 0.0f).asInstanceOf[Int]

    (255 << 24) | (if (r > 255) 255 else r) << 16 | (if (g > 255) 255 else g) << 8 | (if (b > 255) 255 else b)
  }

  def isNaN: Boolean = {
    red.isNaN || green.isNaN || blue.isNaN
  }

  def isBlack: Boolean = {
    return !(red > 0.0f || green > 0.0f || blue > 0.0f)
  }
}

object RGBColor {
  val Gamma = (1.0D / 2.4D).asInstanceOf[Float]
  val Black = RGBColor(0.0f)

  def apply(color: Float) = new RGBColor(color)

  private def gammaCompress(value: Float): Float = {
    if (value <= 0.0031308f) 12.92f * value else (1.055f * scala.math.pow(value, Gamma) - 0.055f).asInstanceOf[Float]
  }
}
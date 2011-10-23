package roygbiv.sampler

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
import collection.mutable.ArrayBuffer
import roygbiv.math.{MathUtils, RandomNumberGenerator}

case class LowDiscrepancySampler(n1DSamples: Int, n2DSamples: Int, rng: RandomNumberGenerator) extends Sampler {
  var oneDSamples = (new ArrayBuffer[Float](n1DSamples)).padTo(n1DSamples, 0.0f);
  var twoDSamples = (new ArrayBuffer[Float](n2DSamples * 2)).padTo(n2DSamples * 2, 0.0f);
  var oneDPos = 0
  var twoDPos = 0

  def generateSamples {
    oneDPos = 0
    twoDPos = 0

    for (i <- 0 until n1DSamples)
      oneDSamples(i) = MathUtils.vanDerCorput(i, rng.nextInt)

    for (i <- 0 until (n2DSamples * 2) by 2) {
      twoDSamples(i) = MathUtils.vanDerCorput(i, rng.nextInt)
      twoDSamples(i + 1) = MathUtils.sobol2(i + 1, rng.nextInt)
    }

    MathUtils.shuffle(oneDSamples, n1DSamples, 1, rng)
    MathUtils.shuffle(twoDSamples, n2DSamples, 2, rng)
  }

  def nextSample1D: Float = {
    if (oneDPos < n1DSamples) {
      val ret = oneDSamples(oneDPos)
      oneDPos += 1
      ret
    } else {
      rng.nextFloat
    }
  }

  def nextSample2D: (Float, Float) = {
    if (twoDPos < n2DSamples) {
      val ret1 = twoDSamples(twoDPos)
      val ret2 = twoDSamples(twoDPos + 1)
      twoDPos += 2
      (ret1, ret2)
    } else {
      (rng.nextFloat, rng.nextFloat)
    }
  }
}
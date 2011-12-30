/**
 * Mersenne twister random number generator, based off the java version written by
 * Sean Luke and Michael Lecuyer
 *
 * <h3>License</h3>
 *
 * Copyright (c) 2003 by Sean Luke. <br>
 * Portions copyright (c) 1993 by Michael Lecuyer. <br>
 * All rights reserved. <br>
 *
 * <p>Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <ul>
 * <li> Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <li> Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <li> Neither the name of the copyright owners, their employers, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * </ul>
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
package roygbiv.math

/**
 * Mersenne twister random number generator.
 */
case class MersenneTwisterRNG (seed: Long = java.lang.System.nanoTime() + Thread.currentThread().getId) extends RandomNumberGenerator {
  val N = 624
  val M = 397
  val MatrixA = 0x9908b0df
  val UpperMask = 0x80000000
  val LowerMask = 0x7fffffff
  val TemperingMaskB = 0x9d2c5680
  val TemperingMaskC = 0xefc60000
  val denominator = (1 << 24).toFloat

  val mt = new Array[Int](N)
  var mti = N + 1
  val mag01 = Array[Int](0x0, MatrixA)

  mt(0) = (seed & 0xffffffff).toInt
  for (i <- 1 until N) mt(i) = (1812433253 * (mt(i - 1) ^ (mt(i - 1) >>> 30)) + i) & 0xffffffff

  /**
   * Returns a random float in the half-open range from [0.0f,1.0f). Thus 0.0f
   * is a valid result but 1.0f is not.
   */
  final def nextFloat: Float = {
    var y = 0

    if (mti >= N) {
      var kk = 0

      while (kk < N - M) {
        y = (mt(kk) & UpperMask) | (mt(kk + 1) & LowerMask)
        mt(kk) = mt(kk + M) ^ (y >>> 1) ^ mag01(y & 0x1)
        kk += 1
      }
      while (kk < N - 1) {
        y = (mt(kk) & UpperMask) | (mt(kk + 1) & LowerMask)
        mt(kk) = mt(kk + (M - N)) ^ (y >>> 1) ^ mag01(y & 0x1)
        kk += 1
      }
      y = (mt(N - 1) & UpperMask) | (mt(0) & LowerMask)
      mt(N - 1) = mt(M - 1) ^ (y >>> 1) ^ mag01(y & 0x1)

      mti = 0
    }

    y = mt(mti)
    mti += 1

    y ^= y >>> 11
    y ^= (y << 7) & TemperingMaskB
    y ^= (y << 15) & TemperingMaskC
    y ^= (y >>> 18)

    (y >>> 8) / denominator
  }

  final def nextInt: Int = {
    var y = 0

    if (mti >= N) {
      var kk = 0

      while (kk < N - M) {
        y = (mt(kk) & UpperMask) | (mt(kk + 1) & LowerMask)
        mt(kk) = mt(kk + M) ^ (y >>> 1) ^ mag01(y & 0x1)
        kk += 1
      }
      while (kk < N - 1) {
        y = (mt(kk) & UpperMask) | (mt(kk + 1) & LowerMask)
        mt(kk) = mt(kk + (M - N)) ^ (y >>> 1) ^ mag01(y & 0x1)
        kk += 1
      }
      y = (mt(N - 1) & UpperMask) | (mt(0) & LowerMask)
      mt(N - 1) = mt(M - 1) ^ (y >>> 1) ^ mag01(y & 0x1)

      mti = 0
    }

    y = mt(mti)
    mti += 1

    y ^= y >>> 11
    y ^= (y << 7) & TemperingMaskB
    y ^= (y << 15) & TemperingMaskC
    y ^= (y >>> 18)

    y
  }
}
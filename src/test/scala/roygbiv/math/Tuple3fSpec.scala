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
package roygbiv.math

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec

@org.junit.runner.RunWith(classOf[org.scalatest.junit.JUnitRunner])
class Tuple3fSpec extends WordSpec with MustMatchers {
  "A Tuple3f" must {
    "add" in {
      val p1 = Tuple3f(1, 2, 3)
      val p2 = Tuple3f(1, 2, 3)
      val p3 = p1 + p2
      p3.x must equal (2)
      p3.y must equal (4)
      p3.z must equal (6)

      val p4 = p3 + p1
      p4.x must equal (3)
      p4.y must equal (6)
      p4.z must equal (9)
    }

    "subtract" in {
      val p1 = Tuple3f(1, 1, 1)
      val p2 = Tuple3f(1, 1, 1)
      val p3 = p1 - p2
      p3.x must equal (0)
      p3.y must equal (0)
      p3.z must equal (0)
    }

    "use equality correctly" in {
      val p1 = Tuple3f(2, 3, 4)
      val p2 = Tuple3f(2, 3, 4)
      val p3 = Tuple3f(3, 4, 5)
      p1 == p1 must be (true)
      p1 == p2 must be (true)
      p1 == p3 must be (false)
    }
  }
}
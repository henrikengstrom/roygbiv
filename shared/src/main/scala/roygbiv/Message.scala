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
package roygbiv

import color.RGBColor
import scene.Scene

trait Message

case class ClientRegistration(remoteAddress: String) extends Message

case object Pause extends Message

case object Stop extends Message

case object Start extends Message

case class WorkInstruction(aggregatorServer: String, scene: Scene)

case class WorkResult(workerId: String, clientThreads: Int, result: Seq[RGBColor])

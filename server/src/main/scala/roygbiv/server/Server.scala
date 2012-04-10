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
package roygbiv.server

import akka.kernel.Bootable
import akka.actor.{ Props, ActorSystem }

class Server extends Bootable {
  val system = ActorSystem("RaytraceServer")
  val aggregator = system.actorOf(Props[Aggregator], "aggregator")
  val distributor = system.actorOf(Props[Distributor], "distributor")
  // TODO : remove
  println("*** RAY TRACE SERVER STARTED ***")

  def startup() {
    aggregator ! "Start"
    distributor ! "Start"
  }

  def shutdown() {
    system.shutdown()
  }
}

object Server extends App {
  new Server
}
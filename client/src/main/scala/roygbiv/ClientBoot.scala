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

import akka.actor.Actor._
import akka.config.Config.config
import akka.config.Supervision
import akka.http.RootEndpoint
import akka.config.Supervision.{SupervisorConfig, Permanent}
import akka.actor.SupervisorFactory
import akka.config.Supervision.OneForOneStrategy
import worker.RegisterClient

object Client {
  def main(args: Array[String]) {
    new ClientBoot
  }
}

class ClientBoot {
  val clientHost = config.getString("akka.remote.server.hostname", "127.0.0.1")
  val clientPort = config.getInt("akka.remote.server.port", 2553)
  val clientWorker = actorOf[ClientWorker].start()
  val serverHost = config.getString("akka.roygbiv.remote.hostname", "127.0.0.1")
  val serverPort = config.getInt("akka.roygbiv.remote.port", 2552)
  val serviceId = "supervisor"

  val factory = SupervisorFactory(
    SupervisorConfig(OneForOneStrategy(List(classOf[Exception]), 3, 100),
      Supervision.Supervise(actorOf[RootEndpoint], Permanent) ::
        Supervision.Supervise(clientWorker, Permanent) ::
        Nil))

  factory.newInstance.start

  // Register clientWorker as an actor waiting for work
  remote.start(clientHost, clientPort)
  remote.register("clientWorker", clientWorker)

  // Call server and ask for work
  remote.actorFor(serviceId, serverHost, serverPort) ! RegisterClient("clientWorker", clientHost, clientPort)
}
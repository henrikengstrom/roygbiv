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

import aggregator.WorkAggregator
import akka.config.Supervision
import akka.http.RootEndpoint
import akka.config.Supervision.{SupervisorConfig, Permanent}
import akka.actor.SupervisorFactory
import akka.actor.Actor._
import akka.config.Supervision.OneForOneStrategy
import akka.config.Supervision.Permanent
import akka.config.Supervision.SupervisorConfig
import akka.config.Config.config

import distributor.WorkDistributor
import http.RoygbivEndpoint
import supervisor.WorkSupervisor

class ServerBoot {
  val endpointActor = actorOf[RoygbivEndpoint]
  val workSupervisor = actorOf[WorkSupervisor].start()
  val workDistributor = actorOf[WorkDistributor].start()

  val factory = SupervisorFactory(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      Supervision.Supervise(actorOf[RootEndpoint], Permanent) ::
        Supervision.Supervise(endpointActor, Permanent) ::
        Supervision.Supervise(workSupervisor, Permanent) ::
        Supervision.Supervise(workDistributor, Permanent) ::
        Nil))

  factory.newInstance.start

  val supervisor = actorOf[WorkSupervisor].start()
  val aggregator = actorOf[WorkAggregator].start()

  // start the remoting as specified in configuration
  val serverHost = config.getString("akka.remote.server.hostname", "127.0.0.1")
  val serverPort = config.getInt("akka.remote.server.port", 2552)

  remote.start(serverHost, serverPort)
  remote.register("supervisor", supervisor)
  remote.register("aggregator", aggregator)
}
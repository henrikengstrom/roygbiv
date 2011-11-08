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
package roygbiv.supervisor

import akka.actor.Actor
import akka.actor.Actor._
import roygbiv.worker.RegisterClient
import roygbiv.distributor.{ClientInformation, WorkDistributor}

class WorkSupervisor extends Actor {
  def receive = {
    case r @ RegisterClient(id, host, port) =>
      println("Registered client with id, host, port: [%s], [%s], [%d]".format(id, host, port))
      actorOf[WorkDistributor].start() ! ClientInformation(id, host, port)
    case _ => // TODO : Add more life cycle methods for clients here
  }
}
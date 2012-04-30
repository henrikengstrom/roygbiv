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
package roygbiv.client

import roygbiv.scene.Scene
import roygbiv.worker.RayTracer
import akka.actor.{ PoisonPill, Props, ActorRef, Actor }
import roygbiv._

case object Initialize

class Worker extends Actor {

  import Worker._

  var aggregatorServer: String = ""
  var scene: Option[Scene] = None
  var workerHandles: Vector[ActorRef] = Vector()

  def receive = {
    case Initialize ⇒
      // Connect the worker with the server
      context.actorFor(context.system.settings.config.getString("akka.roygbiv.server-work-distributor")) ! ClientRegistration
    case WorkInstruction(server, theScene) ⇒
      aggregatorServer = server
      scene = Some(theScene)
    case Start ⇒
      if (scene.isDefined) {
        1 until availableProcessors foreach { i ⇒
          val worker = context.actorOf(Props[RayTracer].withDispatcher("pinned-dispatcher"), "worker" + i)
          worker ! new roygbiv.worker.Work(scene.get)
          workerHandles = worker +: workerHandles
        }
      }
    case Stop ⇒
      workerHandles.foreach(handle ⇒ handle ! PoisonPill)
      workerHandles = Vector()
      self ! PoisonPill
    case wr: WorkResult ⇒
      context.actorFor(aggregatorServer) ! WorkResult(wr.workerId, availableProcessors, wr.result)
  }
}

object Worker {
  val availableProcessors = Runtime.getRuntime.availableProcessors
}
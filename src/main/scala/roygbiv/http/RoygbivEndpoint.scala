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
package roygbiv.http

import akka.http.{RootEndpoint, Endpoint, RequestMethod, Get}
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import akka.event.EventHandler

object EndpointURI {
  final val ServiceQualifierUri = "/roygbiv"
  final val PingServiceUri = ServiceQualifierUri + "/ping"
  final val RenderServiceUri = ServiceQualifierUri + "/render"
}

class RoygbivEndpoint   extends Actor with Endpoint {
  import EndpointURI._

  override def preStart() = {
    val root = Actor.registry.actorsFor(classOf[RootEndpoint]).head
    root ! Endpoint.Attach(hook, provide)
  }

  def receive = handleHttpRequest

  def hook(uri: String): Boolean = {
    (uri ne null) &&
      ((uri == PingServiceUri) ||
      (uri.startsWith(RenderServiceUri)))
  }

  def provide(uri: String): ActorRef = {
    if (uri == PingServiceUri) {
      pingResource
    } else if (uri.startsWith(RenderServiceUri)) {
      renderResource
    } else {
      EventHandler.debug(this, "Could not find any uri matching [%s]".format(uri))
      throw new IllegalArgumentException("Could not find any uri matching [%s]".format(uri))
    }
  }

  val pingResource = actorOf[PingResource].start()
  val renderResource = actorOf[RenderResource].start()
}
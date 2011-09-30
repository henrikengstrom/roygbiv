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

import akka.actor.Actor
import akka.http.{RequestMethod, Get}

class RenderResource extends Actor {
  import RenderResource._

  def receive = {
    case get: Get =>
      get.request.getPathInfo match {
        case StartPattern => get.OK("starting...")
        case StopPattern => get.OK("stopping...")
        case StatusPattern => get.OK("status is ok")
        case other => get.BadRequest("Incorrect uri [%s]".format(get.request.getPathInfo))
      }
    case other: RequestMethod => other.NotAllowed("Unsupported HTTP method for endpoint.")
  }
}

object RenderResource {
  val StartPattern = EndpointURI.RenderServiceUri + "/start"
  val StopPattern = EndpointURI.RenderServiceUri + "/stop"
  val StatusPattern = EndpointURI.RenderServiceUri + "/status"
}
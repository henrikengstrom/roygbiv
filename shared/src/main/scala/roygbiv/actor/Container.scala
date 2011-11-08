package roygbiv.actor

import akka.actor.{ActorRef, Actor}

class Container[T](ownerActorRef: ActorRef) extends Actor {
  var parameter: Option[T] = None

  def receive = {
    case any: Any => parameter = Some(any.asInstanceOf[T])
    case other =>  ownerActorRef ! parameter.get
  }
}
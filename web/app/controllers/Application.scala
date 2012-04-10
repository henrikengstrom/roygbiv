package controllers

import _root_.akka.actor.{Props, ActorSystem, Actor}
import _root_.akka.pattern.ask
import _root_.akka.util.duration._
import play.api.mvc._
import play.api.libs._
import play.api.libs.Comet._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import roygbiv.{ClientRegistration, Stop, Start}

object Application extends Controller {
  import ActorContainer._

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def start = Action {
    println("*** STARTING...")
    Distributor ! Start
    Aggregator ! ClientRegistration
    WebActorHandler ! InitializeServerCommunication
    Ok(views.html.index("ok"))
  }

  def stop = Action {
    Distributor ! Stop
    Ok(views.html.index("ok"))
  }

  def statistics = Action {
    AsyncResult {
      (WebActorHandler ? StatusCallbackMessage).mapTo[Enumerator[String]].asPromise.map {
        chunks => Ok.stream(chunks &> Comet( callback = "parent.statistics"))
      }
    }
  }

  def image = Action {
    AsyncResult {
      (WebActorHandler ? ImageCallbackMessage).mapTo[Enumerator[String]].asPromise.map {
        content => Ok.stream(content &> Comet( callback = "parent.image"))
      }
    }
  }
}
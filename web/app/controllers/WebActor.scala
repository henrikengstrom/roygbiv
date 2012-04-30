package controllers

import javax.swing.ImageIcon
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import org.apache.commons.codec.binary.Base64
import java.awt.image.BufferedImage
import java.awt.{Image, Graphics2D, GraphicsConfiguration}
import play.api.libs.iteratee.{Enumerator, PushEnumerator}
import akka.actor.{Props, ActorSystem, Actor}
import akka.util.duration._
import akka.util.Timeout
import roygbiv.{RenderingStatistics, ClientRegistration}
import roygbiv.server.Aggregator

class WebActor extends Actor {
  import ActorContainer._

  var dataListeners = Seq.empty[PushEnumerator[String]]
  var imageListeners = Seq.empty[PushEnumerator[String]]

  def receive = {
    case InitializeServerCommunication =>
      Aggregator ! ClientRegistration
    case StatusCallbackMessage =>
      lazy val channel:PushEnumerator[String] = Enumerator.imperative[String](
        onComplete = self ! channel
      )
      dataListeners = dataListeners :+ channel
      sender ! channel
    case ImageCallbackMessage =>
      lazy val channel: PushEnumerator[String] = Enumerator.imperative[String](
        onComplete = self ! channel
      )
      imageListeners = imageListeners :+ channel
      sender ! channel
    case image: ImageIcon =>
      val result = createBaseEncodedImage(image)
      imageListeners.foreach(_.push(result))
    case stats: RenderingStatistics =>
      val msg = stats.resultCounter  + "," + stats.totalRays + "," + stats.raysPerSecond
      dataListeners.foreach(_.push(msg))
  }

  def createBaseEncodedImage(src: ImageIcon): String = {
    val baos = new ByteArrayOutputStream(src.getIconHeight * src.getIconWidth)
    ImageIO.write(createBufferedImage(src.getImage), "jpg", baos)
    baos.flush()
    new String(Base64.encodeBase64(baos.toByteArray))
  }

  def createBufferedImage(src: Image): BufferedImage = {
    val image = new BufferedImage(src.getWidth(null), src.getHeight(null), BufferedImage.TYPE_INT_RGB);
    val g2d = image.getGraphics.asInstanceOf[Graphics2D];
    g2d.drawImage(src, 0, 0, null);
    g2d.dispose();
    image
  }
}

object ActorContainer {
  implicit val timeout = Timeout(5 seconds)
  val WebActorSystem = ActorSystem("WebSystem")
  val Distributor = WebActorSystem.actorFor("akka://RaytraceServer@127.0.0.1:2552/user/distributor")
  val Aggregator = WebActorSystem.actorFor("akka://RaytraceServer@127.0.0.1:2552/user/aggregator")
  val WebActorHandler = WebActorSystem.actorOf(Props[WebActor], "webActor")
}
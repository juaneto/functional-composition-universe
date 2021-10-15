package scalacon.webapp

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, document}
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLImageElement

import scala.util.Random

object FunctionalCompositionApp {

  case class Position(x: Double, y: Double)

  case class Size(x: Int, y: Int)

  case class Image(src: String, var angleRotation: Double = 0) {
    private var ready: Boolean = false

    val element: HTMLImageElement = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    element.onload = (_: dom.Event) => ready = true
    element.src = src

    def isReady: Boolean = ready
  }

  type Movement = Position => Position
  type Rotation = Double => Double

  trait SpaceElement {
    val image: Image
    val size: Size
    var position: Position
  }

  trait hasOrbit extends SpaceElement { var orbit: Movement }
  trait hasRotation extends SpaceElement {
    var rotation: Rotation
  }

  case class Sun(image: Image, var position: Position, size: Size) extends SpaceElement
  case class Tenant(image: Image, var position: Position, size: Size, var movement: Movement, var rotation: Rotation) extends SpaceElement with hasRotation
  case class Satellite(image: Image, var position: Position, size: Size, var orbit: Movement, var rotation: Rotation) extends hasOrbit with hasRotation
  case class Planet(image: Image, var position: Position, size: Size, var orbit: Movement, satellites: List[Satellite], var rotation: Rotation) extends hasOrbit with hasRotation

  def main(args: Array[String]): Unit = {
    document.addEventListener("DOMContentLoaded", { (_: dom.Event) =>
      setupUI()
    })
  }

  def setupUI(): Unit = {
    val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    def createBackground(): CanvasRenderingContext2D = {
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      ctx.fillStyle = "#000000"
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      ctx
    }

    canvas.width = dom.window.innerWidth.toInt
    canvas.height = dom.window.innerHeight.toInt

    createBackground()

    dom.document.body.appendChild(canvas)

    val sun = Sun(Image("images/sun.png"), Position((canvas.width / 2) - 20, (canvas.height / 2) - 25), Size(50, 50))
    val moon = Satellite(Image("images/moon.png"), Position((canvas.width / 2) - 50, (canvas.height / 2) - 50), Size(50, 50), (p: Position) => Position(p.x+1, p.y), (r: Double) => r+0.1)
    val moon2 = Satellite(Image("images/moon.png"), Position((canvas.width / 2) - 100, (canvas.height / 2) - 100), Size(20, 20), (p: Position) => Position(p.x+1, p.y), (r: Double) => r+0.3)
    val mars = Planet(Image("images/mars.png"), Position(sun.position.x - 100, sun.position.y - 100), Size(100, 100), (p: Position) => Position(p.x+1, p.y), List(moon, moon2), (r: Double) => r+0.01)
    val dog = Tenant(Image("images/dog.png"), Position(sun.position.x, sun.position.y), Size(25, 25), (p: Position) => Position(p.x, p.y), (r: Double) => r+0.01)

    val planets = List(sun, mars)

    def rotate[T <: hasRotation]= (spaceElement: T) => {
      spaceElement.image.angleRotation = spaceElement.rotation(spaceElement.image.angleRotation)
      spaceElement
    }

    def orbit[T <: hasOrbit] = (planet: T) => {
      planet.position = planet.orbit(planet.position)
      planet
    }

    def liveOnMars = (tenant: Tenant) => {
      tenant.movement = (_: Position) => Position(mars.position.x-Random.between(10, 50), mars.position.y-Random.between(10, 50))
      tenant.position = tenant.movement(tenant.position)
      tenant
    }

    def satellites = (planet: Planet) => {
      planet.satellites.foreach(satellite =>  (orbit compose rotate[Satellite] compose draw[Satellite]) (satellite))
      planet
    }

    def draw[T <: SpaceElement]= (spaceElement: T) => {
      ctx.save()
      ctx.translate(spaceElement.position.x-(spaceElement.size.x/2), spaceElement.position.y-(spaceElement.size.y/2))
      ctx.rotate(spaceElement.image.angleRotation)
      ctx.translate(-spaceElement.position.x-(spaceElement.size.x/2), -spaceElement.position.y-(spaceElement.size.y/2))
      ctx.drawImage(spaceElement.image.element, spaceElement.position.x, spaceElement.position.y, spaceElement.size.x, spaceElement.size.y)
      ctx.restore()
      spaceElement
    }

    def render(): Unit = {
      createBackground()

      if (planets.forall(_.image.isReady)) {
        draw(sun)
        (orbit compose rotate[Planet] compose draw[Planet] compose satellites)(mars)
        (draw compose rotate[Tenant] compose liveOnMars)(dog)
      }
    }

    dom.window.setInterval(() => render(), 100)

  }

}
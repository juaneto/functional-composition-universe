package scalacon.webapp

import cats.effect.IO
import scalacon.webapp.FunctionalCompositionApp.domProxy.{draw, middle, renderScreen, setup, createScenario}

import scala.util.Random

object FunctionalCompositionApp {

  val domProxy: DomProxy[IO] = new DomProxy[IO]()

  case class Position(x: Double, y: Double)

  case class Size(x: Int, y: Int)

  case class Distance(value: Double, speed: Double, toCenter: Double)

  case class Angle(value: Double, speed: Double)

  case class Mass(value: Double)

  case class Image(src: String, var angleRotation: Double = 0)

  type Movement = Position => Position
  type Rotation = Double => Double

  trait SpaceElement {
    val image: Image
    val size: Size
    val mass: Mass
    var position: Position
  }

  trait hasOrbit extends SpaceElement {
    var distance: Distance
    var angle: Angle
  }

  trait hasRotation extends SpaceElement {
    var rotation: Rotation
  }

  case class BlackHole(image: Image, var position: Position, size: Size, mass: Mass, stars: List[Star]) extends SpaceElement

  case class Star(image: Image, var position: Position, size: Size, mass: Mass, var distance: Distance, var angle: Angle, planets: List[Planet]) extends SpaceElement with hasOrbit

  case class Tenant(image: Image, var position: Position, size: Size, mass: Mass, var movement: Movement, var rotation: Rotation, var distance: Distance, var angle: Angle) extends SpaceElement with hasRotation

  case class Satellite(image: Image, var position: Position, size: Size, mass: Mass, var rotation: Rotation, var distance: Distance, var angle: Angle) extends hasOrbit with hasRotation

  case class Planet(image: Image, var position: Position = Position(0,0), size: Size, mass: Mass, satellites: List[Satellite], var rotation: Rotation, var distance: Distance, var angle: Angle, tenants: List[Tenant]) extends hasOrbit with hasRotation

  def main(args: Array[String]): Unit = {
    setup(setupUI)
  }

  def setupUI(): Unit = {
    createScenario()

    val moon = Satellite(
      Image("images/moon.png"),
      Position(0, 0),
      Size(20, 20),
      Mass(0.0004),
      (r: Double) => r + 0.1,
      Distance(1.496 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11)/150),
      Angle(Math.PI / 6, 1.990986 *  Math.pow(10, -7))
    )

    val deimos = Satellite(
      Image("images/moon.png"),
      Position(0, 0),
      Size(20, 20),
      Mass(0.0004),
      (r: Double) => r + 0.1,
      Distance(1.496 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11)/150),
      Angle(Math.PI / 6, 1.990986 *  Math.pow(10, -7)),
    )

    val phobos = Satellite(
      Image("images/moon2.png"),
      Position(0, 0),
      Size(15, 15),
      Mass(0.0004),
      rotation = (r: Double) => r + 1,
      Distance(1.396 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11)/150),
      Angle(Math.PI / 6, 1.990986 *  Math.pow(10, -7)),
    )

    val dog = Tenant(
      Image("images/dog.png"),
      Position(0, 0),
      Size(25, 25),
      Mass(1),
      (p: Position) => Position(p.x, p.y),
      (r: Double) => r + 0.01,
      Distance(0, 0, 0),
      Angle(0, 0)
    )

    val mars = Planet(
      Image("images/mars.png"),
      Position(0, 0),
      Size(50, 50),
      Mass(1.98855 * Math.pow(10, 30)),
      List(deimos, phobos),
      (r: Double) => r + 0.01,
      Distance(1.596 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11)/150),
      Angle(Math.PI / 6, 1.990986 * Math.pow(10, -7)),
      List(dog)
    )

    val earth = Planet(
      Image("images/earth.png"),
      Position(0, 0),
      Size(60, 60),
      Mass(1.98855 * Math.pow(10, 30)),
      List(moon),
      (r: Double) => r + 0.01,
      Distance(1.496 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11)/150),
      Angle(Math.PI / 6, 1.990986 * Math.pow(10, -7)),
      List(dog)
    )

    val sun = Star(
      Image("images/sun.png"),
      middle(),
      Size(75, 75),
      Mass(1.98855 * Math.pow(10, 30)),
      Distance(1.500 * Math.pow(10, 11), 400, 1.5 * Math.pow(10, 11)/75),
      Angle(Math.PI / 1, 1.990986 * Math.pow(10, -7)),
      List(mars, earth)
    )

    val blackHole = BlackHole(
      Image("images/blackHole.png"),
      middle(),
      Size(50, 50),
      Mass(1.48855 * Math.pow(10, 30)),
      List(sun)
    )

    def rotate[T <: hasRotation]: T => T = (spaceElement: T) => {
      spaceElement.image.angleRotation = spaceElement.rotation(spaceElement.image.angleRotation)
      spaceElement
    }

    def orbit[T <: hasOrbit, E <: SpaceElement]: (T, E) => T = (spaceElement: T, spaceElementToOrbit: E) => {
      val physics = new Physics(spaceElementToOrbit)
      (physics.calculateDistanceAcceleration compose physics.calculateAngleAcceleration compose physics.calculateNewOrbitPosition) (spaceElement)

      spaceElement
    }

    def liveOnMars: Tenant => Tenant = (tenant: Tenant) => {
      tenant.position = Position(mars.position.x - Random.between(10, tenant.size.x), mars.position.y - Random.between(10, tenant.size.y))
      tenant
    }

    def orbitSatellites: Planet => Planet = (planet: Planet) => {
      planet.satellites.map(satellite => ((orbit[Satellite, Planet](_, planet)) compose rotate[Satellite] compose draw[Satellite]) (satellite))
      planet
    }

    def orbitPlanets: Star => Star = (star: Star) => {
      star.planets.map(planet => ((orbit[Planet, Star](_,sun)) compose rotate[Planet] compose draw[Planet] compose orbitSatellites compose tenants) (planet))
      star
    }

    def orbitStars: BlackHole => BlackHole = (blackHole: BlackHole) => {
      blackHole.stars.map(star => (draw[Star] compose (orbit[Star, BlackHole](_, blackHole)) compose orbitPlanets) (star))
      blackHole
    }

    def tenants: Planet => Planet = (planet: Planet) => {
      planet.tenants.map(tenant => (draw compose rotate[Tenant] compose liveOnMars) (tenant))
      planet
    }

    def render() = {
      (orbitStars compose draw[BlackHole]) (blackHole)
      //draw (sun)
      //(draw[Planet] compose (orbit[Planet, Star](_, sun)) compose rotate[Planet]) (mars)
    }

    renderScreen(render)

  }

}

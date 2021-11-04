package scalacon.webapp

import cats.effect.IO
import scalacon.webapp.FunctionalCompositionApp.domProxy.{createScenario, draw, middle, renderScreen, setup}
import scalacon.webapp.Physics.{calculateAngleAcceleration, calculateCollision, calculateDistanceAcceleration, calculateNewOrbitPosition}
import scalacon.webapp.model.Model._
import scalacon.webapp.model.{Angle, Distance, Image, Mass, Position, Size}
import scalacon.webapp.model.elements.{BlackHole, Planet, Satellite, Star, Tenant}

import scala.util.Random

object FunctionalCompositionApp {

  val domProxy: DomProxy[IO] = new DomProxy[IO]()

  def main(args: Array[String]): Unit = {
    setup(setupUI)
  }

  def setupUI(): Unit = {
    createScenario()

    val moon = Satellite(
      Image("images/moon.png"),
      middle(Size(20, 20)),
      Size(20, 20),
      Mass(0.0004),
      (r: Double) => r + 0.1,
      Distance(1.3100 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11) / 150),
      Angle(Math.PI / 6, 1.990986 * Math.pow(10, -7)),
    )

    val deimos = Satellite(
      Image("images/deimos.png"),
      middle(Size(20, 20)),
      Size(20, 20),
      Mass(0.0004),
      (r: Double) => r + 0.1,
      Distance(1.496 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11)/150),
      Angle(Math.PI / 6, 1.990986 *  Math.pow(10, -7))
    )

    val phobos = Satellite(
      Image("images/phobos.png"),
      Position(0, 0),
      Size(15, 15),
      Mass(0.0004),
      rotation = (r: Double) => r + 1,
      Distance(1.396 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11)/150),
      Angle(Math.PI / 6, 1.990986 *  Math.pow(10, -7))
    )

    val dog = Tenant(
      Image("images/dog.png"),
      middle(Size(25, 25)),
      Size(25, 25),
      Mass(1),
      (p: Position) => Position(p.x, p.y),
      (r: Double) => r + 0.01,
      Distance(0, 0, 0),
      Angle(0, 0)
    )

    val mars = Planet(
      Image("images/mars.png"),
      middle(Size(275, 275)),
      Size(50, 50),
      Mass(1.98855 * Math.pow(10, 30)),
      List(deimos, phobos),
      (r: Double) => r + 0.01,
      Distance(1.612 * Math.pow(10, 11), 0, 0.896 * Math.pow(10, 11)/200),
      Angle(Math.PI / 6, 2.1 * Math.pow(10, -7)),
      List()
    )

    val earth = Planet(
      Image("images/earth.png"),
      Position(0, 0),
      //middle(Size(60, 60)),
      Size(60, 60),
      Mass(1.98855 * Math.pow(10, 30)),
      List(moon),
      (r: Double) => r + 0.01,
      Distance(1.596 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11) / 150),
      Angle(Math.PI / 6, 1.990986 * Math.pow(10, -7)),
      List(dog)
    )

    val sun = Star(
      Image("images/sun.png"),
      middle(Size(75, 75)),
      Size(75, 75),
      Mass(1.98855 * Math.pow(10, 30)),
      (r: Double) => r + 0.001,
      Distance(1.500 * Math.pow(10, 11), 400, 1.5 * Math.pow(10, 11) / 75),
      Angle(Math.PI / 1, 1.990986 * Math.pow(10, -7)),
      List(mars, earth)
    )

    var blackHole = BlackHole(
      Image("images/blackHole.png"),
      middle(Size(50, 50)),
      Size(50, 50),
      Mass(1.48855 * Math.pow(10, 30)),
      List(sun)
    )

    val blackHole2 = BlackHole(
      Image("images/blackHole.png"),
      middle(Size(500, 500)),
      Size(50, 50),
      Mass(1.48855 * Math.pow(10, 30)),
      List(sun)
    )

    var galaxies = List(blackHole, blackHole2)

    def makeGiant: Tenant => Tenant = (tenant: Tenant) =>
      tenant.copy(size = Size(500, 500), position = middle(Size(500, 500)))

    def liveOnPlanet(tenant: Tenant, planet: Planet): Tenant =
      tenant.copy(position = Position(planet.position.x - Random.between(3, tenant.size.x), planet.position.y - Random.between(3, tenant.size.y)))

    def orbitPlanets(star: Star)(implicit rotable: Rotary[Planet]): Star =
      star.copy(planets = star.planets.map(planet => (draw[Planet] compose rotable.rotate compose (orbit[Planet, Star](_: Planet, star))) (planet)))

    def orbit[T: Orbital, E: Orbital](planet: T, spaceElementToOrbit: E): T =
      ((calculateDistanceAcceleration(_: T, spaceElementToOrbit))
        andThen (calculateAngleAcceleration(_: T))
        andThen (calculateNewOrbitPosition(_: T, spaceElementToOrbit))
        andThen (calculateCollision(_: T, spaceElementToOrbit))) (planet)

    def orbitSatellites(planet: Planet)(implicit rotable: Rotary[Satellite]): Planet =
      planet.copy(satellites = planet.satellites.map(satellite => (draw[Satellite] compose (orbit[Satellite, Planet](_: Satellite, planet)) compose rotable.rotate) (satellite)))

    def orbitStars(blackHole: BlackHole)(implicit rotable: Rotary[Star]): BlackHole =
      blackHole.copy(stars = blackHole.stars.map(star => (draw[Star] compose rotable.rotate compose (orbit[Star, BlackHole](_: Star, blackHole)) compose orbitPlanets) (star)))

    def tenants: Planet => Planet = (planet: Planet) =>
      planet.copy(tenants = planet.tenants.map(tenant => (draw[Tenant] compose (liveOnPlanet(_, planet)))(tenant)))

    def orbitGalaxies: List[BlackHole] => List[BlackHole] = (blackHoles: List[BlackHole]) =>
      blackHoles.map(blackHole => (draw[BlackHole] compose orbitStars) (blackHole))

    def render(): Unit = {
      blackHole = (draw[BlackHole] compose orbitStars) (blackHole)
    }

    renderScreen(render)

  }

}

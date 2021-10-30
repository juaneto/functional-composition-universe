package scalacon.webapp

import cats.effect.IO
import scalacon.webapp.FunctionalCompositionApp.domProxy.{createScenario, draw, middle, renderScreen, setup}
import scalacon.webapp.Model.{Tenant, _}

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
      Distance(1.596 * Math.pow(10, 11), 0, 1.496 * Math.pow(10, 11) / 150),
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
      Image("images/moon2.png"),
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
      List(dog)
    )

    val earth = Planet(
      Image("images/earth.png"),
      Position(0, 0),
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
      (r: Double) => r + 0.01,
      Distance(1.500 * Math.pow(10, 11), 400, 1.5 * Math.pow(10, 11)/75),
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

    def rotate[T <: HasRotation]: T => T = (spaceElement: T) =>
      spaceElement.copyRotation(spaceElement, image = spaceElement.image.copy(angleRotation = spaceElement.rotation(spaceElement.image.angleRotation))).asInstanceOf[T]

    def orbit[T <: HasOrbit, E <: SpaceElement]: (T, E) => T = (spaceElement: T, spaceElementToOrbit: E) => {
      val physics = new Physics(spaceElementToOrbit)
      val spaceElementMoved = (physics.calculateDistanceAcceleration andThen physics.calculateAngleAcceleration andThen physics.calculateNewOrbitPosition andThen physics.calculateCollision) (spaceElement)
      spaceElementMoved.asInstanceOf[T]
    }

    def beGiant: Tenant => Tenant = (tenant: Tenant) =>
      tenant.copy(size = Size(500, 500), position = middle(Size(500, 500)))

    def liveOnPlanet: (Tenant, Planet) => Tenant = (tenant: Tenant, planet: Planet) =>
      tenant.copy(position = Position(planet.position.x - Random.between(10, tenant.size.x), planet.position.y - Random.between(10, tenant.size.y)))

    def orbitSatellites: Planet => Planet = (planet: Planet) =>
      planet.copy(satellites = planet.satellites.map(satellite => (draw[Satellite] compose (orbit[Satellite, Planet](_, planet)) compose rotate[Satellite]) (satellite)))

    def orbitPlanets: Star => Star = (star: Star) =>
      star.copy(planets = star.planets.map(planet => (draw compose rotate[Planet] compose (orbit[Planet, Star](_, star)) compose orbitSatellites) (planet)))

    def orbitStars: BlackHole => BlackHole = (blackHole: BlackHole) =>
      blackHole.copy(stars = blackHole.stars.map(star => (draw[Star] compose (orbit[Star, BlackHole](_, blackHole)) compose orbitPlanets) (star)))

    def tenants: Planet => Planet = (planet: Planet) =>
      planet.copy(tenants = planet.tenants.map(tenant => (draw compose (liveOnPlanet(_, planet))) (tenant)))

    def render(): Unit = {
      //(orbitStars compose draw[BlackHole]) (blackHole)
      //draw (sun)
      //(draw[Planet] compose (orbit[Planet, Star](_, sun)) compose rotate[Planet]) (mars)


      //// CONF SCRIPT ////
      //1 .this is Sarri
      //(draw compose beGiant) (dog)

      //2.
      //(draw compose rotate[Planet]) (earth)
      //draw (sun)
      //earth = (draw[Planet] compose (orbit[Planet, Star](_, sun)) compose rotate[Planet]) (earth)
      //3.
      blackHole = (draw[BlackHole] compose orbitStars) (blackHole)
    }

    renderScreen(render)

  }

}

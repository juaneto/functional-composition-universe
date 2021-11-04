package scalacon.webapp.model.elements

import scalacon.webapp.model.Model.{Drawable, Orbital, Rotary, Rotation}
import scalacon.webapp.model.{Angle, Distance, Image, Mass, Position, Size}


case class Planet(image: Image,
                  position: Position,
                  size: Size,
                  mass: Mass,
                  satellites: List[Satellite],
                  rotation: Rotation,
                  distance: Distance,
                  angle: Angle,
                  tenants: List[Tenant])

object Planet {

  implicit object PlanetOrbital extends Orbital[Planet] {

    def copyOrbit(planet: Planet, position: Position): Planet = {
      Planet(planet.image, position, planet.size, planet.mass, planet.satellites, planet.rotation, planet.distance, planet.angle, planet.tenants)
    }

    def copyOrbit(p: Planet, angle: Angle): Planet = {
      Planet(p.image, p.position, p.size, p.mass, p.satellites, p.rotation, p.distance, angle, p.tenants)
    }

    def copyOrbit(p: Planet, distance: Distance): Planet = {
      Planet(p.image, p.position, p.size, p.mass, p.satellites, p.rotation, distance, p.angle, p.tenants)
    }

    def copyOrbit(p: Planet, image: Image, size: Size, position: Position): Planet = {
      Planet(image, position, size, p.mass, p.satellites, p.rotation, p.distance, p.angle, p.tenants)
    }

    override def currentAngle(x: Planet): Angle = x.angle

    override def currentDistance(x: Planet): Distance = x.distance

    override def currentPosition(x: Planet): Position = x.position

    override def currentMass(x: Planet): Mass = x.mass

    override def currentSize(x: Planet): Size = x.size
  }

  implicit object PlanetRotary extends Rotary[Planet] {

    def rotate(planet: Planet): Planet = {
      planet.copy(image = planet.image.copy(angleRotation = planet.rotation(planet.image.angleRotation)))
    }

    def copyRotation(p: Planet, image: Image): Planet = {
      Planet(image, p.position, p.size, p.mass, p.satellites, p.rotation, p.distance, p.angle, p.tenants)
    }
  }

  implicit object PlanetDrawable extends Drawable[Planet] {
    override def currentPosition(x: Planet): Position = x.position

    override def currentSize(x: Planet): Size = x.size

    override def getImage(x: Planet): Image = x.image
  }

}

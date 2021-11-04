package scalacon.webapp.model.elements

import scalacon.webapp.model.Model._
import scalacon.webapp.model._

case class Satellite(image: Image,
                     position: Position,
                     size: Size,
                     mass: Mass,
                     rotation: Rotation,
                     distance: Distance,
                     angle: Angle)

object Satellite {

  implicit object SatelliteOrbital extends Orbital[Satellite] {
    override def copyOrbit(s: Satellite, position: Position): Satellite = {
      Satellite(s.image, position, s.size, s.mass, s.rotation, s.distance, s.angle)
    }

    override def copyOrbit(s: Satellite, angle: Angle): Satellite = {
      Satellite(s.image, s.position, s.size, s.mass, s.rotation, s.distance, angle)
    }

    override def copyOrbit(s: Satellite, distance: Distance): Satellite = {
      Satellite(s.image, s.position, s.size, s.mass, s.rotation, distance, s.angle)
    }

    override def copyOrbit(s: Satellite, image: Image, size: Size, position: Position): Satellite = {
      Satellite(image, position, size, s.mass, s.rotation, s.distance, s.angle)
    }

    override def currentAngle(x: Satellite): Angle = x.angle

    override def currentDistance(x: Satellite): Distance = x.distance

    override def currentPosition(x: Satellite): Position = x.position

    override def currentMass(x: Satellite): Mass = x.mass

    override def currentSize(x: Satellite): Size = x.size
  }

  implicit object SatelliteRotary extends Rotary[Satellite] {
    override def copyRotation(s: Satellite, image: Image): Satellite = {
      Satellite(image, s.position, s.size, s.mass, s.rotation, s.distance, s.angle)
    }

    override def rotate(satellite: Satellite): Satellite = {
      satellite.copy(image = satellite.image.copy(angleRotation = satellite.rotation(satellite.image.angleRotation)))
    }
  }

  implicit object SatelliteDrawable extends Drawable[Satellite] {
    override def currentPosition(x: Satellite): Position = x.position

    override def currentSize(x: Satellite): Size = x.size

    override def getImage(x: Satellite): Image = x.image
  }
}


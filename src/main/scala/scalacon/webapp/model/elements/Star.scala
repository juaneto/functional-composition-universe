package scalacon.webapp.model

import scalacon.webapp.model.Model._

case class Star(image: Image,
                position: Position,
                size: Size,
                mass: Mass,
                rotation: Rotation,
                distance: Distance,
                angle: Angle,
                planets: List[Planet])

object Star {

  implicit object StarOrbital extends Orbital[Star] {
    override def copyOrbit(s: Star, position: Position): Star = {
      Star(s.image, position, s.size, s.mass, s.rotation, s.distance, s.angle, s.planets)
    }
    override def copyOrbit(s: Star, angle: Angle): Star = {
      Star(s.image, s.position, s.size, s.mass, s.rotation, s.distance, angle, s.planets)
    }
    override def copyOrbit(s: Star, distance: Distance): Star = {
      Star(s.image, s.position, s.size, s.mass, s.rotation, distance, s.angle, s.planets)
    }

    override def copyOrbit(s: Star, image: Image, size: Size, position: Position): Star = {
      Star(image, position, size, s.mass, s.rotation, s.distance, s.angle, s.planets)
    }

    override def currentAngle(x: Star): Angle = x.angle

    override def currentDistance(x: Star): Distance = x.distance

    override def currentPosition(x: Star): Position = x.position

    override def currentMass(x: Star): Mass = x.mass

    override def currentSize(x: Star): Size = x.size
  }

  implicit object StarRotary extends Rotary[Star] {
    override def copyRotation(s: Star, image: Image): Star = {
      Star(image, s.position, s.size, s.mass, s.rotation, s.distance, s.angle, s.planets)
    }

    override def rotate(star: Star): Star = {
      star.copy(image = star.image.copy(angleRotation = star.rotation(star.image.angleRotation)))
    }
  }

  implicit object StarDrawable extends Drawable[Star] {
    override def currentPosition(x: Star): Position = x.position

    override def currentSize(x: Star): Size = x.size

    override def getImage(x: Star): Image = x.image
  }
}


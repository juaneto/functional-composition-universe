package scalacon.webapp.model

import scalacon.webapp.model.Model.{Angle, Distance, Drawable, Image, Mass, Orbital, Position, Size}

case class BlackHole(image: Image,
                     position: Position,
                     size: Size,
                     mass: Mass,
                     stars: List[Star])

object BlackHole {

  implicit object BlackHoleOrbital extends Orbital[BlackHole] {

    def copyOrbit(blackHole: BlackHole, position: Position): BlackHole = blackHole

    def copyOrbit(blackHole: BlackHole, angle: Angle): BlackHole = blackHole

    def copyOrbit(blackHole: BlackHole, distance: Distance): BlackHole = blackHole

    def copyOrbit(blackHole: BlackHole, image: Image, size: Size, position: Position): BlackHole = blackHole

    override def currentAngle(x: BlackHole): Angle = currentAngle(x)

    override def currentDistance(x: BlackHole): Distance = currentDistance(x)

    override def currentPosition(x: BlackHole): Position = x.position

    override def currentMass(x: BlackHole): Mass = x.mass

    override def currentSize(x: BlackHole): Size = x.size
  }

  implicit object Drawable extends Drawable[BlackHole] {

    override def currentPosition(x: BlackHole): Position = x.position

    override def currentSize(x: BlackHole): Size = x.size

    override def getImage(x: BlackHole): Image = x.image
  }

}


package scalacon.webapp.model

object Model {

  case class Position(x: Double, y: Double)

  case class Size(x: Int, y: Int)

  case class Distance(value: Double, speed: Double, toCenter: Double)

  case class Angle(value: Double, speed: Double)

  case class Mass(value: Double)

  case class Image(src: String, angleRotation: Double = 0)

  type Movement = Position => Position

  type Rotation = Double => Double

  trait Drawable[T] {
    def currentPosition(x: T): Position
    def currentSize(x: T): Size
    def getImage(x: T): Image
  }

  trait Orbital[T] {
    def copyOrbit(element: T, position: Position): T
    def copyOrbit(element: T, angle: Angle): T
    def copyOrbit(element: T, distance: Distance): T
    def copyOrbit(element: T, image: Image, size: Size, position: Position): T
    def currentAngle(x: T): Angle
    def currentDistance(x: T): Distance
    def currentPosition(x: T): Position
    def currentMass(x: T): Mass
    def currentSize(x: T): Size
  }


  trait Rotary[T] {
    def rotate(element: T): T
    def copyRotation(element: T, image: Image): T
  }

  object Orbital {
    def apply[T: Orbital]: Orbital[T] = implicitly
  }

  object Rotary {
    def apply[T: Rotary]: Rotary[T] = implicitly
  }

  object Drawable {
    def apply[T: Drawable]: Drawable[T] = implicitly
  }
}


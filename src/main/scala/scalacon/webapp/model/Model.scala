package scalacon.webapp.model

object Model {

  type Movement = Position => Position

  type Rotation = Double => Double

  trait Drawable[T] {
    def currentPosition(x: T): Position
    def currentSize(x: T): Size
    def getImage(x: T): Image
  }

  object Drawable {
    def apply[T: Drawable]: Drawable[T] = implicitly
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

  object Orbital {
    def apply[T: Orbital]: Orbital[T] = implicitly
  }

  trait Rotary[T] {
    def rotate(element: T): T
    def copyRotation(element: T, image: Image): T
  }

  object Rotary {
    def apply[T: Rotary]: Rotary[T] = implicitly
  }

}

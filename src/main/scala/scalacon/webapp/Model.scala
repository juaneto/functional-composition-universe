package scalacon.webapp

object Model {

  case class Position(x: Double, y: Double)

  case class Size(x: Int, y: Int)

  case class Distance(value: Double, speed: Double, toCenter: Double)

  case class Angle(value: Double, speed: Double)

  case class Mass(value: Double)

  case class Image(src: String, angleRotation: Double = 0)

  type Movement = Position => Position

  type Rotation = Double => Double

  sealed trait SpaceElement {
    val image: Image
    val size: Size
    val mass: Mass
    val position: Position
  }

  sealed trait HasOrbit extends SpaceElement {
    val distance: Distance
    val angle: Angle
    def copyOrbit[T <: HasOrbit](hasOrbit: T, position: Position): HasOrbit = hasOrbit match {
      case p: Planet => Planet(p.image, position, p.size, p.mass, p.satellites, p.rotation, p.distance, p.angle, p.tenants)
      case s: Star => Star(s.image, position, s.size, s.mass, s.rotation, s.distance, s.angle, s.planets)
      case s: Satellite => Satellite(s.image, position, s.size, s.mass, s.rotation, s.distance, s.angle)
      case _ => hasOrbit
    }
    def copyOrbit[T <: HasOrbit](hasOrbit: T, angle: Angle): HasOrbit = hasOrbit match {
      case p: Planet => Planet(p.image, p.position, p.size, p.mass, p.satellites, p.rotation, p.distance, angle, p.tenants)
      case s: Star => Star(s.image, s.position, s.size, s.mass, s.rotation, s.distance, angle, s.planets)
      case s: Satellite => Satellite(s.image, s.position, s.size, s.mass, s.rotation, s.distance, angle)
      case _ => hasOrbit
    }
    def copyOrbit[T <: HasOrbit](hasOrbit: T, distance: Distance): HasOrbit = hasOrbit match {
      case p: Planet => Planet(p.image, p.position, p.size, p.mass, p.satellites, p.rotation, distance, p.angle, p.tenants)
      case s: Star => Star(s.image, s.position, s.size, s.mass, s.rotation, distance, s.angle, s.planets)
      case s: Satellite => Satellite(s.image, s.position, s.size, s.mass, s.rotation, distance, s.angle)
      case _ => hasOrbit
    }
    def copyOrbit[T <: HasOrbit](hasOrbit: T, image: Image, size: Size, position: Position): HasOrbit = hasOrbit match {
      case p: Planet => Planet(image, position, size, p.mass, p.satellites, p.rotation, p.distance, p.angle, p.tenants)
      case s: Star => Star(image, position, size, s.mass, s.rotation, s.distance, s.angle, s.planets)
      case s: Satellite => Satellite(image, position, size, s.mass, s.rotation, s.distance, s.angle)
      case _ => hasOrbit
    }

  }

  sealed trait HasRotation extends SpaceElement {
    val rotation: Rotation
    def copyRotation[T <: HasRotation](hasRotation: T, image: Image): HasRotation = hasRotation match {
      case p: Planet => Planet(image, p.position, p.size, p.mass, p.satellites, p.rotation, p.distance, p.angle, p.tenants)
      case s: Star => Star(image, s.position, s.size, s.mass, s.rotation, s.distance, s.angle, s.planets)
      case s: Satellite => Satellite(image, s.position, s.size, s.mass, s.rotation, s.distance, s.angle)
    }
  }

  case class BlackHole(image: Image,
                       position: Position,
                       size: Size,
                       mass: Mass,
                       stars: List[Star]) extends SpaceElement

  case class Star(image: Image,
                  position: Position,
                  size: Size,
                  mass: Mass,
                  rotation: Rotation,
                  distance: Distance,
                  angle: Angle,
                  planets: List[Planet]) extends SpaceElement with HasOrbit with HasRotation

  case class Tenant(image: Image,
                    position: Position,
                    size: Size,
                    mass: Mass,
                    movement: Movement,
                    rotation: Rotation,
                    distance: Distance,
                    angle: Angle) extends SpaceElement

  case class Satellite(image: Image,
                       position: Position,
                       size: Size,
                       mass: Mass,
                       rotation: Rotation,
                       distance: Distance,
                       angle: Angle) extends HasOrbit with HasRotation

  case class Planet(image: Image,
                    position: Position,
                    size: Size,
                    mass: Mass,
                    satellites: List[Satellite],
                    rotation: Rotation,
                    distance: Distance,
                    angle: Angle,
                    tenants: List[Tenant]) extends HasOrbit with HasRotation

}

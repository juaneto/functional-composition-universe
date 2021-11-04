package scalacon.webapp.model.elements

import scalacon.webapp.model.Model._
import scalacon.webapp.model._


case class Tenant(image: Image,
                  position: Position,
                  size: Size,
                  mass: Mass,
                  movement: Movement,
                  rotation: Rotation,
                  distance: Distance,
                  angle: Angle)

object Tenant {

  implicit object TenantDrawable extends Drawable[Tenant] {
    override def currentPosition(x: Tenant): Position = x.position

    override def currentSize(x: Tenant): Size = x.size

    override def getImage(x: Tenant): Image = x.image
  }
}


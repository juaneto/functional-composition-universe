package scalacon.webapp

import scalacon.webapp.Config.collisions
import scalacon.webapp.FunctionalCompositionApp.domProxy.{massDeviation, middle}
import scalacon.webapp.model.{Angle, Image, Position, Size}
import scalacon.webapp.model.Model._

import scala.math.pow

object Physics {

  private val gravitationalConstant: Double = 6.67408 * pow(10, -11)

  private val deltaT = 3600 * 24 / 1

  private def newValue(currentValue: Double, deltaT: Double, derivative: Double): Double = currentValue + deltaT * derivative

  def calculateDistanceAcceleration[T: Orbital, E: Orbital](element: T, planetToOrbit: E): T = {
    // [acceleration of distance] = [distance][angular velocity]^2 - G * M / [distance]^2
    val distanceAcceleration = Orbital[T].currentDistance(element).value * Math.pow(Orbital[T].currentAngle(element).speed, 2) -
      (gravitationalConstant * Orbital[E].currentMass(planetToOrbit).value * massDeviation()) / Math.pow(Orbital[T].currentDistance(element).value, 2)
    Orbital[T].copyOrbit(element, Orbital[T].currentDistance(element).copy(
      value = newValue(Orbital[T].currentDistance(element).value, deltaT, Orbital[T].currentDistance(element).speed),
      speed = newValue(Orbital[T].currentDistance(element).speed, deltaT, distanceAcceleration),
    ))
  }

  def calculateAngleAcceleration[T: Orbital](element: T): T = {
    // [acceleration of angle] = - 2[speed][angular velocity] / [distance]
    val angleAcceleration = -2.0 * Orbital[T].currentDistance(element).speed * Orbital[T].currentAngle(element).speed / Orbital[T].currentDistance(element).value

    if (Orbital[T].currentAngle(element).value > 2 * Math.PI) {
      Orbital[T].copyOrbit(element, Orbital[T].currentAngle(element).copy(value = Orbital[T].currentAngle(element).value % (2 * Math.PI)))
    } else {
      Orbital[T].copyOrbit(element, Angle(
        newValue(Orbital[T].currentAngle(element).value, deltaT, Orbital[T].currentAngle(element).speed),
        newValue(Orbital[T].currentAngle(element).speed, deltaT, angleAcceleration)
      ))
    }
  }

  def calculateNewOrbitPosition[T: Orbital, E: Orbital](element: T, planetToOrbit: E): T = {
    Orbital[T].copyOrbit(element, Position(
      Math.cos(Orbital[T].currentAngle(element).value) * Orbital[T].currentDistance(element).value / Orbital[T].currentDistance(element).toCenter + Orbital[E].currentPosition(planetToOrbit).x,
      Math.sin(-Orbital[T].currentAngle(element).value) * Orbital[T].currentDistance(element).value / Orbital[T].currentDistance(element).toCenter + Orbital[E].currentPosition(planetToOrbit).y
    ))
  }

  def calculateCollision[T: Orbital, E: Orbital](element: T, planetToOrbit: E): T = {
    def isCoincidenceInX: Boolean = (Orbital[T].currentPosition(element).x - Orbital[T].currentSize(element).x/2 > Orbital[E].currentPosition(planetToOrbit).x - Orbital[E].currentSize(planetToOrbit).x) && (Orbital[T].currentPosition(element).x + Orbital[T].currentSize(element).x/2 < Orbital[E].currentPosition(planetToOrbit).x + Orbital[E].currentSize(planetToOrbit).x)
    def isCoincidenceInY: Boolean = (Orbital[T].currentPosition(element).y - Orbital[T].currentSize(element).x/2 > Orbital[E].currentPosition(planetToOrbit).y - Orbital[E].currentSize(planetToOrbit).y) && (Orbital[T].currentPosition(element).y + Orbital[T].currentSize(element).x/2 < Orbital[E].currentPosition(planetToOrbit).y + Orbital[E].currentSize(planetToOrbit).y)
    def isInCollision: Boolean = collisions && isCoincidenceInX && isCoincidenceInY

    if(collisions && isInCollision || Orbital[T].currentSize(element).x == 1000) {
      Orbital[T].copyOrbit(element, Image("images/collision.png"), Size(1000, 1000), middle(Size(1000, 1000)))
    } else {
      element
    }
  }

}

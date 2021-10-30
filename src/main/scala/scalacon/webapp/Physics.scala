package scalacon.webapp

import scalacon.webapp.Config.collisions
import scalacon.webapp.FunctionalCompositionApp.domProxy.{massDeviation, middle}
import scalacon.webapp.Model._

import scala.math.pow

class Physics(val planetTOrbit: SpaceElement) {

  private val gravitationalConstant: Double = 6.67408 * pow(10, -11)

  private val deltaT = 3600 * 24 / 1

  private def newValue(currentValue: Double, deltaT: Double, derivative: Double): Double = currentValue + deltaT * derivative

  type ChangeInOrbit = HasOrbit => HasOrbit

  def calculateDistanceAcceleration: ChangeInOrbit = (spaceElement: HasOrbit) => {
    // [acceleration of distance] = [distance][angular velocity]^2 - G * M / [distance]^2
    val distanceAcceleration = spaceElement.distance.value * Math.pow(spaceElement.angle.speed, 2) -
      (gravitationalConstant * planetTOrbit.mass.value * massDeviation()) / Math.pow(spaceElement.distance.value, 2)
    spaceElement.copyOrbit(spaceElement, spaceElement.distance.copy(
      value = newValue(spaceElement.distance.value, deltaT, spaceElement.distance.speed),
      speed = newValue(spaceElement.distance.speed, deltaT, distanceAcceleration),
    ))
  }

  def calculateAngleAcceleration: ChangeInOrbit = (spaceElement: HasOrbit) => {
    // [acceleration of angle] = - 2[speed][angular velocity] / [distance]
    val angleAcceleration = -2.0 * spaceElement.distance.speed * spaceElement.angle.speed / spaceElement.distance.value

    if (spaceElement.angle.value > 2 * Math.PI) {
      spaceElement.copyOrbit(spaceElement, spaceElement.angle.copy(value = spaceElement.angle.value % (2 * Math.PI)))
    } else {
      spaceElement.copyOrbit(spaceElement, Angle(
        newValue(spaceElement.angle.value, deltaT, spaceElement.angle.speed),
        newValue(spaceElement.angle.speed, deltaT, angleAcceleration)
      ))
    }
  }

  def calculateNewOrbitPosition: ChangeInOrbit = (spaceElement: HasOrbit) => {
    spaceElement.copyOrbit(spaceElement, Position(
      Math.cos(spaceElement.angle.value) * spaceElement.distance.value / spaceElement.distance.toCenter + planetTOrbit.position.x,
      Math.sin(-spaceElement.angle.value) * spaceElement.distance.value / spaceElement.distance.toCenter + planetTOrbit.position.y
    ))
  }

  def calculateCollision: ChangeInOrbit = (planet: HasOrbit) => {
    def isCoincidenceInX: Boolean = (planet.position.x - planet.size.x/2 > planetTOrbit.position.x - planetTOrbit.size.x) && (planet.position.x + planet.size.x/2 < planetTOrbit.position.x + planetTOrbit.size.x)
    def isCoincidenceInY: Boolean = (planet.position.y - planet.size.x/2 > planetTOrbit.position.y - planetTOrbit.size.y) && (planet.position.y + planet.size.x/2 < planetTOrbit.position.y + planetTOrbit.size.y)
    def isInCollision: Boolean = collisions && isCoincidenceInX && isCoincidenceInY

    if(collisions && isInCollision || planet.size.x == 1000) {
      planet.copyOrbit(planet, Image("images/collision.png"), Size(1000, 1000), middle(Size(1000, 1000)))
    } else {
      planet
    }
  }

}

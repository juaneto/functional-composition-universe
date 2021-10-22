package scalacon.webapp

import scalacon.webapp.FunctionalCompositionApp.domProxy.massDeviation
import scalacon.webapp.FunctionalCompositionApp.{Angle, Position, SpaceElement, hasOrbit}

import scala.math.pow

class Physics(val planetTOrbit: SpaceElement) {

  private val gravitationalConstant: Double = 6.67408 * pow(10, -11)

  private val deltaT = 3600 * 24 / 1

  private def newValue(currentValue: Double, deltaT: Double, derivative: Double): Double = currentValue + deltaT * derivative

  type ChangeInOrbit = hasOrbit => hasOrbit

  def calculateDistanceAcceleration: ChangeInOrbit = (planet: hasOrbit) => {
    // [acceleration of distance] = [distance][angular velocity]^2 - G * M / [distance]^2
    val distanceAcceleration = planet.distance.value * Math.pow(planet.angle.speed, 2) -
      (gravitationalConstant * planetTOrbit.mass.value * massDeviation().toString.toDouble) / Math.pow(planet.distance.value, 2)
    planet.distance = planet.distance.copy(
      value = newValue(planet.distance.value, deltaT, planet.distance.speed),
      speed = newValue(planet.distance.speed, deltaT, distanceAcceleration),
    )
    planet
  }

  def calculateAngleAcceleration: ChangeInOrbit = (planet: hasOrbit) => {
    val angleAcceleration = -2.0 * planet.distance.speed * planet.angle.speed / planet.distance.value
    planet.angle = Angle(
      newValue(planet.angle.value, deltaT, planet.angle.speed),
      newValue(planet.angle.speed, deltaT, angleAcceleration)
    )
    if (planet.angle.value > 2 * Math.PI) planet.angle = planet.angle.copy(value = planet.angle.value % (2 * Math.PI))
    planet
  }

  def calculateNewOrbitPosition: ChangeInOrbit = (planet: hasOrbit) => {
    planet.position = Position(
      Math.cos(planet.angle.value) * planet.distance.value / planet.distance.toCenter + planetTOrbit.position.x,
      Math.sin(-planet.angle.value) * planet.distance.value / planet.distance.toCenter + planetTOrbit.position.y
    )
    planet
  }
}

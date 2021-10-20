package scalacon.webapp

import org.scalajs.dom.html.Canvas
import org.scalajs.dom.{CanvasRenderingContext2D, Event, document, window}
import scalacon.webapp.FunctionalCompositionApp.{Position, SpaceElement, domProxy}

class DomProxy[F[_]] {

  private val canvas: Canvas = document.createElement("canvas").asInstanceOf[Canvas]
  private val context: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  def createScenario(): (Canvas, CanvasRenderingContext2D) = {
    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    document.body.appendChild(canvas)
    (canvas, context)
  }

  def createBackground(): CanvasRenderingContext2D = {
    context.clearRect(0, 0, canvas.width, canvas.height)
    context.fillStyle = "#000000"
    context.fillRect(0, 0, canvas.width, canvas.height)
    context
  }

  def draw[T <: SpaceElement]: T => T = (spaceElement: T) => {
    context.save()
    context.translate(spaceElement.position.x - (spaceElement.size.x / 2), spaceElement.position.y - (spaceElement.size.y / 2))
    context.rotate(spaceElement.image.angleRotation)
    context.translate(-spaceElement.position.x - (spaceElement.size.x / 2), -spaceElement.position.y - (spaceElement.size.y / 2))
    context.drawImage(spaceElement.image.element, spaceElement.position.x, spaceElement.position.y, spaceElement.size.x, spaceElement.size.y)
    context.restore()
    spaceElement
  }

  def renderScreen(renderFunction: () => Unit): Unit = {
    window.setInterval(() => renderFunction(), 1)
  }

  def middle(): Position = {
    Position(domProxy.canvas.width / 2, domProxy.canvas.height / 2)
  }

  def setup(setupFunction: () => Unit): Unit = {
    document.addEventListener("DOMContentLoaded", { (_: Event) =>
      setupFunction()
    })
  }

}

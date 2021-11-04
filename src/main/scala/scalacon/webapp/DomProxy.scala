package scalacon.webapp

import org.scalajs.dom._
import org.scalajs.dom.html.{Canvas, Input}
import org.scalajs.dom.raw.{HTMLImageElement, HTMLInputElement}
import scalacon.webapp.Config.sliderActive
import scalacon.webapp.model.Model
import scalacon.webapp.model.Model._

import scala.scalajs.js

class DomProxy[F[_]]() {

  private val canvas: Canvas = document.createElement("canvas").asInstanceOf[Canvas]
  private val context: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  def createScenario(): (Canvas, CanvasRenderingContext2D) = {
    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    document.body.appendChild(canvas)
    if(sliderActive) createSlider()
    (canvas, context)
  }

  private def createSlider(): Node = {
    def updateSlider(slider: Element, e: Event): Unit = {
      slider.setAttribute("value", e.target.asInstanceOf[HTMLInputElement].value)
      js.Dynamic.global.mass = e.target.asInstanceOf[HTMLInputElement].value.toDouble
      document.getElementById("sunMassText").innerHTML = s"Mass of the sun: ${slider.getAttribute("value")}"
    }

    def sliderText(): Node = {
      val text = document.createElement("div")
      text.id = "sunMassText"
      text.innerHTML = "Mass of the sun: 1"
      document.body.appendChild(text)
    }

    val slider = document.createElement("input")
    slider.setAttribute("type", "range")
    slider.setAttribute("min", "0")
    slider.setAttribute("max", "5")
    slider.setAttribute("class", "slider")
    slider.setAttribute("step", "0.1")
    slider.setAttribute("value", "1")
    slider.asInstanceOf[Input].onchange = (e: Event) => {
      updateSlider(slider, e)
    }
    sliderText()
    document.body.appendChild(slider)
  }

  def createBackground(): CanvasRenderingContext2D = {
    context.clearRect(0, 0, canvas.width, canvas.height)
    context.fillStyle = "#000000"
    context.fillRect(0, 0, canvas.width, canvas.height)
    context
  }

  private def drawInCanvas[T: Drawable](spaceElement: T) = {
    context.save()
    context.translate(Drawable[T].currentPosition(spaceElement).x - (Drawable[T].currentSize(spaceElement).x / 2), Drawable[T].currentPosition(spaceElement).y - (Drawable[T].currentSize(spaceElement).y / 2))
    context.rotate(Drawable[T].getImage(spaceElement).angleRotation)
    context.translate(-Drawable[T].currentPosition(spaceElement).x - (Drawable[T].currentSize(spaceElement).x / 2), -Drawable[T].currentPosition(spaceElement).y - (Drawable[T].currentSize(spaceElement).y / 2))
    context.drawImage(createImgElement(Drawable[T].getImage(spaceElement).src), Drawable[T].currentPosition(spaceElement).x, Drawable[T].currentPosition(spaceElement).y, Drawable[T].currentSize(spaceElement).x, Drawable[T].currentSize(spaceElement).y)
    context.restore()
    spaceElement
  }

  def draw[T: Drawable](spaceElement: T): T = drawInCanvas(spaceElement)

  def draw[T: Drawable]: T => T = (spaceElement: T) => drawInCanvas(spaceElement)

  def createImgElement(src: String): HTMLImageElement = {
    val element = document.createElement("img").asInstanceOf[HTMLImageElement]
    element.src = src
    element
  }

  def renderScreen(renderFunction: () => Unit): Unit = {
    window.setInterval(() => {
      createBackground()
      renderFunction()
    }, 1)
  }

  def middle(size: Size): Model.Position = {
    Position((canvas.width / 2) + size.x / 2, (canvas.height / 2 ) + size.y / 2)
  }

  def setup(setupFunction: () => Unit): Unit = {
    document.addEventListener("DOMContentLoaded", { (_: Event) =>
      setupFunction()
    })
  }

  def massDeviation(): Double = {
    js.Dynamic.global.mass.toString.toDouble
  }

}

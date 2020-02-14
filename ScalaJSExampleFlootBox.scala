package example
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.MouseEvent

import scala.scalajs.js
import scala.util.Random

case class Point(x: Int, y: Int){
  def +(p: Point) = Point(x + p.x, y + p.y)
  def /(d: Int) = Point(x / d, y / d)
}

@JSExport
object ScalaJSExample {
  @JSExport
  def main(canvas: html.Canvas): Unit = {
    val renderer = canvas.getContext("2d")
                    .asInstanceOf[dom.CanvasRenderingContext2D]

    canvas.width = canvas.parentElement.clientWidth
    canvas.height = 500

    renderer.textAlign = "center"
    renderer.textBaseline = "middle"

    val obstacleGap = 200
    val holeSize = 50
    val gravity = 0.1

    var playerY = canvas.height / 2.0
    var playerV = 0.0

    var dead = 0
    var frame = -50
    val obstacles = collection.mutable.Queue.empty[Int]

    var count = 0
    var timeOut = 200

    def runLive() = {
      frame += 2
      if(frame >= 0 && frame % obstacleGap == 0) {
        obstacles.enqueue(Random.nextInt(canvas.height - 2 * holeSize) + holeSize)
      }
      if(obstacles.size > 7) {
        obstacles.dequeue()
        frame -= obstacleGap
      }

      playerY = playerY + playerV
      playerV = playerV + gravity

      renderer.fillStyle = "darkblue"
      for((holeY, i) <- obstacles.zipWithIndex) {
        val holeX = i * obstacleGap - frame + canvas.width
        renderer.fillRect(holeX, 0, 5, holeY - holeSize)
        renderer.fillRect(holeX, holeY + holeSize, 5, canvas.height - holeY - holeSize)

        if(math.abs(holeX - canvas.width / 2) < 5 && math.abs(holeY - playerY) > holeSize)
          dead = 50
        if(math.abs(holeX - canvas.width / 2 - 1) < 2) count += 1
      }

      renderer.fillStyle = "darkgreen"
      renderer.fillRect(canvas.width / 2 - 5, playerY - 5, 10, 10)

      renderer.fillStyle = "darkgreen"
      renderer.font = "15px sans-serif"
      renderer.fillText("Your Score = "+count,canvas.width / 2 - 200, canvas.height - canvas.height + 50)

      if(playerY < 0 || playerY > canvas.height) dead = 50

    }

    def runDead() = {
      playerY = canvas.height / 2
      playerV = 0
      //dead = -1
      obstacles.clear()
      //frame = -50
      count = 0
      timeOut = 1000
      renderer.fillStyle = "darkred"
      renderer.font = "40px sans-serif"
      renderer.fillText("Game Over", canvas.width / 2, canvas.height / 2)
    }

    def run() = {
      renderer.clearRect(0,0,canvas.width,canvas.height)
      if(dead > 0) {
        if(frame % 300 == 0)  {
          frame = -50
          dead = -1
        }
        frame +=2
        runDead()
      }
      else runLive()
    }

    println(timeOut)
    dom.window.setInterval(run _, 20)

    canvas.onclick = (e: dom.MouseEvent) => {
      playerV -= 5
    }

  }
}

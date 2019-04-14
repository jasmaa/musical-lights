let i = 0
export class Graph {
  constructor() {
    this.canvas = document.getElementById("graph")
    this.context = this.canvas.getContext("2d")
    this.data = Array()
    this.maxlength = 100
  }

  plot(x) {
    i++
    if (this.data.length = this.maxlength) {
      this.data.shift()
    }
    this.data.push(x/500000*500+250)
    if(i % 1 == 0) {
      this.update()
    }
  }

  update() {
    this.context.strokeStyle = "#000000";
    this.context.clearRect(0, 0, this.canvas.width, this.canvas.height)
    this.context.beginPath()
    this.context.moveTo(0, 500-this.data[0])
    for (let i = 1; i < this.data.length; i++) {
      this.context.lineTo(i * 10, 500-this.data[i])
    }
    this.context.stroke()
  }
}

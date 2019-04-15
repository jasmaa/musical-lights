export class Graph {
  constructor() {
    this.canvas = document.getElementById("graph")
    this.context = this.canvas.getContext("2d")
    this.data = Array()
    this.marks = Array()
    this.maxlength = 79
  }

  plot(x, mark = false) {
    if (this.data.length == this.maxlength) {
      this.data.shift()
      this.marks.shift()
    }
    this.data.push(x/500000*500+250)
    this.marks.push(mark)
    this.update()
  }

  update() {
    this.context.strokeStyle = "#000000";
    this.context.clearRect(0, 0, this.canvas.width, this.canvas.height)
    this.context.beginPath()
    this.context.moveTo(0, 500-this.data[0])
    let unit = this.canvas.width / this.maxlength
    for (let i = 1; i < this.data.length; i++) {
      this.context.lineTo(i * unit, 500-this.data[i])
      if (this.marks[i]) {
        this.context.lineTo(i * unit, 0);
        this.context.moveTo(i * unit, 500-this.data[i])
      }
    }
    this.context.stroke()
  }
}

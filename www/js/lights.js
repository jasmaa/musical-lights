export class Light {
  constructor() {
    this.e = document.createElement("div")
    this.r = 0
    this.g = 0
    this.b = 0
  }

  update() {
    this.r = Math.floor(this.r)
    this.g = Math.floor(this.g)
    this.b = Math.floor(this.b)
    this.e.style.backgroundColor = `rgb(${this.r}, ${this.g}, ${this.b})`
  }

  set(o) {
    this.r = o.r
    this.g = o.g
    this.b = o.b
    return this
  }

  merge(o, factor) {
    this.r = (this.r + o.r * factor) / (factor + 1)
    this.g = (this.g + o.g * factor) / (factor + 1)
    this.b = (this.b + o.b * factor) / (factor + 1)
    return this
  }

  burn(scalar) {
    this.r = this.r * scalar
    this.g = this.g * scalar
    this.b = this.b * scalar
  }

  dodge(scalar) {
    let x = 255 * (1 - scalar)
    this.r = Math.min(x + this.r * scalar, 255)
    this.g = Math.min(x + this.g * scalar, 255)
    this.b = Math.min(x + this.b * scalar, 255)
  }

  saturate() {
    let min = Math.min(this.r, this.g, this.b)
    let scalar = 255 / (Math.max(this.r, this.g, this.b, min+1) - min)
    this.r = (this.r - min) * scalar
    this.g = (this.g - min) * scalar
    this.b = (this.b - min) * scalar
  }
}

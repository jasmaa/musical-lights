import {Light} from "./lights.js"
import {Beat} from "./beat.js"
import {Bluetooth} from "./bt.js"
import {UsbSerial} from "./usb.js"

let N = 280 // number of lights
let lights
let t = 0
let bt = new Bluetooth()
let usb = new UsbSerial()

function run() {
  console.log("run")
  let b = new Beat()
  window.b = b
  if (document.getElementById("mic").checked) {
    b.connect("mic")
    document.getElementById("audio").hidden = true
    document.getElementById("musicinput").hidden = true
  }
  else {
    let a = document.getElementById("audio")
    b.connect(a)
    a.play()
  }
  setInterval(() => step(b), 50)
  document.getElementById("mic").disabled = true
  document.getElementById("run").hidden = true
}

let color = {r:0,g:0,b:255}

function step(b) {
  b.update()
  document.getElementById("amp").textContent = b.amp.toFixed(6)
  let isBeat = b.isBeat2()
  if (isBeat) {
    color.r = 255 - color.r
    color.g = 255 - color.g
    color.b = 255 - color.b
  }

  if (isBeat && (b.beatState.jump > 100000)) {
    if (color.r == color.g) {
      color.r = 0
      color.b = 255
    }
    else if (color.g == color.b){
      color.g = 0
      color.r = 255
    }
    else {
      color.b = 0
      color.g = 255
    }
  }

  lights[135].set(color)
  for (let i = 136; i < 145; i++) {
    lights[i].set(lights[i-1])
  }
  for(let i = 0; i < 135; i++) {
    lights[i].merge(lights[i + 2], 6)
  }
  //lights.forEach(l => l.saturate())
  let flare = b.beatState.jump
  flare = flare ? 1 + flare / 40000 : 1
  //console.log(flare)
  for(let i = 0; i < 140; i++) {
    lights[i].burn(i / 2240 + 0.9375)
  }
  for (let i = 0; i < 70; i++) {
    let x = (1 - (0.9 + i / 700))
    lights[i].dodge(1 - x ** 1.2 * flare)
  }
  for(let i = 140; i < 280; i++) {
    lights[i].set(lights[280-i-1])
  }

  lights.forEach(l => l.update())
  if (bt.connected) {
    let buf = new Uint8Array(1);
    buf[0] = 9
    bt.send(buf)
    lights.forEach(l => {
      let buf = new Uint8Array(3);
      buf[0] = l.r
      buf[1] = l.g
      buf[2] = l.b
      bt.send(buf)
    })
  }
  if (usb.connected) {
    usb.send(lights)
  }
}

let app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false)
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
      console.log("ok")
      document.getElementById("run").onclick = run
      document.getElementById("connect").onclick = () => {
        if (bt.connected) {bt.disconnect()} else bt.connect()
      }
      document.getElementById("usb").onclick = () => {
        usb.connect()
      }
      lights = Array(280).fill(0).map(_ => new Light())
      let strip = document.getElementById("strip")
      lights.forEach(light => {
        light.e.classList.add("light")
        strip.appendChild(light.e)
      })
      let musicInput = document.getElementById("musicinput")
      musicInput.onchange = function(e) {
        let audio = document.getElementById("audio");
        audio.src = URL.createObjectURL(this.files[0]);
        // not really needed in this exact case, but since it is really important in other cases,
        // don't forget to revoke the blobURI when you don't need it
        audio.onend = function(e) {
          URL.revokeObjectURL(this.src);
        }
      }
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        console.log(id)
    }
}

app.initialize()
app.onDeviceReady()

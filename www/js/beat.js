import {Graph} from "./graph.js"

let MAXENERGY

export class Beat {
  constructor() {
    this.beat = 0
    this.decay = 0.999
    this.threshold = 0.12
    this.lastBeat = 0
    this.lastElapsed = 0
    this.amp = 0
    this.avg = 0
    this.dev = this.threshold
    this.threshold2 = 3
    this.graph = new Graph()
    this.beatState = {}
    this.avgEnergy = 0
    this.localMin = 0
    this.recentJump = 0
    this.lastBeatJump = 0
    this.groove = 0
    this.grooveSpace = 0
  }

  async connect(audio, streamsource) {
    this.context = new AudioContext({
      sampleRate: 2048,
    });
    this.analyser = this.context.createAnalyser();
    this.analyser.fftSize = 512;
    this.dataSize = this.analyser.frequencyBinCount
    this.timeData = new Uint8Array(this.dataSize)
    this.freqData = new Uint8Array(this.dataSize)
    this.envelope = new Uint8Array(this.dataSize / 8  )
    MAXENERGY = this.dataSize / 8 * 255 * 255
    this.lastBeatEnvelope = undefined
    let source;
    if (audio=="mic") {
      audio = await navigator.mediaDevices.getUserMedia({ audio: true})
      source = this.context.createMediaStreamSource(audio)
      source.connect(this.analyser)
    }
    else {
      source = this.context.createMediaElementSource(audio);
      source.connect(this.analyser)
      this.analyser.connect(this.context.destination)
    }

  }

  update() {
    this.analyser.getByteTimeDomainData(this.timeData);
    this.analyser.getByteFrequencyData(this.freqData);
    this.amp = this.rmsAmplitude()
  }

  rmsAmplitude() {
    let sumsq = this.timeData.reduce((a, x) => a + (x - 128)*(x-128), 0)
    return Math.sqrt(sumsq / this.dataSize) / 255
  }

  isBeat() {
    let now = performance.now()
    let elapsed = now - this.lastBeat
    this.avg = (this.avg * 10 + this.amp) / 11
    let delta = this.amp - this.avg
    this.dev = (this.dev * 10 + this.amp) / 11
    this.threshold = 2 * this.dev
    if (elapsed < 0.5 * this.lastElapsed) {delta *= 0.8}
    else if (elapsed < 0.95 * this.lastElapsed) {delta *= 1.2}
    if (this.threshold * this.decay ** elapsed < delta) {
      this.lastBeat = now
      this.lastElapsed = elapsed
      return true
    }
    return false
  }


  isBeat2() {
    //this.envelope = this.envelope.map(x => Math.abs(x))
    for (let i = 0; i < this.envelope.length; i++) {
      let max = 0
      for (let j = 0; j < 8; j++) {
        if (this.freqData[i*8 + j] > max) {
          max = this.freqData[i*8 + j]
        }
      }
      this.envelope[i] = max
    }

    if (this.lastBeatEnvelope == undefined) {
      if (this.isBeat()) {
        this.lastBeatEnvelope = this.envelope
        return true
      }
    }
    else {
      let now = performance.now()
      let elapsed = now - this.lastBeat
      let energy = convolve(this.envelope, this.lastBeatEnvelope)
      if (this.avgEnergy == 0) {
        this.avgEnergy = energy
      }
      else {
        let delta = energy - this.avgEnergy
        let jump = delta - this.localMin
        let grooveDiff = Math.min((elapsed/this.groove) % 1, (this.groove/elapsed) % 1, 1-((elapsed/this.groove) % 1), 1-((this.groove/elapsed) % 1))

        if (delta < this.localMin) {
          this.localMin = delta
        }
        else if ((grooveDiff < 0.01 || jump < this.recentJump * 1.08) && elapsed > 250) {
          let a = grooveDiff ?  (Math.max(0.005 / grooveDiff, 4) - this.groove * (grooveDiff - 0.005)) : 1
          let grooveMultiplier = 1 + Math.min(Math.log(1 + Math.min(this.groove, 8))) * a * a
          if (this.recentJump * grooveMultiplier > 2 * (this.lastBeatJump + 10000) * this.decay ** elapsed) {
            this.lastBeatEnvelope = this.envelope
            this.lastBeatJump = jump
            this.lastBeat = now
            //this.localMin = delta
            if (this.groove == 0) {
              this.groove++
              if (elapsed > 500) {
                this.grooveSpace = elapsed / Math.round(elapsed / 500)
              }
              else {
                this.grooveSpace = elapsed * Math.round(500 / elapsed)
              }
            }
            else if (grooveDiff < 0.05) {
              let rhythmSpace = elapsed / Math.round(elapsed/this.grooveSpace)
              if (Math.abs(rhythmSpace - this.grooveSpace) > 20) {
                rhythmSpace = elapsed * Math.round(this.grooveSpace/elapsed)
              }
              this.grooveSpace = (this.grooveSpace * (this.groove + 10) + rhythmSpace) / (this.groove + 11)
              this.groove = this.groove * 0.9 + (1 - grooveDiff * grooveDiff * 20000)
            }
            else {
              let rhythmSpace = elapsed / Math.round(elapsed/500)
              let rhythmSpace2 = elapsed * Math.round(500/elapsed)
              if (Math.abs(rhythmSpace - 500) > Math.abs(rhythmSpace2 - 500)) {
                rhythmSpace = rhythmSpace2
              }
              this.grooveSpace = (this.grooveSpace * (this.groove + 10) + rhythmSpace) / (this.groove + 11)
              this.groove = this.groove*0.9 - 2
            }
            if (this.groove < 0) {this.groove = 0}
            if (this.grooveSpace < 300) {this.grooveSpace = this.grooveSpace * 2}
            else if (this.grooveSpace > 800) {this.grooveSpace = this.grooveSpace / 2}
            //console.log(`this.groove = ${this.groove} space=${this.grooveSpace} diff=${grooveDiff}`)
            this.localMin = delta
            document.getElementById("bpm").textContent = Math.floor(60000/this.grooveSpace)
            this.beatState.jump = jump
            this.graph.plot(delta, true)
            return true
          }
        }
        this.graph.plot(delta, false)
        this.avgEnergy = (this.avgEnergy * 20 + energy) / 21
        this.recentJump = jump
      }
    }
    return false
  }
}

function convolve(e1, e2) {
  let convolutionSum = 0
  for (let i = 0; i < e1.length; i++) {
    convolutionSum += e1[i] * e2[i] * (i + 10) / 26
  }
  return convolutionSum
}

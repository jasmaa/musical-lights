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
      source.connect(this.analyser)
      source = this.context.createMediaElementSource(audio);
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
      if (avgEnergy == 0) {
        avgEnergy = energy
      }
      else {
        let delta = energy - avgEnergy
        this.graph.plot(delta)
        let jump = delta - localMin
        let grooveDiff = Math.min((elapsed/groove) % 1, (groove/elapsed) % 1, 1-((elapsed/groove) % 1), 1-((groove/elapsed) % 1))

        if (delta < localMin) {
          localMin = delta
        }
        else if ((grooveDiff < 0.01 || jump < recentJump * 1.08) && elapsed > 250) {
          let a = grooveDiff ?  (Math.max(0.005 / grooveDiff, 4) - groove * (grooveDiff - 0.005)) : 1
          let grooveMultiplier = 1 + Math.min(Math.log(1 + Math.min(groove, 8))) * a * a
          if (recentJump * grooveMultiplier > 2 * (lastBeatJump) * this.decay ** elapsed) {
            this.lastBeatEnvelope = this.envelope
            lastBeatJump = jump
            this.lastBeat = now
            //localMin = delta
            if (groove == 0) {
              groove++
              if (elapsed > 500) {
                grooveSpace = elapsed / Math.round(elapsed / 500)
              }
              else {
                grooveSpace = elapsed * Math.round(500 / elapsed)
              }
            }
            else if (grooveDiff < 0.05) {
              let rhythmSpace = elapsed / Math.round(elapsed/grooveSpace)
              if (Math.abs(rhythmSpace - grooveSpace) > 20) {
                rhythmSpace = elapsed * Math.round(grooveSpace/elapsed)
              }
              grooveSpace = (grooveSpace * (groove + 10) + rhythmSpace) / (groove + 11)
              groove = groove * 0.9 + (1 - grooveDiff * grooveDiff * 20000)
            }
            else {
              let rhythmSpace = elapsed / Math.round(elapsed/500)
              let rhythmSpace2 = elapsed * Math.round(500/elapsed)
              if (Math.abs(rhythmSpace - 500) > Math.abs(rhythmSpace2 - 500)) {
                rhythmSpace = rhythmSpace2
              }
              grooveSpace = (grooveSpace * (groove + 10) + rhythmSpace) / (groove + 11)
              groove = groove*0.9 - 2
            }
            if (groove < 0) {groove = 0}
            if (grooveSpace < 300) {grooveSpace = grooveSpace * 2}
            else if (grooveSpace > 800) {grooveSpace = grooveSpace / 2}
            //console.log(`groove = ${groove} space=${grooveSpace} diff=${grooveDiff}`)
            localMin = delta
            document.getElementById("bpm").textContent = Math.floor(60000/grooveSpace)
            this.beatState.jump = jump
            return true
          }
        }
        avgEnergy = (avgEnergy * 20 + energy) / 21
        devEnergy = (devEnergy * 20 + jump) / 21
        recentJump = jump
      }
    }
    return false
  }
}

let avgEnergy = 0
let devEnergy = 0
let localMin = 0
let recentJump = 0
let lastBeatJump = 0
let groove = 0
let grooveSpace = 0

function convolve(e1, e2) {
  let convolutionSum = 0
  for (let i = 0; i < e1.length; i++) {
    convolutionSum += e1[i] * e2[i] * (i + 10) / 26
  }
  return convolutionSum
}

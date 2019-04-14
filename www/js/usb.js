export class UsbSerial {
  constructor() {
    this.connected = false
  }

  connect() {
    navigator.usb.requestDevice({filters: [{vendorId: 0x10C4}]})
    .then(selectedDevice => {
       this.device = selectedDevice;
       return device.open(); // Begin a session.
     })
    .then(() => this.device.selectConfiguration(1)) // Select configuration #1 for the this.device.
    .then(() => this.device.claimInterface(2)) // Request exclusive control over interface #2.
    .then(() => this.device.controlTransferOut({
        requestType: 'class',
        recipient: 'interface',
        request: 0x22,
        value: 0x01,
        index: 0x02})) // Ready to receive data
    .then(() => this.connected = true)
    .catch(error => { console.log(error); });
  }

  send(lights) {
    let data = new Uint8Array(280 *3 + 1)
    data[0] = 9
    for (let i = 0; i < 280; i++) {
      data[i * 3 + 1] = lights.r
      data[i * 3 + 2] = lights.g
      data[i * 3 + 3] = lights.b
    }
    transferOut(2, data)
  }
}

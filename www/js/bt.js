/****
Shamelessly copied from hewittwill/WebBluetooth-Terminal
****/

let serviceUuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
let characteristicUuid = "0000ffe1-0000-1000-8000-00805f9b34fb";
let output

export class Bluetooth {
  constructor() {
    this.connected = false
  }

  connect() {
    navigator.bluetooth.requestDevice({acceptAllDevices: true})
    .then(device => {
      log('Connecting...');
      //deviceName = device.name;
      return device.gatt.connect();
    })
    .then(server => {
      console.log('Getting Service...');
      return server.getPrimaryService(serviceUuid);
    })
    .then(service => {
      console.log('Getting Characteristic...');
      return service.getCharacteristic(characteristicUuid);
    })
    .then(characteristic => {
      output = characteristic;
      return output.startNotifications().then(_ => {
        console.log('> Notifications started');
        //log("Connected to: " + deviceName);
        this.connected = true
        output.addEventListener('characteristicvaluechanged',
            handleNotifications);
      });
    })
    .catch(error => {
      console.log('Argh! ' + error);
    });
  }

  disconnect() {
    if (output) {
      output.stopNotifications()
      .then(_ => {
        console.log('> Notifications stopped');
        log("Disconnected")
        output.removeEventListener('characteristicvaluechanged',
            handleNotifications);
        this.connected = false
      })
      .catch(error => {
        console.log('Argh! ' + error);
      });
    }
  }

  // takes ArrayBuffers
  send(buf) {
    output.writeValue(buf)
  }
}

function handleNotifications(event) {
  let value = event.target.value;
  //log(deviceName + "> " + new TextDecoder().decode(value));
  console.log('> Received: ' + new TextDecoder().decode(value));
}


/*str2ab(str) {
  var buf = new ArrayBuffer(str.length*2); // 2 bytes for each char
  var bufView = new Uint16Array(buf);
  for (var i=0, strLen=str.length; i<strLen; i++) {
    bufView[i] = str.charCodeAt(i);
  }
  return buf;
}*/

function log(str) {
  console.log("logged " + str);
}

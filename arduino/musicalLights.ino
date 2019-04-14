/*
 * Musical Lights, BITCAMP2019
 * Prem Chandrasekhar, Jason Maa
 * 
 * Uses code from Mark Kriegsman 2014 DemoReelESP32 and Evandro Copercini SerialToSerialBT
 */

#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;

#include "FastLED.h"
#if defined(FASTLED_VERSION) && (FASTLED_VERSION < 3001000)
#warning "Requires FastLED 3.1 or later; check github for latest code."
#endif

#define DATA_PIN    12
#define LED_TYPE    NEOPIXEL
#define NUM_LEDS    300
CRGB leds[NUM_LEDS];

#define BRIGHTNESS          255
#define FRAMES_PER_SECOND   120

// -- The core to run FastLED.show()
#define FASTLED_SHOW_CORE 0

#define SAMPLES 32             //Must be a power of 2
#define SAMPLING_FREQUENCY 5000 //Hz, must be less than 10000 due to ADC
unsigned int sampling_period_us;
unsigned long microseconds;

double vReal[SAMPLES];

// -- Task handles for use in the notifications
static TaskHandle_t FastLEDshowTaskHandle = 0;
static TaskHandle_t userTaskHandle = 0;

/** show() for ESP32
 *  Call this function instead of FastLED.show(). It signals core 0 to issue a show, 
 *  then waits for a notification that it is done.
 */
void FastLEDshowESP32() {
    if (userTaskHandle == 0) {
        // -- Store the handle of the current task, so that the show task can
        //    notify it when it's done
        userTaskHandle = xTaskGetCurrentTaskHandle();

        // -- Trigger the show task
        xTaskNotifyGive(FastLEDshowTaskHandle);

        // -- Wait to be notified that it's done
        const TickType_t xMaxBlockTime = pdMS_TO_TICKS(200);
        ulTaskNotifyTake(pdTRUE, xMaxBlockTime);
        userTaskHandle = 0;
    }
}


/** show Task
 *  This function runs on core 0 and just waits for requests to call FastLED.show()
 */
void FastLEDshowTask(void *pvParameters)
{
    // -- Run forever...
    for(;;) {
        // -- Wait for the trigger
        ulTaskNotifyTake(pdTRUE, portMAX_DELAY);

        // -- Do the show (synchronously)
        FastLED.show();

        // -- Notify the calling task
        xTaskNotifyGive(userTaskHandle);
    }
}


void setup() {
  delay(3000);
  Serial.begin(115200);
  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");

  FastLED.addLeds<LED_TYPE, DATA_PIN>(leds, NUM_LEDS).setCorrection(TypicalLEDStrip);;
  FastLED.setBrightness(BRIGHTNESS);
  
  int core = xPortGetCoreID();

  for(int i = 0; i<NUM_LEDS; i++){
    leds[i].setRGB(255,0,0);
  }

  xTaskCreatePinnedToCore(FastLEDshowTask, "FastLEDshowTask", 2048, NULL, 2, &FastLEDshowTaskHandle, FASTLED_SHOW_CORE);
}

uint8_t tick = 0; // rotating "base color" used by many of the patterns
int mode = 1;   // for incoming serial data
int incomingByte = 0;
int numModes = 7;
int maxV,minV;
double amplitude, percentAmplitude; 
int r, g, b;

void loop() {
  if (SerialBT.available()) {
    // read the incoming byte:
    incomingByte = SerialBT.read();
    if(incomingByte <= numModes){
      mode = incomingByte;
      if(mode == 2||mode == 4){
        r = SerialBT.read();
        g = SerialBT.read();
        b = SerialBT.read();
      }
    }
  }
  switch(mode){
    case 1: //Rainbow
      rainbow();
      break;

    case 2: //Solid Color
      solidColor(r, g, b);
      break;
      
    case 3: //Pulse
      if(tick%50==0){
        pulse((tick)%255, (tick*10+20)%255, (tick*100+50)%255, 5);
      }
      shiftLEDs();
      break;
      
    case 4: //Strobe
      strobe(r, g, b);
      break;

    case 5: //Volume Bar
      maxV = 0;
      minV = 0;
      //sampling
      for(int i=0; i<SAMPLES; i++)
      {
          microseconds = micros();    //Overflows after around 70 minutes!
       
          vReal[i] = analogRead(0);
          if(vReal[i]>maxV)
            maxV = vReal[i];
          else if(vReal[i]<minV)
            minV = vReal[i];
       
          while(micros() < (microseconds + sampling_period_us)){
          }
      }

      amplitude = maxV - minV;
      
      if(amplitude > 300){
        percentBar(1);
      }else{
        percentBar(.5);
      }
      break;
      
    case 6: //Beat Flash
      maxV = 0;
      minV = 0;
      /*SAMPLING*/
      for(int i=0; i<SAMPLES; i++)
      {
          microseconds = micros();    //Overflows after around 70 minutes!
       
          vReal[i] = analogRead(0);
          if(vReal[i]>maxV)
            maxV = vReal[i];
          else if(vReal[i]<minV)
            minV = vReal[i];
       
          while(micros() < (microseconds + sampling_period_us)){
          }
      }

      amplitude = maxV-minV;
      percentAmplitude = amplitude/500;
      //Filter to make change more dramtic
      percentAmplitude-=0.5;
      percentAmplitude*=3;
      percentBar(percentAmplitude);
      break;

    case 7: //Amplitude Pulse
      maxV = 0;
      minV = 0;
      
      for(int i=0; i<SAMPLES; i++)
      {
          microseconds = micros();    //Overflows after around 70 minutes!
       
          vReal[i] = analogRead(0);
          if(vReal[i]>maxV)
            maxV = vReal[i];
          else if(vReal[i]<minV)
            minV = vReal[i];
       
          while(micros() < (microseconds + sampling_period_us)){
          }
      }

      amplitude = maxV-minV;
      //Filter to make change more dramtic
      percentAmplitude-=0.5;
      percentAmplitude*=3;
      pulse((tick)%255, (tick*10+20)%255, (tick*100+50)%255, 5);
      break;
      
    default:
      rainbow();
  }

  // send the 'leds' array out to the actual LED strip
  FastLEDshowESP32();
  FastLED.delay(1000/FRAMES_PER_SECOND); 
  EVERY_N_MILLISECONDS( 20 ) { tick++; } // slowly cycle the "base color" through the rainbow
}


//Sets the entire length to the RGB value
void solidColor(int r, int g, int b){
  fill_solid( leds, NUM_LEDS, CRGB(r, g, b));
}

// Fades out and into a certain color
void heartBeat(int r, int g, int b){
  exit(1);
}

//Makes a pulse of length trail. Diminishes equally across trail
void pulse(int r, int g, int b, int trail){
  for(int i = trail-1; i>=0; i--){
    leds[i].setRGB(r, g, b);
    leds[i].fadeLightBy(i/(trail-1)*256);
  }
}

//Moves the LEDs down by one
void shiftLEDs(){
  //Shift all leds
  for(int i = NUM_LEDS-1; i>0; i--){
    leds[i] = leds[i-1];
  }
  leds[0] = CRGB::Black;
}

//Sets a certain percent of the leds to the color (e.g. 0.5, 0.69)
void percentBar(float percent){
  for(int i = 0; i<min((int)percent*NUM_LEDS,NUM_LEDS); i++){
    //15% - Violet
    if(i<0.15*NUM_LEDS)
      leds[i] = CRGB::Violet;
    //30% - Blue
    else if(i<0.3*NUM_LEDS)
      leds[i] = CRGB::Blue;
    //45% - Green
    else if(i<0.45*NUM_LEDS)
      leds[i] = CRGB::Green;
    //60% - Yellow
    else if(i<0.6*NUM_LEDS)
      leds[i] = CRGB::Yellow;
    //75% - Orange
    else if(i<0.75*NUM_LEDS)
      leds[i] = CRGB::Orange;
    //90% - Red
    else if(i<0.9*NUM_LEDS)
      leds[i] = CRGB::White;
    //100% - White
    else
      leds[i] = CRGB::White;
    
  }
  for(int i = min((int)percent*NUM_LEDS, NUM_LEDS); i<NUM_LEDS; i++){
    leds[i].setRGB(0,0,0);
  }
}

//Sets a certain percent of the strip to a rainbow
void rainbowPercent(float percent){
  fill_rainbow(leds, percent*NUM_LEDS, tick, 2);
}

//Police Lights
void policeLight() {
  //Flash red 3 times
  
  //Flash blue 3 times
}

//Strobe
void strobe(int r, int g, int b) {
  if(tick%2==0){
    fill_solid(leds, NUM_LEDS, CRGB(r, g, b));
  }
  else{
    fill_solid(leds, NUM_LEDS, CRGB::Black);
  }
}

void rainbow() 
{
  // FastLED's built-in rainbow generator
  fill_rainbow( leds, NUM_LEDS, tick, 2);
}


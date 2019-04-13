#include <FastLED.h>

// How many leds in your strip?
#define NUM_LEDS 300

// For led chips like Neopixels, which have a data line, ground, and power, you just
// need to define DATA_PIN.  For led chipsets that are SPI based (four wires - data, clock,
// ground, and power), like the LPD8806 define both DATA_PIN and CLOCK_PIN
#define DATA_PIN 3


// Define the array of leds
CRGB leds[NUM_LEDS];


void setup() { 
  	  FastLED.addLeds<NEOPIXEL, DATA_PIN>(leds, NUM_LEDS);
}

uint8_t tick = 0;
int mode = 4;
void loop() { 
  switch(mode){
    case 1:
      rainbow();
      break;

    case 2:
      solidColor(20, 20, 20);
      break;
      
    case 3:
      if(tick%50==0){
        pulse((tick)%255, (tick*10+20)%255, (tick*100+50)%255, 5);
      }
      shiftLEDs();
      break;
    case 4:
      strobe();
      break;
    default:
      rainbow();
  }
  
  
  FastLED.show();
  delay(20);
  tick+=1;
}

void rainbow() { 
  // FastLED's built-in rainbow generator
  fill_rainbow( leds, NUM_LEDS, tick, 2);
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
void percentBar(int r, int g, int b, float percent){
  for(int i = 0; i<percent*NUM_LEDS; i++){
    leds[i].setRGB(r,g,b);
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
void strobe() {
  if(tick%2==0){
    fill_solid(leds, NUM_LEDS, CRGB::White);
  }
  else{
    fill_solid(leds, NUM_LEDS, CRGB::Black);
  }
}

CRGB mapToColor(){
  
}

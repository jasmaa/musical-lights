package com.happycampers.musicallights;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ColorSlidersActivity extends AppCompatActivity {

    private Button submitBtn;
    private TextView displayText;
    private SeekBar[] sliders;

    private int currentColor;
    private int r, b, g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_sliders);

        //Intent intent = getIntent();
        //currentColor = Integer.parseInt(intent.getStringExtra("color"));

        sliders = new SeekBar[3];
        sliders[0] = (SeekBar) findViewById(R.id.rSeek);
        sliders[1] = (SeekBar) findViewById(R.id.gSeek);
        sliders[2] = (SeekBar) findViewById(R.id.bSeek);

        for(SeekBar slider : sliders){
            slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    getRGB();
                }
            });
        }

        displayText = (TextView) findViewById(R.id.displayText);

        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ColorSlidersActivity.this, MainActivity.class);
                myIntent.putExtra("color", currentColor+"");
                ColorSlidersActivity.this.startActivity(myIntent);
            }
        });

        //setRGB(currentColor);
        String hexColor = String.format("#%06X", (0xFFFFFF & currentColor));
        displayText.setText(hexColor);
    }

    /**
     * Update current color from sliders
     */
    private void getRGB(){
        r = sliders[0].getProgress();
        g = sliders[1].getProgress();
        b = sliders[2].getProgress();

        currentColor = (0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
        String hexColor = String.format("#%06X", (0xFFFFFF & currentColor));
        displayText.setText(hexColor);
        displayText.setTextColor(currentColor);
    }

    /**
     * Update sliders and display color
     * @param color
     */
    private void setRGB(int color){
        r = (color >> 16) & 0xff;
        g = (color >>  8) & 0xff;
        b = (color) & 0xff;

        sliders[0].setProgress(r);
        sliders[1].setProgress(g);
        sliders[2].setProgress(b);

        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        displayText.setText(hexColor);
        displayText.setTextColor(color);
    }


}

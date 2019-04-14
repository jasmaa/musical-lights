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

/**
 * Controls color selection via sliders
 */
public class ColorSlidersActivity extends AppCompatActivity {

    private Button submitBtn;
    private TextView displayText;
    private SeekBar[] sliders;

    private int currentColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_sliders);

        Intent intent = getIntent();
        currentColor = Integer.parseInt(intent.getStringExtra("color"));

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
                finish();
            }
        });

        setRGB(currentColor);
        String hexColor = String.format("#%06X", (0xFFFFFF & currentColor));
        displayText.setText(hexColor);
        displayText.setTextColor(currentColor);
    }

    /**
     * Update current color from sliders
     */
    private void getRGB(){
        int red = sliders[0].getProgress();
        int green = sliders[1].getProgress();
        int blue = sliders[2].getProgress();

        currentColor = (0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
        String hexColor = String.format("#%06X", (0xFFFFFF & currentColor));
        displayText.setText(hexColor);
        displayText.setTextColor(currentColor);
    }

    /**
     * Update sliders and display color
     * @param color
     */
    private void setRGB(int color){
        int red = (color >> 16) & 0xff;
        int green = (color >>  8) & 0xff;
        int blue = (color) & 0xff;

        sliders[0].setProgress(red);
        sliders[1].setProgress(green);
        sliders[2].setProgress(blue);

        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        displayText.setText(hexColor);
        displayText.setTextColor(color);
    }


}

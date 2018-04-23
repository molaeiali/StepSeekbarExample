package org.molaei.stepseekbarexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import org.molaei.stepseekbar.StepSeekbar;
import org.molaei.stepseekbar.StepSeekbarOnScrollChangedListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final StepSeekbar stepSeekbar = findViewById(R.id.stepSeekbar);
        stepSeekbar.setStepSeekbarOnScrollChangedListener(new StepSeekbarOnScrollChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == seekBar.getMax()){
                    stepSeekbar.setTextAfterProgress("Max");
                } else {
                    stepSeekbar.setTextAfterProgress("");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}

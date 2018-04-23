package org.molaei.stepseekbar;

import android.widget.SeekBar;

/**
 * Created by ali on 4/23/18.
 */

public interface StepSeekbarOnScrollChangedListener {
    void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    void onStartTrackingTouch(SeekBar seekBar);
    void onStopTrackingTouch(SeekBar seekBar);
}

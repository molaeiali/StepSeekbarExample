package org.molaei.stepseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by ali on 4/14/18.
 */

public class StepSeekbar extends FrameLayout {
    private MyStep[] steps;
    private AppCompatSeekBar seekBar;
    private String stringSteps;
    private int max;
    private int progress;
    private int stepsColor;
    private int thumbColor;
    private int stepWidth;
    private int stepHeight;
    private View thumbView;
    private boolean topText, bottomText;
    private Drawable thumbDrawable, stepsDrawable;
    private Typeface textTypeface;

    public StepSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepSeekbar, 0, 0);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //noinspection ConstantConditions
        final View view = inflater.inflate(R.layout.stepseekbar, this, true);
        seekBar = view.findViewById(R.id.seekbar);

        thumbView = LayoutInflater.from(getContext()).inflate(R.layout.stepseekbar_thumb, null, false);

        thumbDrawable = typedArray.getDrawable(R.styleable.StepSeekbar_thumbDrawable);
        if (thumbDrawable == null) {
            thumbDrawable = ContextCompat.getDrawable(getContext(), R.drawable.thumb);
        }
        stepsDrawable = typedArray.getDrawable(R.styleable.StepSeekbar_thumbDrawable);
        if (stepsDrawable == null) {
            stepsDrawable = ContextCompat.getDrawable(getContext(), R.drawable.steps);
        }

        if (typedArray.hasValue(R.styleable.StepSeekbar_textTypeface)) {
            int fontId = typedArray.getResourceId(R.styleable.StepSeekbar_textTypeface, -1);
            textTypeface = ResourcesCompat.getFont(context, fontId);
        } else {
            textTypeface = Typeface.SANS_SERIF;
        }

        stepsColor = typedArray.getColor(R.styleable.StepSeekbar_stepsColor, Color.parseColor("#FFFFFF"));
        thumbColor = typedArray.getColor(R.styleable.StepSeekbar_thumbColor, Color.parseColor("#FF0000"));
        int progressColor = typedArray.getColor(R.styleable.StepSeekbar_progressColor, Color.parseColor("#FF0000"));
        int backgroundColor = typedArray.getColor(R.styleable.StepSeekbar_backgroundColor, Color.parseColor("#FFFFFF"));
        stepWidth = typedArray.getDimensionPixelSize(R.styleable.StepSeekbar_stepWidth, 20);
        stepHeight = typedArray.getDimensionPixelSize(R.styleable.StepSeekbar_stepHeight, 12);

        int textPosition = typedArray.getInt(R.styleable.StepSeekbar_textPosition, 0);
        topText = textPosition == 0 || textPosition == 2;
        bottomText = textPosition == 1 || textPosition == 2;

        seekBar.setBackgroundColor(backgroundColor);

        seekBar.setThumb(getThumb(String.valueOf(progress), topText, bottomText));
        seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));

        seekBar.getProgressDrawable().setColorFilter(progressColor, PorterDuff.Mode.MULTIPLY);

        max = typedArray.getInt(R.styleable.StepSeekbar_max, 100);
        seekBar.setMax(max);

        progress = typedArray.getInt(R.styleable.StepSeekbar_progress, 0);
        seekBar.setProgress(progress);

        stringSteps = typedArray.getString(R.styleable.StepSeekbar_steps);

        if (stringSteps != null) {
            String[] stringStepsArray = stringSteps.split(",");
            steps = new MyStep[stringStepsArray.length + 2];
            steps[0] = new MyStep(0, stepWidth, stepHeight);
            for (int i = 0; i < stringStepsArray.length; i++) {
                steps[i + 1] = new MyStep(Integer.valueOf(stringStepsArray[i]), stepWidth, stepHeight);
            }
            steps[stringStepsArray.length + 1] = new MyStep(max, stepWidth, stepHeight);
        }

        for (MyStep myStep : steps) {
            addView(myStep.seekBar);
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setThumb(getThumb(String.valueOf(progress), topText, bottomText));
                seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                stepper(seekBar);
            }
        });
        stepper(seekBar);

        typedArray.recycle();
    }

    public Drawable getThumb(String string, boolean top, boolean bottom) {
        TextView topTv = thumbView.findViewById(R.id.seekbarTextTop);
        TextView bottomTv = thumbView.findViewById(R.id.seekbarTextBottom);

        ImageView thumb = thumbView.findViewById(R.id.seekbarThumb);
        thumb.setImageDrawable(thumbDrawable);

        topTv.setText(string);
        bottomTv.setText(string);

        topTv.setTypeface(textTypeface);
        bottomTv.setTypeface(textTypeface);

        topTv.setVisibility(top ? VISIBLE : INVISIBLE);
        bottomTv.setVisibility(bottom ? VISIBLE : INVISIBLE);

        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void stepper(SeekBar seekBar) {
        MyStep prevStep = steps[0];
        for (MyStep currentStep : steps) {
            if (currentStep.value < seekBar.getProgress()) {
                prevStep = currentStep;
            } else {
                break;
            }
        }
        MyStep nextStep = steps[Arrays.asList(steps).indexOf(prevStep) + 1];
        if (seekBar.getProgress() > (prevStep.value + nextStep.value) / 2) {
            seekBar.setProgress(nextStep.value);
        } else {
            seekBar.setProgress(prevStep.value);
        }
        for (MyStep step : steps) {
            if (step.value == seekBar.getProgress()) {
                step.seekBar.setVisibility(GONE);
            } else {
                step.seekBar.setVisibility(VISIBLE);
            }
        }
    }

    private class MyStep {
        AppCompatSeekBar seekBar;
        int value;

        MyStep(int value, int stepWidth, int stepHeight) {
            this.value = value;
            seekBar = new AppCompatSeekBar(getContext());
            seekBar.setMax(max);
            seekBar.setProgress(value);
            seekBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.steps_seekbar_drawable));
            Bitmap b = drawableToBitmap(stepsDrawable);
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, stepWidth, stepHeight, false);
            Drawable stepDrawable = new BitmapDrawable(getResources(), bitmapResized);
            stepDrawable.setColorFilter(new PorterDuffColorFilter(stepsColor, PorterDuff.Mode.SRC_IN));
            seekBar.setThumb(stepDrawable);
            seekBar.setEnabled(false);
        }

    }

    private Bitmap drawableToBitmap(Drawable drawable) {
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public int getMax() {
        return seekBar.getMax();
    }

    public void setMax(int max) {
        seekBar.setMax(max);
    }

    public int getProgress() {
        return seekBar.getProgress();
    }

    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }
}

package org.molaei.stepseekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by ali on 4/14/18.
 */

@SuppressWarnings("unused")
public class StepSeekbar extends RelativeLayout {
    private MyStep[] steps;
    private SeekBar seekBar;
    private SeekBar seekBarTextTop;
    private SeekBar seekBarTextBottom;
    private int max;
    private int stepsColor;
    private float textSize;
    private int thumbColor;
    private View textSeekBarThumbView;
    private Drawable stepsDrawable;
    private Typeface textTypeface;
    private String textBeforeProgress;
    private String textAfterProgress;
    private StepSeekbarOnScrollChangedListener stepSeekbarOnScrollChangedListener;
    private boolean firstLaunch;

    @SuppressLint("InflateParams")
    public StepSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepSeekbar, 0, 0);

        firstLaunch = true;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //noinspection ConstantConditions
        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.stepseekbar, this, true);
        seekBar = layout.findViewById(R.id.seekbar);
        seekBarTextTop = layout.findViewById(R.id.seekbarTextTop);
        seekBarTextBottom = layout.findViewById(R.id.seekbarTextBottom);

        textSeekBarThumbView = LayoutInflater.from(getContext()).inflate(R.layout.stepseekbar_text_thumb, null, false);

        Drawable thumbDrawable = typedArray.getDrawable(R.styleable.StepSeekbar_thumbDrawable);
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
        int stepWidth = typedArray.getDimensionPixelSize(R.styleable.StepSeekbar_stepWidth, 20);
        int stepHeight = typedArray.getDimensionPixelSize(R.styleable.StepSeekbar_stepHeight, 12);

        textSize = typedArray.getDimensionPixelSize(R.styleable.StepSeekbar_textSize, 12);

        textBeforeProgress = typedArray.getString(R.styleable.StepSeekbar_textBeforeProgress);
        textAfterProgress = typedArray.getString(R.styleable.StepSeekbar_textAfterProgress);

        if (textBeforeProgress == null)
            textBeforeProgress = "";

        if (textAfterProgress == null)
            textAfterProgress = "";

        int textPosition = typedArray.getInt(R.styleable.StepSeekbar_textPosition, 0);
        boolean topText = textPosition == 0 || textPosition == 2;
        boolean bottomText = textPosition == 1 || textPosition == 2;

        seekBarTextTop.setVisibility(topText ? VISIBLE : GONE);
        seekBarTextBottom.setVisibility(bottomText ? VISIBLE : GONE);

        seekBar.setBackgroundColor(backgroundColor);


        seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));
        seekBarTextTop.getThumb().setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));
        seekBarTextBottom.getThumb().setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));

        seekBar.getProgressDrawable().setColorFilter(progressColor, PorterDuff.Mode.MULTIPLY);

        max = typedArray.getInt(R.styleable.StepSeekbar_max, 100);
        seekBar.setMax(max);
        seekBarTextTop.setMax(max);
        seekBarTextBottom.setMax(max);

        seekBarTextTop.setEnabled(false);
        seekBarTextBottom.setEnabled(false);

        int progress = typedArray.getInt(R.styleable.StepSeekbar_progress, 0);
        seekBar.setProgress(progress);
        seekBarTextTop.setProgress(progress);
        seekBarTextBottom.setProgress(progress);

        seekBar.setThumb(thumbDrawable);
        seekBarTextTop.setThumb(getTextsThumb(String.valueOf(progress)));
        seekBarTextBottom.setThumb(getTextsThumb(String.valueOf(progress)));

        String stringSteps = typedArray.getString(R.styleable.StepSeekbar_steps);

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
            View view = layout.getChildAt(0).findViewById(R.id.seekbarWrapper);
            ((RelativeLayout) view).addView(myStep.seekBar);
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (stepSeekbarOnScrollChangedListener != null)
                    stepSeekbarOnScrollChangedListener.onProgressChanged(seekBar, progress, fromUser);
                seekBarTextTop.setProgress(progress);
                seekBarTextTop.setThumb(getTextsThumb(String.format(Locale.ENGLISH, "%s %d %s", textBeforeProgress, progress, textAfterProgress)));
                seekBarTextTop.getThumb().setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));
                seekBarTextBottom.setProgress(progress);
                seekBarTextBottom.setThumb(getTextsThumb(String.format(Locale.ENGLISH, "%s %d %s", textBeforeProgress, progress, textAfterProgress)));
                seekBarTextBottom.getThumb().setColorFilter(new PorterDuffColorFilter(thumbColor, PorterDuff.Mode.SRC_IN));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (stepSeekbarOnScrollChangedListener != null)
                    stepSeekbarOnScrollChangedListener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (stepSeekbarOnScrollChangedListener != null)
                    stepSeekbarOnScrollChangedListener.onStopTrackingTouch(seekBar);
                stepper(seekBar);
            }
        });
        stepper(seekBar);

        typedArray.recycle();
    }

    public Drawable getTextsThumb(String string) {
        TextView text = textSeekBarThumbView.findViewById(R.id.text);

        text.setText(string);
        text.setTypeface(textTypeface);
        text.setTextSize(pxToDp(textSize));
        text.setTextColor(thumbColor);

        textSeekBarThumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(textSeekBarThumbView.getMeasuredWidth(), textSeekBarThumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        textSeekBarThumbView.layout(0, 0, textSeekBarThumbView.getMeasuredWidth(), textSeekBarThumbView.getMeasuredHeight());
        textSeekBarThumbView.draw(canvas);

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
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            seekBar.setLayoutParams(layoutParams);
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

    public void setTextAfterProgress(String textAfterProgress) {
        this.textAfterProgress = textAfterProgress;
    }

    public void setTextBeforeProgress(String textBeforeProgress) {
        this.textBeforeProgress = textBeforeProgress;
    }

    public void setStepSeekbarOnScrollChangedListener(StepSeekbarOnScrollChangedListener stepSeekbarOnScrollChangedListener) {
        this.stepSeekbarOnScrollChangedListener = stepSeekbarOnScrollChangedListener;
    }

    private float pxToDp(float px) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private float dpToPx(float dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void setTextSize(float textSizeInSp) {
        this.textSize = dpToPx(textSizeInSp);
        if (firstLaunch) {
            firstLaunch = false;
            seekBar.setProgress(seekBar.getProgress() - 1);
            seekBar.setProgress(seekBar.getProgress() + 1);
        }
    }
}

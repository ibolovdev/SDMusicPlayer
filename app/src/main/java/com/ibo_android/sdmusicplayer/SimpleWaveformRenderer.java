package com.ibo_android.sdmusicplayer;


import android.graphics.Canvas;
import 	android.graphics.Paint;
import android.graphics.Path;

public class SimpleWaveformRenderer implements WaveformRenderer
{
    private static final int Y_FACTOR = 0xFF;
    private static final float HALF_FACTOR = 0.5f;

    private final int backgroundColour;
    private final Paint foregroundPaint;
    private final Path waveformPath;

    static SimpleWaveformRenderer newInstance(  int backgroundColour,   int foregroundColour) {
        Paint paint = new Paint();
        paint.setColor(foregroundColour);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        Path waveformPath = new Path();
        return new SimpleWaveformRenderer(backgroundColour, paint, waveformPath);
    }

    SimpleWaveformRenderer(  int backgroundColour, Paint foregroundPaint, Path waveformPath) {
        this.backgroundColour = backgroundColour;
        this.foregroundPaint = foregroundPaint;
        this.waveformPath = waveformPath;
    }

    @Override
    public void render(Canvas canvas, byte[] waveform) {

        canvas.drawColor(backgroundColour);
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        waveformPath.reset();
        if (waveform != null) {
            renderWaveform(waveform, width, height);
        } else {
            renderBlank(width, height);
        }
        canvas.drawPath(waveformPath, foregroundPaint);
    }



    private void renderWaveform(byte[] waveform, float width, float height) {
        float xIncrement = width / (float) (waveform.length);
        float yIncrement = height / Y_FACTOR;
        int halfHeight = (int) (height * HALF_FACTOR);
        waveformPath.moveTo(0, halfHeight);
        for (int i = 1; i < waveform.length; i++) {
            float yPosition = waveform[i] > 0 ? height - (yIncrement * waveform[i]) : -(yIncrement * waveform[i]);
            waveformPath.lineTo(xIncrement * i, yPosition);
        }
        waveformPath.lineTo(width, halfHeight);
    }


    private void renderBlank(float width, float height) {
        int y = (int) (height * HALF_FACTOR);
        waveformPath.moveTo(0, y);
        waveformPath.lineTo(width, y);
    }




}

package com.ibo_android.sdmusicplayer;

public class WebformRendererFactory {

    public WaveformRenderer createSimpleWaveformRenderer(  int foreground,   int background) {
        return SimpleWaveformRenderer.newInstance(background, foreground);
    }
}

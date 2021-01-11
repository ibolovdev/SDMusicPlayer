package com.ibo_android.sdmusicplayer;

import android.media.audiofx.Visualizer;

public class MyOnDataCaptureListener
        implements android.media.audiofx.Visualizer.OnDataCaptureListener
{

    private WaveformView _waveformView;

    public MyOnDataCaptureListener(WaveformView wv)
    {
        _waveformView = wv;
    }



    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate)
    {
        if (_waveformView != null) {
            _waveformView.setWaveform(waveform);
        }
    }


    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate)
    {

    }
}

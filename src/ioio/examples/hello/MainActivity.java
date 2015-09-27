package ioio.examples.hello;

/**
 * ***********************************************************************
 * Morse code test ver 150927A
 * Copyright 2015 Wintriss Technical Schools
 * All rights reserved
 *
 A	.-
 B	-...
 C	-.-.
 D	-..
 E	.
 F	..-.
 G	--.
 H	....
 I	..
 J	.---
 K	-.-
 L	.-..
 M	--
 N	-.
 O	---
 P	.--.
 Q	--.-
 R	.-.
 S	...
 T	-
 U	..-
 V	...-
 W	.--
 X	-..-
 Y	-.--
 Z	--..
 0	-----
 1	.----
 2	..---
 3	...--
 4	....-
 5	.....
 6	-....
 7	--...
 8	---..
 9	----.
 * ************************************************************************
 */

import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class MainActivity extends IOIOActivity implements TextToSpeech.OnInitListener
{
    private TextView mText;
    private TextToSpeech mTts;
    private ScrollView mScroller;
    private DigitalOutput led;//The IOIO board LED
    private DigitalInput thumb;
    private DigitalInput indexFinger;
    private DigitalInput middleFinger;
    private DigitalInput ringFinger;
    private DigitalInput pinkie;
    private AnalogInput analogThumb;
    private AnalogInput analogIndexFinger;
    public static final int THUMB_PIN = 19;//IOIO board pin numbers
    public static final int INDEX_FINGER_PIN = 20;
    public static final int MIDDLE_FINGER_PIN = 21;
    public static final int RING_FINGER_PIN = 22;
    public static final int PINKIE_PIN = 23;
    private float thumbVoltage;
    private float indexFingerVoltage;
    private String nextWord = "";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mText = (TextView) findViewById(R.id.logText);
    }

    @Override
    public void onInit(int status)
    {

    }

    class Looper extends BaseIOIOLooper
    {
        @Override
        protected void setup() throws ConnectionLostException
        {
            led = ioio_.openDigitalOutput(0, true);
            analogIndexFinger = ioio_.openAnalogInput(INDEX_FINGER_PIN);
            thumb = ioio_.openDigitalInput(THUMB_PIN, DigitalInput.Spec.Mode.PULL_UP);
            indexFinger = ioio_.openDigitalInput(INDEX_FINGER_PIN, DigitalInput.Spec.Mode.PULL_UP);
            middleFinger = ioio_.openDigitalInput(MIDDLE_FINGER_PIN, DigitalInput.Spec.Mode.PULL_UP);
            ringFinger = ioio_.openDigitalInput(RING_FINGER_PIN, DigitalInput.Spec.Mode.PULL_UP);
            pinkie = ioio_.openDigitalInput(PINKIE_PIN, DigitalInput.Spec.Mode.PULL_UP);
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException
        {
            led.write(false);
            SystemClock.sleep(500);
            led.write(true);
            SystemClock.sleep(500);
            indexFingerVoltage = analogIndexFinger.getVoltage();
            //if (thumbVoltage < 1.6)
            {
                nextWord += ("v = " + indexFingerVoltage);
            }
            log(nextWord);
        }
    }

    @Override
    protected IOIOLooper createIOIOLooper()
    {
        return new Looper();
    }

    public void log(final String msg)
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                mText.setText(" ");
                mText.append("\r" + msg);
            }
        });
    }

    public void speak(String stuffToSay)
    {
        mTts.setLanguage(Locale.US);
        if (!mTts.isSpeaking())
        {
            mTts.speak(stuffToSay, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
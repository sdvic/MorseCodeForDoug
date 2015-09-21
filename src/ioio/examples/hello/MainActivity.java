package ioio.examples.hello;

/**
 * ***********************************************************************
 * Chord keyboard test ver 150920A
 * Copyright 2015 Wintriss Technical Schools
 * All rights reserved
 * ************************************************************************
 */

import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

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
    public static final int THUMB_PIN = 19;//IOIO board pin numbers
    public static final int INDEX_PIN = 20;
    public static final int MIDDLE_PIN = 21;
    public static final int RING_PIN = 22;
    public static final int PINKIE_PIN = 23;
    private byte fingerCode;
    private String nextWord = "";
    private char[] asciiCode =
            {
                    0, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '?', '.','\r', ' ', ' '
            };

     /* ****************************************************************
     * Thumb  Index    Middle     Ring    Pinkie  Keyboard Layout
     * 16      8         4         2        1
     * ****************************************************************
     * ASCII                            Binary Finger Code     Hex Finger Code
     * 08 BACK SPACE                        11111                   1F  //Erase previous character
     * 0A LINE FEED                         11101                   1D  //Starts a new word on a new line
     * 21 EXCLAMATION                       -----                   --
     * 2E PERIOD                            11100                   1C
     * 3F QUESTION MARK                     11011                   1B
     * 20 SPACE                             11110                   1E
     * 41 A                                 00001                   01
     * 42 B                                 00010                   02
     * 43 C                                 00011                   03
     * 44 D                                 00100                   04
     * 45 E                                 00101                   05
     * 46 F                                 00110                   06
     * 47 G                                 00111                   07
     * 48 H                                 01000                   08
     * 49 I                                 01001                   09
     * 4A J                                 01010                   0A
     * 4B K                                 01011                   0B
     * 4C L                                 01100                   0C
     * 4D M                                 01101                   0D
     * 4E N                                 01110                   0E
     * 4F O                                 01111                   0F
     * 50 P                                 10000                   10
     * 51 Q                                 10001                   11
     * 52 R                                 10010                   12
     * 53 S                                 10011                   13
     * 54 T                                 10100                   14
     * 55 U                                 10101                   15
     * 56 V                                 10110                   16
     * 57 W                                 10111                   17
     * 58 X                                 11000                   18
     * 59 Y                                 11001                   19
     * 5A Z                                 11010                   1A
     * **********************************************************************
     */

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
            thumb = ioio_.openDigitalInput(THUMB_PIN, DigitalInput.Spec.Mode.PULL_UP);
            indexFinger = ioio_.openDigitalInput(INDEX_PIN, DigitalInput.Spec.Mode.PULL_UP);
            middleFinger = ioio_.openDigitalInput(MIDDLE_PIN, DigitalInput.Spec.Mode.PULL_UP);
            ringFinger = ioio_.openDigitalInput(RING_PIN, DigitalInput.Spec.Mode.PULL_UP);
            pinkie = ioio_.openDigitalInput(PINKIE_PIN, DigitalInput.Spec.Mode.PULL_UP);
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException
        {
            led.write(false);
            SystemClock.sleep(500);
            led.write(true);
            SystemClock.sleep(500);
            fingerCode = 0;
            if (!thumb.read())
            {
                fingerCode = (byte) (fingerCode | 0x10);
            }
            if (!indexFinger.read())
            {
                fingerCode = (byte) (fingerCode | 0x8);
            }
            if (!middleFinger.read())
            {
                fingerCode = (byte) (fingerCode | 0x4);
            }
            if (!ringFinger.read())
            {
                fingerCode = (byte) (fingerCode | 0x2);
            }
            if (!pinkie.read())
            {
                fingerCode = (byte) (fingerCode | 0x1);
            }
            if (fingerCode == 0x1f && nextWord.length() >= 1)//back delete
            {
                nextWord = nextWord.substring(0, nextWord.length() - 1);
                log(nextWord);
                fingerCode = 0;
                return;
            }
            if (fingerCode != 0)
            {
                nextWord = nextWord + asciiCode[fingerCode];
                log(nextWord);
            }

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
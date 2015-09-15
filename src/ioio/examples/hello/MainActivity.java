package ioio.examples.hello;

/**
 * ***********************************************************************
 * Chord keyboard test ver 150914A
 * ************************************************************************
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.Locale;


public class MainActivity extends IOIOActivity implements TextToSpeech.OnInitListener
{
    private Accelerometer accelerometer;
    private int i = 0;
    private int j = 0;
    private ToggleButton button;
    private TextView mText;
    private ScrollView mScroller;
    private TextToSpeech mTts;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;
    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;
    private double azimuth;
    private double pitch;
    private double roll;
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
    private boolean isThumbPressed = false;
    private boolean isIndexFingerPressed = false;
    private boolean isMiddleFingerPressed = false;
    private boolean isRingFingerPressed = false;
    private boolean isPinkyPressed = false;
    private byte fingerCode;

     /* ****************************************************************
     * Thumb  Index    Middle     Ring    Pinkie
     * 16      8         4         2        1
     * ****************************************************************
     * ASCII                            Binary Finger Code     Hex Finger Code
     * 08 BACK SPACE                        00000                   1F
     * 0A LINE FEED                         00000                   --
     * 21 EXCLAMATION                       00000                   1D
     * 2E PERIOD                            11100                   1C
     * 3F QUESTION MARK                     11011                   1B
     * 20 SPACE                             11111                   1E
     * 41 A                                 00001                   01
     * 42 B                                 00010                   02
     * 43 C                                 00011                   03
     * 44 D                                 00001                   04
     * 45 E                                 00001                   05
     * 46 F                                 00001                   06
     * 47 G                                 00001                   07
     * 48 H                                 00001                   08
     * 49 I                                 00001                   09
     * 4A J                                 00001                   0A
     * 4B K                                 00001                   0B
     * 4C L                                 00001                   0C
     * 4D M                                 00001                   0D
     * 4E N                                 00001                   0E
     * 4F O                                 00001                   0F
     * 50 P                                 00001                   10
     * 51 Q                                 00001                   11
     * 52 R                                 00001                   12
     * 53 S                                 00001                   13
     * 54 T                                 00001                   14
     * 55 U                                 00001                   15
     * 56 V                                 00001                   16
     * 57 W                                 00001                   17
     * 58 X                                 00001                   18
     * 59 Y                                 00001                   19
     * 5A Z                                 00001                   1A
     * **********************************************************************
     */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (ToggleButton) findViewById(R.id.button);
        mText = (TextView) findViewById(R.id.logText);
        mScroller = (ScrollView) findViewById(R.id.scroller);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];
        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];
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
            SystemClock.sleep(1000);
            led.write(true);
            SystemClock.sleep(1000);
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
            log(Integer.toHexString(fingerCode));
            switch (fingerCode)
            {
                case 1:
                    log("A");
                    break;
                case 2:
                    log("B");
                    break;
                case 3:
                    log("C");
                    break;
                case 4:
                    log("D");
                    break;
                case 5:
                    log("E");
                    break;
                case 6:
                    log("F");
                    break;
                case 7:
                    log("G");
                    break;
                case 8:
                    log("H");
                    break;
                case 9:
                    log("I");
                    break;
                case 0X10:
                    log("J");
                    break;
                case 0X11:
                    log("K");
                    break;
                case 0X12:
                    log("L");
                    break;
                case 0X13:
                    log("M");
                    break;
                case 0X14:
                    log("N");
                    break;
                case 0X15:
                    log("O");
                    break;
                case 0X16:
                    log("P");
                    break;
                case 0X17:
                    log("Q");
                    break;
                case 0X18:
                    log("R");
                    break;
                case 0X19:
                    log("S");
                    break;
                case 0X20:
                    log("T");
                    break;
                case 0X21:
                    log("U");
                    break;
                case 0X22:
                    log("V");
                    break;
                case 0X23:
                    log("W");
                    break;
                case 0X24:
                    log("X");
                    break;
                case 0X25:
                    log("Y");
                    break;
                case 0X26:
                    log("Z");
                    break;
                case 0X27:
                    log("?");
                    break;
                case 0X28:
                    log(".");
                    break;
                case 0X29:
                    log("!");
                    break;
                case 0X30:
                    log("B");
                    break;
                case 0X31:
                    log("SPEAK");
                    break;
                case 0X32:
                    log("SPACE");
                    break;
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
                mText.append(msg);
                mText.append("\n");
                mScroller.smoothScrollTo(0, mText.getBottom());
            }
        });
    }

    public void onSensorChanged(SensorEvent event)
    {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                for (int i = 0; i < 3; i++)
                {
                    valuesAccelerometer[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (int i = 0; i < 3; i++)
                {
                    valuesMagneticField[i] = event.values[i];
                }
                break;
        }

        boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
                valuesAccelerometer, valuesMagneticField);
        log(success + "  success");
        if (success)
        {
            SensorManager.getOrientation(matrixR, matrixValues);
            synchronized (this)
            {
                azimuth = Math.toDegrees(matrixValues[0]);
                pitch = Math.toDegrees(matrixValues[1]);
                roll = Math.toDegrees(matrixValues[2]);
            }
        }
    }

    public synchronized double getAzimuth()
    {
        return azimuth;
    }

    public synchronized double getPitch()
    {
        return pitch;
    }

    public synchronized double getRoll()
    {
        return roll;
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

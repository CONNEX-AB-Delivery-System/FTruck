package example;

import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import ev3dev.actuators.lego.motors.EV3MediumRegulatedMotor;
import ev3dev.sensors.Battery;
import ev3dev.sensors.ev3.EV3ColorSensor;
import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class MyFirstRobot {

    private static int LineReaderS(int colorSampleRed, int colorSampleGreen, int colorSampleBlue) {

        int calibrationValue = 60;
        int steerAngle = 0;

        float average = ((colorSampleRed + colorSampleGreen + colorSampleBlue) / 3) * 100 / 255;
        int sensorValue = Math.round(average);
        sensorValue = sensorValue - calibrationValue;

        System.out.println("EV3-Forklift: R:" + colorSampleRed + " G:" + colorSampleGreen + " B:" + colorSampleBlue
                + "SensorValue:" + sensorValue);

        if (sensorValue >= -10 && sensorValue < 10) {
            steerAngle = 0;
        }
        if (sensorValue >= 10 && sensorValue < 30) {
            steerAngle = 100;
        }
        if (sensorValue >= -30 && sensorValue < -10) {
            steerAngle = -100;
        }
        if (sensorValue >= 30) {
            steerAngle = 200;
        }
        if (sensorValue <= -30) {
            steerAngle = -200;
        }

        return steerAngle;
    }

    private static void Run () {

    }

    public static void main(final String[] args){

        System.out.println("EV3-Forklift: Creating Motor A - Lifting function");
        final EV3LargeRegulatedMotor motorLift = new EV3LargeRegulatedMotor(MotorPort.A);
        System.out.println("EV3-Forklift: Creating Motor B - Tilting function");
        final EV3MediumRegulatedMotor motorTilt = new EV3MediumRegulatedMotor(MotorPort.B);
        System.out.println("EV3-Forklift: Creating Motor C - Driving function");
        final EV3MediumRegulatedMotor motorDrive = new EV3MediumRegulatedMotor(MotorPort.C);
        System.out.println("EV3-Forklift: Creating Motor D - Steering function");
        final EV3MediumRegulatedMotor motorSteer = new EV3MediumRegulatedMotor(MotorPort.D);
        final EV3TouchSensor touchLift = new EV3TouchSensor(SensorPort.S1);
        final EV3ColorSensor palletReader = new EV3ColorSensor(SensorPort.S3);
        final EV3ColorSensor lineReader = new EV3ColorSensor(SensorPort.S4);

        //To Stop the motor in case of pkill java for example
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("Emergency Stop");
                motorLift.stop();
                motorTilt.stop();
                motorDrive.stop();
                motorSteer.stop();
            }
        }));

        System.out.println("EV3-Forklift: Defining the Stop mode");
        motorLift.brake();
        /*motorTilt.brake();
        motorDrive.brake();
        motorSteer.brake();*/

        SampleProvider palletSample = palletReader.getRGBMode();
        int palletSampleSize = palletSample.sampleSize();
        float[] palletSampleColor = new float[palletSampleSize];
        palletSample.fetchSample(palletSampleColor, 0);

        int palletColorSampleRed = (int) palletSampleColor[0];
        int palletColorSampleGreen = (int) palletSampleColor[1];
        int palletColorSampleBlue = (int) palletSampleColor[2];

        float palletAverage = (palletColorSampleRed + palletColorSampleGreen + palletColorSampleBlue) / 3;

        System.out.println("EV3-Forklift: palletReflect:" + palletAverage);

        //lift pallet
        System.out.println("EV3-Forklift: Lift up");
        final int motorLiftSpeed = 300; //- for lifting up, + for lifting down
        motorLift.setSpeed(motorLiftSpeed);
        int liftAngle = -1500;
        motorLift.rotateTo(liftAngle, true);
        Delay.msDelay(3000);

        //drive until red
        final int motorSteerSpeed = 500; //+ for rotating to ?, - for rotating to right
        motorSteer.setSpeed(motorSteerSpeed);

        boolean stopFlag = false;
        int count = 0;
        int steerAngle;

        System.out.println("EV3-Forklift: Drive forward");
        final int motorDriveSpeed = 150; //+ for driving forward, - for driving backward
        motorDrive.setSpeed(motorDriveSpeed);
        motorDrive.forward();

        while (!stopFlag) {
            SampleProvider colorSample = lineReader.getRGBMode();
            int sampleSizeColor = colorSample.sampleSize();
            float[] sampleColor = new float[sampleSizeColor];
            colorSample.fetchSample(sampleColor, 0);
            int colorSampleRed = (int)sampleColor[0];
            int colorSampleGreen = (int)sampleColor[1];
            int colorSampleBlue = (int)sampleColor[2];

            steerAngle = LineReaderS(colorSampleRed, colorSampleGreen, colorSampleBlue);
            motorSteer.rotateTo(steerAngle, true);

            System.out.println("EV3-Forklift: R:" + " steerAngle:" + steerAngle);


            if (colorSampleRed > 300 && colorSampleGreen < 100 && colorSampleBlue < 100)
            {
                motorDrive.stop();
                stopFlag = true;
            }

            count = count + 1;
            if (count == 200) {
                stopFlag = true;
                motorDrive.stop();
            }

            Delay.msDelay(10);
        }


        System.out.println("EV3-Forklift: R:" + " end sequence");

        motorSteer.rotateTo(0, true);

        //lower pallet
        liftAngle = -1000;
        motorLift.rotateTo(liftAngle, true);
        Delay.msDelay(2000);

        System.out.println("EV3-Forklift: R:" + " end sequence 2");

        //drive backward
        motorDrive.backward();
        Delay.msDelay(200);


        //reset status
        motorSteer.rotateTo(0, true);
        motorLift.rotateTo(0, true);
        Delay.msDelay(5000);

        palletSample = palletReader.getRGBMode();
        palletSampleSize = palletSample.sampleSize();
        palletSampleColor = new float[palletSampleSize];
        palletSample.fetchSample(palletSampleColor, 0);

        palletColorSampleRed = (int) palletSampleColor[0];
        palletColorSampleGreen = (int) palletSampleColor[1];
        palletColorSampleBlue = (int) palletSampleColor[2];

        palletAverage = (palletColorSampleRed + palletColorSampleGreen + palletColorSampleBlue) / 3;

        System.out.println("EV3-Forklift: palletReflect:" + palletAverage);

       /* final int motorSteerSpeed = 500; //+ for rotating to ?, - for rotating to right
        motorSteer.setSpeed(motorSteerSpeed);
        motorSteer.rotateTo(-300, true);
        Delay.msDelay(2000);
        motorSteer.rotateTo(0, true);
        Delay.msDelay(2000); */

        /*int[] steer = example.LineFollower.motorsSpeed(colorSampleRed, colorSampleGreen, colorSampleBlue);

        //tilt up
        /*System.out.println("EV3-Forklift: Tilt up");
        final int motorTiltSpeed = 300; //+ for tilting down, - for tilting up
        motorTilt.setSpeed(motorTiltSpeed);
        motorTilt.backward();
        Delay.msDelay(5000);
        motorTilt.stop();

        //lift up
        System.out.println("EV3-Forklift: Lift up");
        final int motorLiftSpeed = 300; //- for lifting up, + for lifting down
        motorLift.setSpeed(motorLiftSpeed);
        motorLift.backward();
        Delay.msDelay(7000);
        motorLift.stop();


        //rotate steering
        System.out.println("EV3-Forklift: Rotate Steering");
        final int motorSteerSpeed = 500; //+ for rotating to ?, - for rotating to right
        motorSteer.setSpeed(motorSteerSpeed);
        motorSteer.forward();
        Delay.msDelay(2000);
        motorSteer.stop();

        //drive forward
        System.out.println("EV3-Forklift: Drive forward");
        final int motorDriveSpeed = 300; //+ for driving forward, - for driving backward
        motorDrive.setSpeed(motorDriveSpeed);
        motorDrive.forward();
        Delay.msDelay(2000);
        motorDrive.stop();

        //return steering
        System.out.println("EV3-Forklift: Return steering");
        motorSteer.setSpeed(motorSteerSpeed);
        motorSteer.backward();
        Delay.msDelay(2000);
        motorSteer.stop();

        //tilt down
        System.out.println("EV3-Forklift: Tilt down");
        motorTilt.setSpeed(motorTiltSpeed);
        motorTilt.forward();
        Delay.msDelay(5000);
        motorTilt.stop();

        //lift down
        System.out.println("EV3-Forklift: Lift down");
        motorLift.setSpeed(motorLiftSpeed);
        motorLift.forward();
        while (!touchLift.isPressed()) {
            Delay.msDelay(100);
        }
        motorLift.stop();

        */





        System.out.println("Checking Battery");
        System.out.println("Votage: " + Battery.getInstance().getVoltage());

        System.exit(0);
    }
}

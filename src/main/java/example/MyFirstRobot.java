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


    private static int LineReader() {
        //TODO: PLACE YOUR CODE HERE

        return 0;
    }

    private static int steerAngle() {
        //TODO: PLACE YOUR CODE HERE

        return 0;
    }

    private static int move() {
        //TODO: PLACE YOUR CODE HERE

        return 0;
    }


    private static void FTruckRun () {

        move();
       //TODO: PLACE YOUR CODE HERE

    }


    //motors
    public static EV3LargeRegulatedMotor motorLift;
    public static EV3MediumRegulatedMotor motorTilt;
    public static EV3MediumRegulatedMotor motorDrive;
    public static EV3MediumRegulatedMotor motorSteer;

    //sensors
    public static EV3TouchSensor touchLift;
    public static EV3ColorSensor palletReader;
    public static EV3ColorSensor lineReader;

    public static void main(final String[] args){

        double minVoltage = 7.200;

        //Always check if battery voltage is enougth
        System.out.println("Battery Voltage: " + Battery.getInstance().getVoltage());
        System.out.println("Battery Current: " + Battery.getInstance().getBatteryCurrent());
        if (Battery.getInstance().getVoltage() < minVoltage) {
            System.out.println("Battery voltage to low. Shutdown down and change the batteries.");
            System.exit(0);
        }

        System.out.println("EV3-Forklift: Creating Motor A - Lifting function");
        motorLift = new EV3LargeRegulatedMotor(MotorPort.A);
        System.out.println("EV3-Forklift: Creating Motor B - Tilting function");
        motorTilt = new EV3MediumRegulatedMotor(MotorPort.B);
        System.out.println("EV3-Forklift: Creating Motor C - Driving function");
        motorDrive = new EV3MediumRegulatedMotor(MotorPort.C);
        System.out.println("EV3-Forklift: Creating Motor D - Steering function");
        motorSteer = new EV3MediumRegulatedMotor(MotorPort.D);
        touchLift = new EV3TouchSensor(SensorPort.S1);
        palletReader = new EV3ColorSensor(SensorPort.S3);
        lineReader = new EV3ColorSensor(SensorPort.S4);

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
        motorTilt.brake();
        motorDrive.brake();
        motorSteer.brake();



        //Main class for executing code
        FTruckRun();

        


        System.out.println("Checking Battery");
        System.out.println("Votage: " + Battery.getInstance().getVoltage());

        System.exit(0);

    }
}

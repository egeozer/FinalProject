<<<<<<< HEAD
=======




>>>>>>> 8c205efcc63da1d9df423b8a6056c5d0e7f96fc9
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private Navigation navi;
	private SampleProvider colorSensor;
	private float[] colorData;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	double lightSensorDist = 3.0; 		//distance added to compensate the distance from the center and to the lightSensor
	double x, y, xTheta, yTheta;
	double eucDistance, heading;
	int axisCounter;
	
	
	public LightLocalizer(Odometer odo, Navigation navi, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.navi = navi;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		Sound.setVolume(50);

		
	}
	
	public void doLocalization(Odometer odo, Navigation navi, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.navi = navi;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		double pointA = 0;		//distances that we need to record for the calculation of 0,0
		double pointB = 0;
		
		leftMotor.setSpeed(100);
		rightMotor.setSpeed(100);
		
		leftMotor.forward();		//robot goes forward until it sees a black line
		rightMotor.forward();
		
		while(true){
			
			colorSensor.fetchSample(colorData, 0);
			if(colorData[0]<0.3){	//if the robot crosses the black line, it will get the distance, pointA(X value from center to black line)
				Sound.beep();
				navi.goForward(lightSensorDist);
			
				leftMotor.stop();
				rightMotor.stop();
				pointA = odo.getX();
				navi.goBackward(pointA);	//once the first distance is recorded, it goes back where it started 
				break;

			}
		
		}
		navi.turnTo(83,false);	//we have an offset of approximately 7 degrees, due to our track being wider
	
		
		leftMotor.forward();
		rightMotor.forward();
	while(true){
			
			colorSensor.fetchSample(colorData, 0);	//second part where it first got pointA, now pointB will be obtained(Y value from center to black line)
			if(colorData[0]<0.3){
				Sound.beep();
				navi.goForward(lightSensorDist);
			
				leftMotor.stop();
				rightMotor.stop();
				pointB = odo.getY();
				navi.goBackward(pointB);
				break;

			}		
		
		}
	//once everything is collected, the odometer is set to the updated position and now we can call it to go to 0,0,0 without any problem
	
	odo.setPosition(new double [] {-(pointA+pointB)/2, -(pointB+pointA)/2, odo.getAng()}, new boolean [] {true, true, true});
	
	navi.travelTo(0,0);
	navi.turnTo(0,true);

	}
		
}
<<<<<<< HEAD
		
=======
		
		
	
>>>>>>> 8c205efcc63da1d9df423b8a6056c5d0e7f96fc9

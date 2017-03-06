


import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 100;

	private Odometer odo;
	private Navigation navi;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private double critDist = 45.0;				// measured distance from the wall
	private double noiseDistFE = 0.0;			// experimentally determined noise margin (falling edge)
	private double noiseDistRE = 2.0;			// experimentally determined noise margin (rising edge)
	private double prevDist  = 0;
	
	public USLocalizer(Odometer odo, Navigation navi, SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.navi = navi;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	}
	
	public void doLocalization() {
		
		double angleA, angleB, deltaAngle, heading;
		
		// get access to motors and set their speed
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
		// falling edge mode
		if (locType == LocalizationType.FALLING_EDGE) {			
			
			// rotate the robot until it sees no wall
			leftMotor.forward();
			rightMotor.backward();
		
			while(true){
				if(getFilteredData() > critDist){
					break;
				}
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			while(true){
				if(getFilteredData() <= critDist){
					angleA = odo.getAng();
					leftMotor.stop();
					rightMotor.stop();
					break;
				}
			}
			
			// switch direction and wait until it sees no wall
			leftMotor.backward();
			rightMotor.forward();
		
			while(true){
				if(getFilteredData() > critDist){
					break;
				}
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			while(true){
				if(getFilteredData() <= critDist){
					angleB = odo.getAng();
					leftMotor.stop();
					rightMotor.stop();
					break;
				}
			}
				
			// calculate the required heading change and update the heading
			if (angleA > angleB){
				deltaAngle = 225 - (angleA + angleB)/2;
			}
			else{
				deltaAngle =  45 - (angleA + angleB)/2;
			}
			
			heading = deltaAngle + angleB;
				
			// update the odometer position
			odo.setPosition(new double [] {0.0, 0.0, heading}, new boolean [] {true, true, true});
			navi.turnTo(0, true);
		}
		// rising edge mode
		else{				
			
			// rotate the robot until it sees the wall
			leftMotor.forward();
			rightMotor.backward();
			
			while(true){
				if(getFilteredData() <= critDist){
					break;
				}
			}
			
			// keep rotating until the robot sees no wall, then latch the angle
			while(true){
				if(getFilteredData() > critDist){
					angleA = odo.getAng();
					leftMotor.stop();
					rightMotor.stop();
					break;
				}
			}
			
			// switch direction and wait until it sees the wall
			leftMotor.backward();
			rightMotor.forward();
		
			while(true){
				if(getFilteredData() <= critDist){
					break;
				}
			}
			
			// keep rotating until the robot sees no wall, then latch the angle
			while(true){
				if(getFilteredData() > critDist){
					angleB = odo.getAng();
					leftMotor.stop();
					rightMotor.stop();
					break;
				}
			}

			// calculate the required heading change and update the heading
			if (angleA > angleB){
				deltaAngle = 225 - (angleA + angleB)/2;
			}
			else{
				deltaAngle =  45 - (angleA + angleB)/2;
			}
			
			heading = deltaAngle + angleA;
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, heading-13}, new boolean [] {true, true, true});
			navi.turnTo(0, true);
		}
	}
	
	private float getFilteredData() {
		
		usSensor.fetchSample(usData, 0);
		float distance =100* usData[0];
		int filterControl = 0;
		
		if (distance >= 255 && filterControl < 25) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} 
		else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			prevDist = distance;
		} 	
		else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			prevDist = distance;
		}
						
		return distance;
	}
	
}

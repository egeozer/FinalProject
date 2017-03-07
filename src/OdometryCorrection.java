/* 
 * OdometryCorrection.java
 */

import java.util.Arrays;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private EV3ColorSensor sensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
	private SampleProvider sampleProvider;
	private Odometer odometer;
	
	//private double gridPositionX = 0;
	//private double gridPostionY = 0;
	//private double gridSize = 30.48; 
	
	// TODO: We will need to initialize values for the axisSquares depending on which corner we start in
	private double xAxisSquares;
	private double yAxisSquares;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		sensor.setFloodlight(true);
	}

	// local variables
	public double theta;
	public double x,y;
	public int beepCounter;
	public double gridSize = 30.48;
	public double tempX, tempY;
		
	
	
	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		//define int for size of data array based on sensor sample size
		int dataSize = sensor.sampleSize();
		Sound.setVolume(10);
				
		while (true) {
			correctionStart = System.currentTimeMillis();

			// put your correction code here
			
			sampleProvider=sensor.getRedMode();
			
			//create array to store sensor data and then retrieve data
			float sensorData[] = new float[dataSize];
			
			sampleProvider.fetchSample(sensorData, 0);

			LCD.drawString("Color: " + Arrays.toString(sensorData), 0, 3);
			
			if(sensorData[0] < .3 ) {
				
				Sound.beep();
				theta = odometer.getAng();
				//beepCounter++;
				
				// crossing North grid line of a square
				if(theta < 110 && theta > 70 ) {
					yAxisSquares++;
					odometer.setY(yAxisSquares*gridSize);
				}	
				
				// crossing South grid line of a square
				else if (theta < 290 && theta > 250 ) {
					yAxisSquares--;
					odometer.setY(yAxisSquares*gridSize);
				}
				
				// crossing West grid line of a square
				else if (theta < 200 && theta > 160 ) {
					xAxisSquares--;
					odometer.setX(xAxisSquares*gridSize);
				}
				
				// crossing East grid line of a square
				else if (theta < 20 && theta > 340 ) {
					xAxisSquares++;
					odometer.setX(xAxisSquares*gridSize);
				}
				
								
				/*if(beepCounter == 1 || beepCounter == 4 || beepCounter == 7 || beepCounter == 10){		// when the robot exits the 
					//theta = odometer.getTheta();														// corner squares
					x = odometer.getX();																
					y = odometer.getY();
				}
				
				if(beepCounter > 1 && beepCounter < 4){			// as the robot travels up along the first grid side (+y)
					tempY = y;
					odometer.setY(tempY + gridSize);
					y = odometer.getY();
				}
				
				if(beepCounter > 4 && beepCounter < 7){			//as the robot travels right along the second grid side (+x)
					tempX = x;
					odometer.setX(tempX + gridSize);
					x = odometer.getX();
				}
				
				if(beepCounter > 7 && beepCounter < 10){		// as the robot travels down along the third grid side (-y)
					tempY = y;
					odometer.setY(tempY - gridSize);
					y = odometer.getY();
				}
				
				if(beepCounter > 10 && beepCounter < 13){		// as the robot travels left along the fourth grid side (-x)
					tempX = x;
					odometer.setX(tempX - gridSize);
					x = odometer.getX();
				}*/
				
				
			}
						
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}
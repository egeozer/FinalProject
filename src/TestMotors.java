package motorTest;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.Button;
import lejos.hardware.lcd.TextLCD;

public class TestMotors {
	
	public static final EV3LargeRegulatedMotor slowMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	// Left motor is connected to output A
	public static final EV3LargeRegulatedMotor fastMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	// Right motor is connected to output D
	public static int buttonC;
	// Instance the text display
	final static TextLCD t = LocalEV3.get().getTextLCD();
		
		public static void main(String[] args) 
		{
			
			slowMotor.setAcceleration(300);
			fastMotor.setAcceleration(300);
			
			slowMotor.setSpeed(500);
			fastMotor.setSpeed(fastMotor.getMaxSpeed());
			
			while(true)
			{
				
				// Get the position of the target from the user
				buttonC = getButtonChoise();
			
			
				if (buttonC == Button.ID_LEFT)
					slowMotor.rotate(-450);
				
				if (buttonC == Button.ID_RIGHT)
					slowMotor.rotate(450);
				
				if (buttonC == Button.ID_UP)
					fastMotor.rotate(-150);
					
				if (buttonC == Button.ID_DOWN)
					fastMotor.rotate(90);
					
				if (buttonC == Button.ID_ESCAPE)
					return;
			}
		}
		
		public static int getButtonChoise()
		{	
			// holds the user's button choice
			int buttonChoice;
			
			// poll the button input until either the up, left or right button is pressed
			do {
				
				// clear the display
				t.clear();

				// ask the user whether the target is to the left, the right or in the center
				t.drawString("< Left | Right >", 0, 0);
				t.drawString("       |        ", 0, 1);
				t.drawString(" ______|_______ ", 0, 2);
				t.drawString("  ---  UP  ---  ", 0, 3);
				t.drawString("      DOWN      ", 0, 4);

				buttonChoice = Button.waitForAnyPress();
			} while (buttonChoice != Button.ID_LEFT
					&& buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice!= Button.ID_DOWN
					&& buttonChoice != Button.ID_ESCAPE);
			
			// return the button pressed by the user
			return buttonChoice;
		}
}

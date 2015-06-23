import processing.core.*;
import java.awt.event.*;


public class RadioBox {
	
	/*
	 * Author: 		Eric Mustee
	 * Date:		Summer 2012 and 5/1/2013
	 * Description: This class creates a simple radio button. This class creates a
	 *				simple radioBox that toggles between all other radio boxes
	 *				defined in the sequence aligner class. This class was orignally
	 *				a checkbox.
	 */
	
	private float x, y;
	private String radioBoxText;
	private boolean status;
	private PApplet parent;
	
	private final static int RADIOBOX_SIZE = 20;
	
	
	public RadioBox(float x, float y, String radioBoxText, boolean status, PApplet parent)
	{
		this.x = x;
		this.y = y;
		this.radioBoxText = radioBoxText;
		this.status = status;
		this.parent = parent;
		parent.registerMouseEvent(this);
	}
	
	// This method draws the RadioBox
	public void draw()
	{
		parent.fill(parent.color(255,255,255,0));
		parent.stroke(0);
		parent.rect(x,y,RADIOBOX_SIZE,RADIOBOX_SIZE);
		if(status)
		{
			parent.line(x, y, x + RADIOBOX_SIZE, y + RADIOBOX_SIZE);
			parent.line(x + RADIOBOX_SIZE, y, x, y + RADIOBOX_SIZE);

		}
		
		parent.fill(0);
		
		parent.textSize(16);
		parent.text(radioBoxText, x + RADIOBOX_SIZE + 4, y + RADIOBOX_SIZE / 4 + 14);
	}
	
	//  This method gets the state of the box
	public boolean getState()
	{
		return status;
	}
	
	// This method toggles the state of the box
	public void toggleState()
	{
		if (status)
		{
			status = false;
		}
		else
		{
			status = true;
		}
	}
	
	// This method sets the state of the box
	public void setState(boolean state)
	{
		status = state;
	}
	
	// This method sets the text of the box
	public void setRadioBoxText(String text)
	{
		radioBoxText = text;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	
	// This method handles all the radioBox's mouse events
	public void mouseEvent(MouseEvent event)
	{
		
		if (event.getID() == MouseEvent.MOUSE_RELEASED)
		{
			SequenceAligner SA = (SequenceAligner)parent;
			
			// These scary comparison makes sure the box was pressed
			if (((parent.mouseX >= x) && (parent.mouseX <= x + RADIOBOX_SIZE)
					&& (parent.mouseY >= y) && (parent.mouseY <= y + RADIOBOX_SIZE)) &&
					((SA.getMousePressedX() >= x) && (SA.getMousePressedX() <= x + RADIOBOX_SIZE)
							&& (SA.getMousePressedY() >= y) && (SA.getMousePressedY() <= y + RADIOBOX_SIZE)))
			{
				
				SA.uncheckRadioBoxes();
				setState(true);
				SA.rescore();
			}	
		}
	}
	

}



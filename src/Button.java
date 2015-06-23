import processing.core.*;

public class Button
{
	/*
	 * Author: 		Eric Mustee
	 * Date:		Summer 2012
	 * Description: This button class creates a button with a simple image.
	 * 				This image supplied should be about 16x16. 
	 */
	private final static int height = 25;
	private float x, y;
	private PApplet parent;
	private String text;
	private boolean active = true, selected = false;
	private int state = 0;
	private PImage image;

	public Button(float x, float y, String text, PImage image, PApplet parent)
	{
		this.x = x;
		this.y = y;
		this.text = text;
		this.parent = parent;
		this.image = image;
	}

	// This method draws the button
	public void draw()
	{
		// Find out the state of the button. If the button is disabled,
		// do not draw it.
		if (!active)
			return;
		
		// Determine the state of the button and draw it.
		if (contains(parent.mouseX, parent.mouseY))
			state = 1;
		else
			state = 0;

		if (state == 1 && parent.mousePressed)
		{
			state = 2;
			selected = true;
		}

		if (!parent.mousePressed)
		{
			selected = false;
		}

		switch (state)
		{
		case 0:
			if (selected)
			{
				// Mouse Over Case
				drawBox(parent.color(239, 228, 176), parent.color(0));
			}
			else
			{
				// Default Case
				drawBox(parent.color(210), parent.color(0));
			}
			break;
		case 1:
			// Mouse Over Case
			drawBox(parent.color(239, 228, 176), parent.color(0));
			break;
		case 2:
			// Mouse Pressed Case
			drawBox(parent.color(249, 238, 186), parent.color(0, 50, 255));
			break;
		}
	}

	public void setState(boolean state)
	{
		this.active = state;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}

	// This method draws the button box and border with the supplied colors. 
	private void drawBox(int backColor, int borderColor)
	{
		parent.textSize(16);
		parent.fill(backColor);
		parent.stroke(borderColor);
		parent.rect(x, y, getWidth(), getHeight());
		parent.fill(0);
		parent.text(text, x + 2 + parent.textWidth(text) / 2 - parent.textWidth(text) / 2, y + 16);
		parent.image(image, x + parent.textWidth(text) + 5, y + 4);
	}

	// This method returns a point is contained within the button
	public boolean contains(float cX, float cY)
	{
		return ((cX >= x && cX <= x + getWidth() && cY >= y && cY <= y + getHeight()) && active);
	}

	public float getWidth()
	{
		parent.textSize(16);
		return parent.textWidth(text) + 25;
	}

	public float getHeight()
	{
		return height;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.x = y;
	}

	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public PImage getImage()
	{
		return image;
	}

	public void setImage(PImage image)
	{
		this.image = image;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public String getText()
	{
		return text;
	}
}

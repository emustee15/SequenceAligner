import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import processing.core.*;

import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

public class SequenceAligner extends PApplet
{
	private int initialTimer = 500;
	private boolean initialize = true;
	private SubstitutionMatrix matrix;
	private Button loadMatrixButton, loadSequenceButton, tableButton, copyButton;
	private JFileChooser openDialog;
	private boolean loadMatrix = false;
	private boolean loadSequence = false;
	private RadioBox semiglobalRadioBox, globalRadioBox;
	private PFont defaultFont, monospaceFont;
	private Sequence A, B;
	private String matched;
	private int displayY = 0;
	private boolean startDrag = false;
	private Scorer scorer = new Scorer(this, matrix);
	private ScoreTable table = new ScoreTable();
	private boolean showTable = false;
	private int maxDisplayY = 0;
	private int mousePressedX;
	private int mousePressedY;

	/*
	 * Authors:		Eric M., Matt N, Brendan M.
	 * Date:		5/1/2013
	 * Descriptoin: This application allows for pairwise sequence alignment
	 *				using the Needleman–Wunsch algorithm. All images were
	 *				created by us.
	 */
	public static void main(String[] args)
	{
		// Start the PApplet.
		PApplet.main(new String[] { "SequenceAligner" });

	}

	public void setup()
	{
		// Make sure this isn't an applet. If it is, exit.
		try
		{
			frame.setTitle("Sequence Aligner");
			ImageIcon icon = new ImageIcon(loadBytes("Icon.png"));
			frame.setIconImage(icon.getImage());
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Error: Please run as an application.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		// Load the fonts and set Cambria as the default font.
		defaultFont = loadFont("Cambria.vlw");
		monospaceFont = loadFont("Monospace.vlw");
		textFont(defaultFont, 24);

		// Create the buttons
		loadMatrixButton = new Button(5, 5, "Load Substitution Matrix", loadImage("matrixButton.png"), this);
		loadSequenceButton = new Button(10 + loadMatrixButton.getWidth(), 5, "Load Sequence File", loadImage("SequenceButton.png"), this);
		tableButton = new Button(15 + loadMatrixButton.getWidth() + loadSequenceButton.getWidth(), 5, "Show Scoring Table", loadImage("TableButton.png"), this);
		copyButton = new Button(20 + loadMatrixButton.getWidth() + loadSequenceButton.getWidth() + tableButton.getWidth(), 5, "Copy",
				loadImage("copyButton.png"), this);

		// Add a mouse event listener so we can use the scroll wheel on
		// the mouse
		addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent mwe)
			{
				mouseWheel(mwe.getWheelRotation());
			}
		});

		// Set up our open file dialog box.
		FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
		openDialog = new JFileChooser();
		openDialog.addChoosableFileFilter(imageFilter);
		openDialog.setFileFilter(imageFilter);
		semiglobalRadioBox = new RadioBox(5, 35, "Semi-global (Needleman-Wunsch)", true, this);
		globalRadioBox = new RadioBox(270, 35, "Global (Needleman-Wunsch)", false, this);

		// Set the size to 600 x 400.
		size(600, 400);
	}

	public void draw()
	{
		// Clear the screen by setting the background to
		// RGB 245,245,245.
		background(245);

		// Display the cool intro credits if necessary
		if (initialize)
		{
			initialize();
		}

		// If certain button presses aren't handled from the draw method, the
		// program usually crashes for some reason. Here, we call the methods
		// to load the file matrix and sequence files, and show the scoring
		// table if necessary.

		if (loadMatrix)
		{
			loadSubstitutionFile();
		}
		if (loadSequence)
		{
			loadSequenceFile();
		}
		if (showTable)
		{
			table.toggleVisibility();
			showTable = false;

			// Update the button's text as the table open and closes.
			if (table.isVisible())
			{
				tableButton.setText("Hide Scoring Table");
			}
			else
			{
				tableButton.setText("Show Scoring Table");
			}

			updateButtonPositions();
		}

		// Draw the output text if the score is valid, otherwise show
		// some useful tips.
		if (scorer.isValid())
		{
			drawText(A.getAllignedSequence(), 0);
			drawText(matched, 1);
			drawText(B.getAllignedSequence(), 2);
		}
		else
		{
			drawText("Please load a valid sequence file.", 0);
			drawText("The file should be formated like this:", 3);
			drawText("Line 1: Misaligned Start Penalty", 4);
			drawText("Line 2: Gap Penalty", 5);
			drawText("Line 3: Match Score (Without Matrix)", 6);
			drawText("Line 4: Mismatch Score (Without Matrix)", 7);
			drawText("Line 5: Name of Sequence A", 8);
			drawText("Line 6: Name of Sequence B", 9);
			drawText("Line 7: Sequence A", 10);
			drawText("Line 8: Sequence B", 11);
			drawText("All penalties/scores should be positive.", 12);

			if (matrix != null)
			{
				drawText("Or try unloading the matrix file.", 1);
			}
		}

		// Draw a border so the text and buttons don't overlap.
		stroke(0);
		line(0, 70, width, 70);
		stroke(255, 255, 255, 0);
		fill(235);
		rect(0, 0, width, 70);

		// Draw all the GUI elements
		loadMatrixButton.draw();
		loadSequenceButton.draw();
		semiglobalRadioBox.draw();
		globalRadioBox.draw();
		tableButton.draw();
		copyButton.draw();

	}

	// This method handles the fancy intro
	public void initialize()
	{
		textSize(24);
		fill(0, 0, 0, initialTimer);
		text("Sequence Aligner\nBy: Eric M., Matt N., and Brendan M.", 5, height - 45);
		initialTimer -= 2;

		if (initialTimer <= 0)
		{
			initialize = false;
		}

	}

	@Override
	public void mousePressed()
	{
		mousePressedX = mouseX;
		mousePressedY = mouseY;
	}

	// This method checks to see any of the buttons has been clicked. If they are,
	// a boolean is flagged and the draw method will handle it.
	@Override
	public void mouseReleased()
	{
		if (loadMatrixButton.contains(mouseX, mouseY) && loadMatrixButton.contains(mousePressedX, mousePressedY))
		{
			loadMatrix = true;
		}
		if (loadSequenceButton.contains(mouseX, mouseY) && loadSequenceButton.contains(mousePressedX, mousePressedY))
		{
			loadSequence = true;
		}
		if (tableButton.contains(mouseX, mouseY) && tableButton.contains(mousePressedX, mousePressedY))
		{
			showTable = true;
		}
		if (copyButton.contains(mouseX, mouseY) && copyButton.contains(mousePressedX, mousePressedY))
		{
			putOnClipBoard();
		}
		startDrag = false;
	}

	// These two methods are used in the radioBox class to make sure the
	// the user presses and releases in the same radioBox.
	public int getMousePressedX()
	{
		return mousePressedX;
	}

	public int getMousePressedY()
	{
		return mousePressedY;
	}

	// This method loads in a substitution matrix and removes
	// the substitution matrix if it already exists.
	public void loadSubstitutionFile()
	{
		if (matrix == null)
		{
			File file;
			if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				file = openDialog.getSelectedFile();
				if (file.isFile())
				{
					matrix = new SubstitutionMatrix(file);
					loadMatrixButton.setText("Remove Loaded Matrix");
					updateButtonPositions();
					rescore();
					if (scorer.isValid())
					{
						table.createTable(scorer.getScores(), scorer.getSequenceA().getSequence(), scorer.getSequenceB().getSequence());

					}
				}
			}
		}
		else
		{
			loadMatrixButton.setText("Load Substitution Matrix");
			updateButtonPositions();
			matrix = null;
			rescore();
		}

		loadMatrix = false;
	}

	// This method loads in a new sequence file. 
	public void loadSequenceFile()
	{
		File file;
		if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			file = openDialog.getSelectedFile();
			if (file.isFile())
			{
				scorer.loadSequenceData(file);
				rescore();

			}
		}

		loadSequence = false;
	}

	// Put any radioboxes to be unchecked that are put of the radio box group.
	public void uncheckRadioBoxes()
	{
		semiglobalRadioBox.setState(false);
		globalRadioBox.setState(false);
	}

	// This method returns an integer representation of what radiobox is selected. This is
	// used in the scorer class to determine the scoring algorithm.
	public int getAlignmentState()
	{
		if (semiglobalRadioBox.getState())
		{
			return 2;
		}
		else
		{
			return 3;
		}
	}

	// This method draws text to the screen. The position is what line to draw it at.
	public void drawText(String line, int position)
	{
		StringBuffer output = new StringBuffer("");
		for (int index = 0; index < position; index++)
		{
			output.append("\n");
		}

		int lineSize = 56;
		for (int index = 0; index < line.length(); index += lineSize)
		{
			if (index + lineSize > line.length())
			{
				lineSize = line.length() - index;
			}
			output.append(line.substring(index, index + lineSize));
			output.append("\n");
			output.append("\n");
			output.append("\n");
		}

		fill(0);
		textFont(monospaceFont, 16);
		text(output.toString(), 5, 90 + displayY - position * 10);

		textFont(defaultFont);

	}

	// This method scrolls the text according to how the user;
	// scrolls the mouse wheel.
	public void mouseWheel(int delta)
	{
		displayY -= delta * 12;

		if (displayY >= 0)
		{
			displayY = 0;
		}

		if (displayY <= maxDisplayY * -1)
		{
			displayY = maxDisplayY * -1;
		}
	}

	// This method allows the user to drag the output up and down.
	// This works similarly to scrolling on iPhone/Android. 
	@Override
	public void mouseDragged()
	{

		if (mouseY > 70 || startDrag)
		{
			displayY += (mouseY - pmouseY);
			startDrag = true;
		}

		if (displayY >= 0)
		{
			displayY = 0;
		}

		if (displayY <= maxDisplayY * -1)
		{
			displayY = maxDisplayY * -1;
		}
	}

	// This method adds the output text to the clipboard.
	public void putOnClipBoard()
	{
		if (scorer.isValid())
		{
			StringBuffer output = new StringBuffer("");
			int lineSize = 56;

			int length = A.getAllignedSequence().length();

			for (int index = 0; index < length; index += lineSize)
			{
				if (index + lineSize > length)
				{
					lineSize = length - index;
				}
				output.append(A.getAllignedSequence().substring(index, index + lineSize) + "\n");
				output.append(matched.substring(index, index + lineSize) + "\n");
				output.append(B.getAllignedSequence().substring(index, index + lineSize) + "\n\n");

				StringSelection stringSelection = new StringSelection(output.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		}
	}

	// This method is called after every UI interaction. The scores are recalculated in this method. If
	// something goes wrong, the score method will become invalid, and details will be displayed in the
	// programs output, and an error/warning message may pop up. 
	public void rescore()
	{
		scorer.setMatrix(matrix);
		scorer.score();
		displayY = 0;

		if (scorer.isValid())
		{
			table.createTable(scorer.getScores(), scorer.getSequenceA().getSequence(), scorer.getSequenceB().getSequence());
			table.setTitle(": " + scorer.getSequenceA().getName() + " vs. " + scorer.getSequenceB().getName() + ", SCORE: " + scorer.getScore());
			A = scorer.getSequenceA();
			B = scorer.getSequenceB();
			matched = scorer.getMatched();
			maxDisplayY = ((scorer.getSequenceA().getAllignedSequence().length() - 56 * 3) / 56) * 75;
			if (maxDisplayY < 0)
			{
				maxDisplayY = 0;
			}
		}
		else
		{
			table.setTitle(": Invalid Data");
			maxDisplayY = 0;
		}
	}

	// This method updates the button's positions after the text changes.
	public void updateButtonPositions()
	{
		loadMatrixButton.setX(5);
		loadSequenceButton.setX(10 + loadMatrixButton.getWidth());
		tableButton.setX(15 + loadMatrixButton.getWidth() + loadSequenceButton.getWidth());
		copyButton.setX(20 + loadMatrixButton.getWidth() + loadSequenceButton.getWidth() + tableButton.getWidth());

	}
}

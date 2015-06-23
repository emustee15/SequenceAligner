import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

// With help from
// http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/SimpleTableDemoProject/src/components/SimpleTableDemo.java

// This class creates a table from a JTable and a JFrame.

public class ScoreTable
{
	private JFrame frame;
	private boolean valid = false;
	private JTable table;
	private JScrollPane pane;

	// This method toggles the visibility of the table. The table
	// will only show if it actually has data.
	public void toggleVisibility()
	{
		if (valid)
		{
			if (frame.isVisible())
			{
				frame.setVisible(false);
			}
			else
			{
				frame.setVisible(true);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Error: Invalid input. Please load a valid input file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// This method returns whether the frame is visible. 
	public boolean isVisible()
	{
		return frame.isVisible();
	}

	// The constructor.
	public ScoreTable()
	{
		// Create our JFrame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("Alignment Scoring Matrix");
		frame.setIconImage(new ImageIcon("TableButton.png").getImage());

		// Create a dumby table
		table = new JTable(1,1);
		table.setPreferredScrollableViewportSize(new Dimension(600, 400));
		frame.setMinimumSize(new Dimension(500, 300));
		pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.pack();
	}
	
	// This method sets the title in addition to the default title.
	public void setTitle(String title)
	{
		frame.setTitle("Alignment Scoring Matrix" + title);
	}
	
	// This method creates a new table using sequence names and there scores.
	public void createTable(Integer[][] scores, String sequenceA, String sequenceB)
	{
		// Remember the current windows dimension. 
		Dimension d = frame.getSize();
		if (scores.length <= 1 || scores[0].length <= 1)
		{
			JOptionPane.showMessageDialog(null, "Error: Please load a sequence.", "Error", JOptionPane.ERROR_MESSAGE);
			valid = false;
			return;
		}
		
		// Remove the current elements.
		frame.remove(table);
		frame.remove(pane);
		
		
		// Create a new table and make it so the user cannot rearrange the 
		// columns or edit the cell. Set the preferred size.
		table = new JTable(sequenceB.length() + 1, sequenceA.length() + 2);
		table.setEnabled(false);
		table.setPreferredScrollableViewportSize(new Dimension(600,400));
		table.getTableHeader().setReorderingAllowed(false);

		// Set the first column to be the same color as the row headings. This gives an
		// illusion that the first column actually contains row headings, when in fact
		// it is a normal column like the rest.
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer()
		{
			public void setBackground(Color c)
			{
				super.setBackground(new Color(238, 238, 238));
			}
		});

		// Make the first column thin because it only contains single characters.
		table.getColumnModel().getColumn(0).setPreferredWidth(25);

		// Set the column sizes. 
		for (int i = 1; i < table.getColumnCount(); i++)
		{
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setMinWidth(25);
			column.setPreferredWidth(40);
		}
		
		// Set the column headings
		table.getColumnModel().getColumn(0).setHeaderValue(" ");
		table.getColumnModel().getColumn(1).setHeaderValue(" ");

		for (int index = 2; index < sequenceA.length() + 2; index++)
		{
			table.getColumnModel().getColumn(index).setHeaderValue(sequenceA.substring(index - 2, index - 1));
		}

		// Set the fake row headings
	
		for (int index = 1; index < sequenceB.length() + 1; index++)
		{
			table.setValueAt(sequenceB.substring(index - 1, index), index, 0);
		}
		
		// Set all the scores
		for (int row = 0; row < scores.length; row++)
		{
			for (int column = 1; column < scores[0].length + 1; column++)
			{
				table.setValueAt(scores[row][column - 1], row, column);
			}
		}

		// Set the row height to be 25. A luxurious size.
		table.setRowHeight(25);
		
		// Make the table so it doesn't auto resize.
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		// Add scrollbars and redraw the  pane. 
		pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.add(pane);
		frame.pack();
		
		// If this isn't the first time creating the table, restore the window to the older dimensions.
		// It gets annoying when the window resizes itself after everytime it gets new data.
		if (valid)
		{
			frame.setSize(d);
		}
		valid = true;
	}
}

import java.io.File;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Scorer
{

	private Sequence sequenceA, sequenceB;
	private String matched;
	private boolean valid = false;

	private SequenceAligner parent;

	private SubstitutionMatrix matrix;
	private Integer[][] scores;
	private int[][] directions;

	private int misalignedStartPenalty = 0;
	private int gapPenalty = 0;
	private int matchScore = 0;
	private int mismatchScore = 0;
	private int score;

	// This class actually contains the algorithms to score and processes the alignments.
	
	// This is the scoring method.
	public void score()
	{
		// We HATE NullPointerExceptions.
		if (scores == null)
		{
			return;
		}
		
		if (!valid)
		{
			return;
		}
	
		sequenceA.reset();
		sequenceB.reset();
		matched = "";
		Integer score = null;

		if (parent.getAlignmentState() == 1) // Local
		{
			// NOT IMPLEMENTED. We do not have enough time to 
			// research affine gap penalties. If our schedules
			// are not busy, we may implement this before next
			// Thursday.
			
		}
		else if (parent.getAlignmentState() == 2) // Semi-global
		{
			// Set up the directions and the scoring arrays. The direction array remembers
			// which direction was taken when scoring so the traceback is simple. 0 represents
			// a diagonal move, 1 represents a move down, and 2 represents a move right.
			for (int index = 0; index < scores[0].length; index++)
			{
				// Setting top
				scores[0][index] = 0;
				directions[0][index] = 2;
			}

			for (int index = 0; index < scores.length; index++)
			{
				// Setting left
				scores[index][0] = 0;
				directions[index][0] = 1;
			}
			
			// This is so the program knows when to stop the traceback.
			directions[0][0] = 3;

			// Score the alignment in a semi-global fashion. 
			score = alignGlobally(false);

		}
		else if (parent.getAlignmentState() == 3) // Global
		{

			// Set up the directions and the scoring arrays. The direction array remembers
			// which direction was taken when scoring so the traceback is simple. 0 represents
			// a diagonal move, 1 represents a move down, and 2 represents a move right.
			for (int index = 0; index < scores[0].length; index++)
			{
				scores[0][index] = misalignedStartPenalty * index * -1;
				directions[0][index] = 2;
			}

			for (int index = 0; index < scores.length; index++)
			{
				scores[index][0] = misalignedStartPenalty * index * -1;
				directions[index][0] = 1;
			}

			directions[0][0] = 3;
			
			// Score the alignment in a global fashion
			score = alignGlobally(true);

		}

		// If the score is not null at this point, the alignment completed successfully.
		// Otherwise, make sure to mark the scoring invalid.
		if (score != null)
		{
			this.score = score;
			valid = true;
			
		}
		else
		{
			
			valid = false;
		}
		

	}

	// Getters and setters
	public Sequence getSequenceA()
	{
		return sequenceA;
	}

	public Sequence getSequenceB()
	{
		return sequenceB;
	}

	public SubstitutionMatrix getMatrix()
	{
		return matrix;
	}
	
	public String getMatched()
	{
		return matched;
	}

	public void setMatrix(SubstitutionMatrix matrix)
	{
		this.matrix = matrix;
	}

	public Integer[][] getScores()
	{
		return scores;
	}
	
	public int getScore()
	{
		return score;
	}

	// This method loads the sequence data. If the loading completed succsfully,
	// then valid is marked true. 
	public void loadSequenceData(File file)
	{
		// First Line of file is the misaligned start penalty
		// Second Line of file is the gap penalty
		// Third line is match score for without substitution matrix
		// Fourth line is mismatch score for without substitution matrix
		// Fifth line is the name of species A
		// Sixth line is name of species B
		// Seventh Line is sequence A
		// Eighth Line is sequence B

		try
		{
			
			// Get the data and set up the arrays. 
			valid = false;
			Scanner in = new Scanner(file);
			misalignedStartPenalty = in.nextInt();
			gapPenalty = in.nextInt();
			matchScore = in.nextInt();
			mismatchScore = in.nextInt();
			String nameA = in.next();
			String nameB = in.next();
			String sequenceAString = in.next();
			String sequenceBString = in.next();
			sequenceAString = sequenceAString.toUpperCase();
			sequenceBString = sequenceBString.toUpperCase();
			sequenceA = new Sequence(nameA, sequenceAString);
			sequenceB = new Sequence(nameB, sequenceBString);
			valid = true;
			scores = new Integer[sequenceBString.length() + 1][sequenceAString.length() + 1];
			directions = new int[sequenceBString.length() + 1][sequenceAString.length() + 1];
			scores[0][0] = 0;
			in.close();
		}
		catch (Exception ex)
		{

		}
	}

	// Constructor
	public Scorer(SequenceAligner parent, SubstitutionMatrix matrix)
	{
		this.parent = parent;
		this.matrix = matrix;
	}

	public boolean isValid()
	{
		return valid;
	}

	// This method gets the max out of three numnbers and marks the choice
	// down in the directions table using the passed in integers. 
	private int getMax(int choice1, int choice2, int choice3, int b, int a)
	{
		if (choice1 > choice2)
		{
			if (choice1 > choice3)
			{
				directions[b][a] = 0;
				return choice1;
			}
			else
			{
				directions[b][a] = 2;
				return choice3;
			}
		}
		else
		{
			if (choice2 > choice3)
			{
				directions[b][a] = 1;
				return choice2;
			}
			else
			{
				directions[b][a] = 2;
				return choice3;
			}
		}
	}

	// This method displays an error when a substitution matrix lookup fails.
	private void matrixError(char a, char b)
	{
		JOptionPane.showMessageDialog(null, "Error: Could not find '" + a + "' and '" + b + "'\n on the substitution matrix.", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	// This method is the global alignment, Needleman-Wunsch algorithm 
	public Integer alignGlobally(boolean trulyGlobal)
	{
		// initialize our three choices
		int choice1, choice2, choice3;

		// This is the algorithm if there is not substitution matrix loaded
		if (matrix == null)
		{
			// Go through all the characters in the string.
			for (int a = 1; a < scores[0].length; a++)
			{
				for (int b = 1; b < scores.length; b++)
				{
					int score = 0;
					
					if (sequenceA.getSequence().charAt(a - 1) == sequenceB.getSequence().charAt(b - 1))
					{
						score = matchScore;
					}
					else
					{
						score = mismatchScore * -1;
					}

					// Use the max of the three values and mark the direction.
					
					choice1 = scores[b - 1][a - 1] + score;
					choice2 = scores[b - 1][a] - gapPenalty;
					choice3 = scores[b][a - 1] - gapPenalty;
					
					scores[b][a] = getMax(choice1, choice2, choice3, b, a);
				}
			}
		}
		else
		{
			for (int a = 1; a < scores[0].length; a++)
			{
				for (int b = 1; b < scores.length; b++)
				{
					Integer score = matrix.getScore(sequenceA.getSequence().charAt(a - 1), sequenceB.getSequence().charAt(b - 1));

					if (score == null)
					{
						valid = false;
						matrixError(sequenceA.getSequence().charAt(a - 1), sequenceB.getSequence().charAt(b - 1));
						return null;
					}

					choice1 = scores[b - 1][a - 1] + score;
					choice2 = scores[b - 1][a] - gapPenalty;
					choice3 = scores[b][a - 1] - gapPenalty;
					scores[b][a] = getMax(choice1, choice2, choice3, b, a);
				}
			}
		}
		
		// Now time for the fun part - the TRACEBACK
		// We start in the very bottom right corner.
		int a = directions[0].length - 1;
		int b = directions.length - 1;
		
		while (a >= 0 && b >= 0)
		{
			// If the direction is 1, we need to go up. Decrease b and add a
			// gap to sequenceA, and mark that no match has occured.
			if (directions[b][a] == 1)
			{
				sequenceA.addGap(a);
				matched = " " + matched;
				b--;
			}
			// If the direction is 2, we need to go left. Decrease a and add a
			// gap to sequenceB, and mark that no match has occured.
			else if (directions[b][a] == 2)
			{
				sequenceB.addGap(b);
				matched = " " + matched;
				a--;
			}
			// A possible match occured!
			else if (directions[b][a] == 0)
			{
				// See if the characters match each other. If so, mark this with a "|"
				if (sequenceA.getSequence().charAt(a-1) == sequenceB.getSequence().charAt(b-1))
				{
				matched = "|" + matched;
				}
				else
				{
					// See if we can give at least a partial match
					if (matrix != null)
					{
						// If the two letters score higher than a 0 on the loaded substitution matrix, give it a
						// partial match denoted by ":"
						if (matrix.getScore((sequenceA.getSequence().charAt(a-1)),sequenceB.getSequence().charAt(b-1)) > 0)
						{
							matched = ":" + matched;
						}
						else
						// The two letters did not score very well on the substitution matrix. Complete mismatch.
						{
							matched = " " + matched;
						}
					}
					else
					{
						// No substitution matrix is loaded so no partial matches for anyone.
						matched = " " + matched;
					}
					
				}
				
				// Move up and to the left diagonally by decreasing a and b
				a--;
				b--;
			}
			else // We reached the end. Stop this madness by setting a to -1.
			{
				a = -1;
			}
		}

		// If the scoring was truly global, then the score is the bottom right corner.
		// If not, the score is the highest score in the table.
		if (trulyGlobal)
		{
			return scores[scores.length - 1][scores[0].length - 1];
		}
		else
		{
			return  max();
		}
	}

	// This method gets the maximum score in the table
	private int max()
	{
		int max = scores[0][0];
		for (int i = 0; i < scores.length; i++)
		{
			for (int j = 0; j < scores[0].length; j++)
			{
				if (scores[i][j] > max)
				{
					max = scores[i][j];
				}
			}
		}

		return max;
	}
}

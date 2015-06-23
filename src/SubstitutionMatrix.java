import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class SubstitutionMatrix
{

	// This class handles loading a substitution matrix. 
	private ArrayList<SubstitutionScore> table;

	// This method loads in a substitution matrix
	public SubstitutionMatrix(File file)
	{
		ArrayList<Character> characters = new ArrayList<Character>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		table = new ArrayList<SubstitutionScore>();

	
		try
		{
			Scanner in = new Scanner(file);
			String line = in.nextLine();

			// YAYA REGULAR EXPRESSIONS. Get only the characters.
			Pattern pattern = Pattern.compile("[a-z]|[A-Z]");
			Matcher matcher = pattern.matcher(line);

			// Add the characters to the characters array.
			while (matcher.find())
			{
				// Convert the characters to upper case
				char newChar = matcher.group().toUpperCase().charAt(0);

				// There can only be unique characters. Make sure they haven't been used already. 
				for (char character : characters)
				{
					if (character == newChar)
					{
						throw new CharacterAlreadyExistsException(character);
					}

				}

				characters.add(newChar);
			}

			// Read in all the integers
			while (in.hasNextInt())
			{
				scores.add(in.nextInt());
			}

			// Make sure the size is okay
			int expectedSize = (characters.size() * (characters.size() + 1)) / 2;

			if (scores.size() != expectedSize)
			{
				throw new IncorrectAmountOfDataException(expectedSize, scores.size());
			}

			in.close();

		}
		catch (FileNotFoundException FNFEx)
		{
			// The user was not having the best day and attempted to load a file that didn't exist.
			JOptionPane.showMessageDialog(null, "Error: File not found.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (CharacterAlreadyExistsException CAEEx)
		{
			// The user attempted to load an invalid substitution matrix.
			JOptionPane.showMessageDialog(null, "Error: Invalid Data.\nThe character '" + CAEEx.getChar()
					+ "' has been found at least twice.\nThe Substitution Matrix is NOT case sensitive.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (IncorrectAmountOfDataException IAODEx)
		{
			// The amount of data added was either more or less than necessary. The matrix will still be valid if this occurs, and may cause
			// an error later if scored on an alignment attempting to access a null score. Luckily, those cases are handled then. 
			JOptionPane.showMessageDialog(null, "Warning: Incorrect Amount of Data.\nThe substitution matrix needs " + IAODEx.getExpectedSize()
					+ " scores.\nThere was/were " + IAODEx.getActualSize() + " score given.\nThe accepted data will still be used.", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
		catch (Exception ex)
		{
			// You get a prize if you somehow get here. Let me know if that happens (because I do not know how).
			JOptionPane.showMessageDialog(null, "Something really bad happened.\nDon't do it again!", "PC LOAD LETTER", JOptionPane.ERROR_MESSAGE);
		}

		
		// Fill the matrix with data
		int rowLength = 1;
		int counterX = 0;
		int counterY = 0;

		try
		{
			for (int index = 0; index < scores.size(); index++)
			{
				table.add(new SubstitutionScore(characters.get(counterX), characters.get(counterY), scores.get(index)));

				counterX++;

				if (counterX == rowLength)
				{
					counterX = 0;
					counterY++;
					rowLength++;
				}
			}
		}
		catch (IndexOutOfBoundsException ex)
		{
			// If the code reaches here, there was too much data. Ignore the extra data. The user was already told 
			// of there wrongdoings. 
		}

	}

	public Integer getScore(char a, char b)
	{
		// Hashtables could make this faster, but would have taken longer to implement.
		// Since these tables are not very large ( 26x25/2 at the largest), this will
		// only hurt performance on large sequence alignments. 
		
		for (SubstitutionScore score : table)
		{
			// See if the combination exists in either way.
			if (score.getFirst() == a && score.getSecond() == b)
			{
				return score.getScore();
			}

			if (score.getFirst() == b && score.getSecond() == a)
			{
				return score.getScore();
			}
		}

		// If the score was not found, return nothing
		return null;
	}

}

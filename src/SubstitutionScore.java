
public class SubstitutionScore
{

	private char first, second;
	private int score;
	
	public SubstitutionScore(char fisrt, char second, int score)
	{
		this.first = fisrt;
		this.second = second;
		this.score = score;
	}

	public char getFirst()
	{
		return first;
	}

	public char getSecond()
	{
		return second;
	}

	public int getScore()
	{
		return score;
	}
	

}


public class IncorrectAmountOfDataException extends Exception
{
	private int actualSize;
	private int expectedSize;
	
	public IncorrectAmountOfDataException(int expectedSize, int actualSize)
	{
		this.actualSize = actualSize;
		this.expectedSize = expectedSize;
	}
	
	public int getActualSize()
	{
		return actualSize;
	}
	
	public int getExpectedSize()
	{
		return expectedSize;
	}
}

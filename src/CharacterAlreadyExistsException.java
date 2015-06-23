
public class CharacterAlreadyExistsException extends Exception
{
	private char a;
	
	public CharacterAlreadyExistsException(char a)
	{
		this.a = a;
	}
	
	public char getChar()
	{
		return a;
	}

}

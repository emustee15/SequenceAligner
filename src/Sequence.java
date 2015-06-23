
public class Sequence
{
	private String name, sequence;
	private String alignedSequence;

	// This class contains sequences, there names, and the
	// operations that can be performed on them.
	public Sequence(String name, String sequence)
	{
		this.name = name;
		this.sequence = sequence;
		this.alignedSequence = sequence;
	}

	public String getName()
	{
		return name;
	}

	public String getSequence()
	{
		return sequence;
	}
	
	public String getAllignedSequence()
	{
		return alignedSequence;
	}


	// This method adds a gap to a sequence at a specified location. Since
	// gaps are added from the end forward, there is never a problem of
	// remember where the gaps are. 
	public void addGap(int location)
	{
		String a = alignedSequence.substring(0, location);
		String b = alignedSequence.substring(location);
		
		alignedSequence = a + "-" + b;
	}
	
	// This method resets the alignedSequence
	public void reset()
	{
		this.alignedSequence = sequence;
	}
}

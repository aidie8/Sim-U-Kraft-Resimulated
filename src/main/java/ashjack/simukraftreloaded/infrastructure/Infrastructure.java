package ashjack.simukraftreloaded.infrastructure;

public class Infrastructure 
{
	public enum infrastructures{None, Water, Electricity};
	
	String infrastructureName = "";
	
	public void setInfrastructureName(String name)
	{
		infrastructureName = name;
	}
}

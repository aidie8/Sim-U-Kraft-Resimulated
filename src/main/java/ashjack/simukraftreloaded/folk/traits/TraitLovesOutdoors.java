package ashjack.simukraftreloaded.folk.traits;

public class TraitLovesOutdoors extends Trait
{
	public TraitLovesOutdoors() 
	{
		super();
		setTraitName("Loves the Outdoors");
		setTraitDescription("Folk that love the outdoors love taking leisurely strolls. "
				+ "They love open spaces, and working outside will make their day.");
		setTraitOpposite(Traits.traitHatesOutdoors);
	}
}

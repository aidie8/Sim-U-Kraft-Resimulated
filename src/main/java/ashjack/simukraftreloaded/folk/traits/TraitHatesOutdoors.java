package ashjack.simukraftreloaded.folk.traits;

public class TraitHatesOutdoors extends Trait
{
	public TraitHatesOutdoors() 
	{
		super();
		setTraitName("Hates the Outdoors");
		setTraitDescription("Folk that hate the outdoors tend to warp more frequently. "
				+ "They dislike open spaces, and hate working on farms.");
		setTraitOpposite(Traits.traitLovesOutdoors);
	}
}

package ashjack.simukraftreloaded.folk.traits;

import ashjack.simukraftreloaded.folk.traits.Trait;

public class TraitReligious extends Trait
{	
	public TraitReligious() 
	{
		super();
		setTraitName("Religious");
		setTraitDescription("Religious folk like to visit church daily to pray. "
				+ "Being unable to do so makes them unhappy. Make sure you build a church"
				+ "in your town.");
		//hasSpecialBuilding("Church", "Attending Church");
	}
}

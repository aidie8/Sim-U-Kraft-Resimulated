package ashjack.simukraftreloaded.folk.traits;

import net.minecraft.block.Block;
import ashjack.simukraftreloaded.core.Unused;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;

public class Trait 
{
	public static String traitName;
	public String traitDesc;
	public Trait traitOpposite;
	public FolkData theFolk;
	
	public Trait()
	{
		
	}
	
	public static Trait getTraitFromName(String searchTerm)
	{
		Trait trait;
		for(int i=0; i<Traits.traitList.length-1; i++)
		{
			if(Traits.traitList[i].traitName.contains(searchTerm))
			{
				trait = Traits.traitList[i];
				return trait;
			}
		}
	return null;	
	}
	
	public static String getTraitName(Trait trait)
	{
		return trait.traitName;
	}
	
	/**
	 * Sets the name of the trait
	 */
	public void setTraitName(String name)
	{
		this.traitName = name;
	}
	
	/**
	 * Sets the description of the trait
	 */
	public void setTraitDescription(String description)
	{
		this.traitDesc = description;
	}
	
	/**
	 * Sets the opposite trait to this one.<br>
	 * If a folk has one trait, he cannot have the opposite one too.
	 */
	public void setTraitOpposite(Trait opposite)
	{
		this.traitOpposite = opposite;
	}
	
	/**
	 * Gets the name of the trait
	 */
	public String getTraitName()
	{
		return traitName;
	}
	
	/**
	 * Gets the description of the trait
	 */
	public String getTraitDescription()
	{
		return traitDesc;
	}
	
	
	/**
	 * Sets the icon of the trait
	 */
	@Unused
	public void setTraitIcon()
	{
		
	}

	/**
     * Tells the folk with this trait where their 'special' building is<br>
     * (if they have one)
     */
	public void hasSpecialBuilding(String buildingName, String visitingText)
	{
		Building specialBuilding = Building.getBuildingBySearch(buildingName);
		
		if(specialBuilding != null)
		{
			theFolk.gotoXYZ(specialBuilding.primaryXYZ, GotoMethod.WALK);
			theFolk.destination.doNotTimeout = true;
			theFolk.statusText = visitingText;
		}
	}
	
	/**
     * Tells the folk with this trait where their 'special' building is<br>
     * (if they have one)
     */
	public void hasSpecialBuilding(String buildingName)
	{
		Building specialBuilding = Building.getBuildingBySearch(buildingName);
		
		theFolk.gotoXYZ(specialBuilding.primaryXYZ, GotoMethod.WALK);
		theFolk.destination.doNotTimeout = true;
		theFolk.statusText = "Visiting the " + specialBuilding.displayName;
	}
	
}

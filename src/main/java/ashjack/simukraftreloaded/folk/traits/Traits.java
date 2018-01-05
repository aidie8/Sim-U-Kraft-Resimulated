package ashjack.simukraftreloaded.folk.traits;

import net.minecraft.init.Blocks;


public class Traits 
{
	public static Trait traitReligious = new TraitReligious();
	public static Trait traitWorkaholic = new TraitWorkaholic();
	public static Trait traitBrave = new TraitBrave();
	public static Trait traitDwarvenHeritage = new TraitDwarvenHeritage();
	public static Trait traitFriendly = new TraitFriendly();
	public static Trait traitGreenThumb = new TraitGreenThumb();
	public static Trait traitHatesOutdoors = new TraitHatesOutdoors();
	public static Trait traitNightOwl = new TraitNightOwl();
	public static Trait traitStrong = new TraitStrong();
	public static Trait traitLovesOutdoors = new TraitLovesOutdoors();
	
	public static Trait traitShy;
	public static Trait traitLazy;

	public static Trait[] traitList;
	public static Trait[] specialTraitList;
	public static void loadTraits()
	{
		traitList = new Trait[9];
		traitList[0] = traitWorkaholic;
		traitList[1] = traitBrave;
		traitList[2] = traitDwarvenHeritage;
		traitList[3] = traitFriendly;
		traitList[4] = traitGreenThumb;
		traitList[5] = traitHatesOutdoors;
		traitList[6] = traitNightOwl;
		traitList[7] = traitStrong;
		traitList[8] = traitLovesOutdoors;
		
		specialTraitList = new Trait[1];
	}
	
}

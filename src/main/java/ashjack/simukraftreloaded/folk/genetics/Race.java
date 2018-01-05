package ashjack.simukraftreloaded.folk.genetics;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.traits.Trait;
import ashjack.simukraftreloaded.folk.traits.Traits;

public class Race implements Serializable
{
	protected static String raceName = "";
	
	static File simfolder = new File(SimukraftReloaded.getSimukraftFolder()+ "races/");
	static String humanFolder = SimukraftReloaded.getSimukraftFolder() + "races/" + "Human/";
	static String elfFolder = SimukraftReloaded.getSimukraftFolder() + "races/" + "Elf/";
	static String darkElfFolder = SimukraftReloaded.getSimukraftFolder() + "races/" + "DarkElf/";
	protected static String[] names = simfolder.list();
	
	public static int humanAdultMaleSkinCount = 0;
	public static int humanAdultFemaleSkinCount = 0;
	
	public static int elvenAdultMaleSkinCount = 0;
	public static int elvenAdultFemaleSkinCount = 0;
	
	public static int darkElvenAdultMaleSkinCount = 0;
	public static int darkElvenAdultFemaleSkinCount = 0;
	
	public Race()
	{
		
	}
	
	public void setRaceName(String name)
	{
		name = this.raceName;
	}
	
	public String getRaceName()
	{
		return this.raceName;
	}
	
	public static void loadRaces()
	{
		humanAdultMaleSkinCount = new File(humanFolder + "Adult/Male/").list().length;
		humanAdultFemaleSkinCount = new File(humanFolder + "Adult/Female/").list().length;
		
		elvenAdultMaleSkinCount = new File(elfFolder + "Adult/Male/").list().length;
		elvenAdultFemaleSkinCount = new File(elfFolder + "Adult/Female/").list().length;
		
		darkElvenAdultMaleSkinCount = new File(darkElfFolder + "Adult/Male/").list().length;
		darkElvenAdultFemaleSkinCount = new File(darkElfFolder + "Adult/Female/").list().length;
		
		Races.loadRaces();
	}
	
	public int getRandomSkinForRace(Race race, int gender)
	{
		if(gender == 0)
		{
			if(race == Races.raceHuman)
			{
				return humanAdultMaleSkinCount;
			}
			else if(race == Races.raceElf)
			{
				return elvenAdultMaleSkinCount;
			}
			else if(race == Races.raceDarkElf)
			{
				return darkElvenAdultMaleSkinCount;
			}
		}
		else
		{
			if(race == Races.raceHuman)
			{
				return humanAdultFemaleSkinCount;
			}
			else if(race == Races.raceElf)
			{
				return elvenAdultFemaleSkinCount;
			}
			else if(race == Races.raceDarkElf)
			{
				return darkElvenAdultFemaleSkinCount;
			}
		}
		return 0;
	}
	
	/*public String toString()
	{
		return raceName;
	}
	
	public void loadRaces()
	{
		try
		{
			for (String g : names)
			{
				if(new File(SimukraftReloaded.getSimukraftFolder()+ "/races/" + g).isDirectory())
				{
					races.add(g);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
    	}
	}*/
	
	public static Race getRaceFromName(String searchTerm)
	{
		Race race;
		for(int i=0; i<Races.raceList.size(); i++)
		{
			if(Races.raceList.get(i).raceName.contentEquals(searchTerm))
			{
				race = Races.raceList.get(i);
				return race;
			}
		}
		
	return null;	
	}
}

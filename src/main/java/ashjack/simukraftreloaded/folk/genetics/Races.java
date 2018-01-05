package ashjack.simukraftreloaded.folk.genetics;

import java.util.ArrayList;
import java.util.List;

public class Races 
{
	public static Race raceHuman = new RaceHuman();
	public static Race raceElf = new RaceElf();
	public static Race raceDarkElf = new RaceDarkElf();
	
	public static List<Race> raceList = new ArrayList<Race>();
	
	public static void loadRaces()
	{
		raceList.add(raceHuman);
	}
}

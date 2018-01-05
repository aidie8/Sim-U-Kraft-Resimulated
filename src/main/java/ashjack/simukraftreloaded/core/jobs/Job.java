package ashjack.simukraftreloaded.core.jobs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;

import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;

public class Job implements Serializable
{
	public static String displayName;
	public static String jobBuildingName;
	public static Building jobBuilding;
	
	public static boolean workOvernight = false;
	public static boolean workDay = true;
	public static boolean ignorePregnancy = false;
	
	public static void loadJobs()
	{
		File simfolder = new File(SimukraftReloaded.getSimukraftFolder()+ "/jobs/");
		try
		{
			for (File g : simfolder.listFiles())
	        {
				
	        
			/*File f = new File(SimukraftReloaded.getSimukraftFolder() + "/jobs/" + displayName + ".txt");
			if (!f.exists())
			{
				return;
			}*/
		
			FileInputStream fstream = new FileInputStream(SimukraftReloaded.getSimukraftFolder() + "/jobs/" + g.getName());
        	DataInputStream in = new DataInputStream(fstream);
        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
        	
        	for(int i=0; i<g.length(); i++)
        	{
        		String strLine = br.readLine().toString().trim();
        		String[] jobFileLine = strLine.split("=");
        	
        		/*Job Name (name)*/
        		if(jobFileLine[0] == "Name")
        		{
        			displayName = jobFileLine[1];
        		}
            
        		/*Job Building (building)*/
        		if(jobFileLine[0] == "Building")
        		{
        			jobBuildingName = jobFileLine[1];
        			jobBuilding = Building.getBuildingBySearch(jobBuildingName);
        		}
            
        		/*Should the folk work overnight? (WorkAtNight)*/
        		if(jobFileLine[0] == "WorkAtNight")
        		{
        			if(Byte.parseByte(jobFileLine[1])==0)
            		{
            			workOvernight = false;
            		}
            		else if(Byte.parseByte(jobFileLine[1])==1)
            		{
            			workOvernight = true;
            		}
            		else
            		{
            			SimukraftReloaded.log.log(Level.WARNING, "WorkAtNight is not 1 or 0, setting to 0 by default");
            			workOvernight = false;
            		}
        		}        		
            
        		/*Should the folk work during the day? (WorkAtDay)*/
        		if(jobFileLine[0] == "WorkAtNight")
        		{
        			if(Byte.parseByte(jobFileLine[1])==0)
            		{
            			workDay = false;
            		}
            		else if(Byte.parseByte(jobFileLine[1])==1)
                	{
            			workDay = true;
                	}
            		else if(Byte.parseByte(jobFileLine[1])==0&&workOvernight==false)
            		{
            			SimukraftReloaded.log.log(Level.WARNING, "WorkAtDay is 0 and WorkAtNight is 0, setting WorkAtDay to 1 to prevent problems");
            			workDay = true;
            		}
            		else
            		{
            			SimukraftReloaded.log.log(Level.WARNING, "WorkAtDay is not 1 or 0, setting to 1 by default");
            			workDay = true;
            		}
        		}
        		
            
        		/*Should the folk work even when pregnant? (IgnorePregnancy)*/
        		if(jobFileLine[0] == "IgnorePregnancy")
        		{
        			if(Byte.parseByte(jobFileLine[1])==0)
            		{
        				ignorePregnancy = false;
            		}
            		else if(Byte.parseByte(jobFileLine[1])==1)
            		{
            			ignorePregnancy = true;
            		}
            		else
            		{
            			SimukraftReloaded.log.log(Level.WARNING, "IgnorePregnancy is not 1 or 0, setting to 0 by default");
            			ignorePregnancy = false;
            		}
        		}
        	
        		
        	}
        	
        	br.close();
	        }
		}
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
	}
}

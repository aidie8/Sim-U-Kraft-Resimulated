package ashjack.simukraftreloaded.core;

import java.io.BufferedOutputStream;
import java.io.File;

import ashjack.simukraftreloaded.core.jobs.Job;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UpdateChecker 
{
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		//Job.loadJobs();
		
		try
        {
            String baseURL = "https://dl.dropboxusercontent.com/u/255688822/Sim-U-Kraft%20Reloaded/versions.txt";
            
            //// check for new version
            String ver = downloadFile(baseURL, SimukraftReloaded.getSimukraftFolder()
                                      + File.separator + "version.txt");

            if (ver != null)
            {
                ver = ver.trim();

                if (!ver.contentEquals(""))
                {
                    if (!ModSimukraft.version.contentEquals(ver))
                    {
                    	SimukraftReloaded.sendChat("NEW update of Sim-U-Kraft Reloaded available (" + ver + ") please check the thread.");
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
       
    public String downloadFile(String url, String localFile) {
        String ret = "";
        url = url.replace(" ", "%20");
        
        try
        {
            java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.net.URL(url).openStream());
			java.io.FileOutputStream fos = new java.io.FileOutputStream(localFile);
            java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[4096];
            int x = 0;

            while ((x = in.read(data, 0, 4096)) >= 0)
            {
                bout.write(data, 0, x);
            }

            bout.flush();
            ret = new String(data);
            bout.close();
            in.close();
        }
        catch (Exception e)
        {
            ret = "";
            e.printStackTrace();
        }

        return ret;
    }
}

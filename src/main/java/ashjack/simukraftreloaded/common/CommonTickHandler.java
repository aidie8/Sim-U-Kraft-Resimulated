package ashjack.simukraftreloaded.common;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;

import ashjack.simukraftreloaded.blocks.functionality.FarmingBox;
import ashjack.simukraftreloaded.blocks.functionality.MiningBox;
import ashjack.simukraftreloaded.client.Gui.other.GuiRunMod;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedConfig;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.packetsNEW.PacketHandler;
import ashjack.simukraftreloaded.packetsNEW.toClient.UpdateMoney;
import ashjack.simukraftreloaded.packetsNEW.toClient.Updatepopulation;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;

public class CommonTickHandler
{
    private World serverWorld = null;
    Long lastSecondTickAt = 0l;
    Long lastMinuteTickAt = 0l;

    GuiRunMod runModui = null;
    String currentWorld = "";
    long lastReset = 0;
    
    boolean haveRunStartup = false;
    
    @SubscribeEvent
    public void tick(WorldTickEvent event)
    {
    	
    }
    int ticks = 0;
    @SubscribeEvent
    public void tick(ServerTickEvent event)
    {
    	if(ticks == 200)
    	{
    		onTickInGame();
    	}
    	else
    	{
    		ticks++;
    	}
    }
    
    
    public void onTickInGame()
    {
    	if ( FMLCommonHandler.instance().getSide().isClient())
    	{
    		if (SimukraftReloaded.states.gameModeNumber == 10)
    		{
    			ModSimukraft.proxy.ranStartup = true;
    			return;
    		}
    	}

        Long now = System.currentTimeMillis();

        if (serverWorld != null)
        {
            //fire onUpdate() for each folkData
            FolkData.triggerAllUpdates();
            
            //handle day-night-day transitions
            SimukraftReloaded.dayTransitionHandler();

            //if a farm needs upgrading, incrementally upgrade it
            if (SimukraftReloaded.farmToUpgrade != null)
            {
            	SimukraftReloaded.upgradeFarm();
            }

            if (SimukraftReloaded.demolishBlocks.size() > 0) //and demolishing buildings
            {
            	SimukraftReloaded.demolishBlocks();
            }
        }

        // ***** ONCE A SECOND
        if (now - lastSecondTickAt > 1000)
        {
            if (ModSimukraft.proxy.ranStartup == false)
            {
               System.out.println("Haven't run startup - doing that now");
               
               serverWorld = MinecraftServer.getServer().getEntityWorld();
               currentWorld = SimukraftReloaded.getSavesDataFolder();
               SimukraftReloaded.log.info("CommTH: Startup - set serverWorld/currentWorld");
               
               System.out.println("Running Reset World Function");
               SimukraftReloaded.resetAndLoadNewWorld();
                            
               //Packets have had a workaround put in place - they need to be fixed.
                            
               
            }
            
            else      //used to detect world change - Still a bug with this, not unloading world when player switches via main menu
            {
            	//System.out.println("Have already run startup");
                if (!currentWorld.contentEquals(SimukraftReloaded.getSavesDataFolder()))
                {
                    if (now - lastReset >30000) {
                    	SimukraftReloaded.log.info("currentWorld="+currentWorld+"     getSaves="+SimukraftReloaded.getSavesDataFolder());
	                	currentWorld = SimukraftReloaded.getSavesDataFolder();
	                    ModSimukraft.proxy.ranStartup = false;
	                    
	                    SimukraftReloaded.resetAndLoadNewWorld();
	                    
	                    
                    }
                    
                }

                //STOP THE RAIN MOD - Implemented this when I had a world where it rained ALL THE TIME!
                if (serverWorld.isRaining() && serverWorld.getWorldInfo().getRainTime() > 1 &&  SimukraftReloadedConfig.configStopRain == true)
                {
                    serverWorld.getWorldInfo().setRainTime(2);  //setting to 1 or 0 doesn't work every time.
                }
           // }
            //if (FMLCommonHandler.instance().getSide().isClient()) {
            	//PacketHandler.SendToServer(new Updatepopulation());
                //SimukraftReloaded.sendChat("SENDING S");
           // }
            //if (GameStates.Clientpop != GameStates.population) {
               // PacketHandler.sendToAll(new Updatepopulation());
                //GameStates.Clientpop = GameStates.population;
                //SimukraftReloaded.sendChat("UPDATE");
               // }
            //PacketHandler.sendToAll(new UpdateMoney());
            //SimukraftReloaded.sendChat("Money Update");
            }
            
            
            // ONCE A SECOND EVERY SECOND
            lastSecondTickAt = now;
            
            
            {

        
        // ***** ONCE A MINUTE
        if (serverWorld != null && System.currentTimeMillis() - lastMinuteTickAt > 60000)
        {
            if (lastMinuteTickAt > 0)
            {
            	if (ModSimukraft.proxy.ranStartup)
            	{
                Long start = System.currentTimeMillis();
                FolkData.generateNewFolk(serverWorld);
                SimukraftReloaded.states.saveStates();
                Building.checkTennants();
                Building.saveAllBuildings();
                CourierTask.saveCourierTasksAndPoints();
                MiningBox.saveMiningBoxes();
                FarmingBox.saveFarmingBoxes();
                Relationship.saveRelationships();
                //PathBox.savePathBoxes();
                SimukraftReloaded.log.info("CTH: Saved game data in " + (System.currentTimeMillis() - start) + " ms");
            }
            	}

            lastMinuteTickAt = now;
        }}}
    }

    public void resetSimUKraft()
    {
        //this resets everything first, if the player has switched worlds, gets hit several times due to weird MC GUI switching,
        // so lastReset stops it from running more than once every 30 seconds.
        if (System.currentTimeMillis() - lastReset > 30000)
        {
            
            lastReset = System.currentTimeMillis();
            Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
            
            SimukraftReloaded.log.info(side.toString()+"-side CommTH: resetSimUKraft()");
        }
    }

    /** runs when a world has loaded, so we can set everything up */
    private void startingWorld()
    {
        if (!ModSimukraft.proxy.ranStartup)
        {
           // TODO: no longer used
           

        }
    }

    ////////////////////////////////////////////////
    



    public String getLabel()
    {
        return "CommonTickHandler";
    }

    public CommonTickHandler()
    {
    	
    }
}



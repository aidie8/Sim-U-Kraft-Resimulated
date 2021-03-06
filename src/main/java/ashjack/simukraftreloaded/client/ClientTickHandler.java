package ashjack.simukraftreloaded.client;

import java.util.EnumSet;
import java.util.Random;

import ashjack.simukraftreloaded.blocks.functionality.FarmingBox;
import ashjack.simukraftreloaded.blocks.functionality.MiningBox;
import ashjack.simukraftreloaded.client.Gui.SimukraftMenuWarning;
import ashjack.simukraftreloaded.common.CourierTask;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedConfig;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.GuiOpenEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientTickHandler
{
    public ClientTickHandler() 
    {}

	Minecraft mc = Minecraft.getMinecraft();
    GuiScreen hud = new GuiScreen();
    Long timeSinceLastSave = 0l;

    public static int beamingStage = 1;
    public static long beamingStartedAt = 0;
    public static V3 beamingTo = null;
    public static EntityPlayer beamingPlayer = null;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
       FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void tick(WorldTickEvent event)
    {
    	onTickInGame();
    	//System.out.println("WorldLoad event tick(client side)");
    }
    
    @SubscribeEvent
    public void tick(ClientTickEvent event)
    {
    	//System.out.println("Client event tick(client side)");
    }
    
    @SubscribeEvent
    public void tick(RenderTickEvent event)
    {
    	onGui(event);
    	//System.out.println("Render event tick(client side)");
    }

    public void onTickInGame()
    {

    	if (mc.currentScreen != null)
        	
        {
            if (mc.currentScreen.toString().toLowerCase().contains("guimainmenu"))
            {
            	//SimukraftReloaded.log.info("CommTH: in Gui Main menu");
            }
        }
    	
        if (beamingTo != null)
        {
            beamingPlayer();
        }
        
        //--------------------------------
        try
        {
            if (SimukraftReloaded.states.gameModeNumber <= 0)
            {
                return;
            }
        }
        catch (Exception e) {}

        if (mc.currentScreen != null)
        {
            if (mc.currentScreen.toString().toLowerCase().contains("ingamemenu"))
            {
                if (System.currentTimeMillis() - timeSinceLastSave > 10000)
                {
                	
                	
                   
                	SimukraftReloadedConfig.config.save();
                	SimukraftReloaded.states.saveStates();
                    Building.saveAllBuildings();
                    CourierTask.saveCourierTasksAndPoints();
                    MiningBox.saveMiningBoxes();
                    FarmingBox.saveFarmingBoxes();

                    for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
                    {
                        FolkData folk = SimukraftReloaded.theFolks.get(f);
                        folk.updateLocationFromEntity();
                        folk.saveThisFolk();
                    }
					
                   // SimukraftReloaded.log("Saved ALL game data via in Game Menu");
                    timeSinceLastSave = System.currentTimeMillis();
                }
            }
        }


    }

    /*@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void openGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) 
        {
            event.gui = new SimukraftMenuWarning();
        }
    }*/
    
    public void onGui(RenderTickEvent event)
    {
    	
        //Draw the HUD
        if (mc.currentScreen == null)
        {
            String worldname = "unknown";

            try
            {
                if (SimukraftReloaded.states.gameModeNumber == 10)
                {
                    return;
                }

                worldname = mc.getIntegratedServer().getFolderName();
                worldname = MinecraftServer.getServer().getFolderName();
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            	int HUDoffset = 0;
            	hud.drawString(mc.fontRenderer, worldname + " (" + SimukraftReloaded.getDayOfWeek() +
                        ") - Population: " + SimukraftReloaded.theFolks.size() +
                        "   Sim-U-credits: " + SimukraftReloaded.displayMoney(SimukraftReloaded.states.credits), hud.width / 2, 2 + HUDoffset, 0xffffff);
                return;
            }

            try
            {
                if (ModSimukraft.proxy.ranStartup)
                {
                	if(Minecraft.getMinecraft().gameSettings.showDebugInfo == false)
                	{
                    int HUDoffset = 0;

                    if (mc.thePlayer.dimension == 1)
                    {
                        HUDoffset = 20;
                    }

                    HUDoffset += SimukraftReloadedConfig.configHUDoffset;

                    if (GameMode.gameMode == GameMode.GAMEMODES.CREATIVE)
                    {
                    	
                        hud.drawString(mc.fontRenderer, worldname + " (" + SimukraftReloaded.getDayOfWeek() +
                                       ") - Population: " + SimukraftReloaded.theFolks.size() , hud.width / 2, 2 + HUDoffset, 0xffffff);
                    }
                    else
                    {
                        hud.drawString(mc.fontRenderer, worldname + " (" + SimukraftReloaded.getDayOfWeek() +
                                       ") - Population: " + SimukraftReloaded.theFolks.size() +
                                       "   Sim-U-credits: " + SimukraftReloaded.displayMoney(SimukraftReloaded.states.credits), hud.width / 2, 2 + HUDoffset, 0xffffff);
                    }
                }
                }
                else
                {
                	if(Minecraft.getMinecraft().gameSettings.showDebugInfo == false)
                	{
                		hud.drawString(mc.fontRenderer, "Loading Sim-U-Kraft...", hud.width / 2, 2, 0xffffff);
                	}
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void beamingPlayer()
    {
    	
        Minecraft mc = Minecraft.getMinecraft();
        Random random = new Random();
        beamingPlayer.motionX = 0;
        beamingPlayer.motionY = 0;
        beamingPlayer.motionZ = 0;
        Double d4 = ((double) random.nextFloat() - 2D) * 2D;

        for (int p = 0; p < 20; p++)
        {
            try
            {
                mc.theWorld.spawnParticle("portal",
                                          beamingPlayer.posX + (random.nextDouble()) - 0.5,
                                          beamingPlayer.posY - 1,
                                          beamingPlayer.posZ + (random.nextDouble()) - 0.5, 0,
                                          -d4, 0);
            }
            catch (Exception e)
            {
            }

            try
            {
                mc.theWorld.spawnParticle("portal",
                                          beamingTo.x + (random.nextDouble()) - 0.5,
                                          beamingTo.y - 1,
                                          beamingTo.z + (random.nextDouble()) - 0.5, 0,
                                          -d4, 0);
            }
            catch (Exception e)
            {
            }
        }

        if (beamingStage == 1)
        {
            if (System.currentTimeMillis() - beamingStartedAt > 6000)
            {
                beamingStage = 2;
                beamingPlayer.setPositionAndUpdate(beamingTo.x, beamingTo.y, beamingTo.z);
               // PacketDispatcher.sendPacketToServer(PacketHandler
                                             //       .makePacket(beamingPlayer.entityId, "shiftplayer", beamingTo.toString(), beamingTo.theDimension));
            }
        }
        else if (beamingStage == 2)
        {
            mc.theWorld.playSound(beamingTo.x, beamingTo.y, beamingTo.z, "ashjacksimukraftreloaded:beamdowntwo" , 1f, 1f, false);
            beamingStage = 3;
        }
        else if (beamingStage == 3)
        {
            if (System.currentTimeMillis() - beamingStartedAt > 10000 || beamingTo == null)
            {
                beamingTo = null;
                beamingPlayer = null;
                return;
            }
        }
    }

   

    public String getLabel()
    {
        return "ClientTickHandler";
    }
}

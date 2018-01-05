package ashjack.simukraftreloaded.proxies;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import ashjack.simukraftreloaded.client.ClientTickHandler;
import ashjack.simukraftreloaded.client.ModelAlignBeam;
import ashjack.simukraftreloaded.client.ModelConBox;
import ashjack.simukraftreloaded.client.ModelFolkFemale;
import ashjack.simukraftreloaded.client.RenderAlignBeam;
import ashjack.simukraftreloaded.client.RenderConBox;
import ashjack.simukraftreloaded.client.RenderFolk;
import ashjack.simukraftreloaded.client.Gui.KeyHandler;
import ashjack.simukraftreloaded.common.CommonTickHandler;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.entity.EntityAlignBeam;
import ashjack.simukraftreloaded.entity.EntityConBox;
import ashjack.simukraftreloaded.entity.EntityFolk;
import ashjack.simukraftreloaded.entity.EntityWindmill;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{
	//Log
    public static Logger log = Logger.getLogger("Sim-U-Kraft Reloaded");
	
    @SideOnly(Side.CLIENT)
    public void setLogLevel()
    {
    	log.setLevel(Level.INFO);
    }
    
    @Override
    public void registerRenderInfo()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityAlignBeam.class, new RenderAlignBeam(new ModelAlignBeam()));
        RenderingRegistry.registerEntityRenderingHandler(EntityFolk.class, new RenderFolk(new ModelFolkFemale()));
        RenderingRegistry.registerEntityRenderingHandler(EntityConBox.class, new RenderConBox(new ModelConBox()));
        //RenderingRegistry.registerEntityRenderingHandler(EntityWindmill.class, new RenderWindmill(new ModelWindmill()));
    }

    @Override
    public void registerMisc()
    {
        super.registerMisc();
        FMLCommonHandler.instance().bus().register(this);

    }
    
    public void registerProxies()
    {
        FMLCommonHandler.instance().bus().register(new KeyHandler());
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }


    @Override
    public void saveObject(String filename, Object o)
    {
        if (o == null)
        {

            return;
        }

        //System.out.println("Saving object "+o.toString());
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
        }
        catch (Exception e)
        {
 
            e.printStackTrace();
        }
    }
    
    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
     // Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
     // your packets will not work because you will be getting a client
     // player even when you are on the server! Sounds absurd, but it's true.

     // Solution is to double-check side before returning the player:
     return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }
    
    
}

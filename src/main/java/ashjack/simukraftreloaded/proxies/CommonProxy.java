package ashjack.simukraftreloaded.proxies;

import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.client.ClientTickHandler;
import ashjack.simukraftreloaded.common.CommonTickHandler;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
public class CommonProxy
{
	Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();

    public boolean ranStartup = false;

    public void registerRenderInfo()
    {
    }

    /** serializes and saves out an object to a file */
    public void saveObject(String filename, Object o)
    {
    }

    /** loads in a serialized object and returns the new object */
    public Object loadObject(String filename)//TODO: REMOVE THIS STUFF - old .suk file loader, now using .sk2
    {
        Object o = null;

        try
        {
            FileInputStream fis2 = new FileInputStream(filename);
            ObjectInputStream in2 = new ObjectInputStream(fis2);
            o = in2.readObject();
            in2.close();
        }
        catch (Exception e)
        {
            SimukraftReloaded.log.info("OLD LOADER - Could not load object " + e.getMessage());
        }

        return o;
    }

    /** A commodity that a bank can sell to the player (rare item) */
    public static class Commodity
    {
        public ItemStack theItemStack = null;
        public int quantity = 0;
        public float priceEach = 0.0f;

        /** holds one item of each commodity potentially available in banks */
        private static ArrayList<ItemStack> availableItems = new ArrayList<ItemStack>();

        public Commodity(ItemStack is, int qty, float price)
        {
            this.theItemStack = is;
            this.quantity = qty;
            this.priceEach = price;
        }

        /** clears out the banks available items and adds in a bunch of new ones (each morning) */
        public static void refreshAvailableCommoditities()
        {
            if (availableItems.size() == 0)
            {
                setupAvailableItems();
            }

            Random rand = new Random();
            SimukraftReloaded.theCommodities.clear();
            int count = rand.nextInt(3) + 2;

            for (int it = 0; it < count; it++)
            {
                int index = rand.nextInt(availableItems.size() - 1);
                int qty = rand.nextInt(10) + 1;
                float price = 300f + rand.nextInt(300) + (rand.nextFloat() * 100f);
                boolean gotIt = false;

                for (int shit = 0; shit < SimukraftReloaded.theCommodities.size(); shit++)
                {
                    Commodity cshit = SimukraftReloaded.theCommodities.get(shit);

                    if (cshit.theItemStack.getDisplayName().contentEquals(availableItems.get(index).getDisplayName()))
                    {
                        gotIt = true;
                        break;
                    }
                }

                if (!gotIt)
                {
                	SimukraftReloaded.theCommodities.add(new Commodity(availableItems.get(index), qty, price));
                }
            }
        }

        /** called when the arraylist hasn't been set up yet */
        private static void setupAvailableItems()
        {
            availableItems.clear();
            availableItems.add(new ItemStack(Items.ender_pearl));
            availableItems.add(new ItemStack(Items.blaze_rod));
            availableItems.add(new ItemStack(Items.bone));
            availableItems.add(new ItemStack(Items.gunpowder));
            availableItems.add(new ItemStack(Items.slime_ball));
            availableItems.add(new ItemStack(Items.string));  //string
            availableItems.add(new ItemStack(Items.spider_eye));
        }
    }

    /** Used throughout mod as 3 dimension vector, as Minecraft's Vec3 is a bit weird, or I'm not very good at Java :-) */
    public static class V3 implements Serializable, Cloneable
    {
        private static final long serialVersionUID = 3681796724829797704L;
        public Double x;
        public Double y;
        public Double z;
        public String name = "";
        public int blockID = 0;
        public int meta = 0;  //currently used for building's special blocks (in cheese factory, burger store)
        public int theDimension = 0;
        public Double destinationAcc=1.5d;
        public boolean doNotTimeout = false;

        public V3()
        {
        }

        @Override
        public V3 clone()
        {
            V3 retV = new V3(this.x, this.y, this.z, this.theDimension);
            return retV;
        }

        /*
        public V3(Double x, Double y, Double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        */

        /** overload to include the dimension 0=overworld, -1=nether 1=end  2 onwards could be Mystcraft ages */
        public V3(Double x, Double y, Double z, int dimension)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.theDimension = dimension;
        }
        
        public V3(int x, int y, int z, int dimension) {
        	this.x = (double) x;
            this.y = (double) y;
            this.z = (double) z;
            this.theDimension = dimension;
        }

        /** overload for loading text-based saved files in as a V3 */
        public V3(String v3) {
        	String[]v=v3.split(",");
        	this.x=Double.parseDouble(v[0]);
        	this.y=Double.parseDouble(v[1]);
        	this.z=Double.parseDouble(v[2]);
        	this.theDimension=Integer.parseInt(v[3]);
        }
        
        // Used any more?
        public V3(Double x, Double y, Double z, int id, int meta)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockID = id;
            this.meta = meta;
        }

        public V3(int i, int j, int k)
        {
            this.x = (double) i;
            this.y = (double) j;
            this.z = (double) k;
        }

        public void setVals(V3 v)
        {
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
        }

        /** compares the x y and z to see if they are the same and only INT values, not double Also compares dimension */
        public boolean isSameCoordsAs(V3 comp, boolean compareDimension, boolean exactly)
        {
            boolean ret = false;

            if (comp == null)
            {
                return false;
            }

            if (exactly)
            {
                if (this.getDistanceTo(comp) == 0 && (this.theDimension == comp.theDimension || !compareDimension))
                {
                    ret = true;
                }
            }
            else
            {
                if (this.getDistanceTo(comp) <= 2 && (this.theDimension == comp.theDimension || !compareDimension))
                {
                    ret = true;
                }
            }

            return ret;
        }

        /** works out and return distance between this V3 and the passed in V3 - assumes same dimension*/
        public int getDistanceTo(V3 other)
        {
            if (other == null)
            {
                return 0;
            }

            double dist = Math.sqrt((other.x - this.x) * (other.x - this.x) +
                                    (other.y - this.y) * (other.y - this.y) +
                                    (other.z - this.z) * (other.z - this.z));
            return (int)dist;
        }

        public String toString()
        {
            return x.intValue() + "," + y.intValue() + "," + z.intValue()+","+theDimension;
        }
    }

    public void registerMisc()
    {
        //TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
        //TickRegistry.registerTickHandler(new CommonTickHandler(), Side.CLIENT);
    	FMLCommonHandler.instance().bus().register(new CommonTickHandler());
    	if (side == Side.CLIENT) {
    	FMLCommonHandler.instance().bus().register(new ClientTickHandler());}
    }

    @SideOnly(Side.CLIENT)
    public World getClientWorld()
    {
        return Minecraft.getMinecraft().theWorld;
    }
    
    /**
     * Returns a side-appropriate EntityPlayer for use during message handling
     */
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
     return ctx.getServerHandler().playerEntity;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}

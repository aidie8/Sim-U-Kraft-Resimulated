package ashjack.simukraftreloaded.blocks;

import java.util.ArrayList;

import ashjack.simukraftreloaded.blocks.functionality.Marker;
import ashjack.simukraftreloaded.client.Gui.blocks.GuiMarker;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedConfig;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedTabs;
import ashjack.simukraftreloaded.entity.EntityAlignBeam;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class BlockMarker extends Block implements IExtendedEntityProperties
{
	
    public static boolean hasPlaced = false;
    public static ArrayList<Marker> markers = new ArrayList<Marker>();
    public V3 location;

    private IIcon icons[];

    public BlockMarker()
    {
        super(Material.wood);
		this.setCreativeTab(SimukraftReloadedTabs.SUKTab);
        setBlockBounds(0.4f, 0.0f, 0.4f,  0.6f, 0.9f, 0.6f);
        this.setLightLevel(0.1f);
        setStepSound(Block.soundTypeWood);
        setHardness(2F);
        setResistance(1.0F);
        setBlockName("SUKmarker");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[1];
        icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockMarker");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return icons[0];
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0.4f, 0.0f, 0.4f,  0.6f, 0.9f, 0.6f); //post shaped
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int meta)
    {
        try
        {
            for (int m = 0; m < markers.size(); m++)
            {
                Marker marker = markers.get(m);

                for (int mm = 0; mm < 4; mm++)
                {
                    try
                    {
                        marker.beams.get(mm).setDead();
                    }
                    catch (Exception e) {}
                }
            }
        }
        catch (Exception e) {}  //index out of bounds when destroying too many

        markers.clear();
        super.onBlockDestroyedByPlayer(world, i, j, k, meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase player, ItemStack is)
    {
    	
    	hasPlaced = true;
        Marker ma;
        if(world.isRemote){
        markers.add(ma = new Marker(i, j, k, world.provider.dimensionId));
        
        String markerCaption = "";
        String helpText = "";
        
       

        if (markers.size() == 1)
        {
        	
            	markerCaption = "Front-Left";
                helpText = "You can place two more markers to mark out an area for a farm or mine etc. If you wish to do this, place another marker at the front-right position now";
                System.out.println(markers.size());
            
        }
        else if (markers.size() == 2)
        {
        	
        	markerCaption = "Front-Right";
            helpText = "Finally, place a marker at the Rear-Left position";
            System.out.println(markers.size());
            
        }
        else if (markers.size() == 3)
        {
        	
        	markerCaption = "Rear-Left";
    		helpText = "You're done, now you can place down a mining box, farming box or right-click the front-left marker to copy a structure!";
    		System.out.println(markers.size());
        }
        
        else
        {
        	System.out.println(markers.size());
            markerCaption = "Too many Markers!";
        }
        

        if (markers.size() < 4)
        {
            V3 pos = new V3((double)i, (double)j, (double)k, world.provider.dimensionId);
            pos.y += 0.01d;

            if (SimukraftReloadedConfig.configEnableMarkerAlignmentBeams)
            {
                EntityAlignBeam beam = new EntityAlignBeam(world);
                ma.caption = markerCaption;
                beam.setLocationAndAngles(pos.x, pos.y, pos.z, 0f, 0f);
                beam.yaw = 0f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam);
                }

                ma.beams.add(beam);
                EntityAlignBeam beam2 = new EntityAlignBeam(world);
                beam2.setLocationAndAngles(pos.x, pos.y, pos.z, 90f, 0f);
                beam2.yaw = 90f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam2);
                }

                ma.beams.add(beam2);
                EntityAlignBeam beam3 = new EntityAlignBeam(world);
                beam3.setLocationAndAngles(pos.x, pos.y, pos.z, 180f, 0f);
                beam3.yaw = 180f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam3);
                }

                ma.beams.add(beam3);
                EntityAlignBeam beam4 = new EntityAlignBeam(world);
                beam4.setLocationAndAngles(pos.x, pos.y, pos.z, 270f, 0f);
                beam4.yaw = 270f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam4);
                }

                ma.beams.add(beam4);
            }
        
        }

        if (!helpText.contentEquals(""))
        {
            SimukraftReloaded.sendChat(helpText);
        }

        super.onBlockPlacedBy(world, i, j, k, player, is);
        }
    }  
    
    
    
   /* public void onBlockAdded(World world, int i, int j, int k)
    {
    	if(!world.isRemote)
    	{
        hasPlaced = true;
        Marker ma;
        markers.add(ma = new Marker(i, j, k, world.provider.dimensionId));
        String markerCaption = "";
        String helpText = "";
        
       

        if (markers.size() == 1)
        {
        	
            	markerCaption = "Front-Left";
                helpText = "You can place two more markers to mark out an area for a farm or mine etc. If you wish to do this, place another marker at the front-right position now";
                System.out.println(markers.size());
            
        }
        else if (markers.size() == 2)
        {
        	
        	markerCaption = "Front-Right";
            helpText = "Finally, place a marker at the Rear-Left position";
            System.out.println(markers.size());
            
        }
        else if (markers.size() == 3)
        {
        	
        	markerCaption = "Rear-Left";
    		helpText = "You're done, now you can place down a mining box, farming box or right-click the front-left marker to copy a structure!";
    		System.out.println(markers.size());
        }
        
        else
        {
        	System.out.println(markers.size());
            markerCaption = "Too many Markers!";
        }
        

        if (markers.size() < 4)
        {
            V3 pos = new V3((double)i, (double)j, (double)k, world.provider.dimensionId);
            pos.y += 0.01d;

            if (ModSimukraft.configEnableMarkerAlignmentBeams)
            {
                EntityAlignBeam beam = new EntityAlignBeam(world);
                ma.caption = markerCaption;
                beam.setLocationAndAngles(pos.x, pos.y, pos.z, 0f, 0f);
                beam.yaw = 0f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam);
                }

                ma.beams.add(beam);
                EntityAlignBeam beam2 = new EntityAlignBeam(world);
                beam2.setLocationAndAngles(pos.x, pos.y, pos.z, 90f, 0f);
                beam2.yaw = 90f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam2);
                }

                ma.beams.add(beam2);
                EntityAlignBeam beam3 = new EntityAlignBeam(world);
                beam3.setLocationAndAngles(pos.x, pos.y, pos.z, 180f, 0f);
                beam3.yaw = 180f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam3);
                }

                ma.beams.add(beam3);
                EntityAlignBeam beam4 = new EntityAlignBeam(world);
                beam4.setLocationAndAngles(pos.x, pos.y, pos.z, 270f, 0f);
                beam4.yaw = 270f;

                if (!world.isRemote)
                {
                    world.spawnEntityInWorld(beam4);
                }

                ma.beams.add(beam4);
            }
        
        }

        if (!helpText.contentEquals(""))
        {
            SimukraftReloaded.sendChat(helpText);
        }

        super.onBlockAdded(world, i, j, k);
    	}  
    }*/

    public static Marker getMarker(V3 position)
    {
        Marker ret = null;

        for (int i = 0; i < markers.size(); i++)
        {
            Marker m = markers.get(i);

            if (m.x == position.x && m.y == position.y && m.z == position.z)
            {
                ret = m;
                break;
            }
        }

        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean onBlockActivated(World world, int i, int j, int k,
                                    EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        this.location = new V3((double)i, (double)j, (double)k, entityplayer.dimension);
        world.playSoundEffect(i, j, k, "ashjacksimukraftreloaded:computer", 1f, 1f);
        GuiMarker ui = new GuiMarker(this.location, entityplayer);
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(ui);
        return true;
    }

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}
}

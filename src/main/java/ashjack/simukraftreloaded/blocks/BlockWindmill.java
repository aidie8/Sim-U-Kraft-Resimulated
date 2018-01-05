package ashjack.simukraftreloaded.blocks;

import java.util.ArrayList;
import java.util.List;

import ashjack.simukraftreloaded.client.Gui.blocks.GuiWindmill;
import ashjack.simukraftreloaded.common.jobs.Job;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWindmill extends BlockContainer {

	protected BlockWindmill(Material p_i45386_1_) {
		super(p_i45386_1_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		// TODO Auto-generated method stub
		return null;
	}

/*	private IIcon icons[];
	
	
	public BlockWindmill(int par1, boolean par2) {
		super(Material.wood);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	 @SideOnly(Side.CLIENT)
	    public void registerIcons(IIconRegister iconRegister) {
	    	icons=new IIcon[1];
	    	icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockWindmill");
	    }

	    @Override
	    @SideOnly(Side.CLIENT)
	    public IIcon getIcon(int side, int meta) {
	    	return icons[0];
	    }

		@Override
		public boolean canPlaceBlockAt(World par1World, int x, int y, int z) {
			ArrayList<V3> blocks=Job.findClosestBlocks(new V3(x,y,z), ModSimukraft.windmill, 6);
			if (blocks==null || blocks.size()==0) {
				return true;
			} else {
				return false;
			}
		}
		
	    @Override
		public void onBlockPlacedBy(World world, int x, int y,
				int z, EntityLivingBase player, ItemStack is) {
	    	
	    	if (!world.isRemote) {
	    		float xx=x;
	    		float zz=z;
	    		
		    	float yaw=player.rotationYaw+180;  // 0 to 360
		    	yaw=(int) Math.round(yaw / 90) * 90;
		    	if (yaw>=360) { yaw-=360; }
		    	SimukraftReloaded.log.info("BlockWindmill placed yaw="+yaw);
		    	if (yaw==0) {xx+=2.68f; zz-=2.68f;}
		    	if (yaw==90) { xx+=3.68; zz+=2.68;  }
		    	if (yaw==180 || yaw==-180) {xx-=1.68; zz+=3.68; }
		    	if (yaw==270 || yaw==-90) { zz-=1.68; xx-=2.68;  }
		    	
		    	EntityWindmill entity=new EntityWindmill(world);
				entity.setLocationAndAngles(xx,y+1,zz,yaw,0f);
				
			
				world.spawnEntityInWorld(entity);
			}
	    	
			TileEntityWindmill teWindmill = (TileEntityWindmill)world.getTileEntity(x,y,z);
			if (teWindmill !=null) {
				teWindmill.setMeta(is.getItemDamage());
			}
			super.onBlockPlacedBy(world, x,y,z, player,	is);
		}

	    
	    
	    
	    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y,int z, int metadata, int fortune) {
			
	    	try {
				TileEntityWindmill te=(TileEntityWindmill) world.getTileEntity(x, y, z);
				ArrayList<ItemStack> is=new ArrayList<ItemStack>();
				if (te !=null) {
					is.add(new ItemStack(ModSimukraft.windmill,1,te.meta));
				} else {
					is.add(new ItemStack(ModSimukraft.windmill,1,0));
				}
				return is;
	    	} catch(Exception e) {e.printStackTrace();}
			
	    	return null;
		}

		public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
	    {
	    	TileEntityWindmill te = (TileEntityWindmill)par1World.getTileEntity(par2, par3, par4);
	    	if (te !=null && te.getStackInSlot(0) !=null) {
	    		EntityItem ei=new EntityItem(par1World,par2,par3,par4,te.getStackInSlot(0).copy()); 
				par1World.spawnEntityInWorld(ei);
        		SimukraftReloaded.log.info("Windmill breakBlock dropped items");
        	}
	    	
	   }
	    
	    @Override
	    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {

	    	List list = world.getEntitiesWithinAABBExcludingEntity(
                        Minecraft.getMinecraft().thePlayer,
                        AxisAlignedBB.getBoundingBox(x,y,z,
                                x + 1.0D, y + 1.0D, z + 1.0D).expand(1.0d, 1.0d,1.0d));
	    	for (int j = 0; j < list.size(); j++)
	        {
	            Entity entity1 = (Entity) list.get(j);

	            if (entity1 instanceof EntityWindmill)
	            {
	            	entity1.setDead();
	            	break;
	            }
	        }
	    	
	    	world.playSoundEffect(x,y,z,"ashjacksimukraftreloaded:powerdown",1f,1f);

	    	super.onBlockDestroyedByPlayer(world, x, y, z,meta);
	    }

	    
	    
	    @Override
		public void onBlockDestroyedByExplosion(World par1World, int par2,
				int par3, int par4, Explosion par5Explosion) {
	    	this.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 0);
			super.onBlockDestroyedByExplosion(par1World, par2, par3, par4, par5Explosion);
		}

		@Override
	    @SideOnly(Side.CLIENT)
	    public boolean onBlockActivated(World world, int i, int j, int k,
	    		EntityPlayer entityplayer, int par6, float par7,float par8, float par9) {
	    	
	    		world.playSoundEffect(i,j,k,"ashjacksimukraftreloaded:computer",1f,1f);
	    		
	            if (world.isRemote)
	            {
	                return true;
	            }
	            else
	            {
	                TileEntityWindmill tileentitywindmill = (TileEntityWindmill)world.getTileEntity(i,j,k);

	                if (tileentitywindmill != null)
	                {
	                	GuiScreen ui = new GuiWindmill(entityplayer,tileentitywindmill);
	                    Minecraft.getMinecraft().displayGuiScreen(ui);
	                }

	                return true;
	            }
	    }

	    
		public TileEntity createNewTileEntity(World world) {
			return new TileEntityWindmill();
		}

		@Override
		public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
			// TODO Auto-generated method stub
			return null;
		}
	    */
}

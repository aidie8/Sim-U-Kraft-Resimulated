package ashjack.simukraftreloaded.blocks.gases;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGas extends Block
{
	public int riseRate = 5;
	
	public BlockGas() 
	{
		super(Material.snow);
	}
	
	public void setRiseRate(int rate)
	{
		riseRate = rate;
	}
	
	  public boolean renderAsNormalBlock()
	  {
	    return false;
	  }
	  
	  public boolean isOpaqueCube()
	  {
	    return false;
	  }
	  
	  public boolean canCollideCheck(int par1, boolean par2)
	  {
	    return (par2) && (par1 == 0);
	  }
	  
	  public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	  {
	    return false;
	  }
	  
	  protected boolean enforceTicking()
	  {
	    return false;
	  }
	  
	  protected int getDelayForUpdate(World world, int x, int y, int z)
	  {
		  return 1;
	  }
	  
	  public void updateTick(World world, int i, int j, int k, Random random)
	  {
		  if(j+1 < 255)
		  {
			  world.setBlockToAir(i, j, k);
			  world.setBlock(i, j+1, k, this);
		  }
		  else
		  {
			  world.setBlockToAir(i, j, k);
		  }
	  }
	  
	  public void onBlockAdded(World par1World, int par2, int par3, int par4)
	  {
	      par1World.scheduleBlockUpdate(par2, par3, par4, this, riseRate);
	  }

}

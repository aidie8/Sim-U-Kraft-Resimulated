package ashjack.simukraftreloaded.blocks;

import java.util.Random;

import ashjack.simukraftreloaded.core.registry.SimukraftReloadedTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;


public class BlockLivingBlock extends BlockCarpet{
	
	private IIcon icons[];

	/*public Material getMaterialType()
	{
		Material blockMat = null;
		
		return blockMat;
	}*/
	
	public BlockLivingBlock() 
	{
		setHardness(0F);
		setResistance(0F);
		setBlockName("SUKlivingBlock");
		this.setCreativeTab(SimukraftReloadedTabs.SUKTab);
	}
	
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return null;
    }
	
	/*@SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
		icons = new IIcon[23];
		icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWood");
		icons[1] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingCobblestone");
		icons[2] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingStone");
		icons[3] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingGrass");
		icons[4] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingStoneBrick");
		icons[5] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingBrick");
		icons[6] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingStoneSlab");
		
		icons[7] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolWhite");
		icons[8] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolOrange");
		icons[9] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolMagenta");
		icons[10] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolLightBlue");
		icons[11] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolYellow");
		icons[12] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolLime");
		icons[13] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolPink");
		icons[14] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolGray");
		icons[15] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolLightGray");
		icons[16] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolCyan");
		icons[17] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolPurple");
		icons[18] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolBlue");
		icons[19] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolBrown");
		icons[20] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolGreen");
		icons[21] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolRed");
		icons[22] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockLivingWoolBlack");
		
    }*/

}

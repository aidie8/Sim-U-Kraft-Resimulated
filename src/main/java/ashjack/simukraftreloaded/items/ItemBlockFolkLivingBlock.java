package ashjack.simukraftreloaded.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockFolkLivingBlock extends ItemBlock
{
	public ItemBlockFolkLivingBlock(Block block) 
	{
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		par3List.add("Place this when marking a");
		par3List.add("build to specify where a folk");
		par3List.add("will stand at night");
		super.addInformation(par1ItemStack, par2EntityPlayer,par3List , par4);
	}
}

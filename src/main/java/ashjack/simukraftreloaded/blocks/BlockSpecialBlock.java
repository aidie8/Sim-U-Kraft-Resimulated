package ashjack.simukraftreloaded.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockSpecialBlock extends Block
{
	
	
	public BlockSpecialBlock() {
		super(Material.air);
		this.setBlockName("blockspecial");
        this.setHardness(100.0F);
        this.setResistance(100.0F);
	}


	//Symbols
	//3: %
	//5: |
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
	    for (int i = 0; i < 8; i ++) {
	        list.add(new ItemStack(item, 1, i));
	    }
	}
}

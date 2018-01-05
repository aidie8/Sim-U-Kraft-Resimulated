package ashjack.simukraftreloaded.items;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockWindmill extends ItemBlock {
	
	public ItemBlockWindmill(int par1, Block par2Block) {
        super(par2Block);
        setHasSubtypes(true);
		
	}
	
	
    public String getItemDisplayName(ItemStack is) {
    	if (is.getItemDamage()==0) {
    		return "Sim-U-Windmill";
    	} else {
    		return "Sim-U-Windmill (colour)";
    	}
	}


	@Override
    public String getUnlocalizedName(ItemStack is)
    {
		return "item.SUKwindmill"+is.getItemDamage();
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		par3List.add("Granulates ores and generates Buildcraft energy");
		super.addInformation(par1ItemStack, par2EntityPlayer,par3List , par4);
	}
    
    public int getMetadata(int par1)
    {
        return par1;
    }

}

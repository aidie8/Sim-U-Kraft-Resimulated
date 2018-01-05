package ashjack.simukraftreloaded.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemWindmillSails extends Item {

	private IIcon icons[];
	
	public ItemWindmillSails() {
		super();
	    maxStackSize = 64;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		icons=new IIcon[16];
		for(int i=0;i<16;i++) {
			icons[i] = iconRegister.registerIcon("ashjacksimukraftreloaded:windmillsails"+i);
		}
	}
	
	@Override
	public IIcon getIconFromDamage(int meta)
	{
		if (meta >=0 && meta < 16) {
			return icons[meta];
		} else {
			return icons[0];
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
	    for (int x = 0; x < 16; x++)
	    {
	        par3List.add(new ItemStack(this, 1, x));
	    }
	}

	@Override
    public String getUnlocalizedName(ItemStack is)
    {
        return "item.windmillsails"+is.getItemDamage();
    }
	
	public String getItemDisplayName(ItemStack par1ItemStack) {
		return "Windmill sails";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		par3List.add("Craft a windmill base to make a windmill");
		super.addInformation(par1ItemStack, par2EntityPlayer,par3List , par4);
	}
	
	@Override
    public int getMetadata(int par1)
    {
        return par1;
    }

}

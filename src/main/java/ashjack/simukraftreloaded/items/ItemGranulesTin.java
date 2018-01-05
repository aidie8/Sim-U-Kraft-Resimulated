package ashjack.simukraftreloaded.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

//NOTE: Like the copper one, Never used this
public class ItemGranulesTin extends Item {
	private IIcon icons[];
	
	public ItemGranulesTin(int par1) {
		super();
		maxStackSize = 64;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		icons=new IIcon[1];
		icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:granulesTin");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return icons[0];
	}

	public String getItemDisplayName(ItemStack par1ItemStack) {
		return "Tin granules";
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return icons[0];
	}
}

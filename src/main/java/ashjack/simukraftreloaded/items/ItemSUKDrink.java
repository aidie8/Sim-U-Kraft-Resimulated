package ashjack.simukraftreloaded.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemSUKDrink extends ItemBucketMilk{

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public ItemSUKDrink() {
		super();    // 6 half-hearts when ate    0.6f is default for ItemFood
		this.setCreativeTab(CreativeTabs.tabFood);
		setHasSubtypes(true);
	}
	
	public static final String[] names = new String[] {"drinkBeerEmpty", "drinkBeer"};
	
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
	       icons = new IIcon[names.length];
	             
	       for(int i = 0; i < icons.length; i++)
	       {
	           icons[i] = par1IconRegister.registerIcon("ashjacksimukraftreloaded:" + names[i]);
	       }
	}
	
	@Override
	public IIcon getIconFromDamage(int meta)
	{
		if (meta >=0 && meta < names.length) {
			return icons[meta];
		} else {
			return null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
	    for (int x = 0; x < names.length; x++)
	    {
	        par3List.add(new ItemStack(this, 1, x));
	    }
	}
	
	
	 @Override
	    public String getUnlocalizedName(ItemStack is)
	    {
	        if (is.getItemDamage() == 0)
	        {
	            return "item.drinkBeerEmpty";
	        }
	        else if (is.getItemDamage() == 1)
	        {
	            return "item.drinkBeer";
	        }
	        else
	        {
	            return null;
	        }
	    }

	@Override
	    public int getMetadata(int par1)
	    {
	        return par1;
	    }
	
	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer player)
    {
		player.addPotionEffect(new PotionEffect(Potion.confusion.id,100,2));
		return new ItemStack(is.getItem(),1,0);
    }
	
}

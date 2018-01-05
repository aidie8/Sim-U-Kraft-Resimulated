package ashjack.simukraftreloaded.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockLightBox extends ItemBlock
{

    public ItemBlockLightBox(Block par1)
    {
        super(par1);
        setHasSubtypes(true);
    }

    /*@Override
    public String getUnlocalizedName(ItemStack is)
    {
        if (is.getItemDamage() == 0)
        {
            return "tile.blockSUKLight.white";
        }
        else if (is.getItemDamage() == 1)
        {
            return "tile.blockSUKLight.red";
        }
        else if (is.getItemDamage() == 2)
        {
            return "tile.blockSUKLight.orange";
        }
        else if (is.getItemDamage() == 3)
        {
            return "tile.blockSUKLight.yellow";
        }
        else if (is.getItemDamage() == 4)
        {
            return "tile.blockSUKLight.green";
        }
        else if (is.getItemDamage() == 5)
        {
            return "tile.blockSUKLight.blue";
        }
        else if (is.getItemDamage() == 6)
        {
            return "tile.blockSUKLight.purple";
        }
        else if (is.getItemDamage() == 7)
        {
            return "tile.blockSUKLight.rainbow";
        }
        else
        {
            return null;
        }
    }*/
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + "_" + stack.getItemDamage();
    }

    @Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		par3List.add("Light up your world!");
		super.addInformation(par1ItemStack, par2EntityPlayer,par3List , par4);
	}
    
    public int getMetadata(int par1)
    {
        return par1;
    }
}

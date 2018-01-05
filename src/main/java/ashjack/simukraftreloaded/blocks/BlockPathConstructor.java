package ashjack.simukraftreloaded.blocks;

import java.util.ArrayList;

import ashjack.simukraftreloaded.client.Gui.blocks.GuiBuildingConstructor;
import ashjack.simukraftreloaded.client.Gui.blocks.GuiMining;
import ashjack.simukraftreloaded.client.Gui.blocks.GuiPathBox;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPathConstructor extends Block
{
    private IIcon icons[];

    public BlockPathConstructor(int par1)
    {
        super(Material.wood);
		//this.setCreativeTab(SimukraftReloadedTabs.SUKTab);
    }

    /*
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
    	icons=new Icon[1];
    	icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockPath");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta) {
    	return icons[0];
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
    	PathBox m;
    	ModSimukraft.thePathBoxes.add(m=new PathBox(new V3((double)i,(double)j,(double)k,world.provider.dimensionId)));

    	if (BlockMarker.markers.size()==1) {
    		m.marker1XYZ= BlockMarker.markers.get(0).toV3();
    	} else {
    		try {
    			int first=BlockMarker.markers.size() - 3;
    			m.marker1XYZ= BlockMarker.markers.get(first).toV3();
    		} catch(Exception e) {}  //if there are none
    	}
    	super.onBlockAdded(world, i, j, k);
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int meta) {
    	FolkData theFolk=FolkData.getFolkByEmployedAt(new V3((double)i,(double)j,(double)k
    			,world.provider.dimensionId));

    	if (theFolk !=null) {
    		theFolk.selfFire();
    	}

    	PathBox m;
    	m=PathBox.getPathBlockByBoxXYZ(new V3(i,j,k));
    	ModSimukraft.thePathBoxes.remove(m);
    	world.playSoundEffect(i,j,k,"ashjacksimukraftreloaded:powerdown",1f,1f);

    	super.onBlockDestroyedByPlayer(world, i, j, k,meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean onBlockActivated(World world, int i, int j, int k,
    		EntityPlayer entityplayer, int par6, float par7,float par8, float par9) {
    		world.playSoundEffect(i,j,k,"ashjacksimukraftreloaded:computer",1f,1f);
    		PathBox miningBlock  =PathBox.getPathBlockByBoxXYZ(new V3((double)i,(double)j,(double)k,entityplayer.dimension));
    		try {
    			miningBlock.location.theDimension=entityplayer.dimension;

    			ArrayList<FolkData> folks=FolkData.getFolksByEmployedAt(new V3((double)i,(double)j,(double)k,entityplayer.dimension));

    			GuiPathBox ui=new GuiPathBox(miningBlock,folks);

    	    	Minecraft mc=ModLoader.getMinecraftInstance();
    	       	mc.displayGuiScreen(ui);
    		} catch(Exception e) {
    			e.printStackTrace();
    			if (world.isRemote) {
    				SimukraftReloaded.sendChat("Sorry, there was a problem with this Path box, try place it again");
    			}
    		}
        return true;
    }
    */
}

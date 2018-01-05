package ashjack.simukraftreloaded.core.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import ashjack.simukraftreloaded.blocks.BlockCheeseBlock;
import ashjack.simukraftreloaded.blocks.BlockCityBox;
import ashjack.simukraftreloaded.blocks.BlockCompositeBrick;
import ashjack.simukraftreloaded.blocks.BlockConstructorBox;
import ashjack.simukraftreloaded.blocks.BlockControlBox;
import ashjack.simukraftreloaded.blocks.BlockFarmingBox;
import ashjack.simukraftreloaded.blocks.BlockFluidMilk;
import ashjack.simukraftreloaded.blocks.BlockLightBox;
import ashjack.simukraftreloaded.blocks.BlockLivingBlock;
import ashjack.simukraftreloaded.blocks.BlockMarker;
import ashjack.simukraftreloaded.blocks.BlockMiningBox;
import ashjack.simukraftreloaded.blocks.BlockSpecialBlock;
import ashjack.simukraftreloaded.blocks.functionality.FluidMilk;
import ashjack.simukraftreloaded.blocks.functionality.TileEntityWindmill;
import ashjack.simukraftreloaded.items.ItemBlockFolkLivingBlock;
import ashjack.simukraftreloaded.items.ItemBlockLightBox;
import ashjack.simukraftreloaded.items.ItemSUKDrink;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;

public class SimukraftReloadedBlocks 
{
	
	public static Block livingBlock;
    public static Block buildingConstructor;
    public static Block controlBox;
    public static Block marker;
    public static Block miningBox;
    public static Block farmingBox;
    public static Block lightBox;
    public static Block specialBlock;
    public static Block cityBox;
    
    static Block lightBoxRed;
    static Block lightBoxOrange;
    static Block lightBoxYellow;
    static Block lightBoxGreen;
    static Block lightBoxBlue;
    static Block lightBoxPurple;
    
    public static Block blockCompositeBrick;
    public static Block blockCheese;
    public static Fluid SUKfluidMilk;
    public static Block blockFluidMilk;
	
	public static void loadBlocks()
	{
		//Fluids
		SUKfluidMilk=new FluidMilk();
        blockFluidMilk=new BlockFluidMilk().setBlockName("fluidMilk");

        //'Special' Blocks
        livingBlock = new BlockLivingBlock();
        specialBlock = new BlockSpecialBlock();
        
        //Blocks
        buildingConstructor = new BlockConstructorBox();
        controlBox = new BlockControlBox();
        cityBox = new BlockCityBox();
        marker = new BlockMarker();
        miningBox = new BlockMiningBox();
        farmingBox = new BlockFarmingBox();
        lightBox = new BlockLightBox();
        
        //Pretty Blocks
        blockCompositeBrick = new BlockCompositeBrick();
        blockCheese=new BlockCheeseBlock();
        
      //windmill = new BlockWindmill(windmillId, false).
      //setStepSound(Block.soundTypeWood).setHardness(3F).setResistance(3.0F).setBlockName("SUKWindmill");

      //pathConstructor = new BlockPathConstructor(pathConstructorId).
      //setStepSound(Block.soundWoodFootstep).setHardness(2F).setResistance(1.0F).setUnlocalizedName("SUKpathConstructor");
      //OreDictionary.registerOre("dustCopper", itemGranulesCopper);
      //OreDictionary.registerOre("dustTin", itemGranulesTin);
        
        registerBlocks();
	}
	
	public static void registerBlocks()
	{
		GameRegistry.registerBlock(livingBlock, ItemBlockFolkLivingBlock.class, "SUKlivingBlock");
        GameRegistry.registerBlock(buildingConstructor, "SUKconstructorBox");
        GameRegistry.registerBlock(controlBox, "SUKcontrol");
        GameRegistry.registerBlock(cityBox, "SUKcitybox");
        GameRegistry.registerBlock(marker, "SUKmarker");
        GameRegistry.registerBlock(miningBox, "SUKmining");
        GameRegistry.registerBlock(farmingBox, "SUKfarming");
        GameRegistry.registerBlock(blockCompositeBrick,"SUKcompositebrick");
        GameRegistry.registerBlock(blockCheese,"SUKcheeseblock");
        GameRegistry.registerBlock(blockFluidMilk,"fluidMilk");
        GameRegistry.registerBlock(lightBox, ItemBlockLightBox.class, "SUKlight");
        GameRegistry.registerBlock(specialBlock, "SUKspecial");
        //GameRegistry.registerBlock(pathConstructor,"SUKpathConstructor");
        GameRegistry.registerTileEntity(TileEntityWindmill.class, "tileentitywindmill");
        
	}
}

package ashjack.simukraftreloaded.core.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import ashjack.simukraftreloaded.blocks.gases.BlockCarbonDioxide;
import ashjack.simukraftreloaded.blocks.gases.BlockGas;
import ashjack.simukraftreloaded.blocks.gases.BlockRadiationGas;
import ashjack.simukraftreloaded.blocks.gases.BlockSulphurDioxide;
import net.minecraft.block.Block;

public class SimukraftReloadedGases 
{
	public static BlockGas blockCarbonDioxide;
	public static BlockGas blockSulphurDioxide;
	public static BlockGas blockRadiationGas;
	
	public static void loadGases()
	{
		blockCarbonDioxide = new BlockCarbonDioxide();
		blockSulphurDioxide = new BlockSulphurDioxide();
		blockRadiationGas = new BlockRadiationGas();
		registerGases();
		nameGases();
	}
	
	public static void registerGases()
	{
		GameRegistry.registerBlock(blockCarbonDioxide, "SUKCO2");
		GameRegistry.registerBlock(blockSulphurDioxide, "SUKSO2");
		GameRegistry.registerBlock(blockRadiationGas, "SUKRadiationGas");
	}
	
	public static void nameGases()
	{
		LanguageRegistry.addName(blockCarbonDioxide, "Carbon Dioxide");
		LanguageRegistry.addName(blockCarbonDioxide, "Sulfur Dioxide");
		LanguageRegistry.addName(blockRadiationGas, "Radiation");
	}
}

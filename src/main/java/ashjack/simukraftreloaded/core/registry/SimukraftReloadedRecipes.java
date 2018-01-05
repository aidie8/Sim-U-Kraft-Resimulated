package ashjack.simukraftreloaded.core.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class SimukraftReloadedRecipes 
{
	public static void registerRecipes()
	{
		GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.buildingConstructor, 1), new Object[]
                {
                    "PPP", "CWC", "CCC",
                    'C', Blocks.cobblestone,
                    'P', Blocks.planks,
                    'W', Blocks.crafting_table
                });
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.marker, 3), new Object[]
                {
                    "G", "S",
                    'S', Items.stick,
                    'G', new ItemStack(Items.dye, 1, 11)
                });

if (SimukraftReloadedConfig.configUseExpensiveRecipies)
{
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.miningBox, 1), new Object[]
                    {
                        "PPP", "CWC", "CCC",
                        'C', Blocks.cobblestone,
                        'P', Blocks.planks,
                        'W', Items.diamond_pickaxe
                    });
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.farmingBox, 1), new Object[]
                    {
                        "PPP", "CWC", "CCC",
                        'C', Blocks.cobblestone,
                        'P', Blocks.planks,
                        'W', Items.diamond_hoe
                    });
}
else
{
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.miningBox, 1), new Object[]
                    {
                        "PPP", "CWC", "CCC",
                        'C', Blocks.cobblestone,
                        'P', Blocks.planks,
                        'W', Items.stone_pickaxe
                    });
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.farmingBox, 1), new Object[]
                    {
                        "PPP", "CWC", "CCC",
                        'C', Blocks.cobblestone,
                        'P', Blocks.planks,
                        'W', Items.stone_hoe
                    });
}

GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 2), new Object[]
                {
                    "LL", "LL",
                    'L', Blocks.torch
                });
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 1, 1), new Object[]
                         {
	SimukraftReloadedBlocks.lightBox, new ItemStack(Items.dye, 1, 1)
                         });
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 1, 2), new Object[]
                         {
	SimukraftReloadedBlocks.lightBox, new ItemStack(Items.dye, 1, 14)
                         });
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 1, 3), new Object[]
                         {
	SimukraftReloadedBlocks.lightBox, new ItemStack(Items.dye, 1, 11)
                         });
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 1, 4), new Object[]
                         {
	SimukraftReloadedBlocks.lightBox, new ItemStack(Items.dye, 1, 10)  //lime
                         });
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 1, 5), new Object[]
                         {
	SimukraftReloadedBlocks.lightBox, new ItemStack(Items.dye, 1, 4)
                         });
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 1, 6), new Object[]
                         {
	SimukraftReloadedBlocks.lightBox, new ItemStack(Items.dye, 1, 5)
                         });
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedBlocks.lightBox, 1, 7), new Object[]
                         {
	SimukraftReloadedBlocks.lightBox, new ItemStack(Items.dye, 1, 1),
                             new ItemStack(Items.dye, 1, 14),
                             new ItemStack(Items.dye, 1, 11),
                             new ItemStack(Items.dye, 1, 10),
                             new ItemStack(Items.dye, 1, 4),
                             new ItemStack(Items.dye, 1, 5)
                         });

GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.blockCheese,1), new Object[] {
"CCC","CCC","CCC",
'C', new ItemStack(SimukraftReloadedItems.itemFood,1,0)
});
GameRegistry.addShapelessRecipe(new ItemStack(SimukraftReloadedItems.itemFood,9,0), new Object[] {
new ItemStack(SimukraftReloadedBlocks.blockCheese)
});

/*
GameRegistry.addRecipe(new ItemStack(pathConstructor, 1), new Object[]{
"PPP", "CWC", "CCC",
'C', Block.cobblestone,
'P', Block.planks,
'W', buildingConstructor
});
*/

GameRegistry.addRecipe(new ItemStack(SimukraftReloadedBlocks.blockCompositeBrick, 1), new Object[]{
"CSC", "SIS", "CSC",
'C', Blocks.hardened_clay,   
'S', Blocks.stone,
'I', Blocks.fence
});  


/*GameRegistry.addRecipe(new ItemStack(SimukraftReloadedItems.itemWindmillBase), new Object[]
{
" C ", 
"CCC",
"CCC",  
'C', SimukraftReloadedBlocks.blockCompositeBrick
});

for (int c=0;c<16;c++) {
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedItems.itemWindmillVane,1,c), new Object[]
 {
  "WWW",
  "SSS",  
  'S', Items.stick, 'W', new ItemStack(Blocks.wool,1,c)
 });
}

for(int c=0;c<16;c++) {
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedItems.itemWindmillSails,1,c), new Object[]
 {
  " V ", 
  "VPV",
  " V ",  
  'V', new ItemStack(SimukraftReloadedItems.itemWindmillVane,1,c) , 'P', Blocks.planks
 });
}

for(int c=0;c<16;c++) {
GameRegistry.addRecipe(new ItemStack(SimukraftReloadedItems.windmill,1,c), new Object[]
{
"S", 
"B", 
'S', new ItemStack(SimukraftReloadedItems.itemWindmillSails,1,c), 'B', SimukraftReloadedItems.itemWindmillBase
});
}

GameRegistry.addSmelting(SimukraftReloadedItems.itemGranulesGold, new ItemStack(Items.gold_ingot), 0.1f);
GameRegistry.addSmelting(SimukraftReloadedItems.itemGranulesIron, new ItemStack(Items.iron_ingot), 0.1f);*/
	}
}

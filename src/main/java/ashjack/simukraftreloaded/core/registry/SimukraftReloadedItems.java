package ashjack.simukraftreloaded.core.registry;

import ashjack.simukraftreloaded.items.ItemGranulesGold;
import ashjack.simukraftreloaded.items.ItemGranulesIron;
import ashjack.simukraftreloaded.items.ItemSUKDrink;
import ashjack.simukraftreloaded.items.ItemSUKFood;
import ashjack.simukraftreloaded.items.ItemWindmillBase;
import ashjack.simukraftreloaded.items.ItemWindmillSails;
import ashjack.simukraftreloaded.items.ItemWindmillVane;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SimukraftReloadedItems 
{
	
	//static Block pathConstructor;
    // static int pathConstructorId = 0;
	public static Block windmill;
    public static Item itemGranulesIron;
    public static Item itemGranulesGold;
    public static Item itemWindmillBase;
    public static Item itemWindmillVane;
    public static Item itemWindmillSails;
    //public static Item itemGranulesCopper;
    //public static int itemGranulesCopperId;
    //public static Item itemGranulesTin;
    //public static int itemGranulesTinId;
    public static Item itemFood;
    public static Item itemFoodCheese;
    public static Item itemFoodFries;
    public static Item itemFoodBurger;
    public static Item itemFoodCheeseburger;
    
    public static Item itemDrink;
    public static Item itemDrinkBeer;
	
	public static void loadItems()
	{
		itemDrink = new ItemSUKDrink().setUnlocalizedName("SUKdrink");
		
		itemGranulesGold=new ItemGranulesGold();
		itemGranulesIron=new ItemGranulesIron();
	//  itemGranulesCopper=new ItemGranulesCopper(itemGranulesCopperId); 
	//  itemGranulesTin=new ItemGranulesTin(itemGranulesTinId); 
		
		itemWindmillBase=new ItemWindmillBase(); 
		itemWindmillVane=new ItemWindmillVane(); 
		itemWindmillSails=new ItemWindmillSails(); 
	//	Item.itemsList[lightBox] = new ItemBlockLightBox(lightBox, lightBox);
	//	Item.itemsList[windmillId] = new ItemBlockWindmill(windmillId - 256, windmill);

		registerItems();
		nameItems();
	}
	
	public static void registerItems()
	{
		GameRegistry.registerItem(itemFood = new ItemSUKFood("SUKfood"), "SUKfood");
	}
	
	public static void nameItems()
	{
		LanguageRegistry.addName(itemGranulesGold, "Gold granules"); 
		LanguageRegistry.addName(itemGranulesIron, "Iron granules"); 
	//	LanguageRegistry.addName(itemGranulesCopper, "dustCopper"); 
	//	LanguageRegistry.addName(itemGranulesTin, "Tin granules"); 
        
		LanguageRegistry.addName(itemWindmillBase, "Windmill base"); 
		LanguageRegistry.addName(itemWindmillVane, "Windmill vane"); 
		LanguageRegistry.addName(itemWindmillBase, "Windmill sails"); 
		
		LanguageRegistry.addName(new ItemStack(itemFood, 1, 0), "Cheese Slice");
        LanguageRegistry.addName(new ItemStack(itemFood, 1, 1), "Hamburger");
        LanguageRegistry.addName(new ItemStack(itemFood, 1, 2), "Fries");
        LanguageRegistry.addName(new ItemStack(itemFood, 1, 3), "Cheeseburger");
        
        LanguageRegistry.addName(new ItemStack(itemDrink, 1, 0), "Empty Beer Bottle");
        LanguageRegistry.addName(new ItemStack(itemDrink, 1, 1), "Beer Bottle");
	}
}

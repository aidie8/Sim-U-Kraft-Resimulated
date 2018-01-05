package ashjack.simukraftreloaded.core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import ashjack.simukraftreloaded.blocks.BlockCheeseBlock;
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
import ashjack.simukraftreloaded.blocks.functionality.FarmingBox;
import ashjack.simukraftreloaded.blocks.functionality.FluidMilk;
import ashjack.simukraftreloaded.blocks.functionality.MiningBox;
import ashjack.simukraftreloaded.blocks.functionality.TileEntityWindmill;
import ashjack.simukraftreloaded.client.ClientTickHandler;
import ashjack.simukraftreloaded.client.Gui.GuiHandler;
import ashjack.simukraftreloaded.client.Gui.other.GuiRunMod;
import ashjack.simukraftreloaded.commands.CommandChangeCredits;
import ashjack.simukraftreloaded.commands.CommandGenerateFolk;
import ashjack.simukraftreloaded.common.CommonTickHandler;
import ashjack.simukraftreloaded.common.CourierTask;
import ashjack.simukraftreloaded.common.GameStates;
import ashjack.simukraftreloaded.common.PricesForBlocks;
import ashjack.simukraftreloaded.common.Relationship;
import ashjack.simukraftreloaded.common.jobs.JobSoldier;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedConfig;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedEntities;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedEvents;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedGases;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedRecipes;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedTabs;
import ashjack.simukraftreloaded.entity.EntityAlignBeam;
import ashjack.simukraftreloaded.entity.EntityConBox;
import ashjack.simukraftreloaded.entity.EntityFolk;
import ashjack.simukraftreloaded.entity.EntityWindmill;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.traits.Trait;
import ashjack.simukraftreloaded.folk.traits.TraitBrave;
import ashjack.simukraftreloaded.folk.traits.TraitDwarvenHeritage;
import ashjack.simukraftreloaded.folk.traits.TraitFriendly;
import ashjack.simukraftreloaded.folk.traits.TraitGreenThumb;
import ashjack.simukraftreloaded.folk.traits.TraitHatesOutdoors;
import ashjack.simukraftreloaded.folk.traits.TraitNightOwl;
import ashjack.simukraftreloaded.folk.traits.TraitReligious;
import ashjack.simukraftreloaded.folk.traits.TraitStrong;
import ashjack.simukraftreloaded.folk.traits.TraitWorkaholic;
import ashjack.simukraftreloaded.folk.traits.Traits;
import ashjack.simukraftreloaded.items.ItemGranulesGold;
import ashjack.simukraftreloaded.items.ItemGranulesIron;
import ashjack.simukraftreloaded.items.ItemSUKDrink;
import ashjack.simukraftreloaded.items.ItemSUKFood;
import ashjack.simukraftreloaded.items.ItemWindmillBase;
import ashjack.simukraftreloaded.items.ItemWindmillSails;
import ashjack.simukraftreloaded.items.ItemWindmillVane;
import ashjack.simukraftreloaded.packetsNEW.PacketHandler;
import ashjack.simukraftreloaded.packetsNEW.toClient.UpdateFolkPositionPacket;
import ashjack.simukraftreloaded.packetsNEW.toServer.LoadBuildingPacket;
import ashjack.simukraftreloaded.proxies.ClientProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.Commodity;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;	
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.*;

@Mod(modid = "ashjacksukreloaded", name = "Sim-U-Kraft Reloaded", version = "1.0.2b", dependencies = "required-after:Forge@[9.10,)")


public class ModSimukraft
{
	
    public static final String version = "1.0.3";
    public static final String modid = "ashjacksukreloaded";
    
    @Mod.Instance(modid)
	public static ModSimukraft instance;
    
    @SidedProxy(clientSide = "ashjack.simukraftreloaded.proxies.ClientProxy",
                serverSide = "ashjack.simukraftreloaded.proxies.CommonProxy")
    
    //Proxies
    public static CommonProxy proxy;
    public static ClientProxy clientProxy; 
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
    	
    	PacketHandler.initPackets();
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    	
        //File check = new File(SimukraftReloaded.getSimukraftFolder());

       /* if (!check.exists())
        {
            System.out.println("Sim-U-Kraft error - Mod not correctly installed, ./minecraft/mods/Simukraft/ folder is missing - copy this file from the zip provided");
        }*/
        
        //Config
        SimukraftReloadedConfig.loadConfigFile(event);
        
        //Creative Tab
        SimukraftReloadedTabs.loadCreativeTabs();
        
        //Blocks
        SimukraftReloadedBlocks.loadBlocks();
        
        //Items
        SimukraftReloadedItems.loadItems();
        
        //Gases
        SimukraftReloadedGases.loadGases();
        

        //Entities
        SimukraftReloadedEntities.loadEntities();
        EntityRegistry.registerGlobalEntityID(EntityAlignBeam.class,
                "AlignBeam", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityAlignBeam.class,"AlignBeam", 0, this, 250, 10, false);
        
        EntityRegistry.registerGlobalEntityID(EntityFolk.class,
                "Folk", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityFolk.class, "Folk", 1, this, 250, 2, true);
        
        EntityRegistry.registerGlobalEntityID(EntityConBox.class,
                "ConBox", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityConBox.class, "ConBox", 2, this, 250, 2, true);
        
        EntityRegistry.registerGlobalEntityID(EntityWindmill.class,
                "SUKWindmill", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityWindmill.class,"SUKWindmill", 3, this, 250, 1, false);
        
        //Traits
        Traits.loadTraits();
        
        //Events
        SimukraftReloadedEvents.loadEvents();
        
        //Recipes
        SimukraftReloadedRecipes.registerRecipes();
        
        proxy.registerRenderInfo();
        proxy.registerMisc();
        
    }

  //Packets
    @EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {
    //PacketHandler.postInitialize();
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new CommandChangeCredits());
    	event.registerServerCommand(new CommandGenerateFolk());
    }

}

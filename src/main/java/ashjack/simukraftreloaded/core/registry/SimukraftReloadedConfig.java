package ashjack.simukraftreloaded.core.registry;

import ashjack.simukraftreloaded.core.ModSimukraft;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class SimukraftReloadedConfig 
{
	
	////config file settings
    public static Configuration config;
    public static int configPopulationLimit = 100;
    public static int configLumberArea = 30;
    public static boolean configDisableBeamEffect = false;
    public static boolean configFolkTalking = true;
    public static boolean configEnableMarkerAlignmentBeams = true;
    public static boolean configUseExpensiveRecipies = false;
    public static int configMaterialReminderInterval = 3;
    public static int configHUDoffset = 0;
    public static boolean configStopRain = false;
    public static boolean configFolkTalkingEnglish = true;
    public static String[] configMaleNames;
    public static String[] configFemaleNames;
    public static String[] configSurnames;
	
	public static void loadConfigFile(FMLPreInitializationEvent event)
	{
		config = new Configuration(event.getSuggestedConfigurationFile());

        try
        {
			// TODO: Change lightbox ID, as it clashes with Tinkers Construct
		
            config.load();
            config.addCustomCategoryComment("Blocks", "Blocks");
            //Block IDs from the config file
            /*constructorBlockId = config.getInt("ConstructorBox", "Blocks", 3210, highest, highest, null);
            controlBlockId = config.getBlock("ControlBox", 3211).getInt();
            markerBlockId = config.getBlock("Marker", 3212).getInt();
            miningBlockId = config.getBlock("MiningBox", 3213).getInt();
            farmingBlockId = config.getBlock("FarmingBox", 3214).getInt();
            lightboxId = config.getBlock("LightBox", 3215).getInt();
            //pathConstructorId=config.getBlock("PathConstructor", 3216).getInt();
            windmillId = config.getBlock("WindmillBox",3243).getInt(3243);
            itemGranulesGoldId=config.getItem("granulesGold", 3244).getInt(3244);
            itemGranulesIronId=config.getItem("granulesIron", 3245).getInt(3245);
            itemWindmillBaseId=config.getItem("WindmillBase", 3246).getInt(3246);
            itemWindmillVaneId=config.getItem("WindmillVane",3247).getInt(3247);
            itemWindmillSailsId=config.getItem("WindmillSails",3248).getInt(3248);
            //itemGranulesCopperId=config.getItem("granulesCopper", 3249).getInt(3249);
            //itemGranulesTinId=config.getItem("granulesTin", 3250).getInt(3250);
            itemFoodId=config.getItem("itemFood", 3251).getInt(3251);
            blockCompositeBrickId=config.getBlock("CompositeBrick", 3252).getInt(3252);
            blockCheeseId=config.getBlock("BlockCheese",3253).getInt(3253);
            blockFluidMilkId=config.getBlock("fluidMilk", 3254).getInt(3254);*/
            
            //other settings
            Property p;
            p = config.get("Settings", "DisableBeamingEffect", false);
            configDisableBeamEffect = p.getBoolean(false);
            p.comment = "This enables or disables the beaming effect (purple particles) - Set to true to turn them off.";
            p = config.get("Settings", "FolkTalking", true);
            configFolkTalking = p.getBoolean(false);
            p.comment = "If the folks BLARG talking gets annoying, set this to false";
            p = config.get("Settings", "FolkTalkingEnglish", true);
            configFolkTalkingEnglish = p.getBoolean(true);
            p.comment = "If the folks ENGLISH talking gets annoying, set this to false";
            p = config.get("Settings", "LumbermillArea", 40);
            configLumberArea = p.getInt();
            p.comment = "The radius in blocks that the lumberjack will look for trees from the starting point, don't set this too high, otherwise it will slow down MC every time they scan for the nearest tree, so 30 to 1000 should be ok (1000 is 1 Kilometre)";
            p = config.get("Settings", "PopulationLimit", 200);
            configPopulationLimit = p.getInt();
            p.comment = "Limit the population from growing beyond this number if you have an older computer";
            p = config.get("Settings", "EnableMarkerAlignmentBeams", true);
            configEnableMarkerAlignmentBeams = p.getBoolean(true);
            p.comment = "When placing a marker it fires out 4 alignment beams, setting this to false will turn those beams off";
            p = config.get("Settings", "UseExpensiveRecipes", false);
            configUseExpensiveRecipies = p.getBoolean(false);
            p.comment = "If you think the mining/farming boxes are too cheap/overpowered, set this to true to make the recipies require diamond tools instead of stone tools";
            p = config.get("Settings", "MaterialReminderInterval", 3);
            configMaterialReminderInterval = p.getInt(3);
            p.comment = "When a builder runs out of materials, they will let you know about it every 3 minutes, set to 0 for no further reminders.";

            if (configMaterialReminderInterval <= 0)
            {
                configMaterialReminderInterval = 2000;
            }

            p = config.get("Settings", "HUDoffset", 0);
            configHUDoffset = p.getInt(0);
            p.comment = "This positions the HUD (population and money text at the top of the screen) - default is 0, which is the top, value is in pixels, so setting 320 will display it 320 pixels from the top of the screen. Alter this to suit your screen resolution and avoid clashing with other text, setting to minus 10 will display it offscreen.";
            
            p = config.get("Settings", "StopRain", false);
            configStopRain = p.getBoolean(false);
            p.comment = "This is just a personal mod :-) If you too find it rains ALL THE TIME in your world and it annoys you/causes lag, set this to true and you'll only have brief showers instead";
       
            ///////////// Random name generator stuff
            p= config.get("Names", "MaleNames", "Aaron, Adam, Alan, Albatrude, Alexander, Amaranth, Andrew, Angelo, Baldric, Bartholomew, Basher, Beau, Ben, Benie, Bennie, Bill, Blaize, Bob, Boots, Brad, Bradley, Breaker, Brian, Bruce, Butler, Cable, Caeser, Carlos, Carrington, Cassius, Clarence, CrazyDave, Dan, Darren, Darth, David, Derek, Dorian, Dougal, Drake, Drakkar, Draven, Earl, Ed, Edward, Fane, Fark, Fernando, Frank, Frankie, Fred, Gabe, Gary, Ged, Gerry, Glynne, Godfrey, Grendel, Grunter, Happy, Harry, Hercules, Horatio, Howard, Ike, Isaac, Jack, James, Jay, Jean-Luc, Jens, Jeremy, Jerry, Jessie, Jesus, Jim, Jimmy, Joe, John, Jose, Jose, Joseph, Juan, Justin, Justin, Kellam, Ken, Kevin, Knuckles, Lars, Lazarus, Lewis, Loki, Lorenzo, Louis,Lumpy, Lynk, Malcolm, Markus, Martin, Maximus, Michael,Mozart, Noire, Norman, Notch, Obsidian, Olaf, Oswaldo, Oxnard, Ozzy, Perkin, Pete, Philip, Pumpkin, Ralph, Randy, Red, Reks, Rick, Rogue, Romeo, Roy,Samuel, Schmitty, Scott, Sean, Seifer, Seth, Seymour, Sheldon, Sid, Simon, Slash, Spud, Steele, Stephen, Steve, Steven, Storm, Stryker, Tazer, Thunder, Tidus, Todd, Tom, Uther, Valen, Vance, Velderveer, Victor, Virion, Wayne, Wendle, William, Willie, Wolfgang, Wyatt, Xensor, Yoda, Zac, Zander, Zelroth, Zero, Zorro");
            String temp= p.getString();
            p.comment="These are the male first names used by the random name generator, keep the format the same or bad things will happen.";
            configMaleNames=temp.split(",");
            
            p= config.get("Names", "FemaleNames", "Adele, Agnes, Alice, Alouette, Amelia, Angela, Anne, Annette, Annie, Anthuria, Audrey, Belladonna, Bellinda, Beryl, Betty, BigDoris, Blossom, Bluebell, Breezy, Bridget, Bubbles, Bunty, Chalice, Charlotte, Chibi, ChiChi, Chlodeswinthe, Cinnamon, Coco, Connie, Cosette, Cressida, Cynthia, Daphne, Dimpleblossom, Dimples, Druscilla, Elizabeth, Elphina, Ermengarde, Essence, Fe Fe, Finola, Floris, Foofi, Forsythia, Foxglove, Francesca, Freesia, Frida, Funnysplash, Gardenia, Georgette, Giggles, Gladys, Glimmer, Gossamer, Gwendoline, Hannah, Harriet,  Hayley, Hazel, Heidi, Helga, Hilary, Himiltrud, Honor, Honoria, Hortensia, Hyacinth, Imeena, Iris, Jane, Janet, Joanne, Juliet, Kali, Karen, Kate, Kathy, Katrina, Kay, Kerry, Kristy, Lavinia, Leeta, LiloLil, Lisa, Lizette, Lobelia, Louise, Lucretia, Lumiona, Luna, Lurleen, Lyndis, Lynette, Macey, Madonna, Maggie, Maple, Marie, Marilee,  Martha, Mary, Maude, Maureen,  Maxine, Maya, Michelle, Mikki, Mildred,  Millie, Minnie, Morningpuff, Morticia, Myrtle, Mystery, Neen, Nicolette, Nightshade, Nina, Ninja, Odette, Olive, Pansy, Pansy, Paprika, Patricia, Peachy, Pearl, Persephone, Phoebe, Pinky, Plumeria, Poppy, Posy, Primrose, Priscilla, Queenie, Quintessa, Rebbeca, Rhonda, Ronni, Rosa, Rosette, Ruby, Scarlet, Schmarina, Semolina, Serena, Severa, Sharron, Sheila, Subrina, Sunflower, Susan, Susie, Suzette, Suzie, Talula, Tamara, Tammie, Tansy, Tera, Tessa, Tiffaney, Tourmaline, Trina, Trinity, Trish, Trudi, Truffles, Tulipdance, Twinkleboots, Ukara, Ursula, Velocity, Velvet, Vervain, Violet, Violet, Wilma, Winterwillow, Xyla, Yvette");
            temp= p.getString();
            p.comment="These are the female first names used by the random name generator, keep the format the same or bad things will happen.";
            configFemaleNames=temp.split(",");
            
            p= config.get("Names", "LastNames", "Acorn, Aferditie, Alebuckle, AnchorArms,  Anvilbrow, Arsette, Arsing, Astley, Bacon, Bailey, Beiber, Bijoux, Bilberry, Binkydiggle, Bitterpool, Blonk, Blueberry, Booth, Boothby, Boozewob, Brandybuck, Brocx, Bugger, Bumbletoad, Bumfondle, Bunce, Buntflog, Button, Butts, Claus, Clinton, Clutz, Cox, Creaper, Cupid, Curlynoggin, Dalek, Dapplewink, Dent, Derp, Derpy, Diaper, Diggle, Dimfury, Dimplegourd, Dimplehorn, Donglefart, Dover, Dugbloron, Dulek, Dumbledug, Eaglefeathers, Easyrider, Featherbottom, Featheroak, Firsty, Fitzwilliam, Flashheart, Flickersand, Flintrock, Freckles, Fumblemore, Garlicfeet, Gawkroger, Giggerty, Glitterbreath, Goodbody, Gravy, Grayblade, Griffin, Griswold, Grubb, Handy, Hather, Havealot, Head, Hogpen, Hunt, Jabberwocky, Jaffa, Jibberjabba, Jigglybop, Jingles, Jones, Kegbuster, Kettle, Kitchen, Kneebiter, LaForce, Laforge, Lister, Loordes, MacArse, Maplebutton, Marblemantle, Mayflower, McBucket, McBurp, McCoy, McDonald, McFries, McNugget, Merry, Moist, Moneypenny, Mucus, Mugwort, Nabaztag, Neon, Nibbles, Oaktoes, O'Brian, O'Leary, O'Mygod, O'Notch, O'Reily, Pebble,  Peculier, Persson, Picard, Plank, Plop, Plumdrop, Plunder, Plunder, Potter, Power, Reed, Riker, Rumble, Shadespyre, Shakespeare, Sherman, Silverwood, Smith, Snot, Sparklebutter, Spitznoggle, Steelfinger, Strider, Stumbletoe, Tate, Testificate, Thornburrow, Twinklefig, Twistybees, Underwood, Vader, Walsch, Windywings, Winterbottom, Wonker, Yenocheq, Yog, Zaragamba,Flooberwag,Norsepapper");
            temp= p.getString();
            p.comment="These are the last names used by the random name generator, keep the format the same or bad things will happen.";
            configSurnames=temp.split(",");
            

        }
        catch (Exception e)
        {
            SimukraftReloaded.log.severe("Could not allocate block/item ID - " + e.toString());
        }
        finally
        {
            config.save();
        }
	}
}

package ashjack.simukraftreloaded.core.building;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.infrastructure.Infrastructure;
import ashjack.simukraftreloaded.infrastructure.Infrastructure.infrastructures;
import ashjack.simukraftreloaded.infrastructure.InfrastructureElectricity;
import ashjack.simukraftreloaded.infrastructure.InfrastructureWater;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

//NOTE: Buildings are stored in .minecraft/mods/simukraft/buildings/...

public class Building implements Serializable
{
    private static final long serialVersionUID = -1132989807904279141L;
    public String displayName;
    public String type; // / residential commercial (folder name)
    public Block[] structure; // / holds a 3D array of block IDs for this
    public int[] structureSub;
    public String description = "None";
    public Infrastructure infrastructureRequirement = null;
    public Infrastructure infrastructureProducer = null;
    // building
    public int layerCount = 0;
    public int ftbCount = 0;
    public int ltrCount = 0;
    public String dimensions = "";
    public int elevationLevel = 0;
    public V3 primaryXYZ; // first block placed and where control panel block is
    // located
    public V3 livingXYZ; // / where folks should walkto to 'live' in the house
    // or work in workplace building
    public boolean buildingComplete = false;
    public int capacity = -1; // single person or mixed couple by default (-1)
    public String buildDirection = "";
    public V3 lumbermillMarker = null; // Lumbermill only used as starting point
    // to search for trees, blank means use
    // primaryXYZ
    public int blocksInBuilding = 0; // calculated when loadBuilding called
    public String pk = "0"; // unique ID, used for the store id (Primary key)
    public String author = "Satscape"; // author of the building (blank if it's
    // a built-in building)
    public String displayNameWithoutPK = "";
    public Float rent = 0f;
    public ArrayList<String> tennants = new ArrayList<String>();
    public ArrayList<V3> blockLocations = new ArrayList<V3>();

    public transient HashMap<ItemStack, Integer> requirements = new HashMap<ItemStack, Integer>();
    public transient V3 conBoxLocation = null;
    private transient static ArrayList<Building> buildingsRes = new ArrayList<Building>();
    private transient static ArrayList<Building> buildingsCom = new ArrayList<Building>();
    private transient static ArrayList<Building> buildingsInd = new ArrayList<Building>();
    private transient static ArrayList<Building> buildingsOth = new ArrayList<Building>();
    private transient static ArrayList<Building> buildingsSpec = new ArrayList<Building>();

    /** special blocks inside the building, used by the mod */
    public ArrayList<V3> blockSpecial = new ArrayList<V3>();
    
    public Building()
    {
      //sk2 uses this constructor
    }

    /** constructor for making new building */
    public Building(String fname, String theType)
    {
        type = theType;
        displayName = fname;
        buildingComplete = false;

        if (requirements == null)
        {
            requirements = new HashMap<ItemStack, Integer>();
        }
    }

    public Building(String fname, String theType, V3 pxyz, V3 lxyz,
                    boolean isComplete)
    {
        type = theType;
        displayName = fname; // no path, just the filename without the .txt on
        // the end
        primaryXYZ = pxyz;
        livingXYZ = lxyz;
        buildingComplete = isComplete;

        if (requirements == null)
        {
            requirements = new HashMap<ItemStack, Integer>();
        }
    }

    @Override
    public Building clone() {
    	Building ret=new Building();
    	ret.displayName=this.displayName;
    	ret.type=this.type;
    	ret.layerCount=this.layerCount;
    	ret.infrastructureRequirement=this.infrastructureRequirement;
    	ret.infrastructureProducer=this.infrastructureProducer;
    	ret.ftbCount=this.ftbCount;
    	ret.ltrCount=this.ltrCount;
    	ret.dimensions=this.dimensions;
    	ret.elevationLevel=this.elevationLevel;
    	ret.description=this.description;
    	try {ret.primaryXYZ=this.primaryXYZ.clone();}catch(Exception e){ret.primaryXYZ=null;}
    	try {ret.livingXYZ=this.livingXYZ.clone();} catch(Exception e) {ret.livingXYZ=null;}
    	ret.buildingComplete=this.buildingComplete;
    	ret.capacity=-1;
    	ret.buildDirection=this.buildDirection;
    	ret.blocksInBuilding=this.blocksInBuilding;
    	ret.pk=this.pk;
    	ret.author=this.author;
    	ret.displayNameWithoutPK=this.displayNameWithoutPK;
    	ret.rent=this.rent;
    	ret.tennants=new ArrayList<String>();
    	ret.blockLocations=new ArrayList<V3>();
    	ret.blockSpecial=new ArrayList<V3>();
        ret.loadStructure();
    	return ret;
    }
    
    /** get an ArrayList<V3> of all special blocks in this building that have the specified meta */
    public ArrayList<V3> getSpecialBlocks(int meta) {
    	ArrayList<V3> ret=new ArrayList<V3>();
    	for(V3 v3: blockSpecial) {
    		if (v3.meta == meta) {
    			ret.add(v3);
    		}
    	}
    	return ret;
    }
    
    // ** evicts/removes tennant from house (died or summat) */
    public void removeTennant(String tennant)
    {
        for (int t = 0; t < tennants.size(); t++)
        {
            String ten = tennants.get(t);

            if (ten.contentEquals(tennant))
            {
                tennants.remove(t);
                break;
            }
        }
    }

    /**
     * only used by Building now to load structures in a thread to reduce lag
     */
    private void loadStructure()
    {    	
        try
        {
            if (requirements != null)
            {
                requirements.clear();
            }
            else
            {
                requirements = new HashMap<ItemStack, Integer>();
            }

            displayNameWithoutPK = displayName;

            if (displayName.startsWith("PKID"))   // PKID123-my house.txt
            {
                int hyphen = displayName.indexOf("-");
                this.pk = displayName.substring(4, hyphen);
                displayNameWithoutPK = displayName.substring(hyphen + 1);
            }

            blocksInBuilding = 0;
            File f = new File(SimukraftReloaded.getSimukraftFolder() + "/buildings/"
                              + type + "/" + displayName + ".txt");

            if (!f.exists())
            {
                return;
            }

            
            FileInputStream fstream = new FileInputStream(
            		SimukraftReloaded.getSimukraftFolder() + "/buildings/" + type
                + "/" + displayName + ".txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            strLine = br.readLine().toString().toLowerCase().trim(); // dimensions
            String[] d = strLine.split("x");
            int[] di = { 0, 0, 0 };
            di[0] = Integer.parseInt(d[0]);
            di[1] = Integer.parseInt(d[1]);
            di[2] = Integer.parseInt(d[2]);
            structure = new Block[di[0] * di[1] * di[2]];
            structureSub = new int[di[0] * di[1] * di[2]];
            this.ltrCount = di[0];
            this.ftbCount = di[1];
            this.layerCount = di[2];
            this.dimensions = d[0] + "x" + d[1] + "x" + d[2];
            strLine = br.readLine().toString().trim(); // key P=5:0;A=23:2; etc
            HashMap thekey = new HashMap();
            d = strLine.split(";");

            for (int i = 0; i < d.length; i++)
            {
                String[] k = d[i].split("=");
                if(!k[0].toUpperCase().contentEquals("AU") && !k[0].toUpperCase().contentEquals("DESC") && !k[0].toUpperCase().contentEquals("ELEV") && !k[0].toUpperCase().contentEquals("REQ") && !k[0].toUpperCase().contentEquals("INFREQ"))
                {
                	 String[] blockInfo = k[1].split(",");
                	 thekey.put(k[0], Block.getIdFromBlock(Block.getBlockFromName(blockInfo[0])) + ":" + blockInfo[1]); // / P minecraft:dirt

                }
                
                if (k[0].toUpperCase().contentEquals("AU"))   // Author of this
                {
                    // building
                    // stored in key
                    // AU=me;
                    this.author = k[1].trim();
                }
                else if (k[0].toUpperCase().contentEquals("DESC"))   // Description of this
                {
                    // building
                    // stored in key
                    // DESC=building description;
                    this.description = k[1];
                }
                else if (k[0].toUpperCase().contentEquals("ELEV"))   // Elevation constructor box should be at
                {
                    // building
                    // stored in key
                    // ELEV=-1
                    this.elevationLevel = Integer.parseInt(k[1]);
                }
                else if (k[0].toUpperCase().contentEquals("REQ"))   // Elevation constructor box should be at
                {
                    // building
                    // stored in key
                    // REQ=farm.wheat
                    this.elevationLevel = Integer.parseInt(k[1]);
                }
                else if (k[0].toUpperCase().contentEquals("INFREQ"))   // Infrastructure that the building requires to be able to function
                {
                    // building
                    // stored in key
                    // INF=water
                	
                	if(k[1].toLowerCase().contentEquals("water"))
                	{
                		this.infrastructureRequirement = new InfrastructureWater();
                	}
                	
                	if(k[1].toLowerCase().contentEquals("electricity"))
                	{
                		this.infrastructureRequirement = new InfrastructureElectricity();
                	}
                	
                }
            }

            int acount = 0, bcount = 0;
            
            for (int i = 0; i < this.layerCount; i++)
            {
                strLine = br.readLine().trim(); // /read one layer
                // AAAABBBBCCCCaaaabbbccc
                bcount = 0;

                for (int ftb = 0; ftb < ftbCount; ftb++)
                {
                    for (int ltr = 0; ltr < ltrCount; ltr++)
                    {
                        try
                        {
                            String ch = strLine.substring(bcount,
                                                          bcount + 1);
                            char cha=ch.charAt(0);

                            if (ch.contentEquals("!"))   // living block
                            {
                                structure[acount] = SimukraftReloadedBlocks.livingBlock;
                                structureSub[acount] = 0;
                            	//structure[acount] = "999:999";
                            }
                            else if (ch.contentEquals("$"))     // /control
                            {
                                // block
                                structure[acount] = SimukraftReloadedBlocks.controlBox;
                                structureSub[acount] = 0;
                            }
                            else if (ch.contentEquals("*"))     // /
                            {
                                // sim-u-lightbox
                                structure[acount] = SimukraftReloadedBlocks.lightBox;
                                structureSub[acount] = 0;
                            }
                            else if (ch.contentEquals("+"))     // /
                            {
                                // sim-u-lightbox
                                structure[acount] = SimukraftReloadedBlocks.lightBox;
                                structureSub[acount] = 3;
                            }
                            else if (ch.contentEquals("-"))     // /
                            {
                                // sim-u-lightbox
                                structure[acount] = SimukraftReloadedBlocks.lightBox;
                                structureSub[acount] = 5;
                            }
                            else if (ch.contentEquals("0"))     // /
                            {
                                // Special Block: 3
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 0;
                            }
                           
                            else if (ch.contentEquals("1"))     // /
                            {
                                // Special Block: 3
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 1;
                            }
                            
                            else if (ch.contentEquals("2"))     // /
                            {
                                // Special Block: 3
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 2;
                            }
                            
                            else if (ch.contentEquals("3"))     // /
                            {
                                // Special Block: 3
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 3;
                            }
                            
                            else if (ch.contentEquals("4"))     // /
                            {
                                // Special Block: 3
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 4;
                            }
                            
                            else if (ch.contentEquals("5"))     // /
                            {
                                // Special Block: 5
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 5;
                            }
                            
                            else if (ch.contentEquals("6"))     // /
                            {
                                // Special Block: 5
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 6;
                            }
                            
                            else if (ch.contentEquals("7"))     // /
                            {
                                // Special Block: 5
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 7;
                            }
                            else if (ch.contentEquals("8"))     // /
                            {
                                // Special Block: 5
                                structure[acount] = SimukraftReloadedBlocks.specialBlock;
                                structureSub[acount] = 8;
                            }
                            else if ((int)cha >=48 && (int)cha <=57) { //special block (air, but special!)
                            	structure[acount] = SimukraftReloadedBlocks.specialBlock;
                            	structureSub[acount] = cha;
                            }
                            
                            /*Light Blocks - Extended ASCII - FOR REFERENCE, DO NOT DELETE*/
                            /*else if(ch.contentEquals("Ã€"))
                            {
                            	structure[acount] = Block.getBlockById(Integer.parseInt(Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":0";
                            }
                            else if(ch.contentEquals("Ã†"))
                            {
                            	structure[acount] = Block.getBlockById(Integer.parseInt(Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":1";
                            }
                            else if(ch.contentEquals("Ã‡"))
                            {
                            	structure[acount] = Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":2";
                            }
                            else if(ch.contentEquals("Ãˆ"))
                            {
                            	structure[acount] = Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":3";
                            }
                            else if(ch.contentEquals("ÃŒ"))
                            {
                            	structure[acount] = Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":4";
                            }
                            else if(ch.contentEquals("Ã�"))
                            {
                            	structure[acount] = Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":5";
                            }
                            else if(ch.contentEquals("Ã‘"))
                            {
                            	structure[acount] = Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":6";
                            }
                            else if(ch.contentEquals("Ã’"))
                            {
                            	structure[acount] = Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox)
                            						+ ":7";
                            }*/
                            
                            else
                            {
                            	//BOOKMARK
                            	String value = (String)thekey.get(ch);
                            	//String[] v = value.split(",");
                            	String[] blockInfo = value.split(":");
                                structure[acount] = Block.getBlockById(Integer.parseInt(blockInfo[0]));
                                structureSub[acount] = Integer.parseInt(blockInfo[1]);
                                //SimukraftReloaded.log.info(ch);
                                Block sbid = structure[acount];
                                int sbidMeta = structureSub[acount];
                                int bid = Block.getIdFromBlock(sbid);
                                // SimukraftReloaded.log("BID="+bid);
                                addToRequirements(Block.getBlockById(bid), sbidMeta, 1);
                            }

                            acount++;
                            bcount++;

                            if (!ch.contentEquals("A"))
                            {
                                blocksInBuilding++;
                            }
                        }
                        catch (Exception e)
                        {
                            //e.printStackTrace();
                        }
                    }
                }
            }

            br.close();
            in.close();
            this.rent = blocksInBuilding * 0.01f;
        }
        catch (Exception e)
        {
            SimukraftReloaded.log.warning("Building loadStructure() "+e.toString());
        }
    }

    private void addToRequirements(Block block, int meta, int amount)
    {
        int val = 0;
        ItemStack theBlock = new ItemStack(block, 1, meta); // id, size, meta

        if (GameMode.gameMode == GameMode.GAMEMODES.NORMAL)
        {
            String name = "";

            try
            {
                name = theBlock.getDisplayName().toLowerCase();
            }
            catch (Exception e)
            {
                name = "????";
            }

            if (name.contains("planks") || name.contentEquals("cobblestone")
                    || name.contentEquals("glass") || name.contains("wool")
                    || name.contentEquals("bricks")
                    || name.contentEquals("dirt")
                    || name.contentEquals("stone bricks")
                    || name.contentEquals("fence")
                    || name.contentEquals("stone")
                    || (name.contains("wood") && !name.contains("slab") && !name.contains("door") 
                    		&& !name.contains("stairs") && !name.contains("grass")))
            {
            	
              /*  if(name.contains("planks"))
            	{
            		planksCount += 1;
            	}
            	
            	if(name.contains("cobblestone"))
            	{
            		cobbleStoneCount += 1;
            	}
            	
            	if(name.contains("glass"))
            	{
            		glassCount += 1;
            	}
            	
            	if(name.contains("wool"))
            	{
            		woolCount += 1;
            	}
            	
            	if(name.contains("bricks"))
            	{
            		bricksCount += 1;
            	}
            	
            	if(name.contains("dirt"))
            	{
            		dirtCount += 1;
            	}
            	
            	if(name.contains("stone bricks"))
            	{
            		stoneBricksCount += 1;
            	}
            	
            	if(name.contains("fence"))
            	{
            		fenceCount += 1;
            	}
            	
            	if(name.contains("stone"))
            	{
            		stoneCount += 1;
            	}
            	
            	if(name.contains("wood"))
            	{
            		woodCount += 1;
            	}
            	*/
            	
                Iterator it = requirements.entrySet().iterator();
                boolean got = false;

                while (it.hasNext())
                {
                    Map.Entry pairs = (Map.Entry) it.next();
                    ItemStack is = (ItemStack) pairs.getKey();

                    if (is.getItem() == theBlock.getItem())
                    {
                        val = (Integer) pairs.getValue();
                        val++;
                        pairs.setValue(val);
                        got = true;
                        break;
                    }
                }

                if (!got)
                {
                    requirements.put(theBlock, 1); 
                }
            }
        }
        else if (GameMode.gameMode == GameMode.GAMEMODES.CREATIVE)
        {
            return;
        }
        else if (GameMode.gameMode == GameMode.GAMEMODES.HARDCORE)
        {
        	String name="";
        	try
            {
                name = theBlock.getDisplayName().toLowerCase();
            }
            catch (Exception e)
            {
                name = "????";
            }
        	if (!name.contains("grass") && !name.contains("bed")) {
	        	Iterator it = requirements.entrySet().iterator();
	            boolean got = false;
	
	            while (it.hasNext())
	            {
	                Map.Entry pairs = (Map.Entry) it.next();
	                ItemStack is = (ItemStack) pairs.getKey();
	                
	
	                if (is.getItem() == theBlock.getItem())
	                {
	                    val = (Integer) pairs.getValue();
	                    val++;
	                    pairs.setValue(val);
	                    got = true;
	                    break;
	                }
	            }
	            
	            if (!got)
	            {
	                requirements.put(theBlock, 1); 
	            }
        	}
        }
    }

    private static void copyArrayList(ArrayList<Building> from, ArrayList<Building> to)
    {
        for (int i = 0; i < from.size(); i++)
        {
            to.add(from.get(i));
        }
    }

    /** returns list of buildings available for specific type eg. Small house */
    public static ArrayList<Building> getBuildingBlueprints(String theType, String searchWords)
    {
        ArrayList<Building> retBuildings = new ArrayList<Building>();
        
        try
        {
            int sizesearch = 0;

            if (theType.contentEquals("residential"))
            {
                copyArrayList(buildingsRes, retBuildings);
            }
            else if (theType.contentEquals("commercial"))
            {
                copyArrayList(buildingsCom, retBuildings);
            }
            else if (theType.contentEquals("industrial"))
            {
                copyArrayList(buildingsInd, retBuildings);
            }
            else if (theType.contentEquals("other"))
            {
                copyArrayList(buildingsOth, retBuildings);
            }
            else if (theType.contentEquals("special"))
            {
                copyArrayList(buildingsSpec, retBuildings);
            }

            if (!searchWords.contentEquals(""))
            {
                try
                {
                    sizesearch = Integer.parseInt(searchWords.substring(2));
                }
                catch (Exception e) {}

                //search by width
                if (searchWords.startsWith("w:"))
                {
                    for (int i = retBuildings.size() - 1; i >= 0; i--)
                    {
                        Building build = retBuildings.get(i);

                        if (build.ltrCount != sizesearch)
                        {
                            retBuildings.remove(i);
                        }
                    }

                    //search by depth
                }
                else if (searchWords.startsWith("d:"))
                {
                    for (int i = retBuildings.size() - 1; i >= 0; i--)
                    {
                        Building build = retBuildings.get(i);

                        if (build.ftbCount != sizesearch)
                        {
                            retBuildings.remove(i);
                        }
                    }

                    //search by height
                }
                else if (searchWords.startsWith("h:"))
                {
                    for (int i = retBuildings.size() - 1; i >= 0; i--)
                    {
                        Building build = retBuildings.get(i);

                        if (build.layerCount != sizesearch)
                        {
                            retBuildings.remove(i);
                        }
                    }

                    // search by word
                }
                else
                {
 
                    for (int i = retBuildings.size() - 1; i >= 0; i--)
                    {
                        Building build = retBuildings.get(i);

                        if (!build.displayName.toLowerCase().contains(searchWords.toLowerCase()))
                        {
                            retBuildings.remove(i);
                        }
                    }
                    
                }
            } 
        }
        catch (Exception e) {}

        return retBuildings;
    }

    /**
     * returns reference to building based on its primaryXYZ or null if it can't
     * find it
     */
    public static Building getBuilding(V3 primaryXYZ)
    {
        Building b = null;

        if (SimukraftReloaded.theBuildings.size() == 0)
        {
            Building.loadAllBuildings();
        }

        for (int x = 0; x < SimukraftReloaded.theBuildings.size(); x++)
        {
        	try {
	        	b = (Building) SimukraftReloaded.theBuildings.get(x);
	
	            if (b.primaryXYZ.isSameCoordsAs(primaryXYZ, false, true))
	            {
	                return b;
	            }
        	} catch(Exception e){}
        }

        return null;
    }
    
    /**
     * returns reference to building based on its primaryXYZ or null if it can't
     * find it
     */
    public static Building getNearestBuilding(V3 primaryXYZ)
    {
        Building b = null;
        Building nearestBuilding = null;

        if (SimukraftReloaded.theBuildings.size() == 0)
        {
            Building.loadAllBuildings();
        }

        for (int x = 0; x < SimukraftReloaded.theBuildings.size(); x++)
        {
        	try 
        	{
	        	b = (Building) SimukraftReloaded.theBuildings.get(x);
	
	        	if(nearestBuilding == null && b != null)
	        	{
	        		nearestBuilding = b;
	        	}
	        	
	        	if(primaryXYZ.getDistanceTo(b.primaryXYZ) < primaryXYZ.getDistanceTo(nearestBuilding.primaryXYZ))
	        	{
	        		nearestBuilding = b;
	        	}
        	}
        	catch(Exception e){}
        }

        return nearestBuilding;
    }

    /**
     * returns reference to building based on search for name. Eg. 'depot' will
     * return first depot in buildings arraylist
     */
    public static Building getBuildingBySearch(String searchWord)
    {
        Building b = null;

        for (int x = 0; x < SimukraftReloaded.theBuildings.size(); x++)
        {
            b = (Building) SimukraftReloaded.theBuildings.get(x);

            if (b.displayName.contains(searchWord))
            {
                return b;
            }
        }

        return null;
    }

    /** overload will return an ArrayList<Building> of all buildings in world that it finds */
    public static ArrayList<Building> getBuildingBySearch(String searchWord,boolean findAll) {
        ArrayList<Building> ret=new ArrayList<Building>();

        for (int x = 0; x < SimukraftReloaded.theBuildings.size(); x++)
        {
            Building b = (Building) SimukraftReloaded.theBuildings.get(x);

            if (b.displayName.toLowerCase().contains(searchWord.toLowerCase()))
            {
                ret.add(b);
            }
        }

        return ret;
    }
    
    /**
     * returns reference to building based on it's building constructor's
     * location
     */
    public static Building getBuildingByConBox(V3 conBoxLoc)
    {
        Building b = null;

        for (int x = 0; x < SimukraftReloaded.theBuildings.size(); x++)
        {
            b = (Building) SimukraftReloaded.theBuildings.get(x);

            try
            {
                if (b.conBoxLocation.isSameCoordsAs(conBoxLoc, true, true))
                {
                    return b;
                }
            }
            catch (Exception e)
            {
            }
        }

        return null;
    }

    public void saveThisBuilding() {
    	ArrayList<String> strings=new ArrayList<String>();
    	strings.clear();
        
        if (this.primaryXYZ !=null) {
            String xyz = "b"+ this.primaryXYZ.toString().replaceAll(",", "_");
        	strings.add("displayname|"+this.displayName);
        	strings.add("type|"+this.type);
        	strings.add("primaryxyz|"+this.primaryXYZ.toString());
        	if (this.livingXYZ==null) {
        		strings.add("livingxyz|null");
        	} else {
        		strings.add("livingxyz|"+this.livingXYZ.toString());
        	}
        	strings.add("buildingcomplete|"+this.buildingComplete);
        	strings.add("capacity|"+this.capacity);
        	strings.add("builddir|"+this.buildDirection);
        	if (this.lumbermillMarker==null) {
        		strings.add("lmarker|null");
        	} else {
        		strings.add("lmarker|"+this.lumbermillMarker.toString());
        	}
        	strings.add("blocksinbuilding|"+this.blocksInBuilding);
        	String temp="tennants|";
        	for(String tennant:this.tennants) {
        		if (!tennant.trim().contentEquals("")) {
        			temp+=tennant.trim()+",";
        		}
        	}
        	strings.add(temp);
        	
        	temp="blocklocs|";
        	for(V3 block:this.blockLocations) {
        		if (block !=null  & block.toString().contains(",")) {
        			temp+=block.toString()+"B";
        		}
        	}
        	strings.add(temp);
        	
        	if(blockSpecial.size() >0) {
	        	temp="blockspecial|";
	        	for(V3 block:this.blockSpecial) {
	        		if (block !=null  & block.toString().contains(",")) {
	        			temp+=block.toString()+","+ block.meta+"B";
	        		}
	        	}
	        	strings.add(temp);
        	}
        	
        	SimukraftReloaded.saveSK2(SimukraftReloaded.getSavesDataFolder() + "Buildings" + File.separator + xyz + ".sk2", strings);

        }
        
    }
    
    public static void saveAllBuildings()
    {
    	Block id;
        Minecraft mc = Minecraft.getMinecraft();
        
       // Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	//if (side==Side.SERVER) { 
    		ArrayList<String> strings=new ArrayList<String>();
    		
	        for (int b = 0; b < SimukraftReloaded.theBuildings.size(); b++)
	        {
	        	strings.clear();
	            Building building = (Building) SimukraftReloaded.theBuildings.get(b);
	            if (building !=null && building.primaryXYZ !=null) {
	            	V3 pxyz = building.primaryXYZ;
                    World buildingWorld = MinecraftServer.getServer().worldServerForDimension(building.primaryXYZ.theDimension);
                    id = buildingWorld.getBlock(pxyz.x.intValue(),pxyz.y.intValue(), pxyz.z.intValue());
                    String xyz = "b"+ building.primaryXYZ.toString().replaceAll(",", "_");
                    if (id == SimukraftReloadedBlocks.controlBox || id == SimukraftReloadedBlocks.buildingConstructor) {
                    	strings.add("displayname|"+building.displayName);
                    	strings.add("type|"+building.type);
                    	strings.add("primaryxyz|"+building.primaryXYZ.toString());
                    	if (building.livingXYZ==null) {
                    		strings.add("livingxyz|null");
                    	} else {
                    		strings.add("livingxyz|"+building.livingXYZ.toString());
                    	}
                    	strings.add("buildingcomplete|"+building.buildingComplete);
                    	strings.add("capacity|"+building.capacity);
                    	strings.add("builddir|"+building.buildDirection);
                    	if (building.lumbermillMarker==null) {
                    		strings.add("lmarker|null");
                    	} else {
                    		strings.add("lmarker|"+building.lumbermillMarker.toString());
                    	}
                    	strings.add("blocksinbuilding|"+building.blocksInBuilding);
                    	String temp="tennants|";
                    	for(String tennant:building.tennants) {
                    		if (!tennant.trim().contentEquals("")) {
                    			temp+=tennant.trim()+",";
                    		}
                    	}
                    	strings.add(temp);
                    	
                    	try {
	                    	temp="blocklocs|";
	                    	for(V3 block:building.blockLocations) {
	                    		if (block !=null  & block.toString().contains(",")) {
	                    			temp+=block.toString()+"B";
	                    		}
	                    	}
	                    	strings.add(temp);
                    	} catch(Exception e) {} //Concurrent exception, not a proper fix!
                    	
                    	try {
	                    	if(building.blockSpecial.size() >0) {
	            	        	temp="blockspecial|";
	            	        	for(V3 block:building.blockSpecial) {
	            	        		if (block !=null  & block.toString().contains(",")) {
	            	        			temp+=block.toString()+","+block.meta+"B";
	            	        		}
	            	        	}
	            	        	strings.add(temp);
	                    	}
                    	} catch(Exception e) {}
                    	
                    	SimukraftReloaded.saveSK2(SimukraftReloaded.getSavesDataFolder() + "Buildings" + File.separator + xyz + ".sk2", strings);
           
                    	
                    } else {  //no control block, building destroyed in world
                    	File f=new File(SimukraftReloaded.getSavesDataFolder() + "Buildings" + File.separator + xyz + ".sk2");
                    	if (f.exists()) { f.delete(); }
                    }
	            }
	        }
	        SimukraftReloaded.log.info("Building.saveAllBuildings "+SimukraftReloaded.theBuildings.size()+" buildings");
        
        /*
        for (int b = 0; b < ModSimukraft.theBuildings.size(); b++)
        {
            Building building = (Building) ModSimukraft.theBuildings.get(b);

            if (building != null)
            {
                if (building.primaryXYZ != null)
                {
                    V3 pxyz = building.primaryXYZ;
                    World buildingWorld = ModLoader
                                          .getMinecraftServerInstance()
                                          .worldServerForDimension(
                                              building.primaryXYZ.theDimension);
                    id = buildingWorld.getBlockId(pxyz.x.intValue(),
                                                  pxyz.y.intValue(), pxyz.z.intValue());
                    String xyz = "b"
                                 + building.primaryXYZ.toString().replaceAll(",",
                                         "_");

                    // primaryxyz will be a constructor block until the control
                    // block has been placed
                    if (id == ModSimukraft.controlBlockId
                            || id == ModSimukraft.constructorBlockId)
                    {
                        ModSimukraft.proxy.saveObject(
                            ModSimukraft.getSavesDataFolder() + "Buildings"
                            + File.separator + xyz + ".suk",
                            building);
                    }
                }

                // SimukraftReloaded.log("Saved building at "+ building.primaryXYZ
                // +" ("+building.displayNameWithoutPK+")");
            }
            else
            {
                SimukraftReloaded.log("NOT saved building, as element null");
            }
        }
        */
        
    }

    /* loads buildings in this world */
    public static void loadAllBuildings()
    {
    	
    	File buildingsFolder = new File(SimukraftReloaded.getSavesDataFolder() + "Buildings" + File.separator);	
    	buildingsFolder.mkdirs();

    	boolean useNewFormat=false;
        //check for new file format
        for (File f : buildingsFolder.listFiles())
        {
            if (f.getName().endsWith(".sk2")){
            	useNewFormat=true;
            	break;
            }
        }

        if (useNewFormat) {
        	SimukraftReloaded.theBuildings.clear();
        	
        	for (File f : buildingsFolder.listFiles())
	        {
	            if (f.getName().endsWith(".sk2"))
	            {
	            	ArrayList<String> strings=SimukraftReloaded.loadSK2(f.getAbsoluteFile().toString());
	            	Building build=new Building();
	            	for(String line:strings) {
	            		if (line.contains("|")) {
	            			int m1=line.indexOf("|");
	        				String name=line.substring(0,m1);
	        				String value=line.substring(m1+1);
	        				
	        				if (name.contentEquals("displayname")) {
	        					build.displayName=value;
	        				} else if (name.contentEquals("type")) {
	        					build.type=value;
	        				} else if (name.contentEquals("primaryxyz")) {
	        					build.primaryXYZ=new V3(value);
	        				} else if (name.contentEquals("livingxyz")) {
	        					if (!value.contentEquals("null")) {
	        						build.livingXYZ=new V3(value);
	        					}
	        				} else if (name.contentEquals("buildingcomplete")) {
	        					build.buildingComplete=Boolean.parseBoolean(value);
	        				} else if (name.contentEquals("capacity")) {
	        					build.capacity=Integer.parseInt(value);
	        				} else if (name.contentEquals("builddir")) {
	        					build.buildDirection=value;
	        				} else if (name.contentEquals("lmarker")) {
	        					if (!value.contentEquals("null")) {
	        						build.lumbermillMarker=new V3(value);
	        					}
	        				} else if (name.contentEquals("blocksinbuilding")) {
	        					build.blocksInBuilding=Integer.parseInt(value);
	        				} else if (name.contentEquals("tennants")) {
	        					if (value.trim().contentEquals("")) {
	        						build.tennants.clear();
	        					} else {
	        						String[] tennants=value.split(",");
	        						for(String tennant:tennants) {
	        							if (!tennant.trim().contentEquals("")) {
	        								build.tennants.add(tennant);
	        							}
	        						}
	        					}
	        				} else if (name.contentEquals("blocklocs")) {
	        					if (value.contains("B") && value.contains(",")) {
	        						String[] blocks=value.split("B");
	        						for(String block:blocks) {
	        							if (block.contains(",")) {
	        								build.blockLocations.add(new V3(block));
	        							}
	        						}
	        					}
	        				} else if (name.contentEquals("blockspecial")) {
	        					if (value.contains("B") && value.contains(",")) {
	        						String[] blocks=value.split("B");
	        						for(String block:blocks) {
	        							if (block.contains(",")) {
	        								int p1=block.lastIndexOf(",");
	        								String v=block.substring(0,p1);
	        								String meta=block.substring(p1+1);
	        								V3 v3=new V3(v);
	        								v3.meta=Integer.parseInt(meta);
	        								build.blockSpecial.add(v3);
	        							}
	        						}
	        					}
	        				}	
	            		}
	            	}
	            	
	            	build.loadStructure();  //fills in several more fields about this building
	            	SimukraftReloaded.theBuildings.add(build);
	            }
	        }
	            	
	            	
	    /// load old format     	
        } else {
    	
	        Minecraft mc = Minecraft.getMinecraft();
	        SimukraftReloaded.theBuildings.clear();
	
	        for (File f : buildingsFolder.listFiles())
	        {
	            if (f.getName().endsWith(".suk"))
	            {
	                Building building = (Building) ModSimukraft.proxy.loadObject(f
	                                    .getAbsoluteFile().toString());
	
	                if (building != null)
	                {
	                    V3 xyz = building.primaryXYZ;
	                    World buildingWorld = MinecraftServer
	                                          .getServer()
	                                          .worldServerForDimension(
	                                              building.primaryXYZ.theDimension);
	                    Block id = buildingWorld.getBlock(xyz.x.intValue(),
	                                                      xyz.y.intValue(), xyz.z.intValue());
	                    Building dupe = null;
	
	                    if (SimukraftReloaded.theBuildings.size() > 0)
	                    {
	                        dupe = Building.getBuilding(xyz);
	                    }
	
	                    if (id == SimukraftReloadedBlocks.controlBox && dupe == null)
	                    {
	                        building.loadStructure();
	                        SimukraftReloaded.theBuildings.add(building);
	                    }
	                    else
	                    {
	                        f.delete();
	                        SimukraftReloaded.log.info("Building: Deleted building as id=" + id
	                                         + " or dupe");
	                    }
	                }
	            }
	        }
        }
    }

    /** checks that the folks (tennants) actually exist now */
    public static void checkTennants()
    {
        for (int b = 0; b < SimukraftReloaded.theBuildings.size(); b++)
        {
            Building building = SimukraftReloaded.theBuildings.get(b);

            for (int t = 0; t < building.tennants.size(); t++)
            {
                try
                {
                    String tennant = building.tennants.get(t);
                    boolean exists = false;

                    for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
                    {
                        FolkData folk = SimukraftReloaded.theFolks.get(f);

                        if (folk.name.contentEquals(tennant))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                    {
                        building.tennants.remove(tennant);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /* loads in all buildings files into an ArrayList for quicker access (slowly in a thread) */
    public static void initialiseAllBuildings()
    {
    	if (runningInitThread) {return;}
    	
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
            	runningInitThread=true;
            	buildingsRes.clear();
            	buildingsCom.clear();
            	buildingsInd.clear();
            	buildingsOth.clear();
            	buildingsSpec.clear();
                initBuildingsOfType("residential");
                initBuildingsOfType("commercial");
                initBuildingsOfType("industrial");
                initBuildingsOfType("other");
                initBuildingsOfType("special");
                runningInitThread=false;
                SimukraftReloaded.log.info("Building: Thread Done Initialising all buildings from disk");
            }
        });
        t.start();
    }

    private static boolean runningInitThread=false;
    
    /** used when loading folks, to get the building they are currently building
     * partialFile name should be just "PK123-buildingname.txt" */
    public static Building getBuildingForFolk(String partialFilename, String type) {
    	File f = new File(SimukraftReloaded.getSimukraftFolder() + "/buildings/" + type+"/"+partialFilename);
    	if (f.exists()) {
    		String name = f.getName().substring(0, f.getName().length() - 4);
            Building build = new Building(name, type);
            build.loadStructure();
            return build;
    	} else {
    		return null;
    	}
    }
    
    private static void initBuildingsOfType(String type)
    {
    	
    	File f = new File(SimukraftReloaded.getSimukraftFolder() + "/buildings/" + type);

        for (int i = 0; i < f.list().length; i++)
        {
            String name = f.list()[i];
            name = name.substring(0, name.length() - 4);
            Building build = new Building(name, type);
            build.loadStructure();

            if (type.contentEquals("residential"))
            {
                buildingsRes.add(build);
            }
            else if (type.contentEquals("commercial"))
            {
                buildingsCom.add(build);
            }
            else if (type.contentEquals("industrial"))
            {
                buildingsInd.add(build);
            }
            else if (type.contentEquals("other"))
            {
                buildingsOth.add(build);
            }
             else if (type.contentEquals("special"))
            {
                buildingsSpec.add(build);
            }

            try
            {
                Thread.sleep(30);
            }
            catch (Exception e) {}
        }
       
    }

    public static Building getFromAllBuildings(String fullname, String type)
    {
        if (type.contentEquals("residential"))
        {
            for (Building build: buildingsRes)
            {
                if (build.displayName.contentEquals(fullname))
                {
                    return build.clone();
					
                }
            }
        }
        else if (type.contentEquals("commercial"))
        {
            for (Building build: buildingsCom)
            {
                if (build.displayName.contentEquals(fullname))
                {
                    return build.clone();
                }
            }
        }
        else if (type.contentEquals("industrial"))
        {
            for (Building build: buildingsInd)
            {
                if (build.displayName.contentEquals(fullname))
                {
                    return build.clone();
                }
            }
        }
        else if (type.contentEquals("other"))
        {
            for (Building build: buildingsOth)
            {
                if (build.displayName.contentEquals(fullname))
                {
                    return build.clone();
                }
            }
        }
        
        else if (type.contentEquals("special"))
        {
            for (Building build: buildingsSpec)
            {
                if (build.displayName.contentEquals(fullname))
                {
                    return build.clone();
                }
            }
        }

        return null;
    }
}

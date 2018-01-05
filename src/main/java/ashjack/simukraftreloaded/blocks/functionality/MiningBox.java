package ashjack.simukraftreloaded.blocks.functionality;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import ashjack.simukraftreloaded.blocks.functionality.FarmingBox.FarmType;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class MiningBox  implements Serializable
{
    private static final long serialVersionUID = 2951402828966206500L;
    public V3 location;
    public V3 marker1XYZ;
    public V3 marker2XYZ;
    public V3 marker3XYZ;
    public int discards = 0;
    public boolean addGlassCover = true;
    /** used for horizontal mining only */
    public int size = 3;

    public MiningBox()
    {
        //used for loading
    }
    public MiningBox(V3 location)
    {
        this.location = location;
    }
    public MiningBox(V3 inxyz, V3 m1xyz, V3 m2xyz, V3 m3xyz, int filterblocks, int size)
    {
        this.location = inxyz;
        marker1XYZ = m1xyz;
        marker2XYZ = m2xyz;
        marker3XYZ = m3xyz;

        try
        {
            discards = filterblocks;
        }
        catch (Exception e) {}

        this.size = size;
    }

    public static MiningBox getMiningBlockByBoxXYZ(V3 location)
    {
        MiningBox ret = null;

        for (int x = 0; x < SimukraftReloaded.theMiningBoxes.size(); x++)
        {
            MiningBox block = (MiningBox) SimukraftReloaded.theMiningBoxes.get(x);

            if (block.location.isSameCoordsAs(location, true, true))
            {
                ret = block;
                break;
            }
        }

        if (ret == null)
        {
            for (int x = 0; x < SimukraftReloaded.theMiningBoxes.size(); x++)
            {
                MiningBox block = (MiningBox) SimukraftReloaded.theMiningBoxes.get(x);

                if (block.location.isSameCoordsAs(location, false, true))
                {
                    ret = block;
                    break;
                }
            }
        }

        return ret;
    }

    public static void loadMiningBoxes()
    {
    	
    	 Minecraft mc = Minecraft.getMinecraft();
         File mineFiles = new File(SimukraftReloaded.getSavesDataFolder() + "Mining" + File.separator);
         mineFiles.mkdirs();

         boolean useNewFormat=false;
         //check for new file format
         for (File f : mineFiles.listFiles())
         {
             if (f.getName().endsWith(".sk2")){
             	useNewFormat=true;
             	break;
             }
         }
         
         if (useNewFormat) {
        	 SimukraftReloaded.theMiningBoxes.clear();
         	
         	for (File f : mineFiles.listFiles())
 	        {
 	            if (f.getName().endsWith(".sk2"))
 	            {
 	            	ArrayList<String> strings=SimukraftReloaded.loadSK2(f.getAbsoluteFile().toString());
 	            	MiningBox box=new MiningBox();
 	            	for(String line:strings) {
 	            		if (line.contains("|")) {
 	            			int m1=line.indexOf("|");
 	        				String name=line.substring(0,m1);
 	        				String value=line.substring(m1+1);
 	        				
 	        				if (name.contentEquals("location")) {
 	        					box.location=new V3(value);
 	        				} else if(name.contentEquals("m1")) {
 	        					if (!value.contentEquals("null")) {
 	        						box.marker1XYZ=new V3(value);
 	        					}
 	        				} else if(name.contentEquals("m2")) {
 	        					if (!value.contentEquals("null")) {
 	        						box.marker2XYZ=new V3(value);
 	        					}
 	        				} else if(name.contentEquals("m3")) {
 	        					if (!value.contentEquals("null")) {
 	        						box.marker3XYZ=new V3(value);
 	        					}
 	        				} else if(name.contentEquals("discards")) {
 	        					box.discards=Integer.parseInt(value);
 	        				} else if(name.contentEquals("cover")) {
 	        					box.addGlassCover=Boolean.parseBoolean(value);
 	        				} else if(name.contentEquals("hsize")) {
 	        					box.size=Integer.parseInt(value);
 	        				}
 	            		}
 	            	}
                     World theWorld = MinecraftServer.getServer().worldServerForDimension(box.location.theDimension);
                     if (theWorld !=null) {
 	                    Block id = theWorld.getBlock(box.location.x.intValue(), box.location.y.intValue(), box.location.z.intValue());                	
 	                    if (id == SimukraftReloadedBlocks.miningBox)     //mining box is found at primaryXYZ
 	                    {
 	                    	SimukraftReloaded.theMiningBoxes.add(box);
 	                    }
 	                    else
 	                    {
 	                        f.delete();
 	                    }
                     }
 	            }
 	        }
         } else {   //load old format

        for (File f : mineFiles.listFiles())
        {
            if (f.getName().endsWith(".suk"))
            {
                MiningBox mining = (MiningBox) ModSimukraft.proxy.loadObject(f.getAbsoluteFile().toString());

                if (mining != null)
                {
                    V3 xyz = mining.location;
                    World theWorld = MinecraftServer.getServer()
                                     .worldServerForDimension(xyz.theDimension);

                    if (theWorld == null)
                    {
                        f.delete();
                    }
                    else
                    {
                        try
                        {
                            Block id = theWorld.getBlock(xyz.x.intValue(), xyz.y.intValue(), xyz.z.intValue());

                            if (id == SimukraftReloadedBlocks.miningBox && mining != null)    //mining box is found at primaryXYZ
                            {
                            	SimukraftReloaded.theMiningBoxes.add(mining);
                            }
                            else
                            {
                                f.delete();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    }

    public static void saveMiningBoxes()
    {
    	Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	if (side==Side.SERVER) { 
    		ArrayList<String> strings=new ArrayList<String>();
    		
	    	for (int b = 0; b < SimukraftReloaded.theMiningBoxes.size(); b++)
	        {
	    		try {
		            MiningBox mining = (MiningBox) SimukraftReloaded.theMiningBoxes.get(b);
		            strings.clear();
		            strings.add("location|"+mining.location.toString());
		            if (mining.marker1XYZ !=null) {
			            strings.add("m1|"+mining.marker1XYZ.toString());
			            if (mining.marker2XYZ !=null) {
			            	strings.add("m2|"+mining.marker2XYZ.toString());
			            }
			            if (mining.marker3XYZ !=null) {
			            	strings.add("m3|"+mining.marker3XYZ.toString());
			            }
			            strings.add("discards|"+mining.discards);
			            strings.add("cover|"+mining.addGlassCover);
			            strings.add("hsize|"+mining.size);
			            
			            String xyz = "m" + mining.location.toString().replaceAll(",", "_");
			            SimukraftReloaded.saveSK2(SimukraftReloaded.getSavesDataFolder() + "Mining" + File.separator + xyz + ".sk2", strings);
		            }
		        } catch(Exception e) {} //fuck it, user error, not saving this
	        }
    	}
    	
    	
    	/*
    	int id;
        Minecraft mc = ModLoader.getMinecraftInstance();

        for (int b = 0; b < ModSimukraft.theMiningBoxes.size(); b++)
        {
            MiningBox mining = (MiningBox) ModSimukraft.theMiningBoxes.get(b);
            V3 pxyz = mining.location;
            World theWorld = ModLoader.getMinecraftServerInstance()
                             .worldServerForDimension(pxyz.theDimension);
            id = theWorld.getBlockId(pxyz.x.intValue(),
                                     pxyz.y.intValue(),
                                     pxyz.z.intValue());
            String xyz = "m" + mining.location.toString().replaceAll(",", "_");

            if (id == ModSimukraft.miningBlockId)
            {
                ModSimukraft.proxy.saveObject(ModSimukraft.getSavesDataFolder() + "Mining" + File.separator + xyz
                                              + ".suk", mining);
            }
            else
            {
                ModSimukraft.theMiningBoxes.remove(b);
                File fi = new File(ModSimukraft.getSavesDataFolder() + "Mining"
                                   + File.separator + xyz);
                fi.delete();
            }
        }
        */
    }
}

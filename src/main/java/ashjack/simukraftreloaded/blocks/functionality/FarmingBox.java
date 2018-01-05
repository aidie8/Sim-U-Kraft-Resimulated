package ashjack.simukraftreloaded.blocks.functionality;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class FarmingBox  implements Serializable
{
    private static final long serialVersionUID = -4049876797684922153L;
    public V3 location;
    public V3 marker1XYZ;
    public V3 marker2XYZ;
    public V3 marker3XYZ;

    /** enum farm type (what crop) */
    public FarmType farmType = FarmType.WHEAT;
    /** added after 0.7.6  used for levelling up the farm */
    public int level = 1;

    public FarmingBox() {
    	//sk2 load uses this
    }
    
    public FarmingBox(V3 inxyz)
    {
        location = inxyz;

        if (level == 0)
        {
            level = 1;
        }
    }
    public FarmingBox(V3 inxyz, V3 m1xyz, V3 m2xyz, V3 m3xyz)
    {
        location = inxyz;
        marker1XYZ = m1xyz;
        marker2XYZ = m2xyz;
        marker3XYZ = m3xyz;

        if (level == 0)
        {
            level = 1;
        }
    }

    public enum FarmType
    {
        WHEAT, MELON, PUMPKIN, POTATO, CARROT, CUSTOM, SUGAR, CACTUS;

        @Override
        public String toString()
        {
            if (this == FarmType.CARROT)
            {
                return "Carrot";
            }
            else if (this == FarmType.MELON)
            {
                return "Melon";
            }
            else if (this == FarmType.POTATO)
            {
                return "Potato";
            }
            else if (this == FarmType.PUMPKIN)
            {
                return "Pumpkin";
            }
            else if (this == FarmType.WHEAT)
            {
                return "Wheat";
            }
            else if (this == FarmType.CUSTOM)
            {
                return "Custom";
            }
            else if (this == FarmType.SUGAR)
            {
                return "Sugar cane";
            }
            else if (this == FarmType.CACTUS)
            {
                return "Cactus";
            }
            else
            {
                return "Unknown";
            }
        }
    }

    /** @return a V3 of one of the markers (1 to 3) */
    public V3 getMarkerVector(int markerNum)
    {
        V3 ret = null;
        String[] s;

        try
        {
            if (markerNum == 1)
            {
                ret = marker1XYZ;
            }
            else if (markerNum == 2)
            {
                ret = marker2XYZ;
            }
            else if (markerNum == 3)
            {
                ret = marker3XYZ;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new V3(0d, 0d, 0d, 0);
        }

        return ret;
    }

    /** @return the size from left to right of this farm */
    public int getSizeWidth()
    {
        int ltr = 0;

        try
        {
            V3 m1 = getMarkerVector(1);
            V3 m2 = getMarkerVector(2);
            V3 m3 = getMarkerVector(3);

            if (m1.x.intValue() == m2.x.intValue())
            {
                ltr = (int)(Math.abs(m2.z - m1.z) - 1);
            }
            else
            {
                ltr = (int)(Math.abs(m2.x - m1.x) - 1);
            }
        }
        catch (Exception e)
        {
            return 5;
        }

        return Math.abs(ltr);
    }

    /** @return the size from front to back of this farm */
    public int getSizeLength()
    {
        int ftb = 0;

        try
        {
            V3 m1 = getMarkerVector(1);
            V3 m2 = getMarkerVector(2);
            V3 m3 = getMarkerVector(3);

            if (m1.x.intValue() == m3.x.intValue())
            {
                ftb = (int)(Math.abs(m3.z - m1.z) - 1);
            }
            else
            {
                ftb = (int)(Math.abs(m3.x - m1.x) - 1);
            }
        }
        catch (Exception e)
        {
            return 5;
        }

        return Math.abs(ftb);
    }

    /** @return an arrayList of V3s for each block of tilled soil */
    public ArrayList<V3> getSoilBlockPoints()
    {
        ArrayList<V3> ret = new ArrayList<V3>();
        V3 m1 = getMarkerVector(1);
        V3 m2 = getMarkerVector(2);
        V3 m3 = getMarkerVector(3);
        V3 c = m1.clone();
        int length = this.getSizeLength();

        if (length == 1)
        {
        	ModSimukraft.clientProxy.log.warning("FarmingBox: Farm size cannot be determined, using 5x5 default");
        }

        for (int o = 0; o <= length; o++)
        {
            for (int i = 0; i <= this.getSizeWidth(); i++)
            {
                ret.add(c.clone());

                if (m2.x > m1.x)
                {
                    c.x = m1.x + i;
                }
                else if (m2.x < m1.x)
                {
                    c.x = m1.x - i;
                }
                else if (m2.z > m1.z)
                {
                    c.z = m1.z + i;
                }
                else if (m2.z < m1.z)
                {
                    c.z = m1.z - i;
                }
            }

            if (m3.x > m1.x)
            {
                c.x = m1.x + o;
            }
            else if (m3.x < m1.x)
            {
                c.x = m1.x - o;
            }
            else if (m3.z > m1.z)
            {
                c.z = m1.z + o;
            }
            else if (m3.z < m1.z)
            {
                c.z = m1.z - o;
            }
        }

        return ret;
    }

    /** @return an arrayList of V3s containing points where a perimeter fence would be */
    public ArrayList<V3> getPerimeterPoints()
    {
        ArrayList<V3> ret = new ArrayList<V3>();

        try
        {
            V3 m1 = getMarkerVector(1);
            V3 m2 = getMarkerVector(2);
            V3 m3 = getMarkerVector(3);
            V3 b = getLocation();
            V3 c = b.clone();

            for (int i = 0; i <= this.getSizeWidth() + 1; i++)
            {
                if (m2.x - b.x > 1)
                {
                    c.x++;
                }
                else if (m2.x - b.x < -1)     //right marker is -x of box
                {
                    c.x--;
                }
                else if (m2.z - b.z > 1)    //right marker is +z of box
                {
                    c.z++;
                }
                else if (m2.z - b.z < -1)    //right marker is -z of box
                {
                    c.z--;
                }

                ret.add(c.clone());
            }

            for (int i = 0; i <= this.getSizeLength() + 2; i++)
            {
                if (m3.x - b.x > 1)  //back marker is +x of box
                {
                    c.x++;
                }
                else if (m3.x - b.x < -1)     //right marker is -x of box
                {
                    c.x--;
                }
                else if (m3.z - b.z > 1)     //right marker is +z of box
                {
                    c.z++;
                }
                else if (m3.z - b.z < -1)     //right marker is -z of box
                {
                    c.z--;
                }

                ret.add(c.clone());
            }

            for (int i = 0; i <= this.getSizeWidth() + 2; i++)
            {
                if (m2.x - b.x > 1)
                {
                    c.x--;
                }
                else if (m2.x - b.x < -1)     //right marker is -x of box
                {
                    c.x++;
                }
                else if (m2.z - b.z > 1)    //right marker is +z of box
                {
                    c.z--;
                }
                else if (m2.z - b.z < -1)    //right marker is -z of box
                {
                    c.z++;
                }

                ret.add(c.clone());
            }

            for (int i = 0; i <= this.getSizeLength() + 2; i++)
            {
                if (m3.x - b.x > 1)  //back marker is +x of box
                {
                    c.x--;
                }
                else if (m3.x - b.x < -1)     //right marker is -x of box
                {
                    c.x++;
                }
                else if (m3.z - b.z > 1)     //right marker is +z of box
                {
                    c.z--;
                }
                else if (m3.z - b.z < -1)     //right marker is -z of box
                {
                    c.z++;
                }

                ret.add(c.clone());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ret;
    }

    /** @return a V3 of the location of the farming block */
    public V3 getLocation()
    {
        return this.location;
    }

    /** @return a farming box based on it's XYZ */
    public static FarmingBox getFarmingBlockByBoxXYZ(V3 xyz)
    {
        FarmingBox ret = null;

        if (SimukraftReloaded.theFarmingBoxes.size() == 0)
        {
            FarmingBox.loadFarmingBoxes();
        }

        for (int x = 0; x < SimukraftReloaded.theFarmingBoxes.size(); x++)
        {
            FarmingBox block = (FarmingBox) SimukraftReloaded.theFarmingBoxes.get(x);

            if (block.location.isSameCoordsAs(xyz, true, true))
            {
                ret = block;
                break;
            }
        }

        return ret;
    }

    public static void loadFarmingBoxes()
    {
        Minecraft mc = Minecraft.getMinecraft();
        File farmFiles = new File(SimukraftReloaded.getSavesDataFolder() + "Farming" + File.separator);
        farmFiles.mkdirs();

        boolean useNewFormat=false;
        //check for new file format
        for (File f : farmFiles.listFiles())
        {
            if (f.getName().endsWith(".sk2")){
            	useNewFormat=true;
            	break;
            }
        }
        
        if (useNewFormat) {
        	SimukraftReloaded.theFarmingBoxes.clear();
        	
        	for (File f : farmFiles.listFiles())
	        {
	            if (f.getName().endsWith(".sk2"))
	            {
	            	ArrayList<String> strings=SimukraftReloaded.loadSK2(f.getAbsoluteFile().toString());
	            	FarmingBox box=new FarmingBox();
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
	        				} else if(name.contentEquals("type")) {
	        					box.farmType=FarmType.valueOf(value);
	        				} else if(name.contentEquals("level")) {
	        					box.level=Integer.parseInt(value);
	        				}
	            		}
	            	}
                    World theWorld = MinecraftServer.getServer().worldServerForDimension(box.location.theDimension);
                    if (theWorld !=null) {
	                    Block id = theWorld.getBlock(box.location.x.intValue(), box.location.y.intValue(), box.location.z.intValue());                	
	                    if (id == SimukraftReloadedBlocks.farmingBox)     //farming box is found at primaryXYZ
	                    {
	                    	SimukraftReloaded.theFarmingBoxes.add(box);
	                    }
	                    else
	                    {
	                        f.delete();
	                    }
                    }
	            }
	        }
        } else {   //load old format
        
	        for (File f : farmFiles.listFiles())
	        {
	            if (f.getName().endsWith(".suk"))
	            {
	                FarmingBox farming = (FarmingBox) ModSimukraft.proxy.loadObject(f.getAbsoluteFile().toString());
	
	                if (farming != null)
	                {
	                    V3 xyz = farming.location;
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
	
	                            if (id == SimukraftReloadedBlocks.farmingBox)     //control block is found at primaryXYZ
	                            {
	                            	SimukraftReloaded.theFarmingBoxes.add(farming);
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
	                else
	                {
	                    f.delete();
	                    SimukraftReloaded.sendChat("One of your farming boxes had a problem with it, you may have to replace it");
	                }
	            }
	        }
        }
    }

    public static void saveFarmingBoxes()
    {
    	Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	if (side==Side.SERVER) { 
    		ArrayList<String> strings=new ArrayList<String>();
    		
	    	for (int b = 0; b < SimukraftReloaded.theFarmingBoxes.size(); b++)
	        {
	            FarmingBox farming = (FarmingBox) SimukraftReloaded.theFarmingBoxes.get(b);
	            strings.clear();
	            if (farming !=null && farming.location !=null && farming.marker1XYZ !=null) {
	            	try {
			            strings.add("location|"+farming.location.toString());
			            strings.add("m1|"+farming.marker1XYZ.toString());
			            strings.add("m2|"+farming.marker2XYZ.toString());
			            strings.add("m3|"+farming.marker3XYZ.toString());
			            strings.add("type|"+farming.farmType.name());
			            strings.add("level|"+farming.level);
			            
			            String xyz = "f" + farming.location.toString().replaceAll(",", "_");
			            SimukraftReloaded.saveSK2(SimukraftReloaded.getSavesDataFolder() + "Farming" + File.separator + xyz + ".sk2", strings);
	            	}catch(Exception e) {} //user fuckup, probably no markers
	            }
	        }
    	}
        
        
        /*
        for (int b = 0; b < ModSimukraft.theFarmingBoxes.size(); b++)
        {
            FarmingBox farming = (FarmingBox) ModSimukraft.theFarmingBoxes.get(b);
            V3 pxyz = farming.location;
            World theWorld = ModLoader.getMinecraftServerInstance()
                             .worldServerForDimension(pxyz.theDimension);
            int id = theWorld.getBlockId(pxyz.x.intValue(),
                                         pxyz.y.intValue(),
                                         pxyz.z.intValue());
            String xyz = "f" + farming.location.toString().replaceAll(",", "_");

            if (id == ModSimukraft.farmingBlockId)
            {
                ModSimukraft.proxy.saveObject(ModSimukraft.getSavesDataFolder() + "Farming" + File.separator
                                              + xyz + ".suk", farming);
            }
            else
            {
                ModSimukraft.theFarmingBoxes.remove(b);
                File fi = new File(ModSimukraft.getSavesDataFolder() + "Farming"
                                   + File.separator + xyz);
                fi.delete();
            }
        }
        */
    }
}

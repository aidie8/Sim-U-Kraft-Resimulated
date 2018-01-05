package ashjack.simukraftreloaded.blocks.functionality;

import java.io.File;

import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

//THIS NEW BOX WAS NEVER IMPLIMENTED
public class PathBox
{
    private static final long serialVersionUID = 2951402725466206500L;
    public V3 location;
    public V3 marker1XYZ;
    public String pathType = "";

    public PathBox()
    {
        //deserialize
    }
    public PathBox(V3 location)
    {
        this.location = location;
    }
    /*
    public static PathBox getPathBlockByBoxXYZ(V3 location) {
    	PathBox ret = null;

    	for(int x=0;x<ModSimukraft.thePathBoxes.size();x++) {
    		PathBox block=(PathBox) ModSimukraft.thePathBoxes.get(x);
    		if (block.location.isSameCoordsAs(location,true,true)) {
    			ret=block;
    			break;
    		}
    	}
    	if (ret==null) {
    		for(int x=0;x<ModSimukraft.thePathBoxes.size();x++) {
    			PathBox block=(PathBox) ModSimukraft.thePathBoxes.get(x);
    			if (block.location.isSameCoordsAs(location,false,true)) {
    				ret=block;
    				break;
    			}
    		}
    	}
    	return ret;
    }

    public static void loadPathBoxes() {
    	Minecraft mc=ModLoader.getMinecraftInstance();

    	File actual = new File(ModSimukraft.getSavesDataFolder() + "Path" + File.separator);
        actual.mkdirs();
        for (File f : actual.listFiles()) {
            if (f.getName().endsWith(".suk")) {
                PathBox mining = (PathBox) ModSimukraft.proxy.loadObject(f.getAbsoluteFile().toString());
                if (mining !=null) {
                    V3 xyz = mining.location;
                    World theWorld=ModLoader.getMinecraftServerInstance()
                    		.worldServerForDimension(xyz.theDimension);
                    if (theWorld==null) { f.delete(); } else {
                        try {
    	                    int id = theWorld.getBlockId(xyz.x.intValue(),xyz.y.intValue(),xyz.z.intValue());

    	                    if (id == ModSimukraft.pathConstructorId && mining !=null) {   //path box is found at primaryXYZ
    	                        ModSimukraft.thePathBoxes.add(mining);
    	                    } else {
    	                        f.delete();
    	                    }
                        } catch(Exception e){e.printStackTrace();}
                    }
                }
            }
        }
    }

    public static void savePathBoxes() {
    	int id;
    	Minecraft mc=ModLoader.getMinecraftInstance();

    	for (int b = 0; b < ModSimukraft.thePathBoxes.size(); b++) {
    		PathBox mining = (PathBox) ModSimukraft.thePathBoxes.get(b);
    		V3 pxyz = mining.location;
    		World theWorld=ModLoader.getMinecraftServerInstance()
            		.worldServerForDimension(pxyz.theDimension);

    		id = theWorld.getBlockId(pxyz.x.intValue(),
    				pxyz.y.intValue(),
    				pxyz.z.intValue());
    		String xyz = "m" + mining.location.toString().replaceAll(",", "_");

    		if (id == ModSimukraft.pathConstructorId) {
    			ModSimukraft.proxy.saveObject(ModSimukraft.getSavesDataFolder()+ "Path" + File.separator + xyz
    					+ ".suk", mining);
    		} else {
    			ModSimukraft.thePathBoxes.remove(b);
    			File fi = new File(ModSimukraft.getSavesDataFolder() + "Path"
    					+ File.separator + xyz);
    			fi.delete();
    		}
    	}
    }
    */

}

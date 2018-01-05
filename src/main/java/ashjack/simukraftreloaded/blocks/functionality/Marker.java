package ashjack.simukraftreloaded.blocks.functionality;

import java.util.ArrayList;

import ashjack.simukraftreloaded.entity.EntityAlignBeam;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;

public class Marker
{
    public int x;
    public int y;
	public int z;
    int dimension;
    public String caption = "";

    public ArrayList<EntityAlignBeam> beams = new ArrayList<EntityAlignBeam>();
    public Marker(int i, int j, int k, int dime)
    {
        x = i;
        y = j;
        z = k;
        this.dimension = dime;
    }
    public String toString()
    {
        return x + "," + y + "," + z;
    }
    public V3 toV3()
    {
        return new V3((double)x, (double)y, (double)z, this.dimension);
    }
}

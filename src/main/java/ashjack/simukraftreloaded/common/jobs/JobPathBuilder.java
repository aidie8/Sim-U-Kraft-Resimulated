package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;

import ashjack.simukraftreloaded.blocks.functionality.PathBox;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobMiner.Stage;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

////NOTE: this job was never implimented, was going to be a path/bridge building job.

public class JobPathBuilder   //extends Job implements Serializable  {
{
    private static final long serialVersionUID = -1177112207904272541L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;
    private transient int step = 1;
    transient Long timeSinceLastGoto = 0l;
    transient ArrayList<IInventory> pathChests = null;
    transient boolean swingToggle = true;
    private PathBox thePathBox;
    public String pathDirection = "";
    public int pathOffset = 1;

    public JobPathBuilder()
    {
    }

    public void resetJob()
    {
        theStage = Stage.IDLE;
        theFolk.isWorking = false;
    }

  
}

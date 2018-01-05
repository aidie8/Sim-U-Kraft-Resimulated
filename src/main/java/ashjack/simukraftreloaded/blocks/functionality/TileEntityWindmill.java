package ashjack.simukraftreloaded.blocks.functionality;

import java.util.ArrayList;

/*
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.energy.ItemEnergyContainer;
import cofh.api.energy.TileEnergyHandler;
import ashjack.simukraftreloaded.common.jobs.Job;
*/
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
/*
import buildcraft.api.power.IEngine;
import buildcraft.api.power.IRedstoneEngineReceiver;
*/
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

//requires Buildcraft API classes



public class TileEntityWindmill extends TileEntity implements IInventory/*, IEnergyProvider, IEnergyReceiver*/{

	private ItemStack windmillItemStack = null;
    /*0 to 1000 - when it reached 1000, the item is processed, increases by 1 every tick or 2 when raining */
    public int processTime;
    private long timeSinceLastSec=0;
    public String errorString="";
    //protected TileEnergyHandler powerHandler;
    public static ArrayList<TileEntity> powerConsumers=new ArrayList<TileEntity>();
    private float storedEnergy=0.0f;
    public int meta=-1;
	
    public TileEntityWindmill() {
    	//powerHandler = new TileEnergyHandler();
		SimukraftReloaded.log.info("TileEntityWindmill: Windmill power on");
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public int getEnergyStored(ForgeDirection from) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.windmillItemStack;
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		 if (this.windmillItemStack != null)
	        {
	            ItemStack itemstack;

	            if (this.windmillItemStack.stackSize <= par2)
	            {
	                itemstack = this.windmillItemStack;
	                this.windmillItemStack = null;
	                return itemstack;
	            }
	            else
	            {
	                itemstack = this.windmillItemStack.splitStack(par2);

	                if (this.windmillItemStack.stackSize == 0)
	                {
	                    this.windmillItemStack = null;
	                }

	                return itemstack;
	            }
	        }
	        else
	        {
	            return null;
	        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
        if (this.windmillItemStack != null)
        {
            ItemStack itemstack = this.windmillItemStack;
            this.windmillItemStack = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack itemstack) {
        this.windmillItemStack = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
	}

	@Override
	public String getInventoryName() {
		return "container.windmill";
	}

	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {

		if (itemstack.getItem()==Item.getItemFromBlock(Blocks.iron_ore)
				|| itemstack.getItem()==Item.getItemFromBlock(Blocks.gold_ore)
				|| itemstack.getItem()==Item.getItemFromBlock(Blocks.cobblestone)) {
			if (windmillItemStack==null) {
				return true;
			} else if (windmillItemStack.stackSize + itemstack.stackSize <=64 
					&& itemstack.getItem()==windmillItemStack.getItem()) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
		
	}
	
	//@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}
	
    public void setMeta(int meta) {
    	this.meta=meta;
    }
    
	

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    
   
  
    
	

	

	

	

	

	

	

	

	



	
	
	/*private void refreshPowerConsumers() {
		powerConsumers.clear();
		try {
		TileEntity tile=worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if (tile !=null && tile instanceof IEnergyReceiver) {
			powerConsumers.add(tile);
		}
		tile=worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
		if (tile !=null && tile instanceof IEnergyReceiver) {
			powerConsumers.add(tile);
		}
		tile=worldObj.getTileEntity(xCoord-1, yCoord, zCoord);
		if (tile !=null && tile instanceof IEnergyReceiver) {
			powerConsumers.add(tile);
		}
		tile=worldObj.getTileEntity(xCoord+1, yCoord, zCoord);
		if (tile !=null && tile instanceof IEnergyReceiver) {
			powerConsumers.add(tile);
		}
		tile=worldObj.getTileEntity(xCoord, yCoord, zCoord-1);
		if (tile !=null && tile instanceof IEnergyReceiver) {
			powerConsumers.add(tile);
		}
		tile=worldObj.getTileEntity(xCoord, yCoord, zCoord+1);
		if (tile !=null && tile instanceof IEnergyReceiver) {
			powerConsumers.add(tile);
		}
		} catch(Exception e) {e.printStackTrace();}
	}
	
	private ForgeDirection getDirectionOf(TileEntity te) {
		if (te.yCoord > yCoord) { return ForgeDirection.UP; }
		if (te.yCoord < yCoord) { return ForgeDirection.DOWN; }
		if (te.zCoord < zCoord) { return ForgeDirection.NORTH; }
		if (te.zCoord > zCoord) { return ForgeDirection.SOUTH; }
		if (te.xCoord > xCoord) { return ForgeDirection.EAST; }
		if (te.xCoord < xCoord) { return ForgeDirection.WEST; }
		return ForgeDirection.UNKNOWN;	
	}

	/** called server side via packet handler, client has requested the meta value */
	/*public void sendMetaToClient() {
		if (meta >=0) {
			PacketDispatcher.sendPacketToAllPlayers(PacketHandler.makePacket(
				new V3(this.xCoord,this.yCoord,this.zCoord).toString(), "announceMeta", String.valueOf(this.meta)));
		}
	}
	
    public void updateEntity()
    {
    	if (System.currentTimeMillis() - timeSinceLastSec >1000) {
	    	if (this.worldObj.isRemote) {  //client side
	    		if(this.meta==-1) {
	    			/*PacketDispatcher.sendPacketToServer(PacketHandler
	        			.makePacket(new V3(this.xCoord,this.yCoord,this.zCoord).toString(), "requestMeta", ""+this.worldObj.provider.dimensionId));
	    		}
	        	
	        } else {// server side...
             	
        		if (this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) !=SimukraftReloadedItems.windmill) {
        			return;
        		}

        		refreshPowerConsumers();
        		if (powerConsumers.size()>0 && storedEnergy <100) {
        			if (this.worldObj.isRaining()) {
        				storedEnergy+=10;
        			} else {
        				storedEnergy+=5;
        			}
        		}
        		
        		// try to pull stuff from chest
        		ArrayList<IInventory> chests=Job.inventoriesFindClosest(new V3(this.xCoord, this.yCoord, this.zCoord), 2);
        		if (chests.size()==0) {
        			errorString="Place at least one chest next to the Windmill";
        			//return;
        		} else {
        			errorString="";
        		}
        		
        		if (chests.size()>0) {
	        		ItemStack chestItem=null;
	        		ArrayList<ItemStack> coppers=OreDictionary.getOres("oreCopper");
	        		ArrayList<ItemStack> tins=OreDictionary.getOres("oreTin");
	        		
	        		//nothing in windmill slot, so pull any valid ore into it
	        		if (windmillItemStack==null) {
	        			
	        			for(ItemStack is:coppers) {
	        				chestItem=Job.inventoriesGet(chests, is, false,true,null);
	        				if (chestItem !=null) { break; }
	        			}
	        			if (chestItem==null) {
	        				
	        				for(ItemStack is:tins) {
		        				chestItem=Job.inventoriesGet(chests, is, false,true,null);
		        				if (chestItem !=null) { break; }
		        			}
	        			}
	        			
	        			if (chestItem==null) {
	        				chestItem=Job.inventoriesGet(chests, new ItemStack(Blocks.gold_ore,1), false,false,null);
	        			}
	        			if (chestItem==null) {
	        				chestItem=Job.inventoriesGet(chests, new ItemStack(Blocks.iron_ore,1), false,false,null);
	        			}
	        			if (chestItem==null) {
	        				chestItem=Job.inventoriesGet(chests, new ItemStack(Blocks.cobblestone,1),false,false,null);
	        			}
	        			if (chestItem!=null) {
	        				windmillItemStack=chestItem.copy();
	        			}
	        		
	        			
	        		//already got something in the windmill slot, so try to match it       			
	        		} else if (windmillItemStack.getItem()==Item.getItemFromBlock(Blocks.gold_ore)) {
	        			if (windmillItemStack.stackSize<64) {
	        				chestItem=Job.inventoriesGet(chests, new ItemStack(Blocks.gold_ore,1), false,false,null);
	        				if (chestItem !=null) {
	        					windmillItemStack.stackSize++;
	        				}
	        			}
	        			
	        		} else if (windmillItemStack.getItem()==Item.getItemFromBlock(Blocks.iron_ore)) {
	        			if (windmillItemStack.stackSize<64) {
	        				chestItem=Job.inventoriesGet(chests, new ItemStack(Blocks.iron_ore,1), false,false,null);
	        				if (chestItem !=null) {
	        					windmillItemStack.stackSize++;
	        				}
	        			}
	        		} else if (windmillItemStack.getItem()==Item.getItemFromBlock(Blocks.cobblestone)) {
	        			if (windmillItemStack.stackSize<64) {
	        				chestItem=Job.inventoriesGet(chests, new ItemStack(Blocks.cobblestone,1), false,false,null);
	        				if (chestItem !=null) {
	        					windmillItemStack.stackSize++;
	        				}
	        			}
	        		
	        		} else { // look for more copper/tin
	        			for(ItemStack is:coppers) {
	        				if (is.getItem()==windmillItemStack.getItem()) {
	        					chestItem=Job.inventoriesGet(chests, is, false,true,null);
	        					if (chestItem !=null) {
	        						windmillItemStack.stackSize++;
	        						break;
	        					}
	        				}
	        			}
	        			for(ItemStack is:tins) {
	        				if (is.getItem()==windmillItemStack.getItem()) {
	        					chestItem=Job.inventoriesGet(chests, is, false,true,null);
	        					if (chestItem !=null) {
	        						windmillItemStack.stackSize++;
	        						break;
	        					}
	        				}
	        			}
	        		}
        		}
        		timeSinceLastSec=System.currentTimeMillis();
        	}
        	
        	
        	//// BC ENERGY PROVIDER
	    	try {
	        	for(TileEntity tile:powerConsumers) {
	        		if (tile !=null && tile instanceof IEnergyReceiver) {
	        			ForgeDirection dir=getDirectionOf(tile);
	        			ItemEnergyContainer receptor = ((TileEnergyHandler) tile).getPowerReceiver(dir);
	        			if (receptor !=null) {
	        				float powerTaken = receptor.receiveEnergy(ItemEnergyContainer.Type.ENGINE, storedEnergy, dir.getOpposite());
	  
	        				storedEnergy-=powerTaken;
	        				if (powerTaken>0.0) {
	        				//SimukraftReloaded.log("stored="+storedEnergy+"  taken "+ powerTaken+" via "+dir.toString());
	        				}
	        			}
	        		}
	        	}
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
        	
        	
        	//process the ore if present
        	if (windmillItemStack ==null) {
        		processTime=0;
        	} else {
        		if (this.worldObj.isRaining()) {
        			processTime+=2;
        		} else {
        			processTime++;
        		}
        		if (processTime>2000) {processTime=2000;}
        		
        		if(processTime==2000) {
        			ArrayList<IInventory> chests=Job.inventoriesFindClosest(new V3(this.xCoord, this.yCoord, this.zCoord), 2);
            		if (chests.size()==0) {
            			errorString="Place at least one chest next to the Windmill";
            			return;
            		}
            		errorString="";
            		
        			processTime=0;
        			windmillItemStack.stackSize--;
        			
        		//	ArrayList<ItemStack> coppers=OreDictionary.getOres("oreCopper");
	        	//	ArrayList<ItemStack> tins=OreDictionary.getOres("oreTin");
        			
        			if (windmillItemStack.itemID==Block.oreGold.blockID) {
        				Job.inventoriesPut2(chests,new ItemStack(ModSimukraft.itemGranulesGold,2));
        			
        			} else if (windmillItemStack.itemID==Block.oreIron.blockID) {
        				Job.inventoriesPut2(chests,new ItemStack(ModSimukraft.itemGranulesIron,2));
        			} else if (windmillItemStack.itemID==Block.cobblestone.blockID) {
        				Job.inventoriesPut2(chests,new ItemStack(Block.sand,1));
        			} else {  
        				
        				for(ItemStack is:coppers) {
        					if (windmillItemStack.isItemEqual(is)) {
        						Job.inventoriesPut2(chests, new ItemStack(ModSimukraft.itemGranulesCopper,2));
        						break;
        					}
        				}
        				
        				for(ItemStack is:tins) {
        					if (windmillItemStack.isItemEqual(is)) {
        						ArrayList<ItemStack> dustTin=OreDictionary.getOres("dustTin");
        						if (dustTin.size()>0) {
        							Job.inventoriesPut2(chests, new ItemStack(dustTin.get(0).getItem(),2));
        							break;
        						} else {
        							//TODO: if we don't have Tin dust
        						}
        					}
        				}
        				
        			}
        			
        			if (windmillItemStack.stackSize<=0) {
        				windmillItemStack=null;
        			}
        			
        		}
        	}
        }
    }

    
	  public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.readFromNBT(par1NBTTagCompound);
	        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");

	        try {
	            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(0);
	            byte b0 = nbttagcompound1.getByte("Slot");
	
	            if (b0 >= 0 && b0 < 1)
	            {
	                this.windmillItemStack = ItemStack.loadItemStackFromNBT(nbttagcompound1);
	            }
	        } catch(Exception e) {} //NPE when the writeNBT has nothing in the stack, no problem!
	        
	        this.processTime = par1NBTTagCompound.getShort("CookTime");
	        this.meta=par1NBTTagCompound.getInteger("meta");
	        SimukraftReloaded.log.warning("NBT loaded meta as "+this.meta);
	        
	    }

	    /**
	     * Writes a tile entity to NBT.
	     */
	   /* public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.writeToNBT(par1NBTTagCompound);
	        par1NBTTagCompound.setShort("CookTime", (short)this.processTime);
	        par1NBTTagCompound.setInteger("meta", this.meta);
	        SimukraftReloaded.log.warning("NBT saved meta as "+this.meta);
	        NBTTagList nbttaglist = new NBTTagList();

            if (this.windmillItemStack != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) 0);
                this.windmillItemStack.writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
	        par1NBTTagCompound.setTag("Items", nbttaglist);

	    }

		@Override
		public boolean canEmitPowerFrom(ForgeDirection side) {
			return true;
		}

		@Override
		public PowerReceiver getPowerReceiver(ForgeDirection side) {
			return powerHandler.getPowerReceiver();
		}


		@Override
		public World getWorld() {
			return this.worldObj;
		}*/



}

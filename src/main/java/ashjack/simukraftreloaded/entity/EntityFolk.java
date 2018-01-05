package ashjack.simukraftreloaded.entity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ashjack.simukraftreloaded.client.Gui.folk.GuiEntityFolk;
import ashjack.simukraftreloaded.client.Gui.folk.GuiMerchant;
import ashjack.simukraftreloaded.common.jobs.JobFisherman;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobFisherman.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedConfig;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// the entity is just for show, EntityData actually does the work, doing it this way doesn't require chunkloading
// so the player can be miles away and work still gets done.
// Not sure if INpc is needed, also EntityAgeable may be a better class to override? 

public class EntityFolk extends EntityCreature implements INpc {
	/** holds a reference to the actual folk code/properties/logic etc */
	public FolkData theData = null;
	/** used to kill Minecraft spawned EntityFolks, we'll spawn them */
	private long ghostTimer = -1;
	private long greetTimer = 0l;
	private long lastHurt = 0l;
	private Calendar cal = new GregorianCalendar();
	private boolean isXmas = false;
	
	//public HatsApi hat;
	//public RenderOnEntityHelper hatHelper;

	public EntityFolk(World par1World) {
		super(par1World);
		this.getNavigator().setAvoidsWater(false);
		this.getNavigator().setEnterDoors(true);
		this.getNavigator().setBreakDoors(true);
		this.getNavigator().setCanSwim(true);
		//this.tasks.addTask(0, new EntityAIWanderSUK(this, 0.3f));
		this.tasks.addTask(1, new EntityAILookIdle(this));
		this.tasks.addTask(2, new EntityAIMoveIndoors(this));
		this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(10, new EntityAIWatchClosest2(this,
				EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(9, new EntityAIWatchClosest(this,
				EntityLiving.class, 8.0F));
		this.tasks.addTask(9, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.3D));
		this.tasks.addTask(4, new EntityAISwimming(this));

		if (!ModSimukraft.proxy.ranStartup) {
			SimukraftReloaded.log.info("EntityFolk: Killed system spawned folk");
			this.setDead();
		}

		int dom = cal.get(Calendar.DAY_OF_MONTH);
		int moy = cal.get(Calendar.MONTH);
		
		if (dom > 23 && dom < 27 && moy == 11) // seriously Java?!...11 is December?!
		{
			this.isXmas = true;
		}

	}

	
	@SideOnly(Side.CLIENT)
	// This used to be an override, but now it's called from RenderFolk to get
	// the skin texture
	public String getTexture() {
		if (this.theData != null) {
			if (this.theData.gender == 0) {
				if (isXmas) {
					return "MrSanta.png"; // easter egg....or is that Christmas egg?
				} else {
					return "male" + this.theData.skinnumber + ".png";
				}
			} else {
				if (isXmas) {
					return "MrsSanta.png"; // easter egg....or is that Christmas egg?
				} else {
					return "female" + this.theData.skinnumber + ".png";
				}
			}
		} else {
			return "male1.png";
		}
	}

	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.0D);
    }
    
	@Override
	public void onUpdate() {
		
		
		if (theData == null) {
			if (!this.isDead) {
				if (ghostTimer == -1) {
					ghostTimer = System.currentTimeMillis();
				}

				theData = FolkData.getFolkDataByEntityId(this.getEntityId());

				if (theData == null
						&& (System.currentTimeMillis() - ghostTimer > 5000)) {
					SimukraftReloaded.log
							.info("EntityFolk: "
									+ this.getEntityId()
									+ " - their data has been null for more than 5s, so killing");
					this.setDead();
				}
			}
		} else {
			if (theData.isWorking) {
				float s = (float) (Math.sin(System.currentTimeMillis() * 0.01) / 10) + 0.1f;
				swingProgress = s;
			} else {
				swingProgress = 0.0f;
			}

			// // greet player
			if (System.currentTimeMillis() - greetTimer > 1000) {
				Random r = new Random();
				double dist = this.theData.getDistanceToPlayer();

				if (SimukraftReloaded.states != null) {
					Long ls=System.currentTimeMillis()- theData.anyFolkLastSpoke;
					if (SimukraftReloadedConfig.configFolkTalkingEnglish == true
							&& ls > 5000) {
						if ((!theData.greetedToday & dist < 5) || (theData.vocation==Vocation.BURGERSWAITER && dist <5 && r.nextInt(20)==2)) {
							theData.greetedToday = true;
							theData.anyFolkLastSpoke = System
									.currentTimeMillis();

							int sf = r.nextInt(25) + 1;
							String fn = "ashjacksimukraftreloaded:";
							
							if(theData.vocation !=null && theData.vocation==Vocation.BURGERSWAITER) {
								 sf=r.nextInt(6);  // 0 to 5
								 fn+="burger";
								 switch(sf) {
								 case 0:
									 if(theData.gender==0) {
										 fn+="ma";
									 } else {
										 fn+="fa";
									 }
									 break;
								 case 1:
									 if(theData.gender==0) {
										 fn+="mb";
									 } else {
										 fn+="fb";
									 }
									 break;
								 case 2:
									 if(theData.gender==0) {
										 fn+="mc";
									 } else {
										 fn+="fc";
									 }
									 break;
								 case 3:
									 if(theData.gender==0) {
										 fn+="md";
									 } else {
										 fn+="fd";
									 }
									 break;
								 case 4:
									 if(theData.gender==0) {
										 fn+="me";
									 } else {
										 fn+="fe";
									 }
									 break;
								 case 5:
									 if(theData.gender==0) {
										 fn+="mf";
									 } else {
										 fn+="ff";
									 }
									 break;
								 }
							
							} else { // non Vocational
							
							if (theData.age >= 18) {
								if (SimukraftReloaded.isDayTime()) {
									if (sf == 1) {
										if (theData.gender == 0) {
											fn += "daymone";
										} else {
											fn += "dayfone";
										}
									} else if (sf == 2) {
										if (theData.gender == 0) {
											fn += "daymtwo";
										} else {
											fn += "dayftwo";
										}
									} else if (sf == 3) {
										if (!this.worldObj.isRaining()) {
											if (theData.gender == 0) {
												fn += "daymthree";
											} else {
												fn += "dayfthree";
											}
										} else {
											if (theData.gender == 0) {
												fn += "mwxbad";
											} else {
												fn += "fwxbad";
											}
										}
									} else if (sf == 4) {
										if (theData.gender == 0) {
											fn += "meight";
										} else {
											fn += "feight";
										}
									} else if (sf == 5) {
										if (theData.gender == 0) {
											fn += "mnine";
										} else {
											fn += "fnine";
										}
									} else if (sf == 6) {
										if (theData.gender == 0) {
											fn += "mseven";
										} else {
											fn += "fseven";
										}
									} else if (sf > 6) {
										// 7 > 97
										if (theData.gender == 0) {
											fn += "mspeak";
										} else {
											fn += "fspeak";
										}

										fn = fn
												+ Character
														.toString((char) (sf + 90));
									}

								} else {
									if (sf == 1) {
										if (theData.gender == 0) {
											fn += "nightmone";
										} else {
											fn += "nightfone";
										}
									} else if (sf == 2) {
										if (theData.gender == 0) {
											fn += "nightmtwo";
										} else {
											fn += "nightftwo";
										}
									} else if (sf == 3) {
										if (theData.gender == 0) {
											fn += "nightmthree";
										} else {
											fn += "nightfthree";
										}
									} else if (sf == 4) {
										if (theData.gender == 0) {
											fn += "meight";
										} else {
											fn += "feight";
										}
									} else if (sf == 5) {
										if (theData.gender == 0) {
											fn += "mnine";
										} else {
											fn += "fnine";
										}
									} else if (sf == 6) {
										if (theData.gender == 0) {
											fn += "mseven";
										} else {
											fn += "fseven";
										}
									}
								}
							} else // child speaking
							{
								sf = r.nextInt(3) + 1;
								fn += "cspeak";
								fn = fn + Character.toString((char) (sf + 96));
							}
							
							}
							
							
							if (r.nextBoolean()) {
								try {
									// SimukraftReloaded.log("greeting: " + fn);
									ModSimukraft.proxy.getClientWorld()
											.playSound(this.posX, this.posY,
													this.posZ, fn, 1.0f, 1.0f,
													false);
								} catch (Exception e) {
								}
							}
						}
					}
				}

				// /are they starving?
				try {
					if (theData.levelFood < 0) {
						this.onDeath(DamageSource.starve);
					}
				} catch (Exception e) {
				}

				greetTimer = System.currentTimeMillis();
			}
		}

		// /pick up food items
		List list1 = worldObj.getEntitiesWithinAABBExcludingEntity(
				this,
				AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1.0D,
						posY + 1.0D, posZ + 1.0D).expand(2D, 4D, 2D));
		Iterator iterator1 = list1.iterator();

		if (!list1.isEmpty()) {
			do {
				if (!iterator1.hasNext()) {
					break;
				}

				Entity entity1 = (Entity) iterator1.next();

				// //// PICK UP FOOD
				if (entity1 instanceof EntityItem) {
					EntityItem entityitem = (EntityItem) entity1;
					ItemStack is = entityitem.getEntityItem();

					try {
						ItemFood food = (ItemFood) is.getItem();

						if (this.theData.levelFood < 10 && food != null) {
							worldObj.playSoundAtEntity(this, "random.burp",
									1.0f, 1.0f);
							entityitem.setDead();
							this.theData.levelFood++;
						}
					} catch (Exception e) {
					} // Cast Exception when not food
				} else if (entity1 instanceof EntityFolk) {
					if ((int) this.posX == (int) entity1.posX
							&& (int) this.posZ == (int) entity1.posZ) {
						this.motionX += 0.1f;

						try {
							theData.stayPut = false;
						} catch (Exception e) {
						}
					}
				}
			} while (true);
		}

		try {super.onUpdate();} catch(Exception e) {}
	}

	public boolean gotPath;
	@Override
	public void moveEntity(double d, double d1, double d2) {
		if (this.isDead || theData == null) {
			return;
		}

		double dist = 0;
		
		// See if we need to walk somewhere
		if (theData.destination != null && theData.beamingTo == null) {
			try {
				dist = this.getDistance(theData.destination.x,
						theData.destination.y, theData.destination.z);
			} catch (Exception e) {
				SimukraftReloaded.log.warning("Folk's theData.destination was null in moveEntity()");
				return;
			} // this can still NPE, not important, so skip rest of code here

			if (dist <=2) {
				try {
					SimukraftReloaded.log.info("EntityFolk: " + theData.name
							+ " has arrived at "
							+ theData.destination.toString() + " Dim:"
							+ theData.destination.theDimension);
				} catch (Exception e) {
				}
				
				theData.updateLocationFromEntity();
				this.motionX = 0;
				this.motionZ = 0;
				theData.stayPut = true;
				theData.destination = null;
				this.getNavigator().clearPathEntity();
				gotPath=false;

				if (theData.actionArrival != null) {
					theData.action = theData.actionArrival;
					theData.actionArrival = null;
				}
				
			} else {
				try {
					if (!gotPath) {
						
						 PathEntity path=this.worldObj.getEntityPathToXYZ(this, theData.destination.x.intValue(), 
								 theData.destination.y.intValue(),theData.destination.z.intValue()
								 , 40F, true, true, true, true);
						if (path !=null) {
							getNavigator().setPath(path, 0.3f);
							gotPath=true;
						}
						/*SimukraftReloaded.log.info(theData.name +" gotPath="+gotPath +" Destination:"+theData.destination.toString()
								+" entity location="+(int)this.posX+","+
								+(int)this.posY+","+(int)this.posZ);*/

					}
				} catch (Exception e) {
				}
			}

			boolean donttimeout = false;

			try {
				donttimeout = theData.destination.doNotTimeout;
			} catch (Exception e) {
			}

			if (theData.timeStartedGotoing != null && donttimeout == false) {
				if (System.currentTimeMillis() - theData.timeStartedGotoing > 40000
						&& theData.beamingTo == null) {
					getNavigator().clearPathEntity();

					if (dist > 2) {
						SimukraftReloaded.log.info("EntityFolk: " + theData.name
								+ " took too long to walk, so beaming...");
						theData.stayPut = true;
						theData.timeStartedGotoing = System.currentTimeMillis();
						theData.beamMeTo(theData.destination);
					}
				}
			}
		}

		if (theData.stayPut) {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			getNavigator().clearPathEntity();
		} else {
			super.moveEntity(d, d1, d2);
		}
	}

	@Override
	public ItemStack getHeldItem() {
		if (this.theData == null) {
			return null;
		}

		if (this.theData.theirJob == null) {
			return null;
		} else if (this.theData.vocation == Vocation.CROPFARMER) {
			return new ItemStack(Items.stone_hoe, 1);
		} else if (this.theData.vocation == Vocation.LUMBERJACK) {
			return new ItemStack(Items.stone_axe, 1);
		} else if (this.theData.vocation == Vocation.MINER) {
			return new ItemStack(Items.stone_pickaxe, 1);
		} else if (this.theData.vocation == Vocation.BAKER) {
			return new ItemStack(Items.wooden_shovel, 1);
		} else if (this.theData.vocation == Vocation.SOLDIER) {
			return new ItemStack(Items.stone_sword, 1);
		} else if (this.theData.vocation == Vocation.BUILDER) {
			return new ItemStack(Blocks.cobblestone, 1);
		} else if (this.theData.vocation == Vocation.SHEPHERD) {
			return new ItemStack(Items.shears, 1);
		} else if (this.theData.vocation == Vocation.GROCER) {
			return new ItemStack(Items.melon, 1);
		} else if (this.theData.vocation == Vocation.COURIER) {
			return new ItemStack(Blocks.chest, 1);
		} else if (this.theData.vocation == Vocation.MERCHANT) {
			return new ItemStack(Blocks.brick_block, 1);
		} else if (this.theData.vocation == Vocation.BUTCHER) {
			return new ItemStack(Items.porkchop, 1);
		} else if (this.theData.vocation == Vocation.CATTLEFARMER) {
			return new ItemStack(Items.golden_axe, 1);
		} else if (this.theData.vocation == Vocation.PIGFARMER) {
			return new ItemStack(Items.iron_axe, 1);
		} else if (this.theData.vocation == Vocation.CHICKENFARMER) {
			return new ItemStack(Items.stone_axe, 1);
		} else if (this.theData.vocation == Vocation.TERRAFORMER) {
			return new ItemStack(Items.diamond_shovel, 1);
		} else if (this.theData.vocation == Vocation.GLASSMAKER) {
			return new ItemStack(Blocks.glass_pane, 1);
		} else if (this.theData.vocation == Vocation.DAIRYFARMER) {
			return new ItemStack(Items.milk_bucket, 1);
		} else if (this.theData.vocation == Vocation.CHEESEMAKER) {
			return new ItemStack(SimukraftReloadedBlocks.blockCheese, 1);
		} else if (this.theData.vocation == Vocation.BURGERSMANAGER) {
			return new ItemStack(SimukraftReloadedItems.itemFood, 1,3);
		} else if (this.theData.vocation == Vocation.BURGERSFRYCOOK) {
			return new ItemStack(Items.iron_shovel, 1);
		} else if (this.theData.vocation == Vocation.BURGERSWAITER) {
			return new ItemStack(SimukraftReloadedItems.itemFood,1, 2);
			
		} else if (this.theData.vocation == Vocation.FISHERMAN) {
			JobFisherman jf = (JobFisherman) this.theData.theirJob;

			if (jf.theStage == Stage.IDLE) {
				return new ItemStack(Items.fish, 1);
			} else {
				return new ItemStack(Items.fishing_rod, 1);
			}
		}

		return null;
	}

	// // Right clicked this folk, bring up their GUI
	@Override
	@SideOnly(Side.CLIENT)
	public boolean interact(EntityPlayer entityplayer) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.currentScreen = null;
		GuiScreen ui = null;

		if (this.theData == null) {
			this.setDead();
			return false;
		}

		if (this.theData.theirJob != null) {
			if (this.theData.vocation == Vocation.MERCHANT
					&& SimukraftReloaded.isDayTime()) {
				ui = new GuiMerchant();
			} else {
				ui = new GuiEntityFolk(this.theData, entityplayer);
			}
		} else {
			ui = new GuiEntityFolk(this.theData, entityplayer);
		}

		mc.displayGuiScreen(ui);

		if (theData.age < 18) {
			this.worldObj.playSound(this.posX, this.posY, this.posZ,
					"ashjacksimukraftreloaded:helloc", 1.0f, 1.0f, false);
		} else if (theData.gender == 0) {
			this.worldObj.playSound(this.posX, this.posY, this.posZ,
					"ashjacksimukraftreloaded:hellom", 1.0f, 1.0f, false);
		} else {
			this.worldObj.playSound(this.posX, this.posY, this.posZ,
					"ashjacksimukraftreloaded:hellof", 1.0f, 1.0f, false);
		}

		return true;
	}

	@Override
	public void onDeath(DamageSource d) {
		theData.eventDied(d);
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	protected String getHurtSound() {
		if (this.isBurning()) {
		} else {
			this.heal(10);

			if (this.theData != null) {
				if (this.theData.stayPut) {
					this.theData.stayPut = false;
				}
			}

			Block idX1 = this.worldObj.getBlock((int) this.posX + 1,
					(int) this.posY, (int) this.posZ);
			Block idX2 = this.worldObj.getBlock((int) this.posX - 1,
					(int) this.posY, (int) this.posZ);
			Block idZ1 = this.worldObj.getBlock((int) this.posX,
					(int) this.posY, (int) this.posZ + 1);
			Block idZ2 = this.worldObj.getBlock((int) this.posX + 1,
					(int) this.posY, (int) this.posZ - 1);
			this.motionY += 0.4;

			if (idX1 == null) {
				this.motionX += 0.9f;
			} else if (idX2 == null) {
				this.motionX -= 0.9f;
			} else if (idZ1 == null) {
				this.motionZ += 0.9f;
			} else if (idZ2 == null) {
				this.motionZ -= 0.9f;
			}

		}

		if (theData == null) {
			return null;
		}

		if (SimukraftReloadedConfig.configFolkTalking) {
			if (System.currentTimeMillis() - lastHurt > 10000) {
				lastHurt = System.currentTimeMillis();

				if (theData.gender == 0) {
					return "ashjacksimukraftreloaded:OuchM";
				} else {
					return "ashjacksimukraftreloaded:OuchF";
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public int getTalkInterval() {
		Random r = new Random();
		return 1000 + r.nextInt(1000);
	}

	/*
	 * @Override public int getHealth() { if (this.isBurning()) { return 0; }
	 * return 90; }
	 */

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 200;
	}

	@Override
	public boolean canDespawn() {
		return true;
	}

	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		return par1Entity.boundingBox;
	}

	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	/*
	 * @Override public int getMaxHealth() { return 90; }
	 */

	public void onPlayerLogin(EntityPlayer player) {
	}

	public void onPlayerLogout(EntityPlayer player) {

	}

	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	public void onPlayerRespawn(EntityPlayer player) {
	}
	
	/*public void giveHat()
	{
		hat.registerHelper(hatHelper);
		//hat.createHatInfo("FolkHat", 128, 128, 128);
		hat.renderHat(hat.getRandomHatInfo(128, 128, 128), 0, hatHelper.getHatScale(this), 1, 1, 1, hatHelper.getRenderYaw(this), hatHelper.getRotationYaw(this), hatHelper.getRotationPitch(this), hatHelper.getRotationRoll(this), hatHelper.getRotatePointVert(this), hatHelper.getRotatePointHori(this), hatHelper.getRotatePointSide(this), 1, 1, 1, true, true, hatHelper.renderTick);
	}*/
}

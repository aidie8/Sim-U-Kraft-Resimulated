package ashjack.simukraftreloaded.folk;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.common.Relationship;
import ashjack.simukraftreloaded.common.jobs.Job;
import ashjack.simukraftreloaded.common.jobs.JobBaker;
import ashjack.simukraftreloaded.common.jobs.JobBrickMaker;
import ashjack.simukraftreloaded.common.jobs.JobBuilder;
import ashjack.simukraftreloaded.common.jobs.JobBuildersMerchant;
import ashjack.simukraftreloaded.common.jobs.JobBurgersFryCook;
import ashjack.simukraftreloaded.common.jobs.JobBurgersManager;
import ashjack.simukraftreloaded.common.jobs.JobBurgersWaiter;
import ashjack.simukraftreloaded.common.jobs.JobButcher;
import ashjack.simukraftreloaded.common.jobs.JobCheesemaker;
import ashjack.simukraftreloaded.common.jobs.JobCourier;
import ashjack.simukraftreloaded.common.jobs.JobCropFarmer;
import ashjack.simukraftreloaded.common.jobs.JobDairyFarmer;
import ashjack.simukraftreloaded.common.jobs.JobEggFarmer;
import ashjack.simukraftreloaded.common.jobs.JobFisherman;
import ashjack.simukraftreloaded.common.jobs.JobGlassMaker;
import ashjack.simukraftreloaded.common.jobs.JobGrocer;
import ashjack.simukraftreloaded.common.jobs.JobLivestockFarmer;
import ashjack.simukraftreloaded.common.jobs.JobLumberjack;
import ashjack.simukraftreloaded.common.jobs.JobMiner;
import ashjack.simukraftreloaded.common.jobs.JobShepherd;
import ashjack.simukraftreloaded.common.jobs.JobSoldier;
import ashjack.simukraftreloaded.common.jobs.JobTerraformer;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobTerraformer.TerraformerType;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedConfig;
import ashjack.simukraftreloaded.entity.EntityFolk;
import ashjack.simukraftreloaded.folk.genetics.Race;
import ashjack.simukraftreloaded.folk.genetics.Races;
import ashjack.simukraftreloaded.folk.traits.Trait;
import ashjack.simukraftreloaded.folk.traits.Traits;
import ashjack.simukraftreloaded.packetsNEW.PacketHandler;
import ashjack.simukraftreloaded.packetsNEW.toClient.UpdateFolkPositionPacket;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

// THE FOLK'S Logic and properties, gets ticked even when EntityFolk is despawned.

public class FolkData implements Serializable
{
    private static final long serialVersionUID = -2617939828256928361L;

    /** the location the folk is employed at or NULL if they are unemployed */
    public V3 employedAt = null;
    /** their vocation type or null if unemployed - used to create theirJob object */
    public Vocation vocation = null;
    /** the reference to their Job (job class and subclasses) or null if unemployed - created on start up based
     * on their vocation field */
    protected ItemStack[] validTools = new ItemStack[0];
    public transient Job theirJob = null;
    /** their full name as a string */
    public String name = "";
    /** age of folk 0 to 17 = child    18+ adult */
    public int age = 18;
    /** gender of this folk 0=male 1=female */
    public int gender = 0;
    public int skinnumber = 1;
    public Race folkRace = null;
    public String folkRaceName = "";
    /** name of the folk they are partnered with */
   // public String partneredWith = "";
    public String trait1 = "";
    public String trait2 = "";
    public String trait3 = "";
    public String trait4 = "";
    /** their level of food  10=well fed   0=starving */
    public int levelFood = 10;
    /** their level of fun  10=having a blast   0=stressed */
    public int levelFun = 10;
    /** their level of fun  10=having a blast   0=stressed */
    public int levelSocial = 10;
    /** their level of fun  10=having a blast   0=stressed */
    public int levelEnvironment = 10;
    /** this folks building level, increases with experience which makes them build faster */
    public float levelBuilder = 1.0f;
    /** this folks mining level, increases with experience which makes the mine faster */
    public float levelMiner = 1.0f;
    /** the level of soldier, 1 to 10, higher level is less delay between kills */
    public float levelSoldier = 1.0f;
    /** their current action (atwork athome etc) */
    public FolkAction action = FolkAction.WANDER;
    /** NULL or the FolkAction they should be on upon arrival */
    public FolkAction actionArrival = null;
    /** should they be standing still on the spot */
    public Boolean stayPut = false;
    /** their destination as a V3 or null if they have none */
    public V3 destination = null;
    /** their current location including dimension (updates as they work and simulate work) */
    public V3 location = null;
    /** their status text that appears over their heads */
    public String statusText = "Wandering";
    public String status1 = "";
    public String status2 = "";
    public String status3 = "";
    public String status4 = "";
    public String funStatus = "";
    public String socialStatus = "";
    public String environmentStatus = "";
    public float pregnancyStage = 0.0f;
    public transient boolean isWorking = false;

    /** has this folk greeted the player today? */
    public boolean greetedToday = false;
    public static transient long anyFolkLastSpoke=0l;
    

    /////// VARIOUS WORK RELATED VARIABLES THAT CAN'T BE STORED IN THE JOB CLASS
    /** reference to the building they are building (if they are a builder) - bodge, but no where else to keep it */
    public Building theBuilding = null;
    public TerraformerType terraformerType = null;
    public int terraformerRadius = 1;

    /////// TRANSIENT VARIABLES NOT STORED WHEN SAVING THIS FOLK
    /** the folk's own inventory for carrying stuff around the world and dropping off stuff */
    public transient ArrayList<ItemStack> inventory = new ArrayList<ItemStack>();
    /** a reference to the entity so we can check it's isDead() and mess with it etc */
    public transient EntityFolk theEntity = null;
    /** set when they are GOTO'ing and walking/beaming, used to beam them if they can't get there within 40 seconds */
    public transient Long timeStartedGotoing = 0l;
    public transient GotoMethod gotoMethod = null;
    private transient long timeSinceLastSave = 0l;
    private transient long timeSinceLastStatusUpdate = 0l;
    private transient long timeSinceLastMinute = 0l;
    /** location they are beaming to or null if they are not beaming */
    public transient V3 beamingTo = null;

    private transient FolkData hangingWith = null;
    private transient int talkCounter = 0;
    public transient float matingStage = -1.0f; // -1 not had today  0.0 to 0.9=having     1.0=had
    private transient int entityId;


    public FolkData()
    {
        //used by sk2 file loading
    }

    /** called after deserializing the folk to activate them, respawns them and adds to arraylist */
    public void hasLoaded()
    {
        String voc = "none";
        String vocat = "";

        if (vocation != null && employedAt != null)
        {
            voc = vocation.toString();
            vocat = employedAt.toString();
        }

        if (employedAt == null)
        {
            vocation = null;
        }

        if (vocation == null)
        {
            employedAt = null;
        }

        // needed to upgrade to new folkdata structure
        if (levelMiner < 1.0f)
        {
            levelMiner = 1.0f;
        }

        if (levelBuilder < 1.0f)
        {
            levelBuilder = 1.0f;
        }

        if (levelSoldier < 1.0f)
        {
            levelSoldier = 1.0f;
        }

        try {
        SimukraftReloaded.log.info("FolkData: hasLoaded() " + this.name + " (" + voc + ") at "
                         + vocat + " location= " + this.location.toString() + "  "  + SimukraftReloaded.theFolks.size() + " folks in total");
        } catch(Exception e) {}
        this.inventory = new ArrayList<ItemStack>();
        this.setTheirJob(vocation);
        respawnEntity(MinecraftServer.getServer().worldServerForDimension(this.location.theDimension));
        SimukraftReloaded.theFolks.add(this);
    }

    /** create a new folk with random name, skin etc and drop into arrayList */
    public FolkData(World theWorld)
    {
        Random rand = new Random();
        this.gender = rand.nextInt(2);
        this.name = generateName(gender, false, "");
        this.age = 18;

        if (gender == 0)
        {
        	//this.folkRace = Races.raceList.get(rand.nextInt(Races.raceList.size()));
        	//this.folkRaceName = folkRace.getRaceName();
            this.skinnumber = rand.nextInt(63) + 1; //1 to 63    male;
        }
        else
        {
        	//this.folkRace = Races.raceList.get(rand.nextInt(Races.raceList.size()));
        	//this.folkRaceName = folkRace.getRaceName();
            this.skinnumber = rand.nextInt(58) + 1; //1 to 58    female;
        }
        
        //this.folkRaceName = Races.raceList.get(rand.nextInt(Races.raceList.size())).getRaceName();
        

        location = getLocationCloseToPlayer();

        if (location == null)
        {
            return;
        }
        
        //generateTraits();

        respawnEntity(theWorld);
        SimukraftReloaded.theFolks.add(this);
        SimukraftReloaded.sendChat(this.name + " has just wandered into the area.");
    }
    
    public FolkData(World theWorld, String theName)
    {
        Random rand = new Random();
        this.gender = rand.nextInt(2);
        this.name = theName;
        this.age = 18;

        if (gender == 0)
        {
        	//this.folkRace = Races.raceList.get(rand.nextInt(Races.raceList.size()));
        	//this.folkRaceName = folkRace.getRaceName();
            this.skinnumber = rand.nextInt(63) + 1; //1 to 63    male;
        }
        else
        {
        	//this.folkRace = Races.raceList.get(rand.nextInt(Races.raceList.size()));
        	//this.folkRaceName = folkRace.getRaceName();
            this.skinnumber = rand.nextInt(58) + 1; //1 to 58    female;
        }
        
        //this.folkRaceName = Races.raceList.get(rand.nextInt(Races.raceList.size())).getRaceName();
        

        location = getLocationCloseToPlayer();

        if (location == null)
        {
            return;
        }
        
        generateTraits();

        respawnEntity(theWorld);
        SimukraftReloaded.theFolks.add(this);
        SimukraftReloaded.sendChat(this.name + " has just wandered into the area.");
    }

    /** spawn a brand-new child into the world, passes back reference folkData */
    public FolkData(World theWorld, FolkData mother, FolkData father)
    {
        Random rand = new Random();
        String surname = "Unknown";
        
        generateTraits();

        if (father != null)
        {
            surname = father.name.substring(father.name.indexOf(" ") + 1).trim();
        }
        else if (mother != null)
        {
            surname = mother.name.substring(mother.name.indexOf(" ") + 1).trim();
        }

        this.gender = rand.nextInt(2);
        this.name = generateName(gender, true, surname) + " " + surname;
        this.age = 0;

        if (gender == 0)
        {
            this.skinnumber = rand.nextInt(63) + 1; 
        }
        else
        {
            this.skinnumber = rand.nextInt(58) + 1;
        }

        if (mother.getHome() !=null) {
        	mother.getHome().tennants.add(this.name);
        }

        mother.updateLocationFromEntity();
        World mworld=null;
        if (mother.isSpawned()) {
        	mworld=mother.theEntity.worldObj;
        }
        location=Job.findAdjacentSpace(mother.location,mworld);
        
        respawnEntity(theWorld);
        SimukraftReloaded.theFolks.add(this);
        SimukraftReloaded.sendChat(this.name + " has just been born!");
        World world = ModSimukraft.proxy.getClientWorld();

        if (world != null)
        {
            EntityPlayer p = Minecraft.getMinecraft().thePlayer;

            if (p != null)
            {
                ModSimukraft.proxy.getClientWorld().playSound(p.posX, p.posY, p.posZ, "ashjacksimukraftreloaded:birth", 1.0f, 1.0f, false);
            }
        }

        Relationship.setupBloodRelationships(this, father, mother);
        //inherit skills from father and mother
        try {
	        this.levelBuilder = (float) Math.floor(father.levelBuilder / 2) + (float) Math.floor(mother.levelBuilder / 2);
	
	        if (this.levelBuilder > 10.0f)
	        {
	            this.levelBuilder = 10.0f;
	        }
	
	        this.levelMiner = (float) Math.floor(father.levelMiner / 2) + (float) Math.floor(mother.levelMiner / 2);
	
	        if (this.levelMiner > 10.0f)
	        {
	            this.levelMiner = 10.0f;
	        }
	
	        this.levelSoldier = (float) Math.floor(father.levelSoldier / 2) + (float) Math.floor(mother.levelSoldier / 2);
	
	        if (this.levelSoldier > 10.0f)
	        {
	            this.levelSoldier = 10.0f;
	        }
        }catch(Exception e) {} //one parent is dead
    }

    /** called from the esc menu to save this folks position based on where the entity is, if it has one */
    public void updateLocationFromEntity()
    {
        if (this.isSpawned())
        {
            this.location = new V3(theEntity.posX, theEntity.posY, theEntity.posZ, this.location.theDimension);
        }
    }

    /** spawn or respawn the entity, but only if they are within 50 blocks of a player */
    public void respawnEntity(World world)
    {
        if (world == null)
        {
            return;
        }

        if (beamingTo != null)
        {
            return;    //dont re-spawn mid beam
        }

        if (theEntity != null)
        {
            if (!theEntity.isDead)
            {
                return;
            }
        } //already spawned, so no need

        if (getDistanceToPlayer() < 50)
        {
            this.theEntity = new EntityFolk(world);
            this.theEntity.setLocationAndAngles(location.x, location.y, location.z, 0f, 0f);

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(this.theEntity);
            }

            //this.theEntity=entity;
            entityId = this.theEntity.getEntityId();
            SimukraftReloaded.log.info("FolkData:repawnEntity() " + this.name + " at " + this.location.toString() + " in dim " + this.location.theDimension + " ENTITY:" + this.theEntity.getEntityId());
        }
    }

    /** this is called from CommonTickHandler to fire off all the folkData onUpdate()'s */
    public static void triggerAllUpdates()
    {
        for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
        {
            FolkData fd = SimukraftReloaded.theFolks.get(f);
            fd.onUpdate();
        }
    }

    /** runs client side recivinng updates where this folk actual is - NOT IMPLIMENTED? */
    public void serverToClientLocationUpdate(V3 newLocation) {
    	this.location=newLocation.clone();
    	if (this.theEntity !=null) {
    		newLocation.x=Math.floor(newLocation.x)+0.5f;
    		newLocation.z=Math.floor(newLocation.z)+0.5f;
    		this.theEntity.posX=newLocation.x;
    		this.theEntity.posY=newLocation.y;
    		this.theEntity.posZ=newLocation.z;
    	}
    }
    
    //public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("SUKMain");
    
    
    
    /** update loop for this folk, gets called inDirectly and frequently from CommonTickHandler's inGameTick */
    public void onUpdate()
    {
        Random rand = new Random();
        Long now = System.currentTimeMillis();
        
        
        
        //////// ONCE A MINUTE TASKS
        if (now - timeSinceLastMinute > 60000)
        {
        	if(!this.statusText.contains("Hanging") && !this.statusText.startsWith("Shopping")&&!this.statusText.contains("Visiting") && !this.statusText.contains("Staying")&&!this.statusText.contains("Relaxing")&&this.levelFun > 1&&this.isWorking==false)
        	{
        		levelFun -= 1;
        	}
        	
        	if(this.isWorking == true&&this.levelFun > 1)
        	{
        		levelFun -= 1;
        	}
        	
            if (this.getHome() == null && timeSinceLastMinute > 0)
            {
                getHomeForHomeless();
            }
            
            if(!statusText.contains("Hanging")&&this.levelSocial > 1)
            {
            	levelSocial -= 1;
            }

            //if pregnant and day 9, give birth!
            long t = MinecraftServer.getServer().worldServers[0].getWorldTime() % 24000;

            if (t<2000 && this.pregnancyStage>=1.0f) {
            	for(Building build:SimukraftReloaded.theBuildings) {
            		if (build !=null && build.primaryXYZ !=null && build.displayName.contains("Clinic")) {
            			if (this.destination==null) {
            				if (!build.blockSpecial.isEmpty()) {
            					V3 bed=build.blockSpecial.get(0);
            					this.gotoXYZ(bed, null);
            					SimukraftReloaded.sendChat(this.name+" is about to have a baby, she's on her way to the clinic!");
            				}
            			}
            		}
            	}
            	this.action=FolkAction.HAVINGBABY;
            	
            } else if (t > 2000 && this.pregnancyStage >= 1.0f) {  
            	this.statusText="Just had a baby";
            	//this.action=FolkAction.WANDER;
                pregnancyStage = 0.0f;
                FolkData male = Relationship.isFolkLivingWithSomeone(this, true);
                new FolkData(MinecraftServer.getServer().worldServerForDimension(0), this, male);
            }

            // when staying at home or relaxin at home, make sure they don't wander too far away
            if (action == FolkAction.ATHOME || action == FolkAction.STAYINGHOME)
            {
                this.updateLocationFromEntity();

                try
                {
                    V3 liveAt = null;

                    if (this.getHome().livingXYZ != null)
                    {
                        liveAt = this.getHome().livingXYZ.clone();
                    }

                    if (liveAt == null)
                    {
                        liveAt = this.getHome().primaryXYZ.clone();
                    }

                    if (this.location.getDistanceTo(liveAt) > 5 && destination == null
                            || this.location.theDimension != this.getHome().primaryXYZ.theDimension)
                    {
                        this.actionArrival = action;
                        if (liveAt !=null) {
                        	gotoXYZ(liveAt, GotoMethod.WALK);
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Non-critical Exception in Sim-U-Kraft:");
                    e.printStackTrace();
                }
            }

            //make them wander to shops and houses if they are wandering
            boolean gotWanderPoint = false;

            if (action == FolkAction.WANDER && this.isSpawned() && this.employedAt == null && this.age >= 18 && !this.statusText.contains("baby"))
            {
                for (int i = 0; i < SimukraftReloaded.theBuildings.size(); i++)
                {
                    Building b = SimukraftReloaded.theBuildings.get(rand.nextInt(SimukraftReloaded.theBuildings.size()));
                    double dist = this.location.getDistanceTo(b.primaryXYZ);

                    if (b.type.contentEquals("commercial") && dist < 40)
                    {
                        boolean hasShopKeeper = false;

                        for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
                        {
                            FolkData keeper = SimukraftReloaded.theFolks.get(f);

                            if (keeper.employedAt != null && keeper.employedAt.isSameCoordsAs(b.primaryXYZ, true, true))
                            {
                                hasShopKeeper = true;
                                break;
                            }
                        }

                        if (hasShopKeeper)
                        {
                            SimukraftReloaded.log.info("FolkData:onUpdate() "+name + " is wandering to " + b.displayName + " " + dist + " blocks away.");
                            gotoXYZ(b.primaryXYZ, GotoMethod.WALK);
                            this.destination.doNotTimeout = true;
                            this.statusText = "Shopping at the " + b.displayName;
                            gotWanderPoint = true;

                            if (this.hangingWith != null)
                            {
                                hangingWith.statusText = "Wandering";
                                hangingWith.hangingWith = null;
                                hangingWith = null;
                            }

                            break;
                        }
                    }
                    else if (b.type.contentEquals("industrial") && dist < 40
                             && !b.displayName.toLowerCase().contains("farm"))
                    {
                        
                        try {
                        	SimukraftReloaded.log.info("FolkData: onUpdate() "+name + " is wandering to " + b.displayName + " " + dist + " blocks away.");
                            gotoXYZ(b.primaryXYZ, GotoMethod.WALK);
                        	this.destination.doNotTimeout = true;
	                        this.statusText = "Visiting the " + b.displayName;
	                        gotWanderPoint = true;
                        } catch(Exception e) {}// destination can be null, just before the building is built
                        
                        if (this.hangingWith != null)
                        {
                            hangingWith.statusText = "Wandering";
                            hangingWith.hangingWith = null;
                            hangingWith = null;
                        }

                        break;
                    }
                    else if (b.type.contentEquals("residential") && dist < 40 && hangingWith == null)
                    {
                        if (b.tennants != null && b.tennants.size() > 0)
                        {
                            FolkData resy = getFolkByName(b.tennants.get(0));
                            try {
	                            if (!resy.name.contentEquals(this.name) && resy.hangingWith == null)
	                            {
	                                if (resy.action == FolkAction.WANDER || resy.action == FolkAction.STAYINGHOME)
	                                {
	                                    SimukraftReloaded.log.info("FolkData:onUpdate() "+name + " is wandering to " + b.displayName + " " + dist + " blocks away.");
	                                    gotoXYZ(b.primaryXYZ, GotoMethod.WALK);
	                                    gotWanderPoint = true;
	                                    this.statusText = "Hanging out with " + resy.name;
	                                    resy.gotoXYZ(b.primaryXYZ, GotoMethod.WALK);
	
	                                    if (this.destination != null)
	                                    {
	                                        this.destination.doNotTimeout = true;
	                                    }
	
	                                    resy.statusText = "Hanging out with " + this.name;
	                                    this.hangingWith = resy;
	                                    resy.hangingWith = this;
	                                    break;
	                                }
	                            }
                            }catch (Exception e) {} //really don't care if this NPEs
                        }
                    }
                }

                if (!gotWanderPoint)
                {
                    int xo = rand.nextInt(60) - 30;
                    int zo = rand.nextInt(60) - 30;
                    V3 wanderTo = new V3(location.x + xo, location.y, location.z + zo, location.theDimension);
                    World world = MinecraftServer.getServer().worldServerForDimension(location.theDimension);

                    while (world.getBlock(wanderTo.x.intValue(), wanderTo.y.intValue(), wanderTo.z.intValue()) != null
                            && wanderTo.y < 255)
                    {
                        wanderTo.y++;
                    }

                    SimukraftReloaded.log.info("FolkData:onUpdate() WANDER COMMAND FOR " + this.name + " to " + wanderTo.toString());
                    this.gotoXYZ(wanderTo, GotoMethod.WALK);

                    if (this.destination != null)
                    {
                        this.destination.doNotTimeout = true;
                    }

                    statusText = "Wandering";
                    this.stayPut = false;
                }
            }
            else if (action == FolkAction.WANDER && this.isSpawned() && this.age < 18)
            {
                FolkData mother = Relationship.getMotherOf(this);

                if (mother != null)
                {
                    this.gotoXYZ(mother.location, null);
                }
            }

            if (this.hangingWith != null)
            {
                if (!this.hangingWith.statusText.contains(this.name))
                {
                    this.hangingWith = null;
                    this.statusText = "Wandering";
                }
            }

            if (!SimukraftReloaded.isDayTime() && Relationship.isFolkLivingWithSomeone(this) && matingStage < 0)
            {
                tryForBaby();
            }

            timeSinceLastMinute = now - rand.nextInt(20000);
        }

        ///////// ONCE A SECOND TASKS
        if (now - timeSinceLastStatusUpdate > 1000)
        {
        	if(this.trait1 == "" || this.trait2 == "" || this.trait3 == "" || this.trait4 == "")
            {
        		generateTraits();
            }
            updateStatusLines();

            //if they were wandering to a folk house when you hire them, they get stuck on stayput once arrived
            if (this.statusText.contentEquals("Going to my new job..."))
            {
                this.stayPut = false;
            }

            ////////////RESPAWN THEM IF IN RANGE OF PLAYER
            if (!isSpawned())
            {
                int range = this.getDistanceToPlayer();

                if (range < 50)
                {
                    respawnEntity(MinecraftServer.getServer()
                                  .worldServerForDimension(this.location.theDimension));
                }
            }
            else      //if they are spawned, see if they're out of range and force a despawn
            {
                this.theEntity.dimension = this.location.theDimension;
                this.updateLocationFromEntity();
                int range = this.getDistanceToPlayer();

                if (range >= 50)
                {
                    if (theEntity != null)
                    {
                        SimukraftReloaded.log.info("FolkData: onSecTasks - Manually Despawned " + this.name + " as they are " + range + " blocks away");
                        theEntity.setDead();
                    }
                }
            }

            ////////////GOTO WORK IF ITS DAYTIME AND THEY HAVE A JOB
            if (SimukraftReloaded.isDayTime() && this.employedAt != null &&
                    (this.action != FolkAction.ONWAYTOWORK && this.action != FolkAction.ATWORK && pregnancyStage == 0.0f))
            {
                SimukraftReloaded.log.info("FolkData: "+this.name + " is going to work");
                this.statusText = "Going to work";
                action = FolkAction.ONWAYTOWORK;
                gotoXYZ(employedAt, null);
                return;
            }

            if (pregnancyStage > 0.0f && this.employedAt != null && SimukraftReloaded.isDayTime())
            {
                this.statusText = "On Maternity leave";
            }

            if (SimukraftReloaded.isDayTime() && this.employedAt != null && this.action != FolkAction.ATWORK
                    && this.destination == null && pregnancyStage == 0.0f)
            {
                this.statusText = "Going to work";
                action = FolkAction.ONWAYTOWORK;
                SimukraftReloaded.log.warning("FolkData:onUpdate() "+this.name + " is still going to work");
                this.updateLocationFromEntity();
                V3 temp=employedAt.clone();
                temp.x+=5;
                gotoXYZ(temp,GotoMethod.SHIFT);
                gotoXYZ(employedAt, null);
                return;
            }

            if (this.action == FolkAction.ONWAYTOWORK)
            {
                this.statusText = "Going to work";
                this.stayPut = false;

                if (this.destination == null)
                {
                    gotoXYZ(employedAt, null);
                }
            }

            if (this.action == FolkAction.STAYINGHOME && this.hangingWith == null)
            {
                this.statusText = "Staying at home";
                this.stayPut = true;
            }

            if (SimukraftReloaded.isDayTime() && this.statusText.contains("for a baby"))
            {
                this.statusText = "Wandering";
                action = FolkAction.WANDER;
            }
            
            if (this.action==FolkAction.HAVINGBABY) {
            	if (this.pregnancyStage<1.0f) {
            		this.statusText="Just had a baby";
            	} else {
            		this.statusText="Having a baby!";
            	}
            }

            /////// UNEMPLOYED SO STAY AT HOME OR WANDER
            if (SimukraftReloaded.isDayTime() && this.employedAt == null && action == FolkAction.ATHOME)
            {
                isWorking = false;

                if (new Random().nextInt(4) == 1)
                {
                    this.statusText = "Staying Home";
                    action = FolkAction.STAYINGHOME;
                }
                else
                {
                    this.statusText = "Wandering";
                    action = FolkAction.WANDER;
                }
            }

            if (action == FolkAction.STAYINGHOME && this.employedAt != null)
            {
                action = FolkAction.WANDER; //if they are staying at home, but then get a job
            }

            /////////// AT NIGHT WANDER OR GO HOME
            boolean isSoldier = false;

            if (this.vocation != null)
            {
                if (this.vocation == Vocation.SOLDIER)
                {
                    isSoldier = true;
                }
            }

            if (!SimukraftReloaded.isDayTime() && !isSoldier)
            {
                this.action = FolkAction.WANDER;
                this.isWorking=false;

                if (this.getHome() == null)
                {
                    this.statusText = "Wandering";
                    this.stayPut = false;
                }
                else
                {
                    if (this.gotoMethod == GotoMethod.WALK)
                    {
                        this.updateLocationFromEntity();
                    }

                    V3 liveAt=null;

                    try
                    {
                        liveAt = this.getHome().livingXYZ.clone();

                        if (liveAt == null)
                        {
                            liveAt = this.getHome().primaryXYZ.clone();
                        }
                    }
                    catch (Exception e)
                    {
                        SimukraftReloaded.log.warning(this.name+" has no liveAt");
                    }

                    if (liveAt !=null) {
	                    int dist = location.getDistanceTo(liveAt);
	
	                    if (dist > 1 && this.destination == null)
	                    {
	                        this.stayPut = false;
	                        gotoXYZ(liveAt, null);
	                        this.action = FolkAction.GOINGHOME;
	                        this.statusText = "Going Home";
	                        isWorking = false;
	                    }
	
	                    if (dist <= 1 && !this.statusText.contains("baby"))
	                    {
	                        this.stayPut = true;
	                        this.action = FolkAction.ATHOME;
	                        this.statusText = "Relaxing at home";
	                        isWorking = false;
	                    }
                    }
                }
            }
            else
            {
                this.stayPut = false;
            }

            if (action == FolkAction.WANDER)
            {
                stayPut = false;
            }

            ////// SAVE THIS FOLK EVERY 15 SECONDS OR SO (10 to 20)
            int about10 = rand.nextInt(10000) + 10000;

            if (System.currentTimeMillis() - timeSinceLastSave > about10)
            {
            	Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
            	if (side==Side.SERVER) {
            		//PacketDispatcher.sendPacketToAllPlayers(PacketHandler.makePacket(this.name, "updateFolkPosition", this.location.toString()));
            		//SimukraftPacket.cmd = "updateFolkPosition";
            		//SimukraftPacket.folkName = this.name;
            		//SimukraftPacket.par1 = this.location.toString();
            		//INSTANCE.registerMessage(PacketHandler.class, SimukraftPacket.class, 0, Side.CLIENT);
            		//INSTANCE.sendToAll(new SimukraftPacket());

            		PacketHandler.net.sendToServer(new UpdateFolkPositionPacket(this.location.toString() + ";" + name));//(new UpdateFolkPositionMessage(this.location.toString() + ";" + name),location.theDimension);
            		//BOOKMARK
            		this.saveThisFolk();
            	}

                if(this.statusText.contains("Hanging")&&this.levelSocial < 10)
                {
                	levelSocial += 1;
                }
                
                if(this.statusText.contains("Visiting") || this.statusText.contains("Hanging") || this.statusText.contains("Staying")|| this.statusText.contains("Relaxing") || this.statusText.contains("Shopping") &&this.levelFun < 10)
                {
                	levelFun += 1;
                }
            	
                timeSinceLastSave = System.currentTimeMillis();
            }

            if ((this.statusText.startsWith("Hanging out ") || this.statusText.startsWith("Shopping at the "))
                    && this.destination == null && this.theEntity != null)
            {
                talkCounter++;

                if (talkCounter == 12)
                {
                    if (SimukraftReloadedConfig.configFolkTalking)
                    {
                        int ch = rand.nextInt(26) + 97;
                        String letter = "ashjacksimukraftreloaded:blarg" + Character.toString((char) ch);
                        ModSimukraft.proxy.getClientWorld().playSound(location.x, location.y, location.z, letter, 1.0f, 1.0f, false);
                        //SimukraftReloaded.log(this.name + " Spoke: " + letter);
                    }

                    talkCounter = 0;

                    if (this.hangingWith != null)
                    {
                        Relationship.meddleWithRelationship(this, this.hangingWith);
                    }
                }
            }

            //making a baby!
            if (matingStage >= 0.0f && matingStage < 1.0f && this.gender == 1 && pregnancyStage == 0.0f)
            {
                if (SimukraftReloaded.isDayTime())
                {
                    matingStage = -1.0f;
                }
                else
                {
                    FolkData male = Relationship.isFolkLivingWithSomeone(this, true);

                    if (male != null)
                    {
                        matingStage += 0.02f;

                        if (isSpawned())
                        {
                        	World theWorld = null;
                        	if(!MinecraftServer.getServer().isDedicatedServer())
                        	{
                            	theWorld = Minecraft.getMinecraft().theWorld;
                        	}
                        	else
                        	{
                        		theWorld = MinecraftServer.getServer().getEntityWorld();
                        	}
                            double d0 = rand.nextDouble() * 0.5D;
                            double d1 = rand.nextDouble() * 0.5D;
                            double d2 = rand.nextDouble() * 0.5D;
                            theWorld.spawnParticle("heart", this.theEntity.posX,
                                                   this.theEntity.posY + 2.1,
                                                   this.theEntity.posZ, d0, d1, d2);
                            male.updateLocationFromEntity();

                            if (matingStage < 0.15)
                            {
                                this.gotoXYZ(male.location, GotoMethod.SHIFT); //sometime they wander off during mating LOL :-)
                            }

                            theWorld.spawnParticle("heart", male.location.x,
                                                   male.location.y + 2.1,
                                                   male.location.z, d0, d1, d2);
                            this.statusText = "Trying for a baby";
                            male.statusText = "Trying for a baby";
                            male.stayPut = true;
                        }
                    }
                }
            }
            else if (matingStage >= 1.0f && matingStage < 1.1f)   //finished
            {
                matingStage = 1.1f;
                int chance = rand.nextInt(7); 
                SimukraftReloaded.log.info("FolkData: Finished Trying for baby - chance=" + chance);
                FolkData male = Relationship.isFolkLivingWithSomeone(this, true);
                this.statusText = "Relaxing at home";
                male.statusText = "Relaxing at home";

                if (chance == 1 && this.age < 45)   // 1 in 7 chance of pregnancy, also female needs be be less than 45 yo
                {
                    pregnancyStage = 0.1f;
                    SimukraftReloaded.sendChat("Good news! " + this.name + " and " + male.name + " are expecting a baby!");

                    if (this.isSpawned())
                    {
                        this.theEntity.setJumping(true);
                    }

                    if (male.isSpawned())
                    {
                        male.theEntity.setJumping(true);
                    }

                    World world = ModSimukraft.proxy.getClientWorld();

                    if (world != null)
                    {
                        EntityPlayer p = Minecraft.getMinecraft().thePlayer;

                        if (p != null)
                        {
                            ModSimukraft.proxy.getClientWorld().playSound(p.posX, p.posY, p.posZ, "ashjacksimukraftreloaded:pregnant", 1.0f, 1.0f, false);
                        }
                    }
                }
            }

            timeSinceLastStatusUpdate = now;
        }

        ////// THE REST IS RAN EVERY UPDATE/TICK

        ///////////// DO BEAMING PROGRESS IF THEY ARE BEAMING
        if (this.beamingTo != null)
        {
            doBeaming();
        }

        ////////// FIRE OFF THE JOB'S ONUPDATE() all day and night
        if (this.theirJob != null)
        {
            theirJob.onUpdate();
        }
    }

    /** this is called on the 'once a minute' but is only called once a night and
     * only at night and they have a partner */
    private void tryForBaby()
    {
        if (this.gender == 1 && this.pregnancyStage == 0.0f) //only need to do this for non-preg females
        {
            FolkData malePartner = Relationship.isFolkLivingWithSomeone(this, true);

            if (malePartner != null && this.action == FolkAction.ATHOME && malePartner.action == FolkAction.ATHOME)
            {
                matingStage = 0.0f; //having  - one second tasks handles the rest of this

                if (malePartner.isSpawned())
                {
                    this.gotoXYZ(new V3(malePartner.theEntity.posX, malePartner.theEntity.posY,
                                        malePartner.theEntity.posZ, malePartner.theEntity.dimension), GotoMethod.WALK);
                }

                //SimukraftReloaded.log("tryForBaby() called");
            }
        }
        else
        {
            matingStage = -1.0f;
        }
    }

    /** runs this once a second ONLY if they are homeless */
    private void getHomeForHomeless()
    {
        if (this.action == FolkAction.WANDER)
        {
        	Building.loadAllBuildings();
        	
            for (int b = 0; b < SimukraftReloaded.theBuildings.size(); b++)
            {
                Building building = (Building) SimukraftReloaded.theBuildings.get(b);

                if (building.tennants.size() == 0 && building.buildingComplete == true
                        && building.type.contentEquals("residential"))
                {
                    building.tennants.add(this.name);
                    this.action = FolkAction.GOINGHOME;
                    this.actionArrival = FolkAction.STAYINGHOME;

                    if (building.livingXYZ != null)
                    {
                        gotoXYZ(building.livingXYZ, null);
                    }
                    else
                    {
                        gotoXYZ(building.primaryXYZ, null);
                    }

                   //SimukraftReloaded.states.population++;
                    SimukraftReloaded.states.saveStates();
                    SimukraftReloaded.sendChat(this.name + " is moving into their " + building.displayNameWithoutPK);
                    statusText = "Moved into my " + building.displayNameWithoutPK;
                    Building.saveAllBuildings();
                    break;
                }
            }
        }
    }

    private void updateStatusLines()
    {
    	
    	
        //// UPDATE THEIR STATUS LINES
        if (vocation == null)
        {
            status1 = "Unemployed";
        }
        else
        {
            try
            {
                status1 = vocation.toString();
            }
            catch (Exception e)
            {
                status1 = "";
            }
        }
        Random rand = new Random();
        

        if (this.getHome() != null)
        {
            status2 = "Home owner";
        }
        else
        {
            status2 = "Homeless";
        }

        if (!Relationship.isFolkLivingWithSomeone(this))
        {
            status3 = "Single";
        }
        else
        {
            status3 = "Living with someone";
        }

        if (this.levelFood == 10)
        {
            status4 = "Well fed";
        }
        else if (levelFood > 5)
        {
            status4 = "A little hungry";
        }
        else if (levelFood > 1)
        {
            status4 = "Quite hungry";
        }
        else
        {
            status4 = "VERY hungry!";
        }
        
        if (this.levelFun == 10)
        {
            funStatus = "Having a blast!";
        }
        else if (levelFun > 7)
        {
        	funStatus = "Enjoying themself";
        }
        else if (levelFun > 4)
        {
        	funStatus = "Bored";
        }
        else
        {
        	funStatus = "Stressed";
        }
        
        if (this.levelSocial == 10)
        {
            socialStatus = "Great Banter!";
        }
        else if (levelSocial > 7)
        {
        	socialStatus = "Socially Fulfilled";
        }
        else if (levelSocial > 4)
        {
        	socialStatus = "Lonely";
        }
        else if (levelSocial > 2)
        {
        	socialStatus = "Very Lonely";
        }
        else
        {
        	socialStatus = "Going Insane";
        }
        if (this.levelEnvironment == 10)
        {
            environmentStatus = "Beautiful Surroundings";
        }
        else if (levelEnvironment > 7)
        {
        	environmentStatus = "Nice Surroundings";
        }
        else if (levelEnvironment > 4)
        {
        	environmentStatus = "Poor Surroundings";
        }
        else
        {
        	environmentStatus = "Horrific Surroundings";
        }
    }

    /** returns if the entity is currently spawned (using either the entity==null or entity.isDead being true)*/
    public boolean isSpawned()
    {
        if (this.theEntity == null)
        {
            try
            {
                this.theEntity = FolkData.getFolkByName(this.name).theEntity;
            }
            catch (Exception e) {}  //NPE's at start up
        }

        if (this.theEntity == null)
        {
            return false;
        }
        else
        {
            return !this.theEntity.isDead;
        }
    }

    /** calculates the current distance between this folk and the player */
    public int getDistanceToPlayer()
    {
        EntityPlayer p = FolkData.getClosestPlayer(this.location);

        if (p == null)
        {
            return 9999;
        }

        V3 pv = new V3(p.posX, p.posY, p.posZ, this.location.theDimension);
        return this.location.getDistanceTo(pv);
    }

    /** finds a nice spot close to the player (about 30 blocks away) that is safe to place a folk down*/
    public V3 getLocationCloseToPlayer()
    {
        EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        V3 ret;

        try
        {
            ret = new V3(p.posX, 5d, p.posZ, p.dimension);
        }
        catch (Exception e)
        {
            SimukraftReloaded.log.warning("getLocationCloseToPlayer: player was null, returned null V3");
            return new V3(0d, 5d, 0d, 0);
        }

        boolean found = false;
        Block bid = null;

        try
        {
            for (int go = 30; go > 1; go--)
            {
                ret = new V3(p.posX, 5d, p.posZ + go, p.dimension);

                while (!found)
                {
                    bid = p.worldObj.getBlock(ret.x.intValue(), ret.y.intValue(), ret.z.intValue()); // this can NPE

                    if ((p.worldObj.canBlockSeeTheSky(ret.x.intValue(), ret.y.intValue(), ret.z.intValue()) ||
                            p.dimension != 0) &&
                            bid != Blocks.leaves &&
                            bid == null)
                    {
                        found = true;
                    }

                    ret.y++;

                    if (ret.y > 200)
                    {
                        break;
                    }
                }

                if (found)
                {
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (!found)
        {
            return new V3(0d, 5d, 0d, 0);
        }

        return ret;
    }

    /** returns the closest player to a location - make sure you set theDimension too */
    public static EntityPlayer getClosestPlayer(V3 location)
    {
        try
        {
            World world = MinecraftServer.getServer().worldServerForDimension(location.theDimension);
            EntityPlayer ret = world.getClosestPlayer(location.x, location.y, location.z, 60);
            return ret;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /** random name generator for folks - ensures name doesn't exist in this world */
    public static String generateName(int gender, boolean firstNameOnly, String lastNameOptional)
    {
        Random randomGenerator = new Random();
        String firstName = "", lastName = "";
        int i;
        FolkData test = null;

        for (int go = 0; go < 200; go++)
        {
            if (gender == 0)
            {
                i = randomGenerator.nextInt(SimukraftReloadedConfig.configMaleNames.length);
                firstName = SimukraftReloadedConfig.configMaleNames[i].trim();
            }
            else
            {
                i = randomGenerator.nextInt(SimukraftReloadedConfig.configFemaleNames.length);
                firstName = SimukraftReloadedConfig.configFemaleNames[i].trim();
            }

            i = randomGenerator.nextInt(SimukraftReloadedConfig.configSurnames.length);

            if (lastName.contentEquals(""))
            {
                lastName = SimukraftReloadedConfig.configSurnames[i].trim();
            }
            else
            {
                lastName = lastNameOptional;
            }

            test = FolkData.getFolkByName(firstName + " " + lastName);

            if (test == null)
            {
                break;
            }
        } //next go

        if (test != null)  //after 50 go's it still not unique
        {
            lastName += " II";
        }

        if (!firstNameOnly)
        {
            return firstName + " " + lastName;
        }
        else
        {
            return firstName;
        }
    }

    /** fire this folk, resets lots of values */
    public void selfFire()
    {
        SimukraftReloaded.log.info("FolkData: selfFire() " + this.name);
        isWorking = false;

        if (this.inventory.size() > 0)
        {
            int count = 0;

            for (int inv = 0; inv < inventory.size(); inv++)
            {
                ItemStack is = inventory.get(inv);

                if (is != null)
                {
                    if (theEntity != null)
                    {
                        try
                        {
                            theEntity.entityDropItem(is, is.stackSize);
                        }
                        catch (Exception e) {}
                    }
                    else
                    {
                        try
                        {
                            FolkData.getClosestPlayer(this.location).entityDropItem(is, is.stackSize);
                        }
                        catch (Exception e) {}
                    }

                    count += is.stackSize;
                }
            }

            if (count > 0)
            {
                SimukraftReloaded.sendChat(this.name + " has dropped " + count + " items from their inventory");
            }
        }

        this.inventory.clear();

        if (this.theEntity != null)
        {
            theEntity.swingProgress = 0.0f;
            theEntity.getNavigator().clearPathEntity();
        }

        this.employedAt = null;

        if (this.vocation == Vocation.BUILDER)
        {
            theBuilding = null;
        }

        this.vocation = null;
        this.theirJob = null;
        this.action = FolkAction.WANDER;
        this.statusText = "Wandering";
        this.stayPut = false;
        this.saveThisFolk();
    }

    /** tell the folk to goto a location, method will decide how to get them there if you pass NULL for method,
     *  arrival can be NULL */
    public void gotoXYZ(V3 whereTo, GotoMethod methodOfTravel)
    {
    	
    	/*if(theEntity.worldObj.isRemote == true);
    	{
    		SimukraftReloaded.log.info("Is Remote");
    	}
    	if(theEntity.worldObj.isRemote == false)
    	{
    		SimukraftReloaded.log.info("Is NOT Remote");
    	}*/
    	    	
        if (whereTo == null)
        {
            return;
        }

        this.stayPut = false;
        this.destination = whereTo.clone();

        if (this.destination == null)
        {
            return;
        }

        this.destination.doNotTimeout = false;
        int dist = this.location.getDistanceTo(whereTo);

        if (!this.isSpawned())
        {
            methodOfTravel = null;
        }

        if (methodOfTravel == null)
        {
            //decide method of travel
            EntityPlayer pl;
            V3 playpos = null;

            try
            {
                pl = FolkData.getClosestPlayer(this.location);

                if (pl == null || (this.location.theDimension != this.destination.theDimension))
                {
                    dist = 999;
                }
                else
                {
                    playpos = new V3(pl.posX, pl.posY, pl.posZ, pl.dimension);
                }
            }
            catch (Exception e)
            {
                dist = 999;
            }

            if (dist < 40)
            {
                this.gotoMethod = GotoMethod.WALK;
            }

            if (!this.isSpawned() || dist >= 40)
            {
                this.gotoMethod = GotoMethod.BEAM;
            }

            if (playpos != null)  //will be null if player in different dimension or WAY out of range
            {
                if (this.location.getDistanceTo(playpos) >= 40 && whereTo.getDistanceTo(playpos) >= 40)
                {
                    this.gotoMethod = GotoMethod.SHIFT;
                }

                try
                {
                    if (location.theDimension != Minecraft.getMinecraft().thePlayer.dimension &&
                            this.destination.theDimension != Minecraft.getMinecraft().thePlayer.dimension)
                    {
                        this.gotoMethod = GotoMethod.SHIFT;
                    }
                }
                catch (Exception e)
                {
                    this.gotoMethod = GotoMethod.SHIFT;
                }
            }

            //catch all
            if (methodOfTravel == null)
            {
                methodOfTravel = GotoMethod.SHIFT;
            }
        }
        else
        {
            this.gotoMethod = methodOfTravel;
        }

        try
        {
            SimukraftReloaded.log.info("FolkData: GOTOXYZ() for " + this.name + " to " + whereTo.toString() + " - Method:" + gotoMethod.toString()
                             + " DIM:" + whereTo.theDimension);
        }
        catch (Exception e)
        {
            SimukraftReloaded.log.info("FolkData: GOTOXYZ() for " + this.name + " - NULL whereTo");
            return;
        }

        //// start them going
        if (this.destination == null)
        {
            return;
        }

        if (gotoMethod == GotoMethod.SHIFT)
        {
            int xxx = destination.x.intValue();
            int zzz = destination.z.intValue();
            destination.x = xxx + 0.5d;
            destination.z = zzz + 0.5d;

            if (this.theEntity != null)
            {
                //this.theEntity.setPosition(destination.x, destination.y, destination.z);
                //this.theEntity.setPositionAndUpdate(destination.x, destination.y+1, destination.z);
               
                this.theEntity.posX = destination.x;
            	this.theEntity.posY = destination.y;
            	this.theEntity.posZ = destination.z;

                try {
	                if (location.theDimension != destination.theDimension)
	                {
	                	
	                    this.theEntity.travelToDimension(destination.theDimension);
	                    this.theEntity.dimension = destination.theDimension;
	                    this.location.theDimension = destination.theDimension;
	                	
	                }
                } catch(Exception e) {} //NPE destination or .theDimension?
            }

            try {
            	this.location = destination.clone();
            } catch(Exception e) {} // NPEs when above NPEs
        	this.destination = null;
        }
        else if (gotoMethod == GotoMethod.BEAM)
        {
            this.timeStartedGotoing = System.currentTimeMillis();
            beamMeTo(whereTo);
        }
        else if (gotoMethod == GotoMethod.WALK)
        {
            this.stayPut = false;
            this.timeStartedGotoing = System.currentTimeMillis();
            if (this.theEntity !=null) {
            	this.theEntity.gotPath=false;
            }
            //already set their destination, so this is all it needs
            //It will only get here if they are spawned AND within walking distance
            //so the entity's MoveEntity() method takes over now.
        }
    }

    /** beams the folk to the specified location */
    public void beamMeTo(V3 whereToIn)
    {
        this.stayPut = true;
        this.updateLocationFromEntity(); //only does this if they are currently spawned

        if (this.beamingTo != null)
        {
            SimukraftReloaded.log.warning("FolkData:beamMeTo() already beaming " + this.name);
            return;
        }

        if (whereToIn == null)
        {
            SimukraftReloaded.log.warning("FolkData: beamMeTo() whereTo was NULL, cancelled beaming");
            return;
        }

        timeStartedGotoing = System.currentTimeMillis();
        V3 whereTo = whereToIn.clone();
        World destWorld = MinecraftServer.getServer().worldServerForDimension(whereTo.theDimension);

        for (int y = 0; y < 200; y++)
        {
            Block id1 = destWorld.getBlock(whereTo.x.intValue(), whereTo.y.intValue(), whereTo.z.intValue());
            Block id2 = destWorld.getBlock(whereTo.x.intValue(), whereTo.y.intValue() + 1, whereTo.z.intValue());

            if (id1 == null && id2 == null)
            {
                break;
            }

            whereTo.y++;
        }

        try
        {
            int xxx = whereTo.x.intValue();
            whereTo.x = xxx + 0.5d;
            xxx = whereTo.z.intValue();
            whereTo.z = xxx + 0.5d;
            whereTo.y -= 199d;
        }
        catch (Exception e)
        {
            return;
        }

        this.destination = whereTo.clone();
        SimukraftReloaded.log.info("FolkData: BeamMeTo() for " + this.name + " to " + whereTo.toString() + " Dim:" + whereTo.theDimension);
        this.stayPut = true;

        if (isSpawned())
        {
            this.theEntity.getNavigator().clearPathEntity();
        }

        try {
	        if (ModSimukraft.proxy.getClientWorld() != null)
	        {
	            //ModSimukraft.proxy.getClientWorld().playSound(location.x, location.y, location.z, "ashjacksimukraftreloaded:beamdown", 1.0f, 1.0f, false);
	            //ModSimukraft.proxy.getClientWorld().playSound(whereTo.x, whereTo.y, whereTo.z, "ashjacksimukraftreloaded:beamdown", 1f, 1f, false);
	        }
	
	        this.beamingTo = whereTo.clone(); //setting this will trigger the beaming progress in the tick/game loop
        } catch(Exception e) {}
    }

    /** called repeatedly during the beaming progress */
    private void doBeaming()
    {
        try
        {
            if (System.currentTimeMillis() - timeStartedGotoing > 4000 || beamingTo == null)
            {
                // arrived via beaming
                if (this.theEntity != null)
                {
                    //this.theEntity.setPosition(beamingTo.x, beamingTo.y, beamingTo.z);
                	this.theEntity.setPositionAndUpdate(beamingTo.x, beamingTo.y+1, beamingTo.z);
                	
                	//this.theEntity.serverPosX = Integer.parseInt(beamingTo.x.toString());
                	//this.theEntity.serverPosY = Integer.parseInt(beamingTo.y.toString());
                	//this.theEntity.serverPosZ = Integer.parseInt(beamingTo.z.toString());
                	
                	//this.theEntity.posX = beamingTo.x;
                	//this.theEntity.posY = beamingTo.y;
                	//this.theEntity.posZ = beamingTo.z;
                	
                    if (theEntity.dimension != beamingTo.theDimension)
                    {
                        this.theEntity.travelToDimension(beamingTo.theDimension);
                        this.theEntity.dimension = beamingTo.theDimension;
                        this.location.theDimension = beamingTo.theDimension;
                    }
                }

                SimukraftReloaded.log.info("FolkData: doBeaming() complete for " + this.name + " to " + beamingTo.toString() + " (dim " + beamingTo.theDimension + ")");
                this.location = beamingTo.clone();
                this.destination = null;
                beamingTo = null;
                respawnEntity(MinecraftServer.getServer().worldServerForDimension(location.theDimension));
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            this.destination = null;
            beamingTo = null;
            return;
        }

        Random random = new Random();
        Double d4 = ((double) random.nextFloat() - 2D) * 2D;
        this.stayPut = true;
        if(!MinecraftServer.getServer().isDedicatedServer())
        {
        	World theWorld = Minecraft.getMinecraft().theWorld; //only need the client world for particles
        

        for (int p = 0; p < 10; p++)
        {
            try
            {
                if (!SimukraftReloadedConfig.configDisableBeamEffect)
                {
                    theWorld.spawnParticle("portal", location.x + (random.nextDouble()) - 0.5,
                                           location.y - 1, location.z + (random.nextDouble()) - 0.5, 0, -d4, 0);
                }
            }
            catch (Exception e) {}

            try
            {
                if (!SimukraftReloadedConfig.configDisableBeamEffect)
                {
                    theWorld.spawnParticle("portal", beamingTo.x + (random.nextDouble()) - 0.5,
                                           beamingTo.y - 1, beamingTo.z + (random.nextDouble()) - 0.5, 0, -d4, 0);
                }
            }
            catch (Exception e)
            {
            }
        }
        }
    }

    /** method that gotoXYZ has decided to use */
    public enum GotoMethod
    {
        WALK, BEAM, SHIFT;

        public String toString()
        {
            if (this == GotoMethod.BEAM)
            {
                return "Beaming";
            }
            else if (this == GotoMethod.SHIFT)
            {
                return "Shifting";
            }
            else if (this == GotoMethod.WALK)
            {
                return "Walking";
            }
            else
            {
                return "";
            }
        }
    }



    /** loads in all the pre-existing folks data, FolkData class will decide if it should spawn them into world */
    public static void loadAndSpawnFolks()
    {
    	SimukraftReloaded.theFolks.clear();
    	
    	File folksFolder = new File(SimukraftReloaded.getSavesDataFolder() + "folks" + File.separator);

        if (!folksFolder.exists())
        {
            folksFolder.mkdirs();
        }

        boolean useNewFormat=false;
        //check for new file format
        for (File f : folksFolder.listFiles())
        {
            if (f.getName().endsWith(".sk2")){
            	useNewFormat=true;
            	break;
            }
        }

        if (useNewFormat) {
        	for (File f : folksFolder.listFiles())
	        {
	            if (f.getName().endsWith(".sk2"))
	            {
	            	ArrayList<String> strings=SimukraftReloaded.loadSK2(f.getAbsoluteFile().toString());
	            	FolkData folkd = new FolkData();
	            	
	            	for(String line:strings) {
	            		if (line.contains("|")) {
	            			int m1=line.indexOf("|");
	        				String name=line.substring(0,m1);
	        				String value=line.substring(m1+1);
	        				
	        				
	            			try {
	        	    			if (name.contentEquals("employedat")) {
	        	    				if (!value.contentEquals("null")) {
	        	    					folkd.employedAt=new V3(value);
	        	    				}
	        	    				
	        	    			}else if (name.contentEquals("vocation")) {
	        	    				if (!value.contentEquals("null")) {
	        	    					folkd.vocation=Vocation.valueOf(value);
	        	    				}
	        	    			}
	        	    			else if (name.contentEquals("name")) 
	        	    			{
	        	    				folkd.name=value;
	        	    			}
	        	    			else if (name.contentEquals("age")) {
	        	    				folkd.age=Integer.parseInt(value);
	        	    			}
	        	    			else if (name.contentEquals("gender")) {
	        	    				folkd.gender=Integer.parseInt(value);
	        	    			}
	        	    			else if (name.contentEquals("race")) {
	        	    				folkd.folkRaceName=value;
	        	    			}
	        	    			else if (name.contentEquals("skin")) {
	        	    				folkd.skinnumber=Integer.parseInt(value);
	        	    			}
	        	    			else if (name.contentEquals("levelfood")) {
	        	    				folkd.levelFood=Integer.parseInt(value);
	        	    			}
	        	    			else if (name.contentEquals("trait1")) {
	        	    				folkd.trait1=value;
	        	    			}
	        	    			else if (name.contentEquals("trait2")) {
	        	    				folkd.trait2=value;
	        	    			}
	        	    			else if (name.contentEquals("trait3")) {
	        	    				folkd.trait3=value;
	        	    			}
	        	    			else if (name.contentEquals("trait4")) {
	        	    				folkd.trait4=value;
	        	    			}
	        	    			else if (name.contentEquals("levelbuilder")) {
	        	    				folkd.levelBuilder=Float.parseFloat(value);
	        	    			}
	        	    			else if (name.contentEquals("levelminer")) {
	        	    				folkd.levelMiner=Float.parseFloat(value);
	        	    			}
	        	    			else if (name.contentEquals("levelsoldier")) {
	        	    				folkd.levelSoldier=Float.parseFloat(value);
	        	    			}
	        	    			else if (name.contentEquals("stayput")) {
	        	    				folkd.stayPut=Boolean.parseBoolean(value);
	        	    			}
	        	    			else if (name.contentEquals("location")) {
	        	    				folkd.location=new V3(value);
	        	    			}
	        	    			else if (name.contentEquals("pregnancy")) {
	        	    				folkd.pregnancyStage= Float.parseFloat(value);
	        	    			}
	        	    			else if (name.contentEquals("building")) {
	        	    				if (!value.contentEquals("null")) {
	        	    					int m2=value.indexOf("|");
	        	    					int m3=value.indexOf("||");
	        	        				String fn=value.substring(0,m2);
	        	        				String type=value.substring(m2+1,m3);
	        	        				String dir=value.substring(m3+2);  
	        	    					folkd.theBuilding=Building.getBuildingForFolk(fn, type);
	        	    					folkd.theBuilding.buildDirection=dir;
	        	    				}
	        	    			}else if (name.contentEquals("terraformtype")) {
	        	    				if (!value.contentEquals("null")) {
	        	    					folkd.terraformerType=TerraformerType.valueOf(value);
	        	    				}
	        	    			}else if (name.contentEquals("terraformradius")) {
	        	    				folkd.terraformerRadius=Integer.parseInt(value);
	        	    			}
	        	    			
	        	    			
	            			} catch(Exception e) {e.printStackTrace(); }
	            		}
	            	}
	            	if (folkd !=null) {
	            		SimukraftReloaded.log.info("FolkData: loadAndSpawnFolks() Loaded "+folkd.name+" using new file system");
	            		folkd.hasLoaded();
	            	}
	            }
	        }
        	
        } else {   // use the old format (first time only)

	        for (File f : folksFolder.listFiles())
	        {
	            if (f.getName().endsWith(".suk"))
	            {
	                FolkData folkd = (FolkData) ModSimukraft.proxy.loadObject(f.getAbsoluteFile().toString());
	
	                if (folkd != null)
	                {

	                    folkd.hasLoaded();
	                }
	                else
	                {
	                    f.delete();
	                }
	            }
	        }
        }
    }
    
    /** saves this Folk only (folkData) so they can be loaded in next session */
    public void saveThisFolk()
    {
        String folder = SimukraftReloaded.getSavesDataFolder() + "folks" + File.separator;
        File f = new File(folder);

        if (!f.exists())
        {
            f.mkdirs();
        }

        Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	if (side==Side.SERVER) { 
    		ArrayList<String> strings=new ArrayList<String>();
    		if (employedAt ==null) {
    			strings.add("employedat|null");
    		} else {
    			strings.add("employedat|"+this.employedAt.toString());
    		}
	        if (vocation==null) {
	        	strings.add("vocation|null");
	        } else {
	        	strings.add("vocation|"+this.vocation.name());
	        }
    		strings.add("name|"+this.name);
    		strings.add("age|"+this.age);
    		strings.add("gender|"+this.gender);
    		strings.add("skin|"+ this.skinnumber);
    		strings.add("race|"+this.folkRaceName);
    		strings.add("trait1|"+this.trait1);
    		strings.add("trait2|"+this.trait2);
    		strings.add("trait3|"+this.trait3);
    		strings.add("trait4|"+this.trait4);
    		strings.add("levelfood|"+this.levelFood);
    		strings.add("levelbuilder|"+this.levelBuilder);
    		strings.add("levelminer|"+this.levelMiner);
    		strings.add("levelsoldier|"+this.levelSoldier);
    		strings.add("stayput|"+this.stayPut.toString());
    		strings.add("location|"+this.location.toString());
    		strings.add("pregnancy|"+this.pregnancyStage);
    		if (this.theBuilding==null) {
    			strings.add("building|null");
    		} else {
    			strings.add("building|"+theBuilding.displayName+".txt|"+theBuilding.type+"||"+theBuilding.buildDirection);
    		}
    		if (this.terraformerType==null) {
    			strings.add("terraformtype|null");
    		} else {
    			strings.add("terraformtype|"+this.terraformerType.name());
    		}
    		strings.add("terraformradius|"+this.terraformerRadius);
    		
    		if (!this.name.contentEquals("")) {
    			SimukraftReloaded.saveSK2(folder+this.name+".sk2", strings);
    		}
    	}
        

        //ModSimukraft.proxy.saveObject(folder + this.name + ".suk", this);
    }

    /** get the building/home that this folk lives in, null if homeless */
    public Building getHome()
    {
        for (int b = 0; b < SimukraftReloaded.theBuildings.size(); b++)
        {
            Building home = SimukraftReloaded.theBuildings.get(b);

            for (int t = 0; t < home.tennants.size(); t++)
            {
                String tennant = home.tennants.get(t);

                if (tennant.contentEquals(this.name))
                {
                    return home;
                }
            }
        }

        return null;
    }



    /** creates and optionally spawns in a brand-new folk - gets called every minute, but may not spawn every minute */
    public static void generateNewFolk(World world)
    {
        ArrayList<FolkData> fds = FolkData.getFolkHomeless();

        if (fds.size() == 0 && SimukraftReloaded.theFolks.size() < SimukraftReloadedConfig.configPopulationLimit)
        {
            new FolkData(world);
        }
    }
    
    public static void forceGenerateNewFolk(World world)
    {
        FolkData folk = new FolkData(world);
    }
    
    public static void forceGenerateNewFolk(World world, String nme)
    {
        FolkData folk = new FolkData(world, nme);
    }

    /** return a folk based on their name */
    public static FolkData getFolkByName(String name)
    {
        FolkData f = null;

        for (int x = 0; x < SimukraftReloaded.theFolks.size(); x++)
        {
            f = (FolkData) SimukraftReloaded.theFolks.get(x);

            if (f.name.contentEquals(name))
            {
                return f;
            }
        }

        return null;
    }

    /** return a folkData based on their position */
    public static FolkData getFolkByLocation(V3 loc)
    {
        FolkData f = null;

        for (int x = 0; x < SimukraftReloaded.theFolks.size(); x++)
        {
            f = (FolkData) SimukraftReloaded.theFolks.get(x);

            if (f.location.isSameCoordsAs(loc, true, false))
            {
                return f;
            }
        }

        return null;
    }

    /** find a folk who is employed at a particular xyz */
    public static FolkData getFolkByEmployedAt(V3 employedAt)
    {
        FolkData f = null;

        for (int x = 0; x < SimukraftReloaded.theFolks.size(); x++)
        {
            f = (FolkData) SimukraftReloaded.theFolks.get(x);

            if (f.employedAt != null)
            {
                if (f.employedAt.isSameCoordsAs(employedAt, true, false))
                {
                    return f;
                }
            }
        }

        return null;
    }

    /** PLURAL of getFolkByEmployedAt (which returns the first one it finds) */
    public static ArrayList<FolkData> getFolksByEmployedAt(V3 v)
    {
        ArrayList<FolkData> ret = new ArrayList<FolkData>();

        for (int x = 0; x < SimukraftReloaded.theFolks.size(); x++)
        {
            FolkData f = (FolkData) SimukraftReloaded.theFolks.get(x);

            if (f.employedAt != null)
            {
                if (f.employedAt.isSameCoordsAs(v, true, false))
                {
                    ret.add(f);
                }
            }
        }

        return ret;
    }

    /** if showEmployed==true it shows all employed folks    false will return unemployed folks */
    public static ArrayList getFolkUnemployed(boolean showEmployed)
    {
        ArrayList f = new ArrayList();

        for (int x = 0; x < SimukraftReloaded.theFolks.size(); x++)
        {
            FolkData folk = (FolkData) SimukraftReloaded.theFolks.get(x);

            if (showEmployed)
            {
                if (folk.employedAt != null)
                {
                    f.add(folk);
                }
            }
            else
            {
                if (folk.employedAt == null && folk.age > 17 && folk.pregnancyStage == 0.0f)
                {
                    f.add(folk);
                }
            }
        }

        return f;
    }

    /** returns an arraylist of folks that are homeless */
    public static ArrayList getFolkHomeless()
    {
        ArrayList f = new ArrayList();

        for (int x = 0; x < SimukraftReloaded.theFolks.size(); x++)
        {
            FolkData folk = (FolkData) SimukraftReloaded.theFolks.get(x);

            if (folk.getHome() == null)
            {
                f.add(folk);
            }
        }

        return f;
    }

    /** used for spawning so entity can get a reference to the data, returns null if it can't find it */
    public static FolkData getFolkDataByEntityId(int id)
    {
        for (int i = 0; i < SimukraftReloaded.theFolks.size(); i++)
        {
            FolkData fd = SimukraftReloaded.theFolks.get(i);

            if (fd.theEntity != null)
            {
                if (fd.theEntity.getEntityId() == id)
                {
                    return fd;
                }
            }
        }

        return null;
    }

    /** what each folk is doing */
    public enum FolkAction
    {
        ONWAYTOWORK, ATWORK, WANDER, ATHOME, GOINGHOME, STAYINGHOME, HAVINGBABY;

        @Override
        public String toString()
        {
            String ret = "doing nothing";

            if (this == FolkAction.WANDER)
            {
                ret = "just wandering";
            }
            else if (this == FolkAction.ATWORK)
            {
                ret = "at work";
            }
            else if (this == FolkAction.ONWAYTOWORK)
            {
                ret = "on my way to work";
            }
            else if (this == FolkAction.ATHOME)
            {
                ret = "relaxing at home";
            }
            else if (this == FolkAction.GOINGHOME)
            {
                ret = "going home";
            }
            else if (this == FolkAction.STAYINGHOME)
            {
                ret = "staying at home";
            } else if (this==FolkAction.HAVINGBABY) {
            	ret= "having a baby";
            }

            return ret;
        }
    }

    /** called from the entity when the folk has died of something */
    public void eventDied(DamageSource d)
    {
    	Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	SimukraftReloaded.log.info("eventDied in FolkData fired on "+side.toString()+" side");
    	
    	String oldJob = "";

        if (this.vocation != null)
        {
            oldJob = " (" + this.vocation.toString() + ")";
        }

        this.employedAt = null;
        this.vocation = null;
        String deathBy = "";

        if (d == DamageSource.cactus)
        {
            deathBy = "(death by cactus... the worst kind.) ";
        }

        if (d == DamageSource.drown)
        {
            deathBy = "(drowned) ";
        }

        if (d == DamageSource.generic)
        {
            deathBy = "(Natural causes/old age) ";
        }

        if (d == DamageSource.inFire)
        {
            deathBy = "(Spontanious combustion) ";
        }

        if (d == DamageSource.lava)
        {
            deathBy = "(while walking on lava) ";
        }

        if (d == DamageSource.onFire)
        {
            deathBy = "(Burned alive!) ";
        }

        if (d == DamageSource.outOfWorld)
        {
            deathBy = "(Fell out of the world!) ";
        }

        if (d == DamageSource.starve)
        {
            deathBy = "(starvation, Build farms, bakeries and grocery stores!) ";
        }

        if (d == DamageSource.fall)
        {
            deathBy = "(Fell off a cliff, or were they pushed?!)";
        }

        if (d == DamageSource.inWall)
        {
            deathBy = "(Buried alive under gravel/sand)";
        }

        if (deathBy.contentEquals(""))
        {
            Random r = new Random();
            int ded = r.nextInt(6);

            if (ded == 0)
            {
                deathBy = "(Electrocuted while in bath) ";
            }
            else if (ded == 1)
            {
                deathBy = "(Tripped on roller-skate on stairs) ";
            }
            else if (ded == 2)
            {
                deathBy = "(Trampled by cows) ";
            }
            else if (ded == 3)
            {
                deathBy = "(Ran over by minecart) ";
            }
            else if (ded == 4)
            {
                deathBy = "(Slipped on banana skin) ";
            }
            else if (ded == 5)
            {
                deathBy = "(killed by Notch) ";
            }
        }

        String only = "";

        if (this.age < 80)
        {
            only = "They were only " + this.age + " years old.";
        }
        else
        {
            only = "They were " + this.age + " years old, oh well, they had a good long life!";
        }

        SimukraftReloaded.sendChat(this.name
                              + oldJob + " has just died! " + deathBy + only);
        this.action = FolkAction.WANDER;
        int i = 0;

        //find the folk in the array
        for (int fn = 0; fn < SimukraftReloaded.theFolks.size(); fn++)
        {
            FolkData fo = SimukraftReloaded.theFolks.get(fn);

            if (fo.name.contentEquals(this.name))
            {
                i = fn;
                break;
            }
        }

        evictThem();

        //if partnered, un-partner them
        try
        {
            //remove all their relationships
            for (int q = 0; q < SimukraftReloaded.theRelationships.size(); q++)
            {
                try
                {
                    Relationship rel = SimukraftReloaded.theRelationships.get(q);

                    if (rel.folk1.name.contentEquals(this.name) || rel.folk2.name.contentEquals(this.name))
                    {
                        String fn = rel.folk1.name.replaceAll(" ", "") + rel.folk2.name.replaceAll(" ", "");
                        File f = new File(SimukraftReloaded.getSavesDataFolder() + "Relationships" + File.separator
                                          + fn + ".sk2");
                        f.delete();
                        SimukraftReloaded.theRelationships.remove(q);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            File f = new File(SimukraftReloaded.getSavesDataFolder() + "folks" + File.separator + this.name + ".sk2");
            f.delete();

            if (i >= 0)
            {
                if (i < SimukraftReloaded.theFolks.size())
                {
                	SimukraftReloaded.theFolks.remove(i);
                }
            }
        }
        catch (Exception e)
        {
            SimukraftReloaded.log.warning("FolkData: eventDied() "+e.toString());

        }
    }

    /** called when they die, also when they turn 18 and get evicted from their parents house */
    public void evictThem()
    {
        //evict them from their home :-)
        if (this.getHome() != null)
        {
            //SimukraftReloaded.states.population--;

            for (int b = 0; b < SimukraftReloaded.theBuildings.size(); b++)
            {
                Building building = (Building) SimukraftReloaded.theBuildings.get(b);

                if (building != null && this.getHome() != null)
                {
                    if (building.primaryXYZ.isSameCoordsAs(this.getHome().primaryXYZ, true, false))
                    {
                        building.removeTennant(this.name);
                    }
                }
            }
        }
    }

    /** this is called when loading them in, as well as hiring them via GuiEmployFolks()
     * a null will be passed in if they have no job */
    public void setTheirJob(Vocation vocation)
    {
        if (vocation == null)
        {
            return;
        }

        this.vocation = vocation;

        if (this.vocation == Vocation.BUILDER)
        {
            this.theirJob = new JobBuilder(this);
        }
        else if (this.vocation == Vocation.BAKER)
        {
            this.theirJob = new JobBaker(this);
        }
        else if (this.vocation == Vocation.BUTCHER)
        {
            this.theirJob = new JobButcher(this);
        }
        else if (this.vocation == Vocation.CATTLEFARMER)
        {
            this.theirJob = new JobLivestockFarmer(this);
        }
        else if (this.vocation == Vocation.CHICKENFARMER)
        {
            this.theirJob = new JobLivestockFarmer(this);
        }
        else if (this.vocation == Vocation.COURIER)
        {
            this.theirJob = new JobCourier(this);
        }
        else if (this.vocation == Vocation.CROPFARMER)
        {
            this.theirJob = new JobCropFarmer(this);
        }
        else if (this.vocation == Vocation.GLASSMAKER)
        {
            this.theirJob = new JobGlassMaker(this);
        }
        else if (this.vocation == Vocation.BRICKMAKER)
        {
            this.theirJob = new JobBrickMaker(this);
        }
        else if (this.vocation == Vocation.GROCER)
        {
            this.theirJob = new JobGrocer(this);
        }
        else if (this.vocation == Vocation.LUMBERJACK)
        {
            this.theirJob = new JobLumberjack(this);
        }
        else if (this.vocation == Vocation.MERCHANT)
        {
            this.theirJob = new JobBuildersMerchant(this);
        }
        else if (this.vocation == Vocation.MINER)
        {
            this.theirJob = new JobMiner(this);
        }
        else if (this.vocation == Vocation.PIGFARMER)
        {
            this.theirJob = new JobLivestockFarmer(this);
        }
        else if (this.vocation == Vocation.SHEPHERD)
        {
            this.theirJob = new JobShepherd(this);
        }
        else if (this.vocation == Vocation.SOLDIER)
        {
            this.theirJob = new JobSoldier(this);
        }
        else if (this.vocation == Vocation.TERRAFORMER)
        {
            this.theirJob = new JobTerraformer(this);
        }
        else if (this.vocation == Vocation.FISHERMAN)
        {
            this.theirJob = new JobFisherman(this);
        }
        else if (this.vocation == Vocation.PATHBUILDER)
        {
            //this.theirJob=new JobPathBuilder(this);
        }
        else if (this.vocation == Vocation.DAIRYFARMER)
        {
            this.theirJob = new JobDairyFarmer(this);
        }
        else if (this.vocation == Vocation.CHEESEMAKER)
        {
            this.theirJob = new JobCheesemaker(this);
        }
        else if (this.vocation == Vocation.BURGERSMANAGER)
        {
            this.theirJob = new JobBurgersManager(this);
        }
        else if (this.vocation == Vocation.BURGERSFRYCOOK)
        {
            this.theirJob = new JobBurgersFryCook(this);
        }
        else if (this.vocation == Vocation.BURGERSWAITER)
        {
            this.theirJob = new JobBurgersWaiter(this);
        }
        else if (this.vocation == Vocation.EGGFARMER)
        {
            this.theirJob = new JobEggFarmer(this);
        }

        this.theirJob.resetJob();
        this.theirJob.step = 1;
    }
    
    public void generateTraits()
    {
    	Random rand = new Random();
    	//Trait 1
        this.trait1 = Traits.traitList[rand.nextInt(Traits.traitList.length-1)].traitName;
        
        
        //Trait 2
    	this.trait2 = Traits.traitList[rand.nextInt(Traits.traitList.length-1)].traitName;
    	
    	while(this.trait2 == this.trait1 || this.traitHasOpposite(trait2))
    	{
    		this.trait2 = Traits.traitList[rand.nextInt(Traits.traitList.length-1)].traitName;
    	}
    	
    	
    	//Trait 3
    	this.trait3 = Traits.traitList[rand.nextInt(Traits.traitList.length-1)].traitName;
    	
    	while(this.trait3 == this.trait2 || this.trait3 == this.trait1 || this.traitHasOpposite(trait3))
    	{
    		this.trait3 = Traits.traitList[rand.nextInt(Traits.traitList.length-1)].traitName;
    	}
    	
    	
    	//Trait 4
    	this.trait4 = Traits.traitList[rand.nextInt(Traits.traitList.length-1)].traitName;
    	
    	while(this.trait4 == this.trait1 || this.trait4 == this.trait2 || this.trait4 == this.trait3 || this.traitHasOpposite(trait4))
    	{
    		this.trait4 = Traits.traitList[rand.nextInt(Traits.traitList.length-1)].traitName;
    	}
    }
    
    public boolean traitHasOpposite(String trait)
    {
    	if(Trait.getTraitFromName(trait).traitOpposite != null)
    	{
    		if(trait.contains(Trait.getTraitFromName(trait).traitOpposite.traitName))
    		{
    			return true;
    		}
    	}
    	return false;
    }
      
      public boolean hasTrait(Trait trait)
      {
    	  if(this.trait1.contentEquals(trait.traitName) || this.trait2.contentEquals(trait.traitName) || this.trait3.contentEquals(trait.traitName) || this.trait4.contentEquals(trait.traitName))
    	  {
    		  return true;
    	  }
    	  else
    	  {
    		  return false;
    	  }
      }
      
      
      
      
      
      
      
      
      
}

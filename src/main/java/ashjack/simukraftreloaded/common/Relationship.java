package ashjack.simukraftreloaded.common;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/** describes a relationship between two folks both blood-relations and friendships*/
public class Relationship implements Serializable
{
    private static final long serialVersionUID = -1617919828251928361L;
    public enum Level
    {
        ENEMY, DESPISE, HATE, DISLIKE, AQUAINTANCE, FRIEND, GOODFRIEND, BESTFRIENDS, PARTNER, MARRIED,
        MOTHERDAUGHTER, MOTHERSON, FATHERDAUGHTER, FATHERSON, SISTERSISTER, BROTHERBROTHER, SISTERBROTHER,
        GRANDMOTHERDAUGHTER, GRANDMOTHERSON, GRANDFATHERDAUGHTER, GRANDFATHERSON,
        AUNTNEPHEW, AUNTNEICE, UNCLENEPHEW, UNCLENEICE
    }

    public FolkData folk1 = null;
    public FolkData folk2 = null;
    /** the level this relationship is on */
    public Level theLevel = Level.AQUAINTANCE;
    /** the sub-level, each level starts at sublevel 0, when it goes above 100, it goes up a level */
    public int theSubLevel = 0;
    public boolean isBloodRelation = false;

    private Random rand = new Random();

    public Relationship() {
    	//used by sk2 loader
    }
    
    /** folk2 can be a folk or NULL for player, folk1 MUST ALWAYS be a folk */
    public Relationship(FolkData folk1, FolkData folk2, Level startingLevel, boolean isBlood)
    {
        this.folk1 = folk1;
        this.folk2 = folk2;
        this.theLevel = startingLevel;
        this.isBloodRelation = isBlood;
    }

    /** returns a string to use as a sentence FolkX "is best friends with" folkY   */
    @Override
    public String toString()
    {
        if (this.theLevel == Level.AQUAINTANCE)
        {
            return "is an aquaintance with";
        }
        else if (this.theLevel == Level.BESTFRIENDS)
        {
            return "is best friends with";
        }
        else if (this.theLevel == Level.DESPISE)
        {
            return "despises";
        }
        else if (this.theLevel == Level.DISLIKE)
        {
            return "dislikes";
        }
        else if (this.theLevel == Level.ENEMY)
        {
            return "is an ememy of";
        }
        else if (this.theLevel == Level.FRIEND)
        {
            return "is friends with";
        }
        else if (this.theLevel == Level.GOODFRIEND)
        {
            return "is good friends with";
        }
        else if (this.theLevel == Level.HATE)
        {
            return "hates";
        }
        else if (this.theLevel == Level.MARRIED)
        {
            return "is married to";
        }
        else if (this.theLevel == Level.PARTNER)
        {
            return "is living with";
        }
        else
        {
            return "has an unknown relationship with";
        }
    }

    /** returns the relationship <folk> has with other person in this relationship */
    public String toStringPersepctive(FolkData folk)
    {
        String other = "";
        FolkData fother;

        if (folk.name.contentEquals(this.folk1.name))
        {
            other = this.folk2.name;
            fother = this.folk2;
        }
        else
        {
            other = this.folk1.name;
            fother = this.folk1;
        }

        if (this.theLevel == Level.AQUAINTANCE)
        {
            return other + ": Aquaintance";
        }
        else if (this.theLevel == Level.BESTFRIENDS)
        {
            return other + ": Best friends";
        }
        else if (this.theLevel == Level.DESPISE)
        {
            return other + ": Despise";
        }
        else if (this.theLevel == Level.DISLIKE)
        {
            return other + ": Dislike";
        }
        else if (this.theLevel == Level.ENEMY)
        {
            return other + ": Enemy";
        }
        else if (this.theLevel == Level.FRIEND)
        {
            return other + ": Friends";
        }
        else if (this.theLevel == Level.GOODFRIEND)
        {
            return other + ": Good friends";
        }
        else if (this.theLevel == Level.HATE)
        {
            return other + ": Hate";
        }
        else if (this.theLevel == Level.MARRIED)
        {
            return other + ": Married";
        }
        else if (this.theLevel == Level.PARTNER)
        {
            return other + ": Partner";
        }
        else if (this.theLevel == Level.MOTHERDAUGHTER)
        {
            if (fother.age > folk.age)
            {
                return other + ": Mother";
            }
            else
            {
                return other + ": Daughter";
            }
        }
        else if (this.theLevel == Level.MOTHERSON)
        {
            if (fother.age > folk.age)
            {
                return other + ": Mother";
            }
            else
            {
                return other + ": Son";
            }
        }
        else if (this.theLevel == Level.FATHERSON)
        {
            if (fother.age > folk.age)
            {
                return other + ": Father";
            }
            else
            {
                return other + ": Son";
            }
        }
        else if (this.theLevel == Level.FATHERDAUGHTER)
        {
            if (fother.age > folk.age)
            {
                return other + ": Father";
            }
            else
            {
                return other + ": Daughter";
            }
        }
        else if (this.theLevel == Level.SISTERSISTER)
        {
            return other + ": Sister";
        }
        else if (this.theLevel == Level.BROTHERBROTHER)
        {
            return other + ": Brother";
        }
        else if (this.theLevel == Level.SISTERBROTHER)
        {
            if (fother.gender == 0)
            {
                return other + ": Brother";
            }
            else
            {
                return other + ": Sister";
            }
        }
        else if (this.theLevel == Level.GRANDFATHERDAUGHTER)
        {
            if (fother.age > folk.age)
            {
                return other + ": Grandfather";
            }
            else
            {
                return other + ": Granddaughter";
            }
        }
        else if (this.theLevel == Level.GRANDFATHERSON)
        {
            if (fother.age > folk.age)
            {
                return other + ": Grandfather";
            }
            else
            {
                return other + ": Grandson";
            }
        }
        else if (this.theLevel == Level.GRANDMOTHERDAUGHTER)
        {
            if (fother.age > folk.age)
            {
                return other + ": Grandmother";
            }
            else
            {
                return other + ": Granddaughter";
            }
        }
        else if (this.theLevel == Level.GRANDMOTHERSON)
        {
            if (fother.age > folk.age)
            {
                return other + ": Grandmother";
            }
            else
            {
                return other + ": Grandson";
            }
        }
        else if (this.theLevel == Level.AUNTNEPHEW)
        {
            if (fother.age > folk.age)
            {
                return other + ": Aunt";
            }
            else
            {
                return other + ": Nephew";
            }
        }
        else if (this.theLevel == Level.AUNTNEICE)
        {
            if (fother.age > folk.age)
            {
                return other + ": Aunt";
            }
            else
            {
                return other + ": Neice";
            }
        }
        else if (this.theLevel == Level.UNCLENEPHEW)
        {
            if (fother.age > folk.age)
            {
                return other + ": Uncle";
            }
            else
            {
                return other + ": Nephew";
            }
        }
        else if (this.theLevel == Level.UNCLENEICE)
        {
            if (fother.age > folk.age)
            {
                return other + ": Uncle";
            }
            else
            {
                return other + ": Neice";
            }
        }
        else
        {
            return other + ": has an unknown relationship";
        }
    }

    /** returns the FolkData of this person Mother */
    public static FolkData getMotherOf(FolkData sonDaughter)
    {
        ArrayList<Relationship> rels = Relationship.getRelationshipsFor(sonDaughter);

        for (Relationship rel : rels)
        {
            if (rel.theLevel == Level.MOTHERDAUGHTER || rel.theLevel == Level.MOTHERSON)
            {
                if (rel.folk1.age > rel.folk2.age)
                {
                    return FolkData.getFolkByName(rel.folk1.name);
                }
                else
                {
                    return FolkData.getFolkByName(rel.folk2.name);
                }
            }
        }

        return null;
    }

    public static void addRelationship(Relationship rel)
    {
        boolean got = false;

        for (Relationship relation : SimukraftReloaded.theRelationships)
        {
        	try {
	            if (relation.folk1.name.contentEquals(rel.folk1.name) && relation.folk2.name.contentEquals(rel.folk2.name))
	            {
	                got = true;
	            }
	
	            if (relation.folk1.name.contentEquals(rel.folk2.name) && relation.folk2.name.contentEquals(rel.folk1.name))
	            {
	                got = true;
	            }
        	} catch(Exception e){}
        }

        if (!got)
        {
        	SimukraftReloaded.theRelationships.add(rel);
        }
    }

    /** Set up all blood-relationships of a new Child (Brother/sisters/aunt/uncles/mother/father) */
    public static void setupBloodRelationships(FolkData newChild, FolkData father, FolkData mother)
    {
        ArrayList<Relationship> mothers = Relationship.getRelationshipsFor(mother);

        for (Relationship rel : mothers)
        {
            // the other folk in the mother's relationship
            FolkData other;

            if (rel.folk1.name.contentEquals(mother.name))
            {
                other = rel.folk2;
            }
            else
            {
                other = rel.folk1;
            }

            //siblings
            if (rel.theLevel == Level.MOTHERDAUGHTER)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.SISTERBROTHER, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.SISTERSISTER, true));
                }
            }
            else if (rel.theLevel == Level.MOTHERSON)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.BROTHERBROTHER, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.SISTERBROTHER, true));
                }
            }

            //mother's parents > grandparents
            if (rel.theLevel == Level.MOTHERDAUGHTER && !rel.folk1.name.contentEquals(newChild.name) && !rel.folk2.name.contentEquals(newChild.name))
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDMOTHERSON, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDMOTHERDAUGHTER, true));
                }
            }
            else if (rel.theLevel == Level.FATHERDAUGHTER)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDFATHERSON, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDFATHERDAUGHTER, true));
                }
            }

            //add mother's siblings as aunt/uncles of this new child
            if (rel.theLevel == Level.SISTERBROTHER)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.UNCLENEPHEW, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.UNCLENEICE, true));
                }
            }
            else if (rel.theLevel == Level.SISTERSISTER)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.AUNTNEPHEW, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.AUNTNEICE, true));
                }
            }
        }

        ArrayList<Relationship> fathers = Relationship.getRelationshipsFor(father);

        for (Relationship rel : fathers)
        {
            FolkData other;

            if (rel.folk1.name.contentEquals(father.name))
            {
                other = rel.folk2;
            }
            else
            {
                other = rel.folk1;
            }

            //father's parents > grandparents
            if (rel.theLevel == Level.FATHERSON && !rel.folk1.name.contentEquals(newChild.name) && !rel.folk2.name.contentEquals(newChild.name))
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDFATHERSON, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDFATHERDAUGHTER, true));
                }
            }
            else if (rel.theLevel == Level.MOTHERSON)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDMOTHERSON, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.GRANDMOTHERDAUGHTER, true));
                }
            }

            //add father's siblings as aunt/uncles of this new child
            if (rel.theLevel == Level.SISTERBROTHER)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.AUNTNEPHEW, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.AUNTNEICE, true));
                }
            }
            else if (rel.theLevel == Level.BROTHERBROTHER)
            {
                if (newChild.gender == 0)
                {
                    addRelationship(new Relationship(newChild, other, Level.UNCLENEPHEW, true));
                }
                else
                {
                    addRelationship(new Relationship(newChild, other, Level.UNCLENEICE, true));
                }
            }
        }

        //mother
        if (newChild.gender == 0)
        {
            addRelationship(new Relationship(newChild, mother, Level.MOTHERSON, true));
        }
        else
        {
            addRelationship(new Relationship(newChild, mother, Level.MOTHERDAUGHTER, true));
        }

        //father
        if (newChild.gender == 0)
        {
            addRelationship(new Relationship(newChild, father, Level.FATHERSON, true));
        }
        else
        {
            addRelationship(new Relationship(newChild, father, Level.FATHERDAUGHTER, true));
        }
    }

    /** return the full sentence "Fred blogs in best friends with Jane smith" */
    public String toFullString()
    {
        String folk2name = "";

        if (folk2 == null)
        {
            folk2name = "You";
        }
        else
        {
            folk2name = folk2.name;
        }

        return folk1.name + " " + this.toString() + " " + folk2name;
    }

    /** increase the sublevel amount (0 to 100), going above 100 takes it to the next level under most circumstances */
    public void levelIncrease(int byAmount)
    {
        String oldLevel = this.toFullString();
        SimukraftReloaded.log.info("Relationship: + current level and sublevel:" + this.theLevel.toString() + " " + this.theSubLevel);
        this.theSubLevel += byAmount;

        if (theSubLevel > 100)
        {
            //  ENEMY,DESPISE,HATE,DISLIKE,AQUAINTANCE,FRIEND,GOODFRIEND,BESTFRIENDS,PARTNER,MARRIED
            if (this.theLevel == Level.AQUAINTANCE)
            {
                theLevel = Level.FRIEND;
                theSubLevel = 0;
            }
            else if (this.theLevel == Level.BESTFRIENDS)
            {
                if (folk2 == null)
                {
                    theSubLevel = 100;
                }
                else
                {
                    /// mixed gender, BOTH of them has a home, neither are already living with someone, both are 18+
                    if (folk1.gender != folk2.gender)
                    {
                        if (folk1.getHome() != null && folk2.getHome() != null)
                        {
                            if (!Relationship.isFolkLivingWithSomeone(folk1) && !Relationship.isFolkLivingWithSomeone(folk2))
                            {
                                if (folk1.age >= 18 && folk2.age >= 18 && !this.isBloodRelation)
                                {
                                    theSubLevel = 50;

                                    if (rand.nextBoolean())
                                    {
                                        theLevel = Level.MARRIED;
                                        changeFemaleSurname();
                                    }
                                    else
                                    {
                                        theLevel = Level.PARTNER;
                                    }

                                    Building oldhome = folk1.getHome();
                                    Building newhome = folk2.getHome();

                                    if (oldhome != null)
                                    {
                                        oldhome.removeTennant(folk1.name);
                                    }

                                    if (newhome != null)
                                    {
                                        newhome.tennants.add(folk1.name);
                                    }

                                    Building.saveAllBuildings();

                                    if (folk1.employedAt == null && folk2.employedAt == null)
                                    {
                                        try
                                        {
                                            folk1.action = FolkAction.GOINGHOME;
                                            folk1.actionArrival = FolkAction.ATHOME;
                                            folk1.gotoXYZ(folk1.getHome().primaryXYZ, null);
                                            folk2.action = FolkAction.GOINGHOME;
                                            folk2.actionArrival = FolkAction.ATHOME;
                                            folk2.gotoXYZ(folk2.getHome().primaryXYZ, null);
                                        }
                                        catch (Exception e) {}  // NPE if house gets removed.
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (this.theLevel == Level.DESPISE)
            {
                theLevel = Level.HATE;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.DISLIKE)
            {
                theLevel = Level.FRIEND;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.ENEMY)
            {
                theLevel = Level.DESPISE;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.FRIEND)
            {
                theLevel = Level.GOODFRIEND;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.GOODFRIEND)
            {
                theLevel = Level.BESTFRIENDS;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.HATE)
            {
                theLevel = Level.DISLIKE;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.MARRIED)
            {
                theSubLevel = 100;
            }
            else if (this.theLevel == Level.PARTNER)
            {
                theSubLevel = 100;
            }

            if (!this.toFullString().contentEquals(oldLevel))
            {
                this.notifyRelationshipChange();
            }
        }
    }

    /* if they get married change the female surname to match the male */
    private void changeFemaleSurname()
    {
        FolkData femaleFolk;
        FolkData maleFolk;

        if (folk1.gender == 1)
        {
            femaleFolk = folk1;
            maleFolk = folk2;
        }
        else
        {
            femaleFolk = folk2;
            maleFolk = folk1;
        }

        for (int b = 0; b < SimukraftReloaded.theBuildings.size(); b++)
        {
            Building building = (Building) SimukraftReloaded.theBuildings.get(b);

            if (building != null && femaleFolk.getHome() != null)
            {
                if (building.primaryXYZ.isSameCoordsAs(femaleFolk.getHome().primaryXYZ, true, false))
                {
                    building.removeTennant(femaleFolk.name);
                }
            }
        }

        File f = new File(SimukraftReloaded.getSavesDataFolder() + "Folks" + File.separator + femaleFolk.name + ".sk2");
        f.delete();
        String surname = maleFolk.name.substring(maleFolk.name.indexOf(" ") + 1).trim();
        int m = femaleFolk.name.indexOf(" ");
        femaleFolk.name = femaleFolk.name.substring(0, m).trim() + " " + surname;
    }

    /** descrease the sublevel amount (0 to 100), going below 0 takes the level down under most circumstances */
    public void levelDecrease(int byAmount)
    {
        String oldLevel = this.toFullString();
        this.theSubLevel -= byAmount;

        if (theSubLevel < 0)
        {
            //  ENEMY,DESPISE,HATE,DISLIKE,AQUAINTANCE,FRIEND,GOODFRIEND,BESTFRIENDS,PARTNER,MARRIED
            if (this.theLevel == Level.AQUAINTANCE)
            {
                theLevel = Level.DISLIKE;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.BESTFRIENDS)
            {
                if (folk2 == null)
                {
                    theSubLevel = 50;
                }
                else
                {
                    theLevel = Level.GOODFRIEND;
                    theSubLevel = 50;
                }
            }
            else if (this.theLevel == Level.DESPISE)
            {
                theLevel = Level.ENEMY;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.DISLIKE)
            {
                theLevel = Level.HATE;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.ENEMY)
            {
                theSubLevel = 0;
            }
            else if (this.theLevel == Level.FRIEND)
            {
                theLevel = Level.DISLIKE;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.GOODFRIEND)
            {
                theLevel = Level.FRIEND;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.HATE)
            {
                theLevel = Level.DESPISE;
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.MARRIED)
            {
                theSubLevel = 50;
            }
            else if (this.theLevel == Level.PARTNER)
            {
                theSubLevel = 50;
            }

            if (!this.toFullString().contentEquals(oldLevel))
            {
                this.notifyRelationshipChange();
            }
        }
    }

    /** this is fired when a relationship has changed, sends a chat text if one of the people is the player */
    private void notifyRelationshipChange()
    {
        if (folk2 == null || theLevel == Level.MARRIED || theLevel == Level.PARTNER)
        {
            SimukraftReloaded.sendChat(this.toFullString().replaceAll(" is ", " is now "));
        }
    }

    /** loads all relationships into the main array for all folks */
    public static void loadRelationships()
    {
        File relFiles = new File(SimukraftReloaded.getSavesDataFolder() + "Relationships" + File.separator);
        relFiles.mkdirs();

        boolean useNewFormat=false;
        //check for new file format
        for (File f : relFiles.listFiles())
        {
            if (f.getName().endsWith(".sk2")){
            	useNewFormat=true;
            	break;
            }
        }
        
        if (useNewFormat) {
        	SimukraftReloaded.theRelationships.clear();
        	
        	for (File f : relFiles.listFiles())
	        {
	            if (f.getName().endsWith(".sk2"))
	            {
	            	ArrayList<String> strings=SimukraftReloaded.loadSK2(f.getAbsoluteFile().toString());
	            	Relationship rel=new Relationship();
	            	for(String line:strings) {
	            		if (line.contains("|")) {
	            			int m1=line.indexOf("|");
	        				String name=line.substring(0,m1);
	        				String value=line.substring(m1+1);
	        				
	        				if (name.contentEquals("folk1")) {
	        					if (!value.contentEquals("") && !value.contentEquals("null")) {
	        						FolkData folk=FolkData.getFolkByName(value);
	        						if (folk !=null) {
	        							rel.folk1=folk;
	        						}
	        					}
	        				} else if(name.contentEquals("folk2")) {
	        					if (!value.contentEquals("") && !value.contentEquals("null")) {
	        						FolkData folk=FolkData.getFolkByName(value);
	        						if (folk !=null) {
	        							rel.folk2=folk;
	        						}
	        					}
	        				} else if(name.contentEquals("level")) {
	        					rel.theLevel=Level.valueOf(value);
	        				} else if(name.contentEquals("sublevel")) {
	        					rel.theSubLevel=Integer.parseInt(value);
	        				} else if(name.contentEquals("bloodrelation")) {
	        					rel.isBloodRelation=Boolean.parseBoolean(value);
	        				}
	            		}
	            	}
	            	if (rel.folk1 !=null && rel.folk2 !=null) {
	            		addRelationship(rel);
	            	}
	            	
	            }
	        }
        
        } else {  //old format
	        for (File f : relFiles.listFiles())
	        {
	            if (f.getName().endsWith(".suk"))
	            {
	                Relationship rel = (Relationship) ModSimukraft.proxy.loadObject(f.getAbsoluteFile().toString());
	
	                if (rel != null)
	                {
	                    addRelationship(rel);
	                }
	            }
	        }
        }
    }

    /** saves all relationships of all folks out to disk */
    public static void saveRelationships()
    {
    	Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	if (side==Side.SERVER) { 
    		ArrayList<String> strings=new ArrayList<String>();
    		
	    	for (int b = 0; b < SimukraftReloaded.theRelationships.size(); b++)
	        {
	            try {
		        	Relationship rel = (Relationship) SimukraftReloaded.theRelationships.get(b);
		            String fn = rel.folk1.name.replaceAll(" ", "") + rel.folk2.name.replaceAll(" ", "");
		            strings.clear();
		            
		            strings.add("folk1|"+rel.folk1.name);
		            strings.add("folk2|"+rel.folk2.name);
		            strings.add("level|"+rel.theLevel.name());
		            strings.add("sublevel|"+rel.theSubLevel);
		            strings.add("bloodrelation|"+rel.isBloodRelation);
		            
		            SimukraftReloaded.saveSK2(SimukraftReloaded.getSavesDataFolder() + "Relationships" + File.separator
	                                              + fn + ".sk2", strings);
		            
	            }catch(Exception e) {}
	        }
    	}
    	/*
        for (int b = 0; b < ModSimukraft.theRelationships.size(); b++)
        {
            try {
	        	Relationship rel = (Relationship) ModSimukraft.theRelationships.get(b);
	            String fn = rel.folk1.name.replaceAll(" ", "") + rel.folk2.name.replaceAll(" ", "");
	
	            if (rel != null)
	            {
	                ModSimukraft.proxy.saveObject(ModSimukraft.getSavesDataFolder() + "Relationships" + File.separator
	                                              + fn + ".suk", rel);
	            }
            } catch(Exception e) {}
        }*/
    }

    /** called in multiple placed to randomly meddle with a pair of folks relationship (creates one if they don't have one)	 */
    public static void meddleWithRelationship(FolkData folk1, FolkData folk2)
    {
        if (folk1.name.contentEquals(folk2.name))
        {
        	SimukraftReloaded.log.warning("Relationship: meddleWithRelationship() with same folk for both");
            return;
        }

        Relationship rel = Relationship.getRelationshipBetween(folk1, folk2);

        if (rel == null)
        {
            addRelationship(new Relationship(folk1, folk2, Level.AQUAINTANCE, false));
        }
        else
        {
            Random r = new Random();
            int rr = r.nextInt(5); //0 to 4

            if (rr == 0)
            {
                rel.levelDecrease(r.nextInt(30));
            }
            else
            {
                rel.levelIncrease(r.nextInt(30));
            }
        }
    }

    /** returns the Relationship object for a pair of folks, if 2nd folk is null, it means the Player */
    public static Relationship getRelationshipBetween(FolkData folk1, FolkData folk2)
    {
        for (int b = 0; b < SimukraftReloaded.theRelationships.size(); b++)
        {
            Relationship rel = (Relationship) SimukraftReloaded.theRelationships.get(b);

            try {
	            if (folk2 == null && rel.folk2 == null && rel.folk1.name.contentEquals(folk1.name))
	            {
	                return rel;
	            }
	            else if (rel.folk1.name.contentEquals(folk1.name) && rel.folk2.name.contentEquals(folk2.name))
	            {
	                return rel;
	            }
	            else if (rel.folk1.name.contentEquals(folk2.name) && rel.folk2.name.contentEquals(folk1.name))
	            {
	                return rel;
	            }
            } catch(Exception e) {}
        }

        return null; //no relationship between these two
    }

    /** returns and ArrayList<Relationship> of all relationships the passed in folk has */
    public static ArrayList<Relationship> getRelationshipsFor(FolkData theFolk)
    {
        ArrayList<Relationship> rels = new ArrayList<Relationship>();

        for (int i = 0; i < SimukraftReloaded.theRelationships.size(); i++)
        {
           try {
        	Relationship rel = SimukraftReloaded.theRelationships.get(i);

            if (rel.folk1.name.contentEquals(theFolk.name) || rel.folk2.name.contentEquals(theFolk.name))
            {
                rels.add(rel);
            }
           }catch(Exception e){}
        }

        return rels;
    }

    /** return TRUE/FALSE if the passed in folk is either MARRIED or PARTNERED with someone (other folk) */
    public static boolean isFolkLivingWithSomeone(FolkData theFolk)
    {
        ArrayList<Relationship> rels = Relationship.getRelationshipsFor(theFolk);
        boolean ret = false;

        for (int i = 0; i < rels.size(); i++)
        {
            Relationship rel = rels.get(i);

            if (rel.theLevel == Level.MARRIED || rel.theLevel == Level.PARTNER)
            {
                ret = true;
                break;
            }
        }

        return ret;
    }

    /** OVERLOAD - returns FolkData or NULL of folk they are living with */
    public static FolkData isFolkLivingWithSomeone(FolkData theFolk, boolean returnFolk)
    {
        ArrayList<Relationship> rels = Relationship.getRelationshipsFor(theFolk);

        for (int i = 0; i < rels.size(); i++)
        {
            Relationship rel = rels.get(i);

            if (rel.theLevel == Level.MARRIED || rel.theLevel == Level.PARTNER)
            {
                if (rel.folk1.name.contentEquals(theFolk.name))
                {
                    return FolkData.getFolkByName(rel.folk2.name);
                }
                else
                {
                    return FolkData.getFolkByName(rel.folk1.name);
                }
            }
        }

        return null;
    }
}

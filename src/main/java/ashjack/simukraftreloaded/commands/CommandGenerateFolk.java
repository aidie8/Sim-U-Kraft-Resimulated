package ashjack.simukraftreloaded.commands;

import java.util.ArrayList;
import java.util.List;

import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.packetsNEW.PacketHandler;
import ashjack.simukraftreloaded.packetsNEW.toServer.GenerateFolkPacket;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandGenerateFolk implements ICommand{
	
	private final List aliases;
	
	public CommandGenerateFolk()
	{
		aliases = new ArrayList(); 
        aliases.add("generatefolk");
        aliases.add("generatesim");
	}

	@Override
	public int compareTo(Object arg0) 
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return "generatefolk";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) 
	{
		return "generatefolk <name>";
	}

	@Override
	public List getCommandAliases() 
	{
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] argString) 
	{
		if(argString.length == 0)
		{
			//FolkData.forceGenerateNewFolk(sender.getEntityWorld());
			PacketHandler.net.sendToServer(new GenerateFolkPacket(sender.getEntityWorld(), true));
		}
		else if(argString.length == 1)
		{
			FolkData.forceGenerateNewFolk(sender.getEntityWorld(), argString[0]);
		}
		else if(argString.length == 2)
		{
			FolkData.forceGenerateNewFolk(sender.getEntityWorld(), argString[0] + " " + argString[1]);
		}
		else
		{
			SimukraftReloaded.sendChat("Invalid arguments, should be: /generatefolk <name> or /generatefolk");
		}	
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) 
	{
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) 
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) 
	{
		return false;
	}

}

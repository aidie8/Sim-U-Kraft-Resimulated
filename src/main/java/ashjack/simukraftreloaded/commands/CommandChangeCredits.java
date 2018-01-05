package ashjack.simukraftreloaded.commands;

import java.util.ArrayList;
import java.util.List;

import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandChangeCredits implements ICommand{
	
	private final List aliases;
	
	public CommandChangeCredits()
	{
		aliases = new ArrayList(); 
        aliases.add("credits"); 
	}

	@Override
	public int compareTo(Object arg0) 
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return "credits";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "changecredits <amount>";
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
			SimukraftReloaded.sendChat("Invalid arguments, should be: /credits <amount>");
			return;
		}
		
		try
		{
			if(argString.length < 2)
			{
				SimukraftReloaded.states.credits = Float.parseFloat(argString[0]);
				SimukraftReloaded.states.saveStates();
			}
			else
			{
				SimukraftReloaded.sendChat("Invalid arguments, should be: /credits <amount>");
				return;
			}
		}
		catch(Exception e)
		{
			SimukraftReloaded.sendChat("The amount must be a number!");
			return;
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

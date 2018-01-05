package ashjack.simukraftreloaded.client.Gui;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class KeyHandler
{
	public static final int FOLKCLOSE_KEY = 0;
	
	private static final String[] keyDesc = {"key.ashjacksimukraftreloaded.desc"};
	private static final int[] keyValues = {Keyboard.KEY_ESCAPE};
	private final KeyBinding[] keys;
	
	public KeyHandler()
	{
		keys = new KeyBinding[keyValues.length];
		for(int i = 0; i < keyValues.length; i++)
		{
			keys[i] = new KeyBinding(keyDesc[i], keyValues[i], "key.ashjacksimukraftreloaded.category");
			ClientRegistry.registerKeyBinding(keys[i]);
		}
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		if(!FMLClientHandler.instance().isGUIOpen(GuiChat.class))
		{
			int key = Keyboard.getEventKey();
			boolean isDown = Keyboard.getEventKeyState();
			
			if(isDown && key == keyValues[FOLKCLOSE_KEY])
			{
				
			}
		}
	}
}
package ashjack.simukraftreloaded.packetsNEW.toServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import ashjack.simukraftreloaded.core.building.Building;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LoadBuildingPacket implements IMessage
{
	 private String GuiBuildingCon;

	 public LoadBuildingPacket() {}
	 
	 public LoadBuildingPacket(String GuiBuildingCon) 
	 {
		 this.GuiBuildingCon = GuiBuildingCon;
	 }

	 @Override
	 public void fromBytes(ByteBuf buf) 
	 {
		 GuiBuildingCon = ByteBufUtils.readUTF8String(buf);
	 }

	 @Override
	 public void toBytes(ByteBuf buf) 
	 {
		 ByteBufUtils.writeUTF8String(buf, GuiBuildingCon);
	 }
	 
	 public static class Handler implements IMessageHandler<LoadBuildingPacket, IMessage> {
	 @Override
	 public IMessage onMessage(LoadBuildingPacket message, MessageContext ctx) {
	
	 Building.loadAllBuildings();
		 
	 //player.openGui(ModSimukraft.instance, message.id, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	 return null;
	 }
	 }
}

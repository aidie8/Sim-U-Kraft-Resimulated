package ashjack.simukraftreloaded.packetsNEW.toClient;

import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UpdateFolkPositionPacket implements IMessage
{
	
	private String posString;
	String[] data;
	private static String folkName;
	private static String pos;

	public UpdateFolkPositionPacket() {}
	
	public UpdateFolkPositionPacket(String posString) 
	 {
		 this.posString = posString;
		 data = posString.split(";");
		 pos = data[0];
		 folkName = data[1];
	 }
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		
	}
	
	public static class Handler implements IMessageHandler<UpdateFolkPositionPacket, IMessage> 
	{
        @Override
        public IMessage onMessage(UpdateFolkPositionPacket message, MessageContext ctx) 
        {
        	FolkData folk=FolkData.getFolkByName(folkName);
         	V3 newpos=new V3(pos);
         	if (folk !=null && newpos !=null) 
         	{
         		folk.serverToClientLocationUpdate(newpos);
         	}
         	return null;
        }
    }

}

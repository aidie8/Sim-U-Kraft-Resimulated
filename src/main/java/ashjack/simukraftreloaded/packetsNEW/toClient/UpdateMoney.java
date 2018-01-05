package ashjack.simukraftreloaded.packetsNEW.toClient;

import ashjack.simukraftreloaded.common.GameStates;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class UpdateMoney implements IMessage {
	
	public UpdateMoney(){}
	private static float money = SimukraftReloaded.states.credits ;
	public UpdateMoney(int money) {
		this.money = money;
	}
	
	public void toBytes(ByteBuf buf) {
	buf.writeFloat(money);	
	}
	
	
	public void fromBytes(ByteBuf buf) {
		money = buf.readFloat();
	}
	public static class Handler implements IMessageHandler<UpdateMoney, IMessage> 
	{
		@Override
        public IMessage onMessage(UpdateMoney message, MessageContext ctx) 
        {
        System.out.print(message.money);
        GameStates.setmoney(message.money);
        
        return null;
        }
        }

		private int money(int money) {
			return money;
			
			
		}

         	
	}


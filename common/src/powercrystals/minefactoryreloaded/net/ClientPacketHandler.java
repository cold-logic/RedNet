package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.utils.PacketWrapper;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ClientPacketHandler implements IPacketHandler {
	
	@SuppressWarnings("rawtypes")
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		Class[] decodeAs = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Byte.class };
		Object[] packetReadout = PacketWrapper.readPacketData(data, decodeAs);
		TileEntity te = ((EntityPlayer) player).worldObj.getBlockTileEntity((Integer) packetReadout[0], (Integer) packetReadout[1], (Integer) packetReadout[2]);
		if (te instanceof TileEntityRedNetCable) {
			TileEntityRedNetCable tec = (TileEntityRedNetCable) te;
			tec.setSideColor(ForgeDirection.DOWN, (Integer) packetReadout[3]);
			tec.setSideColor(ForgeDirection.UP, (Integer) packetReadout[4]);
			tec.setSideColor(ForgeDirection.NORTH, (Integer) packetReadout[5]);
			tec.setSideColor(ForgeDirection.SOUTH, (Integer) packetReadout[6]);
			tec.setSideColor(ForgeDirection.WEST, (Integer) packetReadout[7]);
			tec.setSideColor(ForgeDirection.EAST, (Integer) packetReadout[8]);
			tec.setMode((Byte) packetReadout[9]);
		}
	}
}
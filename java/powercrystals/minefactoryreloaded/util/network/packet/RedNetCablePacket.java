package powercrystals.minefactoryreloaded.util.network.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.network.NetworkManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class RedNetCablePacket extends AbstractPacket {

	String channel;
	int packetID, x, y, z, down, up, north, south, west, east;
	byte mode;
	
	public RedNetCablePacket(){
		
	}
	
	public RedNetCablePacket(String modnetworkchannel, int packetID, int x, int y, int z, int down, int up, int north, int south, int west, int east, byte mode){
		this.channel = modnetworkchannel;
		this.packetID = packetID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.down = down;
		this.up = up;
		this.north = north;
		this.south = south;
		this.west = west;
		this.east = east;
		this.mode = mode;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(down);
		buffer.writeInt(up);
		buffer.writeInt(north);
		buffer.writeInt(south);
		buffer.writeInt(west);
		buffer.writeInt(east);
		buffer.writeByte(mode);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        down = buffer.readInt();
        up = buffer.readInt();
        north = buffer.readInt();
        south = buffer.readInt();
        west = buffer.readInt();
        east = buffer.readInt();
        mode = buffer.readByte();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		World world = player.worldObj;
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileEntityRedNetCable)
        {
        	TileEntityRedNetCable tec = (TileEntityRedNetCable) te;
        	tec.setSideColor(ForgeDirection.DOWN, (Integer) down);
			tec.setSideColor(ForgeDirection.UP, (Integer) up);
			tec.setSideColor(ForgeDirection.NORTH, (Integer) north);
			tec.setSideColor(ForgeDirection.SOUTH, (Integer) south);
			tec.setSideColor(ForgeDirection.WEST, (Integer) west);
			tec.setSideColor(ForgeDirection.EAST, (Integer) east);
			tec.setMode((Byte) mode);
        }
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
        
	}

}
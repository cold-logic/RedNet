package powercrystals.minefactoryreloaded.tile.rednet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetDecorative;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.utils.BlockPosition;
import powercrystals.minefactoryreloaded.utils.INeighboorUpdateTile;
import powercrystals.minefactoryreloaded.utils.PacketWrapper;

import java.util.Arrays;
import java.util.List;

public class TileEntityRedNetCable extends TileEntity implements INeighboorUpdateTile
{
	private int[] _sideColors = new int [6];
	private byte _mode; // 0: standard, 1: force connection, 2: connect to cables only
	
	private RedstoneNetwork _network;
	private boolean _needsNetworkUpdate;
	
	private static final int _maxVanillaBlockId = 158;
	private static List<Integer> _connectionWhitelist = Arrays.asList(23, 25, 27, 28, 29, 33, 46, 55, 64, 69, 70, 71, 72, 75, 76, 77, 93, 94, 96, 107, 123, 124, 131, 143, 147, 148, 149, 150, 151, 152, 154, 157, 158);
	
	public void setSideColor(ForgeDirection side, int color)
	{
		if(side == ForgeDirection.UNKNOWN)
		{
			return;
		}
		_sideColors[side.ordinal()] = color;
		updateNetwork();
	}
	
	public int getSideColor(ForgeDirection side)
	{
		if(side == ForgeDirection.UNKNOWN)
		{
			return 0;
		}
		return _sideColors[side.ordinal()];
	}
	
	public byte getMode()
	{
		return _mode;
	}
	
	public void setMode(byte mode)
	{
		boolean mustUpdate = (mode != _mode);
		_mode = mode;
		if(mustUpdate)
		{
			_needsNetworkUpdate = true;
		}
	}
	
	public RedNetConnectionType getConnectionState(ForgeDirection side) {
		BlockPosition bp = new BlockPosition(this);
		bp.orientation = side;
		bp.moveForwards(1);
		
		int blockId = worldObj.getBlockId(bp.x, bp.y, bp.z);
		Block b = Block.blocksList[blockId];
		
		if(b == null)
		{
			return RedNetConnectionType.None;
		}
		else if(blockId == MineFactoryReloadedCore.rednetCableBlock.blockID) 
		{
			return RedNetConnectionType.CableAll;
		}
		else if(_mode == 3) 
		{
			return RedNetConnectionType.None;
		}
		else if(b instanceof IConnectableRedNet) 
		{
			RedNetConnectionType type = ((IConnectableRedNet)b).getConnectionType(worldObj, bp.x, bp.y, bp.z, side.getOpposite());
			return type.isConnectionForced && !(_mode == 1 | _mode == 2) ? RedNetConnectionType.None : type; 
		}
		else if(b instanceof IRedNetNoConnection || b.isAirBlock(worldObj, bp.x, bp.y, bp.z))
		{
			return RedNetConnectionType.None;
		}
		else if (_mode == 2 && b.isBlockSolidOnSide(worldObj, bp.x, bp.y, bp.z, side.getOpposite()))
		{
			return RedNetConnectionType.ForcedCableSingle;
		}
		else if(_mode == 1) 
		{
			return RedNetConnectionType.ForcedPlateSingle;
		}
		else if ((blockId <= _maxVanillaBlockId && !_connectionWhitelist.contains(blockId)) || b instanceof IRedNetDecorative)
		{
			return RedNetConnectionType.None;
		}
		else if(b.isBlockSolidOnSide(worldObj, bp.x, bp.y, bp.z, side.getOpposite()))
		{
			return RedNetConnectionType.CableSingle;
		}
		else
		{
			return RedNetConnectionType.PlateSingle;
		}
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		return PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, 11, new Object[]{xCoord, yCoord, zCoord, _sideColors[0], _sideColors[1], _sideColors[2], _sideColors[3], _sideColors[4], _sideColors[5], _mode});
	}


	@Override
	public void validate()
	{
		super.validate();
		_needsNetworkUpdate = true;
	}
	
	@Override
	public void updateEntity()
	{
		if(worldObj.isRemote)
			return;
		if(_network != null && _network.isInvalid())
		{
			_network = null;
			_needsNetworkUpdate = true;
		}
		if(_needsNetworkUpdate)
		{
			_needsNetworkUpdate = false;
			updateNetwork();
		}
		_network.tick();
	}
	
	@Override
	public void onNeighboorChanged()
	{
		_needsNetworkUpdate = true;
	}
	
	public RedstoneNetwork getNetwork()
	{
		return _network;
	}
	
	public void setNetwork(RedstoneNetwork network)
	{
		_network = network;
		_network.addCable(new BlockPosition(this));
	}
	
	private void updateNetwork()
	{
		if(worldObj.isRemote)
		{
			return;
		}
		
		BlockPosition ourbp = new BlockPosition(this);
		if(_network == null)
		{
			for(BlockPosition bp : ourbp.getAdjacent(true))
			{
				TileEntity te = bp.getTileEntity(worldObj);
				if(te instanceof TileEntityRedNetCable)
				{
					TileEntityRedNetCable cable = ((TileEntityRedNetCable)te);
					if(cable.getNetwork() != null && !cable.getNetwork().isInvalid())
					{
						_network = cable.getNetwork();
						break;
					}
				}
			}
		}
		if(_network == null)
		{
			setNetwork(new RedstoneNetwork(worldObj));
		}
		for(BlockPosition bp : ourbp.getAdjacent(true))
		{
			TileEntity te = bp.getTileEntity(worldObj);
			if(te instanceof TileEntityRedNetCable)
			{
				TileEntityRedNetCable cable = ((TileEntityRedNetCable)te);
				if(cable.getNetwork() == null)
				{
					cable.setNetwork(_network);
				}
				else if(cable.getNetwork() != _network && cable.getNetwork() != null && !cable.getNetwork().isInvalid())
				{
					_network.mergeNetwork(cable.getNetwork());
				}
			}
			else
			{
				updateNearbyNode(bp);
			}
		}
	}
	
	public void updateNodes()
	{
		for(BlockPosition bp : new BlockPosition(this).getAdjacent(true))
		{
			updateNearbyNode(bp);
		}
	}
	
	private void updateNearbyNode(BlockPosition bp)
	{
		int subnet = getSideColor(bp.orientation);
		RedNetConnectionType connectionType = getConnectionState(bp.orientation);
		
		if(!worldObj.isAirBlock(bp.x, bp.y, bp.z))
		{
			if(connectionType.isAllSubnets)
			{
				_network.addOrUpdateNode(bp);
			}
			else if(connectionType.isCable)
			{
				_network.addOrUpdateNode(bp, subnet, false);
			}
			else if(connectionType.isPlate)
			{
				_network.addOrUpdateNode(bp, subnet, true);
			}
			else
			{
				_network.removeNode(bp);
			}
		}
		else
		{
			_network.removeNode(bp);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setIntArray("sideSubnets", _sideColors);
		tag.setByte("mode", _mode);
		tag.setByte("v", (byte)1);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		_sideColors = tag.getIntArray("sideSubnets");
		if(_sideColors.length == 0)
		{
			_sideColors = new int[6];
		}
		_mode = tag.getByte("mode");
		switch (tag.getByte("v"))
		{
		case 0:
			if (_mode == 2)
				_mode = 3;
			break;
		default:
			break;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
}

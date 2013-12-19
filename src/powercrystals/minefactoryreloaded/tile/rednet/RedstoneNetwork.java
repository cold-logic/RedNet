package powercrystals.minefactoryreloaded.tile.rednet;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.bouncycastle.util.Arrays;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.utils.BlockPosition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RedstoneNetwork {

	private boolean _ignoreUpdates;
	private boolean _mustUpdate;
	private boolean _invalid;

	private Map<Integer, List<BlockPosition>> _singleNodes = new HashMap<Integer, List<BlockPosition>>();
	private List<BlockPosition> _omniNodes = new LinkedList<BlockPosition>();
	private List<BlockPosition> _weakNodes = new LinkedList<BlockPosition>();
	private List<BlockPosition> _cables = new LinkedList<BlockPosition>();

	private int[] _powerLevelOutput = new int[16];
	private BlockPosition[] _powerProviders = new BlockPosition[16];

	private World _world;
	public RedstoneNetwork(World world) {
		_world = world;
		for (int i = 0; i < 16; i++) {
			_singleNodes.put(i, new LinkedList<BlockPosition>());
		}
	}

	public void tick() {
		if (_mustUpdate) {
			_mustUpdate = false;
			updatePowerLevels();
		}
	}

	public void setInvalid() {
		_invalid = true;
	}

	public boolean isInvalid() {
		return _invalid;
	}

	public int getPowerLevelOutput(int subnet) {
		return _powerLevelOutput[subnet];
	}

	public boolean isWeakNode(BlockPosition node) {
		return _weakNodes.contains(node);
	}

	public void addOrUpdateNode(BlockPosition node) {
		int blockId = _world.getBlockId(node.x, node.y, node.z);
		if (blockId == MineFactoryReloadedCore.rednetCableBlock.blockID) {
			return;
		}

		if (!_omniNodes.contains(node)) {
			_omniNodes.add(node);
			notifyOmniNode(node);
		}

		for (int subnet = 0; subnet < 16; subnet++) {
			int power = getOmniNodePowerLevel(node, subnet);
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
				notifyNodes(subnet);
			} else if (node.equals(_powerProviders[subnet]) && Math.abs(power) < Math.abs(_powerLevelOutput[subnet])) {
				updatePowerLevels(subnet);
			}
		}
	}

	public void addOrUpdateNode(BlockPosition node, int subnet, boolean allowWeak) {
		int blockId = _world.getBlockId(node.x, node.y, node.z);
		if (blockId == MineFactoryReloadedCore.rednetCableBlock.blockID) {
			return;
		}

		if (!_singleNodes.get(subnet).contains(node)) {
			removeNode(node);
			_singleNodes.get(subnet).add(node);
			notifySingleNode(node, subnet);
		}

		if (allowWeak) {
			_weakNodes.add(node);
		} else {
			_weakNodes.remove(node);
		}

		int power = getSingleNodePowerLevel(node);
		if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
			_powerLevelOutput[subnet] = power;
			_powerProviders[subnet] = node;
			notifyNodes(subnet);
		} else if (node.equals(_powerProviders[subnet]) && Math.abs(power) < Math.abs(_powerLevelOutput[subnet])) {
			updatePowerLevels(subnet);
		}
	}

	public void removeNode(BlockPosition node) {
		boolean notify = false;
		boolean omniNode = _omniNodes.contains(node);

		if (omniNode)
			_omniNodes.remove(node);
		_weakNodes.remove(node);

		for (int subnet = 0; subnet < 16; subnet++) {
			if (_singleNodes.get(subnet).contains(node)) {
				notify = true;
				_singleNodes.get(subnet).remove(node);
			}

			if (node.equals(_powerProviders[subnet])) {
				updatePowerLevels(subnet);
			}
		}

		int blockId = _world.getBlockId(node.x, node.y, node.z);
		if (notify) {
			if (blockId == MineFactoryReloadedCore.rednetCableBlock.blockID) {
				return;
			} else if (Block.blocksList[blockId] instanceof IConnectableRedNet) {
				((IConnectableRedNet) Block.blocksList[blockId]).onInputChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), 0);
			}
		} else if (omniNode && Block.blocksList[blockId] instanceof IConnectableRedNet) {
			((IConnectableRedNet) Block.blocksList[blockId]).onInputsChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		}
		_world.notifyBlockOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock.blockID);
		_world.notifyBlocksOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock.blockID);
	}

	public void addCable(BlockPosition cable) {
		if (!_cables.contains(cable)) {
			_cables.add(cable);
		}
	}

	public void mergeNetwork(RedstoneNetwork network) {
		if (_invalid) {
			return;
		}
		
		network.setInvalid();
		for (int subnet = 0; subnet < 16; subnet++) {
			_singleNodes.get(subnet).addAll(network._singleNodes.get(subnet));
		}

		_omniNodes.addAll(network._omniNodes);
		_weakNodes.addAll(network._weakNodes);
		_mustUpdate = _mustUpdate | network._mustUpdate;

		for (BlockPosition cable : network._cables) {
			_cables.add(cable);
			TileEntity te = cable.getTileEntity(_world);
			if (te instanceof TileEntityRedNetCable) {
				((TileEntityRedNetCable) te).setNetwork(this);
			}
		}

		updatePowerLevels();
	}

	public void updatePowerLevels() {
		for (int subnet = 0; subnet < 16; subnet++) {
			updatePowerLevels(subnet);
		}
	}

	public void updatePowerLevels(int subnet) {
		int lastPower = _powerLevelOutput[subnet];

		_powerLevelOutput[subnet] = 0;
		_powerProviders[subnet] = null;

		for (BlockPosition node : _singleNodes.get(subnet)) {
			if (!isNodeLoaded(node)) {
				continue;
			}
			int power = getSingleNodePowerLevel(node);
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
			}
		}

		for (BlockPosition node : _omniNodes) {
			if (!isNodeLoaded(node)) {
				continue;
			}
			int power = getOmniNodePowerLevel(node, subnet);
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet])) {
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
			}

		}
		if (_powerLevelOutput[subnet] != lastPower) {
			notifyNodes(subnet);
		}
	}

	private void notifyNodes(int subnet) {
		if (_ignoreUpdates) {
			_mustUpdate = true;
			return;
		}
		_ignoreUpdates = true;
		for (int i = 0; i < _singleNodes.get(subnet).size(); i++) {
			BlockPosition bp = _singleNodes.get(subnet).get(i);
			notifySingleNode(bp, subnet);
		}
		for (int i = 0; i < _omniNodes.size(); i++) {
			BlockPosition bp = _omniNodes.get(i);
			notifyOmniNode(bp);
		}
		_ignoreUpdates = false;
	}

	private boolean isNodeLoaded(BlockPosition node) {
		return _world.getChunkProvider().chunkExists(node.x >> 4, node.z >> 4);
	}

	private void notifySingleNode(BlockPosition node, int subnet) {
		if (isNodeLoaded(node)) {
			int blockId = _world.getBlockId(node.x, node.y, node.z);
			if (blockId == MineFactoryReloadedCore.rednetCableBlock.blockID) {
				return;
			} else if (Block.blocksList[blockId] instanceof IConnectableRedNet) {
				((IConnectableRedNet) Block.blocksList[blockId]).onInputChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), _powerLevelOutput[subnet]);
			} else {
				_world.notifyBlockOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock.blockID);
				_world.notifyBlocksOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock.blockID);
			}
		}
	}

	private void notifyOmniNode(BlockPosition node) {
		if (isNodeLoaded(node)) {
			int blockId = _world.getBlockId(node.x, node.y, node.z);
			if (Block.blocksList[blockId] instanceof IConnectableRedNet) {
				((IConnectableRedNet) Block.blocksList[blockId]).onInputsChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), Arrays.clone(_powerLevelOutput));
			}
		}
	}

	private int getOmniNodePowerLevel(BlockPosition node, int subnet) {
		if (!isNodeLoaded(node)) {
			return 0;
		}
		IConnectableRedNet b = ((IConnectableRedNet) Block.blocksList[_world.getBlockId(node.x, node.y, node.z)]);
		if (b != null) {
			return b.getOutputValue(_world, node.x, node.y, node.z, node.orientation.getOpposite(), subnet);
		} else {
			return 0;
		}
	}

	private int getSingleNodePowerLevel(BlockPosition node) {
		if (!isNodeLoaded(node)) {
			return 0;
		}

		int offset = 0;
		int blockId = _world.getBlockId(node.x, node.y, node.z);
		if (blockId == Block.redstoneWire.blockID) {
			offset = -1;
		}

		int ret = 0;
		if (_weakNodes.contains(node) || Block.blocksList[blockId] instanceof IConnectableRedNet) {
			int weakPower = _world.getIndirectPowerLevelTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
			int strongPower = _world.isBlockProvidingPowerTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
			ret = Math.abs(weakPower) > Math.abs(strongPower) ? weakPower : strongPower;
		} else {
			ret = _world.isBlockProvidingPowerTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
		}

		if (offset == ret)
			return 0;
		return ret;
	}
}
package powercrystals.minefactoryreloaded.api.rednet;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * 
 * You should not implement this yourself. Instead, use this to look for cables to notify from your IConnectableRedNet as this does not
 * require a block update. This will be implemented on the cable's Block class.
 *
 */
public interface IRedNetNetworkContainer
{
	/**
	 * Tells the network to recalculate all subnets.
	 * @param world The world this cable is in.
	 * @param x The x-coordinate of this cable.
	 * @param x The y-coordinate of this cable.
	 * @param x The z-coordinate of this cable.
	 */
	public void updateNetwork(World world, int x, int y, int z);
	
	/**
	 * Tells the network to recalculate a specific subnet.
	 * @param world The world this cable is in.
	 * @param x The x-coordinate of this cable.
	 * @param x The y-coordinate of this cable.
	 * @param x The z-coordinate of this cable.
	 * @param subnet The subnet to recalculate.
	 */
	public void updateNetwork(World world, int x, int y, int z, int subnet);

	void registerIcons(IIconRegister ir);

	TileEntity createNewTileEntity(World world);

	void onNeighborBlockChange(World world, int x, int y, int z, Block blockId);

	void breakBlock(World world, int x, int y, int z, int id, int meta);

	boolean isBlockSolidOnSide(World world, int x, int y, int z,
			ForgeDirection side);
}

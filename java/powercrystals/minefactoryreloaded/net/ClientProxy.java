package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.render.tileentity.RedstoneCableRenderer;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		MineFactoryReloadedCore.renderIdRedstoneCable = RenderingRegistry.getNextAvailableRenderId();
		RedstoneCableRenderer renderer = new RedstoneCableRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetCable.class, renderer);
		RenderingRegistry.registerBlockHandler(renderer);
	}
}

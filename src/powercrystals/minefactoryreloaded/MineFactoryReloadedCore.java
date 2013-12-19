package powercrystals.minefactoryreloaded;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import powercrystals.minefactoryreloaded.block.BlockRedNetCable;
import powercrystals.minefactoryreloaded.item.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.net.ClientPacketHandler;
import powercrystals.minefactoryreloaded.net.CommonProxy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

@Mod(modid = MineFactoryReloadedCore.modId, name = MineFactoryReloadedCore.modName, version = MineFactoryReloadedCore.version, dependencies = "after:BuildCraft|Core;after:BuildCraft|Factory;after:BuildCraft|Energy;after:BuildCraft|Builders;after:BuildCraft|Transport;required-after:TechWorld")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels = { MineFactoryReloadedCore.modNetworkChannel }, packetHandler = ClientPacketHandler.class))
public class MineFactoryReloadedCore {
	
	@SidedProxy(clientSide = "powercrystals.minefactoryreloaded.net.ClientProxy", serverSide = "powercrystals.minefactoryreloaded.net.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String modId = "MineFactoryReloaded";
	public static final String modNetworkChannel = "MFReloaded";
	public static final String version = "1.6.2R2.7.4B1";
	public static final String modName = "Minefactory Reloaded";

	public static int renderIdRedstoneCable = 1002;
	public static BlockRedNetCable rednetCableBlock;
	public static Item rednetMeterItem;

	private static MineFactoryReloadedCore instance;

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		instance = this;
		rednetCableBlock = new BlockRedNetCable(2177);
		rednetMeterItem = (new ItemRedNetMeter(13265)).setUnlocalizedName("mfr.rednet.meter").setMaxStackSize(1);
		
		GameRegistry.registerBlock(rednetCableBlock, rednetCableBlock.getUnlocalizedName());
		LanguageRegistry.addName(rednetCableBlock, "RedNet Cable");
		MinecraftForge.setBlockHarvestLevel(MineFactoryReloadedCore.rednetCableBlock, 0, "pickaxe", 0);
		GameRegistry.registerTileEntity(TileEntityRedNetCable.class, "factoryRedstoneCable");

		MinecraftForge.EVENT_BUS.register(instance);
		MinecraftForge.EVENT_BUS.register(proxy);

		OreDictionary.registerOre("cableRedNet", MineFactoryReloadedCore.rednetCableBlock);
		proxy.init();
	}
}
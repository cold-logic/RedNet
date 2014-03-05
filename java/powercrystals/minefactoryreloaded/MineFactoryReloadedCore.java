package powercrystals.minefactoryreloaded;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import powercrystals.minefactoryreloaded.block.BlockRedNetCable;
import powercrystals.minefactoryreloaded.item.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.net.CommonProxy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

//@Mod(modid = MineFactoryReloadedCore.modId, name = MineFactoryReloadedCore.modName, version = MineFactoryReloadedCore.version, dependencies = "after:BuildCraft|Core;after:BuildCraft|Factory;after:BuildCraft|Energy;after:BuildCraft|Builders;after:BuildCraft|Transport;required-after:TechWorld")
//@Mod(modid = MineFactoryReloadedCore.modId, name = MineFactoryReloadedCore.modName, version = MineFactoryReloadedCore.version, dependencies = "after:BuildCraft|Core;after:BuildCraft|Factory;after:BuildCraft|Energy;after:BuildCraft|Builders;after:BuildCraft|Transport")
@Mod(modid = MineFactoryReloadedCore.modId, name = MineFactoryReloadedCore.modName, version = MineFactoryReloadedCore.version, acceptedMinecraftVersions = "[1.7.2,1.8)", dependencies = "required-after:Forge@[10.12.0.1024,)")
public class MineFactoryReloadedCore {
	
//	CreativeTab
//	public static CreativeTabs tabRedstone = new CreativeTabs("RedNet") {
//		public Item getTabIconItem() {
//			return new Item();
//		}
//	};
	
	@SidedProxy(clientSide = "powercrystals.minefactoryreloaded.net.ClientProxy", serverSide = "powercrystals.minefactoryreloaded.net.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String modId = "MineFactoryReloaded";
	public static final String modNetworkChannel = "MFReloaded";
	public static final String version = "1.6.2R2.7.4B1";
	public static final String modName = "Minefactory Reloaded";

	public static final String tileEntityFolder = modId + ":" + "textures/tile/";
	
	public static int renderIdRedstoneCable = 1002;
	public static BlockRedNetCable rednetCableBlock = new BlockRedNetCable(2177);
	public static Item rednetMeterItem = new ItemRedNetMeter(13265);

	private static MineFactoryReloadedCore instance;

	@EventHandler
	public void init(FMLInitializationEvent evt)
	{		
		GameRegistry.registerBlock(rednetCableBlock, rednetCableBlock.getUnlocalizedName());
		LanguageRegistry.addName(rednetCableBlock, "RedNet Cable");
		
		GameRegistry.registerItem(rednetMeterItem, rednetMeterItem.getUnlocalizedName());
		LanguageRegistry.addName(rednetMeterItem, "RedNet Meter");
		
		GameRegistry.registerTileEntity(TileEntityRedNetCable.class, "factoryRedstoneCable");
		
		OreDictionary.registerOre("cableRedNet", rednetCableBlock);

//		MinecraftForge.EVENT_BUS.register(this);
//		MinecraftForge.EVENT_BUS.register(proxy);
		
		proxy.registerRenderInformation();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
//		CreativeTabs
//		LanguageRegistry.instance().addStringLocalization("itemGroup.tabRedstone", "en_US", "RedNet");
	}
}
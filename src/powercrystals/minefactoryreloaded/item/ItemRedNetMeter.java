package powercrystals.minefactoryreloaded.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

public class ItemRedNetMeter extends Item {
	
	private final String[] nazvyBarev = new String[] { "White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };

    public ItemRedNetMeter(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
		if (world.isRemote) {
			return true;
		}
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityRedNetCable) {
            int value;
            int foundNonZero = 0;
            for (int i = 0; i < 16; i++) {
                value = ((TileEntityRedNetCable) te).getNetwork().getPowerLevelOutput(i);
				if (value != 0) {
					player.sendChatToPlayer(new ChatMessageComponent().addText(nazvyBarev[i]).addText(": " + value));
					++foundNonZero;
				}
			}
			if (foundNonZero == 0) {
				player.sendChatToPlayer(new ChatMessageComponent().addKey("chat.info.mfr.rednet.meter.cable.allzero"));
			} else if (foundNonZero < 16) {
				player.sendChatToPlayer(new ChatMessageComponent().addKey("chat.info.mfr.rednet.meter.cable.restzero"));
			}
			return true;
		}
		return false;
	}
}
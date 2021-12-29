package org.samo_lego.tradernpcs.profession;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.samo_lego.taterzens.api.professions.TaterzenProfession;
import org.samo_lego.taterzens.npc.TaterzenNPC;
import org.samo_lego.tradernpcs.gui.TradeGUI;
import org.samo_lego.tradernpcs.gui.TradeMenuGUI;
import org.samo_lego.tradernpcs.mixin.MerchantOfferAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import static org.samo_lego.tradernpcs.Traders.MOD_ID;

public class SurvivalTraderProfession extends TraderNPCProfession {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "survival_trader");
    private UUID ownerUUID = null;
    private final ArrayList<ItemStack> inventory = new ArrayList<>(54);  // 54 slots in "double chest" inventory (9 * 6)
    private boolean itemsAdded = false;

    @Override
    public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
        if (player.getUUID().equals(this.ownerUUID)) {
            // It's the owner, so editing is allowed
            new TradeMenuGUI(this, (ServerPlayer) player).open();
            return InteractionResult.SUCCESS;
        }
        if (!player.isShiftKeyDown()) {
            new TradeGUI(this, (ServerPlayer) player).open();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        this.ownerUUID = tag.getUUID("OwnerUUID");

        if (this.ownerUUID == null) {
            this.setOwner();
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putUUID("OwnerUUID", this.ownerUUID);

        // Serialize inventory
        CompoundTag invTag = new CompoundTag();
        invTag.putInt("Size", this.inventory.size());
    }

    @Override
    public void addTrade(ItemStack tradeStack1, ItemStack tradeStack2, ItemStack sellStack) {
        if (!tradeStack1.isEmpty() || !tradeStack2.isEmpty() || !sellStack.isEmpty()) {
            this.trades.add(new MerchantOffer(tradeStack1, tradeStack2, sellStack, this.getStockStack(sellStack).getCount(), 0, 0));
        }
    }

    @Override
    public MerchantOffers getTrades() {
        if (this.itemsAdded) {
            for (MerchantOffer offer : this.trades) {
                // Check stock
                ItemStack result = offer.getResult();
                if (!result.isEmpty()) {
                    int maxTrades = this.getStockStack(offer).getCount() / result.getCount();
                    ((MerchantOfferAccessor) offer).setMaxUses(maxTrades);
                }
            }
        }
        return this.trades;
    }

    private void setOwner() {
        final AABB box = this.npc.getBoundingBox().inflate(4.0D);
        final Iterator<ServerPlayer> playersIt = this.npc.getLevel().getEntitiesOfClass(ServerPlayer.class, box).iterator();

        // Assign player to profession
        if (playersIt.hasNext()) {
            ServerPlayer owner = playersIt.next();
            this.ownerUUID = owner.getUUID();
            owner.sendMessage(new TextComponent(owner.getGameProfile().getName() + ", I'm your new survival trader!"), this.npc.getUUID());
        }
    }

    @Override
    public TaterzenProfession create(TaterzenNPC taterzen) {
        SurvivalTraderProfession prof = new SurvivalTraderProfession();
        prof.npc = taterzen;

        prof.setOwner();

        return prof;
    }

    @Override
    public boolean onTrade(MerchantOffer offer) {
        ItemStack stockStack = this.getStockStack(offer);

        boolean hasStock = !stockStack.isEmpty();
        if (hasStock) {
            stockStack.shrink(offer.getResult().getCount());
        }

        return hasStock;
    }

    private ItemStack getStockStack(ItemStack result) {
        for (ItemStack stockStack : this.inventory) {
            ItemStack copy = stockStack.copy();
            copy.setCount(result.getCount());

            if (ItemStack.matches(copy, result)) {
                // Item is the same, get stock
                return stockStack;
            }
        }
        return ItemStack.EMPTY;
    }
    
    public ItemStack getStockStack(MerchantOffer offer) {
        ItemStack result = offer.getResult();
        return this.getStockStack(result);
    }

    public ArrayList<ItemStack> getInventoryItems() {
        return this.inventory;
    }

    public void setDirty() {
        this.itemsAdded = true;
    }
}

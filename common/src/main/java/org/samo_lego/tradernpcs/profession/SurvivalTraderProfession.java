package org.samo_lego.tradernpcs.profession;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
import org.samo_lego.taterzens.npc.TaterzenNPC;
import org.samo_lego.tradernpcs.gui.TradeGUI;
import org.samo_lego.tradernpcs.gui.TradeMenuGUI;
import org.samo_lego.tradernpcs.item.SearchableInventory;
import org.samo_lego.tradernpcs.mixin.MerchantOfferAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import static org.samo_lego.tradernpcs.Traders.MOD_ID;

public class SurvivalTraderProfession extends TraderNPCProfession {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "survival_trader");
    private UUID ownerUUID = null;
    private final SearchableInventory inventory = new SearchableInventory(54);  // 54 slots in "double chest" inventory (9 * 6)
    private boolean itemsAdded = false;

    public SurvivalTraderProfession(TaterzenNPC npc) {
        super(npc);
        this.setOwner();
    }

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

        ListTag invTag = (ListTag) tag.get("Inventory");
        if (invTag != null) {
            LinkedList<ItemStack> itemStacks = new LinkedList<>();
            for (Tag itemTag : invTag) {
                itemStacks.add(ItemStack.of((CompoundTag) itemTag));
            }
            this.inventory.setFromArray(itemStacks);
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putUUID("OwnerUUID", this.ownerUUID);

        // Serialize inventory
        ListTag invTag = new ListTag();
        ArrayList<ItemStack> itemStacks = this.inventory.toArray();
        for (ItemStack itemStack : itemStacks) {
            invTag.add(itemStack.save(new CompoundTag()));
        }
        tag.put("Inventory", invTag);
    }

    @Override
    public void addTrade(ItemStack tradeStack1, ItemStack tradeStack2, ItemStack sellStack) {
        if (!tradeStack1.isEmpty() || !tradeStack2.isEmpty() || !sellStack.isEmpty()) {
            int count = sellStack.getCount();
            int maxUses = count == 0 ? 0 : this.inventory.getCommonStackSize(sellStack) / count;
            this.trades.add(
                    new MerchantOffer(
                            tradeStack1.copy(),
                            tradeStack2.copy(),
                            sellStack.copy(),
                            maxUses,
                            0,
                            0
                    )
            );
        }
    }

    @Override
    public MerchantOffers getTrades() {
        if (this.itemsAdded) {  //todo still a bit funky
            for (MerchantOffer offer : this.trades) {
                // Update stock data
                ItemStack result = offer.getResult();
                int count = result.getCount();
                int maxUses = count == 0 ? 0 : this.inventory.getCommonStackSize(result) / count;
                ((MerchantOfferAccessor) offer).setMaxUses(maxUses);
                offer.resetUses();
            }
            this.itemsAdded = false;
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

            // Todo - better implementation once Taterzens gets better lang support
            owner.sendSystemMessage(Component.literal(owner.getGameProfile().getName() + ", I'm your survival trader!"));
        }
    }

    @Override
    public void onTrade(MerchantOffer offer) {
        ItemStack outputStack = offer.getResult();
        ItemStack stockStack = this.inventory.getSmallestSimilarStack(outputStack);

        // Update stock
        this.inventory.decreaseStack(outputStack, stockStack);

        int leftover = this.inventory.getCommonStackSize(outputStack);
        int maxTrades = leftover / outputStack.getCount();

        // Ugly workaround, fixme
        // For some reason, the offer is not allowed to be traded when the stock is set to 1 in this case?
        if (maxTrades == 1) {
            ((MerchantOfferAccessor) offer).setMaxUses(2);
        } else {
            ((MerchantOfferAccessor) offer).setMaxUses(maxTrades);
        }
        offer.resetUses();

        ItemStack paymentA = offer.getBaseCostA();
        ItemStack paymentB = offer.getCostB();

        // Add payment to inventory
        this.inventory.addStack(paymentA);
        this.inventory.addStack(paymentB);
    }

    @Override
    public boolean mayTrade(Player player, MerchantOffer offer) {
        ItemStack outputStack = offer.getResult();
        int stockCount = this.inventory.getCommonStackSize(outputStack);

        return outputStack.getCount() <= stockCount;
    }

    public SearchableInventory getInventoryItems() {
        return this.inventory;
    }

    @Override
    public void setDirty() {
        this.itemsAdded = true;
    }
}

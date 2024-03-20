package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Selena;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Trader;
import astramusfate.wizardry_tales.api.classes.IRandomTrading;
import astramusfate.wizardry_tales.api.classes.IRendaCreature;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.DiscoverSpellEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.misc.WildcardTradeList;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static electroblob.wizardry.entity.living.EntityWizard.getBookStackForSpell;

public class EntityMidnightTrader extends EntityCreature implements INpc, IMerchant, IRendaCreature, IRandomTrading {
    private int lifetime = -1;
    private UUID casterUUID;
    /** Trades. */
    private MerchantRecipeList trades;
    /** Current customer. */
    @Nullable
    private EntityPlayer customer;

    private int timeUntilReset;

    /** addDefaultEquipmentAndRecipies is called if this is true */
    private boolean updateRecipes;

    public EntityMidnightTrader(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void applyEntityAttributes(){
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5);
    }


    @Override
    protected void updateAITasks(){
        if(!this.isTrading() && this.timeUntilReset > 0){

            --this.timeUntilReset;

            if(this.timeUntilReset <= 0){

                if(this.updateRecipes){

                    for(MerchantRecipe merchantrecipe : this.trades){

                        if(merchantrecipe.isRecipeDisabled()){
                            // Increases the number of allowed uses of a disabled recipe by a random number.
                            merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                        }
                    }

                    if(this.trades.size() < 12){
                        this.addRandomRecipes(1);
                    }

                    this.updateRecipes = false;
                }

                this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
            }
        }

        super.updateAITasks(); // This actually does nothing
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if(Solver.doEvery(this,4) && this.getHealth() < this.getMaxHealth() * 0.5f){
            Alchemy.applyPotion(this, Solver.duration(6), MobEffects.INVISIBILITY);
        }
    }

    @Override
    protected void damageEntity(@Nonnull DamageSource damageSrc, float damageAmount) {
        this.despawnEntity();
        super.damageEntity(damageSrc, damageAmount);
    }

    private Element getElement(){return Element.NECROMANCY;}

    private ItemStack getRandomItemOfTier(Tier tier){

        int randomiser;

        // All enabled spells of the given tier
        List<Spell> spells = Spell.getSpells(new Spell.TierElementFilter(tier, null, SpellProperties.Context.TRADES));
        // All enabled spells of the given tier that match this wizard's element
        List<Spell> specialismSpells = Spell.getSpells(new Spell.TierElementFilter(tier, this.getElement(), SpellProperties.Context.TRADES));

        // Wizards don't sell scrolls
        spells.removeIf(s -> !s.isEnabled(SpellProperties.Context.BOOK));
        specialismSpells.removeIf(s -> !s.isEnabled(SpellProperties.Context.BOOK));

        // This code is sooooooo much neater with the new filter system!
        switch(tier){

            case NOVICE:
                randomiser = rand.nextInt(5);
                if(randomiser < 4 && !spells.isEmpty()){
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()){
                        // This means it is more likely for spell books sold to be of the same element as the wizard if the
                        // wizard has an element.
                        return getBookStackForSpell(specialismSpells.get(rand.nextInt(specialismSpells.size())));
                    }else{
                        return getBookStackForSpell(spells.get(rand.nextInt(spells.size())));
                    }
                }else{
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0){
                        // This means it is more likely for wands sold to be of the same element as the wizard if the wizard
                        // has an element.
                        return new ItemStack(ItemWand.getWand(tier, this.getElement()));
                    }else{
                        return new ItemStack(
                                ItemWand.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
                    }
                }

            case APPRENTICE:
                randomiser = rand.nextInt(Wizardry.settings.discoveryMode ? 12 : 10);
                if(randomiser < 5 && !spells.isEmpty()){
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()){
                        // This means it is more likely for spell books sold to be of the same element as the wizard if the
                        // wizard has an element.
                        return getBookStackForSpell(specialismSpells.get(rand.nextInt(specialismSpells.size())));
                    }else{
                        return getBookStackForSpell(spells.get(rand.nextInt(spells.size())));
                    }
                }else if(randomiser < 6){
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0){
                        // This means it is more likely for wands sold to be of the same element as the wizard if the wizard
                        // has an element.
                        return new ItemStack(ItemWand.getWand(tier, this.getElement()));
                    }else{
                        return new ItemStack(
                                ItemWand.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
                    }
                }else if(randomiser < 8){
                    return new ItemStack(WizardryItems.arcane_tome, 1, 1);
                }else if(randomiser < 10){
                    EntityEquipmentSlot slot = InventoryUtils.ARMOUR_SLOTS[rand.nextInt(InventoryUtils.ARMOUR_SLOTS.length)];
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0){
                        // This means it is more likely for armour sold to be of the same element as the wizard if the
                        // wizard has an element.
                        return new ItemStack(WizardryItems.getArmour(this.getElement(), slot));
                    }else{
                        return new ItemStack(
                                WizardryItems.getArmour(Element.values()[rand.nextInt(Element.values().length)], slot));
                    }
                }else{
                    // Don't need to check for discovery mode here since it is done above
                    return new ItemStack(WizardryItems.identification_scroll);
                }

            case ADVANCED:
                randomiser = rand.nextInt(12);
                if(randomiser < 5 && !spells.isEmpty()){
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()){
                        // This means it is more likely for spell books sold to be of the same element as the wizard if the
                        // wizard has an element.
                        return getBookStackForSpell(specialismSpells.get(rand.nextInt(specialismSpells.size())));
                    }else{
                        return getBookStackForSpell(spells.get(rand.nextInt(spells.size())));
                    }
                }else if(randomiser < 6){
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0){
                        // This means it is more likely for wands sold to be of the same element as the wizard if the wizard
                        // has an element.
                        return new ItemStack(ItemWand.getWand(tier, this.getElement()));
                    }else{
                        return new ItemStack(
                                WizardryItems.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
                    }
                }else if(randomiser < 8){
                    return new ItemStack(WizardryItems.arcane_tome, 1, 2);
                }else{
                    List<Item> upgrades = new ArrayList<Item>(WandHelper.getSpecialUpgrades());
                    randomiser = rand.nextInt(upgrades.size());
                    return new ItemStack(upgrades.get(randomiser));
                }

            case MASTER:
                // If a regular wizard rolls a master trade, it can only be a simple master wand or a tome of arcana
                randomiser = this.getElement() != Element.MAGIC ? rand.nextInt(8) : 5 + rand.nextInt(3);

                if(randomiser < 5 && this.getElement() != Element.MAGIC && !specialismSpells.isEmpty()){
                    // Master spells can only be sold by a specialist in that element.
                    return getBookStackForSpell(specialismSpells.get(rand.nextInt(specialismSpells.size())));

                }else if(randomiser < 6){
                    if(this.getElement() != Element.MAGIC && rand.nextInt(4) > 0){
                        // Master elemental wands can only be sold by a specialist in that element.
                        return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
                    }else{
                        return new ItemStack(WizardryItems.master_wand);
                    }
                }else{
                    return new ItemStack(WizardryItems.arcane_tome, 1, 3);
                }
        }

        return new ItemStack(Blocks.STONE);
    }


    /**
     * This is called once on initialisation and then once each time the wizard gains new trades (the particle thingy).
     */
    private void addRandomRecipes(int numberOfItemsToAdd){

        MerchantRecipeList merchantrecipelist;
        merchantrecipelist = new MerchantRecipeList();

        for(int i = 0; i < numberOfItemsToAdd; i++){

            ItemStack itemToSell = ItemStack.EMPTY;

            boolean itemAlreadySold = true;

            Tier tier = Tier.NOVICE;

            while(itemAlreadySold){

                itemAlreadySold = false;

                /* New way of getting random item, by giving a chance to increase the tier which depends on how much the
                 * player has already traded with the wizard. The more the player has traded with the wizard, the more
                 * likely they are to get items of a higher tier. The -4 is to ignore the original 4 trades. For
                 * reference, the chances are as follows: Trades done Basic Apprentice Advanced Master 0 50% 25% 18% 8%
                 * 1 46% 25% 20% 9% 2 42% 24% 22% 12% 3 38% 24% 24% 14% 4 34% 22% 26% 17% 5 30% 21% 28% 21% 6 26% 19%
                 * 30% 24% 7 22% 17% 32% 28% 8 18% 15% 34% 33% */

                int rand = Solver.randInt(1,10);

                // Uses its own special weighting
                if(rand < 10)
                    tier = Tier.ADVANCED;
                else
                    tier = Tier.MASTER;

                itemToSell = this.getRandomItemOfTier(tier);

                for(MerchantRecipe recipe : merchantrecipelist){
                    if(ItemStack.areItemStacksEqual(recipe.getItemToSell(), itemToSell))
                        itemAlreadySold = true;
                }

                if(this.trades != null){
                    for(MerchantRecipe recipe : this.trades){
                        if(ItemStack.areItemStacksEqual(recipe.getItemToSell(), itemToSell))
                            itemAlreadySold = true;
                    }
                }
            }

            // Don't know how it can ever be empty here, but it's a failsafe.
            if(itemToSell.isEmpty()) return;

            ItemStack secondItemToBuy = new ItemStack(WizardryItems.grand_crystal,1);
            if(tier == Tier.MASTER) secondItemToBuy = new ItemStack(WizardryItems.grand_crystal,4);

            merchantrecipelist.add(new MerchantRecipe(this.getRandomPrice(tier), secondItemToBuy, itemToSell));
        }

        Collections.shuffle(merchantrecipelist);

        if(this.trades == null){
            this.trades = new WildcardTradeList();
        }

        this.trades.addAll(merchantrecipelist);
    }


    @SuppressWarnings("unchecked")
    private ItemStack getRandomPrice(Tier tier){

        Map<Pair<ResourceLocation, Short>, Integer> map = Wizardry.settings.currencyItems;
        // This isn't that efficient but it's not called very often really so it doesn't matter
        Pair<ResourceLocation, Short> itemName = map.keySet().toArray(new Pair[0])[rand.nextInt(map.size())];
        Item item = Item.REGISTRY.getObject(itemName.getLeft());
        short meta = itemName.getRight();
        int value;

        if(item == null){
            Wizardry.logger.warn("Invalid item in currency items: {}", itemName);
            item = Items.EMERALD; // Fallback item
            value = 6;
        }else{
            value = map.get(itemName);
        }

        // ((tier.ordinal() + 1) * 16 + rand.nextInt(6)) gives a 'value' for the item being bought
        // This is then divided by the value of the currency item to give a price
        // The absolute maximum stack size that can result from this calculation (with value = 1) is 64.
        return new ItemStack(item, MathHelper.clamp((8 + tier.ordinal() * 16 + rand.nextInt(9)) / value, 1, 64), meta);
    }

    @Override
    public void setCustomer(@Nullable EntityPlayer player){
        this.customer = player;
    }

    @Nullable
    @Override
    public EntityPlayer getCustomer(){
        return this.customer;
    }

    public boolean isTrading(){
        return this.getCustomer() != null;
    }

    // Setter + getter implementations

    @Override public int getLifetime(){ return lifetime; }
    @Override public void setLifetime(int lifetime){ this.lifetime = lifetime; }
    @Override public UUID getOwnerId(){ return casterUUID; }

    @Nullable
    @Override
    public Entity getOwner() {
        return this.getCaster();
    }

    @Override public void setOwnerId(UUID uuid){ this.casterUUID = uuid; }

    @Override
    public void onSpawn() {
        this.spawnParticleEffect();
    }

    @Override
    public void onDespawn() {
        this.spawnParticleEffect();
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        super.onDeath(cause);
        this.spawnParticleEffect();
        if(cause.getTrueSource() instanceof EntityPlayer) {
            if (!this.world.isRemote) {
                EntityMidnightGuard guard = new EntityMidnightGuard(world);
                guard.setLifetime(Solver.asTicks(60));
                guard.setOwnerId(this.getUniqueID());
                // Let's find the player
                List<EntityPlayer> list = Selena.getAround(world, 10, this.getPosition(), EntityPlayer.class);
                if (!list.isEmpty()) {
                    EntityPlayer player = (EntityPlayer) Selena.findNearestLiving(this.getPositionVector(), list);
                    guard.setAttackTarget(player);
                } else {
                    guard.setAttackTarget(this.getAttackTarget());
                }
                guard.setPosition(this.posX, this.posY, this.posZ);
                this.world.spawnEntity(guard);

                EntityMidnightGuard guard2 = new EntityMidnightGuard(world);
                guard2.setLifetime(Solver.asTicks(60));
                guard2.setOwnerId(this.getUniqueID());
                // Let's find the player
                if (!list.isEmpty()) {
                    EntityPlayer player = (EntityPlayer) Selena.findNearestLiving(this.getPositionVector(), list);
                    guard2.setAttackTarget(player);
                } else {
                    guard2.setAttackTarget(this.getAttackTarget());
                }
                guard2.setPosition(this.posX, this.posY, this.posZ);
                this.world.spawnEntity(guard2);
            }
        }
    }

    @Override
    public boolean hasParticleEffect() {
        return true;
    }

    private void spawnParticleEffect(){
        if(this.world.isRemote){
            for(int i = 0; i < 15; i++){
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + this.rand.nextFloat() - 0.5f,
                        this.posY + this.rand.nextFloat() * 2, this.posZ + this.rand.nextFloat() - 0.5f, 0, 0, 0);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setRecipes(MerchantRecipeList recipeList){
        // Apparently nothing goes here, and nothing's here in EntityVillager either...
    }

    @Override
    public void useRecipe(MerchantRecipe merchantrecipe){

        merchantrecipe.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();

        if(this.getCustomer() != null){

            // Achievements
            WizardryAdvancementTriggers.wizard_trade.triggerFor(this.getCustomer());

            if(merchantrecipe.getItemToSell().getItem() instanceof ItemSpellBook){

                Spell spell = Spell.byMetadata(merchantrecipe.getItemToSell().getItemDamage());

                if(spell.getTier() == Tier.MASTER) WizardryAdvancementTriggers.buy_master_spell.triggerFor(this.getCustomer());

                // Spell discovery (a lot of this is the same as in the event handler)
                WizardData data = WizardData.get(this.getCustomer());

                if(data != null){

                    if(!MinecraftForge.EVENT_BUS.post(new DiscoverSpellEvent(this.getCustomer(), spell,
                            DiscoverSpellEvent.Source.PURCHASE)) && data.discoverSpell(spell)){

                        data.sync();

                        if(!world.isRemote && !this.getCustomer().isCreative() && Wizardry.settings.discoveryMode){
                            // Sound and text only happen server-side, in survival, with discovery mode on
                            EntityUtils.playSoundAtPlayer(this.getCustomer(), WizardrySounds.MISC_DISCOVER_SPELL, 1.25f, 1);
                            this.getCustomer().sendMessage(new TextComponentTranslation("spell.discover",
                                    spell.getNameForTranslationFormatted()));
                        }
                    }
                }
            }
        }

        // Changed to a 4 in 5 chance of unlocking a new recipe.
        if(this.rand.nextInt(5) > 0 || ItemArtefact.isArtefactActive(customer, WizardryItems.charm_haggler)){
            this.timeUntilReset = 40;
            this.updateRecipes = true;

            if(this.getCustomer() != null){
                this.getCustomer().getName();
            }
        }
    }

    // This is called from the gui in order to display the recipes (no surprise there), and this is actually where
    // the initialisation is done, i.e. the trades don't actually exist until some player goes to trade with the
    // villager, at which point the first is added.
    @Override
    public MerchantRecipeList getRecipes(@Nonnull EntityPlayer par1EntityPlayer){

        if(this.trades == null){

            this.trades = new WildcardTradeList();

            // All wizards will buy spell books
            ItemStack anySpellBook = new ItemStack(WizardryItems.spell_book, 1, OreDictionary.WILDCARD_VALUE);
            ItemStack crystalStack = new ItemStack(WizardryItems.magic_crystal, 5);

            this.trades.add(new MerchantRecipe(anySpellBook, crystalStack));

            ItemStack grandCrystal = new ItemStack(WizardryItems.grand_crystal, 1);

            this.addRandomTrading(8);
            this.addRandomRecipes(2);
        }

        return this.trades;
    }

    // Sells item to buy item(by player perspective)
    public void newRecipe(ItemStack sell, ItemStack buy){
        this.trades.add(new MerchantRecipe(buy, sell));
    }

    public void addRandomTrading(int amount){
        MerchantRecipeList list;
        list = new MerchantRecipeList();

        for(int i = 0; i < amount; i++) {

            boolean itemAlreadySold = true;

            while (itemAlreadySold) {

                itemAlreadySold = false;

                MerchantRecipe recipe = getRandomTrading();
                list.add(recipe);

                if(ItemStack.areItemStacksEqual(recipe.getItemToSell(), recipe.getItemToBuy()))
                    itemAlreadySold = true;

                if(this.trades != null){
                    for(MerchantRecipe recipes : this.trades){
                        if(ItemStack.areItemStacksEqual(recipes.getItemToSell(), recipe.getItemToBuy()))
                            itemAlreadySold = true;
                    }
                }

                //if(itemAlreadySold) list.remove(recipe);
            }
        }

        Collections.shuffle(list);

        if(this.trades == null){
            this.trades = new WildcardTradeList();
        }

        this.trades.addAll(list);
    }

    public MerchantRecipe getRandomTrading(){
        MerchantRecipeList list;
        list = new MerchantRecipeList();
        ItemStack stack;
        stack = Trader.getItemToSell(v -> v instanceof ItemArtefact && v.getForgeRarity(stack(v)) != EnumRarity.EPIC);

        int rarity = stack.getItem().getForgeRarity(stack) == EnumRarity.EPIC ? 4
                : stack.getItem().getForgeRarity(stack) == EnumRarity.RARE ? 3
                : stack.getItem().getForgeRarity(stack) == EnumRarity.UNCOMMON ? 2 : 1;

        list.add(getRecipe(stack, new ItemStack(WizardryItems.grand_crystal, rarity)));

        /*
        if (Solver.chance(35)) list.add(getRecipe(stack(WizardryItems.charm_light), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.charm_feeding), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_full_moon), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_mind_control), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.amulet_glide), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_mana_return), new ItemStack(WizardryItems.grand_crystal, 3)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_seeking), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_earth_biome), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_earth_melee), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_ice_melee), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.amulet_wisdom), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.charm_silk_touch), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.charm_spell_discovery), new ItemStack(WizardryItems.grand_crystal, 3)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.charm_undead_helmets), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.charm_flight), new ItemStack(WizardryItems.grand_crystal, 3)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.charm_lava_walking), new ItemStack(WizardryItems.grand_crystal, 3)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(WizardryItems.ring_combustion), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(35))
            list.add(getRecipe(stack(TalesItems.ring_protector), new ItemStack(WizardryItems.grand_crystal, 1)));

        if (Solver.chance(10))
            list.add(getRecipe(stack(Chants.chant_demonic_seal), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(10))
            list.add(getRecipe(stack(Chants.chant_ring_leeching), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(10))
            list.add(getRecipe(stack(Chants.chant_nature_beings), new ItemStack(WizardryItems.grand_crystal, 2)));
        if (Solver.chance(10))
            list.add(getRecipe(stack(Chants.chant_house_protection), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(10))
            list.add(getRecipe(stack(Chants.chant_ore_sight_iron), new ItemStack(WizardryItems.grand_crystal, 1)));
        if (Solver.chance(10))
            list.add(getRecipe(stack(Chants.chant_ore_sight_coal), new ItemStack(WizardryItems.grand_crystal, 1)));
        */

        Collections.shuffle(list);
        return list.get(0);
    }

    // Sells item to buy item(by player perspective)
    public MerchantRecipe getRecipe(ItemStack to_get, ItemStack to_give){
        return new MerchantRecipe(to_give, to_get);
    }


    @Nonnull
    @Override
    public World getWorld(){
        return this.world;
    }

    @Nonnull
    @Override
    public BlockPos getPos(){
        return new BlockPos(this);
    }

    @Override
    public void verifySellingItem(@Nonnull ItemStack stack){
        // Ignore this
    }

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand){

        ItemStack stack = player.getHeldItem(hand);

        // Won't trade with a player that has attacked them.
        if(this.isEntityAlive() && !this.isTrading() && !this.isChild()){
            if(!this.world.isRemote){
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);
                // player.displayGUIMerchant(this, this.getElement().getWizardName());
            }

            return true;
        }else{
            return false;
        }
    }

}

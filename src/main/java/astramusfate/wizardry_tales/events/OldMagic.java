package astramusfate.wizardry_tales.events;

public class OldMagic {
    // New iteration
    /*
    public static void createSpell(List<String> words, Entity focal, Entity target, boolean isServer, @Nullable String original) {
        World world = focal.world;
        if (world == null) return;
        String[] spell = words.toArray(new String[0]);

        EntityLivingBase caster = getCaster(focal);
        Entity tempTarget = target;
        SpellParams mods = new SpellParams();
        BlockPos pos = focal.getPosition();
        boolean wasRay = false;

        List<String> set = Lists.newArrayList();
        set.addAll(Arrays.asList(spell));

        String previous = "";
        for (int i = 0; i < spell.length; i++) {
            String next = i + 1 < spell.length ? spell[i + 1] : "";
            String next2 = i + 2 < spell.length ? spell[i + 2] : "";
            String next3 = i + 3 < spell.length ? spell[i + 3] : "";
            String next4 = i + 4 < spell.length ? spell[i + 4] : "";
            String word = spell[i];

            Number number = getParamNumber(next, next2, next3, Integer.MAX_VALUE);

            if (number.intValue() != Integer.MAX_VALUE){
                mods.calcParam(word, number);
            }else{
                mods.calcParam(word, next);
            }

            if (findIn(next, "ally allies allied")){
                if (findIn(word, "allow")) mods.canAlly=true;
                if (findIn(word, "deny")) mods.canAlly=false;
            }

            if (findIn(next, "owner caster")){
                if (findIn(word, "remove")) mods.hasOwner =true;
                if (findIn(word, "add")) mods.hasOwner =false;
            }

            if (findIn(word, "filter")){
                if (findIn(next, "mobs")) mods.filter = e -> e instanceof IMob;
                else if (findIn(next, "construct constructs magic")) mods.filter = e -> e instanceof EntityMagicConstruct || e instanceof EntityMagic;
                else if (findIn(next, "human humans")) mods.filter = e -> e instanceof EntityPlayer || e instanceof EntityWizard || e instanceof EntityEvilWizard;
                else if (findIn(next, "undead undeads")) mods.filter = e -> e instanceof EntityLivingBase && ((EntityLivingBase) e).isEntityUndead();
                else if (findIn(next, "creature creatures")) mods.filter = e -> e instanceof EntityCreature;
                else if (findIn(next, "living")) mods.filter = e -> e instanceof EntityLivingBase;
            }

            // Overrides what happens above to Shape, and also sets it to found good value instead!
            if (findIn(word, par_shape)){
                String shape = findShape(next, next2, next3);
                mods.shape.setValue(shape);
                set.remove(par_shape);
            }

            String shape = mods.shape.val();
            // Shapes do their work here!
            if (findIn(shape, shape_ray) && mods.spellBlock == null) {
                try {
                    set.remove(shape_ray);
                } catch (Exception ignore) {
                }
                // Trying to raycast entity first.
                boolean finalCanAlly = mods.canAlly;
                mods.ray = Solver.standardEntityRayTrace(world, focal,
                        mods.range.num(), false,
                        e -> (!(e instanceof EntityLivingBase) || e == caster || e == focal ||
                                (AllyDesignationSystem.isAllied(caster, (EntityLivingBase) e) && !finalCanAlly))
                                || (((EntityLivingBase) e).deathTime > 0));
                if (mods.ray != null && mods.ray.typeOfHit == RayTraceResult.Type.ENTITY) {
                    target = mods.ray.entityHit;
                    mods.spellBlock = mods.ray.entityHit.getPosition();
                }

                // if fails - raycast blocks!
                if (mods.spellBlock == null) {
                    mods.ray = Solver.standardBlockRayTrace(world, caster == null ? focal : caster, mods.range.num(),
                            false, true, false);
                    if (mods.ray != null && mods.ray.typeOfHit == RayTraceResult.Type.BLOCK
                            && world.getBlockState(mods.ray.getBlockPos()).getBlock() != Blocks.AIR) {
                        mods.spellBlock = mods.ray.getBlockPos().offset(mods.ray.sideHit);
                    }
                }
                if ((mods.ray == null || mods.ray.entityHit == null) && mods.spellBlock == null) return;
                if (mods.castingTargeting == 2 && (target == caster || target == focal)) return;
                wasRay = true;
            }

            if (mods.spellBlock == null) {
                mods.spellBlock = focal.getPosition();
            }

            if (findInSmart(shape, shape_inscribe) && caster instanceof EntityPlayer && original != null) {
                try {
                    set.remove(shape_inscribe);
                } catch (Exception ignore) {
                }

                ItemStack stack = Thief.getInHands((EntityPlayer) caster);
                if (stack == null) return;

                ItemStack reagent = Thief.getItem((EntityPlayer) caster, p -> p.getItem() == TalesItems.chanting_stone,
                        new ItemStack(TalesItems.chanting_stone));
                if (reagent == null) {
                    Aterna.messageBar((EntityPlayer) caster, "You need one Chanting Stone in inventory!");
                    return;
                }
                reagent.shrink(1);

                NBTTagCompound tag = getOrCreateTagCompound(stack);
                tag.setString("spell", original);
                if (stack.getItem() instanceof ISpellCastingItem){
                    int slot = getChosenSpellIndex(stack);
                    if (slot >= 0){
                        tag.setString("spell" + slot, original);
                        tag.setString("spell", "Mana shape inscribe");
                    }
                }


                Wizard.castParticles(world, Element.MAGIC, caster.getPositionVector(), 18);
                return;
            }

            if (findInSmart(shape, shape_adjust) && caster instanceof EntityPlayer && original != null &&
                    findIn(previous, "add")
                    && (findIn(word, "parameter") || findIn(word, "condition"))){
                if (findIn(word, "parameter")) {
                    try {
                        set.remove("add");
                        set.remove("parameter");
                    }catch (Exception ignore){}

                    ItemStack stack = Thief.getInHands((EntityPlayer) caster, (s -> !(s.getItem() instanceof IInscribed) || (s.getItem() instanceof IInscribed && ((IInscribed)s.getItem()).applyParameters())));
                    if (stack == null) return;
                    NBTTagCompound tag = getOrCreateTagCompound(stack);
                    tag.setInteger("parameter", number.intValue());


                    Wizard.castParticles(world, Element.MAGIC, caster.getPositionVector(), 18);
                    continue;
                }

                if (findIn(word, "condition")) {
                    try {
                        set.remove("add");
                        set.remove("condition");
                    }catch (Exception ignore){}
                    ItemStack stack = Thief.getInHands((EntityPlayer) caster,(s -> !(s.getItem() instanceof IInscribed) || (s.getItem() instanceof IInscribed && ((IInscribed)s.getItem()).applyConditions())));
                    if (stack == null) return;
                    String condition = findWithin(conditions, "", next, next2, next3, next4);
                    if (!condition.equals("") && SpellcastingHandler.checkCondition(stack, condition)) {
                        NBTTagCompound tag = getOrCreateTagCompound(stack);
                        tag.setString("condition", condition);
                    }


                    Wizard.castParticles(world, Element.MAGIC, caster.getPositionVector(), 18);
                    continue;
                }
                return;
            }

            if (findIn(shape, shape_area)) {
                try {
                    set.remove(shape_area);
                }catch (Exception ignore){}
                EntityCircleAreaOnceCast entity = new EntityCircleAreaOnceCast(world, set);
                entity.setLocation(Element.MAGIC.func_176610_l());
                if (mods.hasOwner) entity.setCaster(caster);
                if (mods.canAlly) entity.workOnAllies();
                entity.filter = mods.filter;
                entity.setLifetime(mods.lifetime.num() < 0.0F ? 1 : Solver.asTicks(mods.lifetime.num()));
                entity.setPosition(mods.spellBlock.getX() + 0.5, mods.spellBlock.getY(), mods.spellBlock.getZ() + 0.5);
                entity.setSizeMultiplier((mods.size.num()));
                Tenebria.create(world, entity);
                return;
            }

            if (findIn(shape, shape_sigil)) {
                try {
                    set.remove(shape_sigil);
                }catch (Exception ignore){}
                EntityCustomSigil entity = new EntityCustomSigil(world, set);
                entity.setLocation(Element.MAGIC.func_176610_l());
                if (mods.hasOwner) entity.setCaster(caster);
                entity.setLifetime(Solver.asTicks(mods.lifetime.num()));
                if (mods.canAlly) entity.workOnAllies();
                entity.setPosition(mods.spellBlock.getX() + 0.5, mods.spellBlock.getY(), mods.spellBlock.getZ() + 0.5);
                entity.setSizeMultiplier(mods.size.num());
                if(world.getEntitiesWithinAABB(entity.getClass(), entity.getEntityBoundingBox()).isEmpty())
                    Tenebria.create(world, entity);
                return;
            }


            if (findIn(word, "choose")){
                if (findIn(next, "caster")) target = caster;
                else if (findIn(next, "focal")) target = focal;
            }

            if (findIn(word, "focus")){
                if (findIn(next, "blocks block")) mods.castingTargeting = 1;
                else if (findIn(next, "entities entity")) mods.castingTargeting = 2;

                if (findIn(previous, "reset remove") || findIn(next, "reset remove")) mods.castingTargeting = 0;
            }

            //... Setup for everything to work...
            List<String> actions = Lists.newArrayList(word, next, next2, next3, next4, previous);
            mods.focal = focal;
            if(target != null) mods.target = target;
            else mods.target = tempTarget;
            mods.isServer = isServer;
            mods.original = original;
            // And here, finally we do actions!

            try {
                applyActions(actions, mods, set);
            }catch (Exception exception){
                WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Chanting! ----");
                exception.printStackTrace();
                WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Chanting! ----");

                if (caster instanceof EntityPlayer)
                    Aterna.messageBar((EntityPlayer) caster, "Spell is broken! Give developers your log to fix this!");

                if (Tales.chanting.debug)
                    throw new RuntimeException(exception);
            }

            if (findIn(previous, "add change") && findIn(word, "position pos")){
                float x;
                float y;
                float z;
                if(!findIn(next, ignore)) {
                    x = getParamNumber(next, 0.0F);
                    y = getParamNumber(next2, 0.0F);
                    z = getParamNumber(next3, 0.0F);
                }else{
                    x = getParamNumber(next2, 0.0F);
                    y = getParamNumber(next3, 0.0F);
                    z = getParamNumber(next4, 0.0F);
                }
                mods.addition.add(x, y, z);
            }

            if (Math.abs(mods.addition.getX() + mods.addition.getY() + mods.addition.getZ()) > 0){
                mods.spellBlock.add(mods.addition);
                //mods.addition = new BlockPos(0, 0, 0);
            }

            previous = word;
        }

        if (caster != null){
            caster.playSound(WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND, 0.4F, 1.2F + Solver.randFloat(-0.2F, 0.4F));

            if (isServer){
                if (wasRay) {
                    EntityMagicCircleVertical circle = Wizard.getVerticalCircle(world, "construct", caster.getPositionVector(), caster);
                    circle.setSizeMultiplier(2);
                    Tenebria.create(world, circle);
                }else{
                    EntityMagicCircle circle = Wizard.getCircle(world, "construct", caster.getPositionVector());
                    circle.setSizeMultiplier(3);
                    Tenebria.create(world, circle);
                }
            }
        }
    }
    */


    // Old iteration
    /*
    public static void affectWithMagicOld(List<String> words, Entity focal, Entity target, boolean isServer, @Nullable String original){
        World world = focal.world;
        String[] spell = words.toArray(new String[0]);

        EntityLivingBase caster = null;
        if (focal instanceof EntityLivingBase) caster = (EntityLivingBase) focal;
        if(caster == null && focal instanceof EntityMagic) caster = ((EntityMagic) focal).getCaster();

        double potency = 1.0F;
        double duration = 5;
        double range = 4;
        double lifetime = 5;
        double health = 1;
        String shape = "me";
        Element element = Sage.getElementByText("magic");
        BlockPos savedPos = null;
        Entity tempTarget = target;
        boolean canAlly = false;
        boolean attackAlly = false;
        BlockPos spellBlock = null;
        RayTraceResult blockRay = null;

        List<String> set = Lists.newArrayList();
        set.addAll(Arrays.asList(spell));

        String previous = "";
        for (int i = 0; i < spell.length; i++) {
            String next = i + 1 < spell.length ? spell[i+1] : "";
            String next2 = i + 2 < spell.length ? spell[i+2] : "";
            String next3 = i + 3 < spell.length ? spell[i+3] : "";
            String next4 = i + 4 < spell.length ? spell[i+4] : "";
            String word = spell[i];

            // Dynamic parameters:
            float number = getParamNumber(next, next2, next3, 0.0F).floatValue();

            // Numbers required
            if(number > 0.0F) {
                if (findIn(word, par_potency)) potency = number;
                if (findIn(word, par_range)) range = number;
                if (findIn(word, par_duration)) duration = number;
                if (findIn(word, par_health)) health = number;
            }

            if (number != 0.0F) {
                if (findIn(word, par_lifetime)) lifetime = number;
            }

            // NO Numbers
            if (findIn(word, par_shape)){ shape = findShape(next, next2, next3); set.remove(par_shape);}

            if (findIn(shape, shape_ray)) {
                try {
                    set.remove(shape_ray);
                }catch (Exception ignore){}
                EntityLivingBase finalCaster = caster;
                boolean finalCanAlly = canAlly;
                RayTraceResult ray = Solver.standardEntityRayTrace(world, focal, range, false,
                        e ->  (!(e instanceof EntityLivingBase) || e == finalCaster || e == focal || (AllyDesignationSystem.isAllied(finalCaster, (EntityLivingBase) e) && !finalCanAlly)) || (((EntityLivingBase)e).deathTime > 0));
                if (ray != null && ray.typeOfHit == RayTraceResult.Type.ENTITY) {
                    target = ray.entityHit;
                    spellBlock = ray.entityHit.getPosition();
                }
                if (spellBlock == null){
                    blockRay = Solver.standardBlockRayTrace(world, caster == null ? focal : caster, range,
                            false, true, false);
                    if (blockRay != null && blockRay.typeOfHit == RayTraceResult.Type.BLOCK
                            && world.getBlockState(blockRay.getBlockPos()).getBlock() != Blocks.AIR) {
                        //BlockPos pos = ray.getBlockPos().offset(ray.sideHit);
                        spellBlock = blockRay.getBlockPos().offset(blockRay.sideHit);
                    }
                }
                if (ray == null || ray.entityHit == null) return;
            }
            if (spellBlock == null) spellBlock = focal.getPosition();

            if (findIn(next, "ally allies allied")){
                if (findIn(word, "allow")) canAlly=true;
                if (findIn(word, "deny")) canAlly=false;
                if (findIn(word, "attack")) attackAlly = !findIn(previous, "not");
            }

            if (findInSmart(shape, shape_adjust) && caster instanceof EntityPlayer && original != null &&
                    findIn(previous, "add")
            && (findIn(word, "parameter") || findIn(word, "condition"))){
                if (findIn(word, "parameter")) {
                    try {
                        set.remove("add");
                        set.remove("parameter");
                    }catch (Exception ignore){}

                    ItemStack stack = Thief.getInHands((EntityPlayer) caster, (s -> !(s.getItem() instanceof IInscribed) || (s.getItem() instanceof IInscribed && ((IInscribed)s.getItem()).applyParameters())));
                    if (stack == null) return;
                    NBTTagCompound tag = getOrCreateTagCompound(stack);
                    if ((number == 0.0F && tag.hasKey("parameter"))) tag.removeTag("parameter");
                    else tag.setFloat("parameter", number);

                    Wizard.castParticles(world, element, caster.getPositionVector(), 18);
                    continue;
                }

                if (findIn(word, "condition")) {
                    try {
                        set.remove("add");
                        set.remove("condition");
                    }catch (Exception ignore){}
                    ItemStack stack = Thief.getInHands((EntityPlayer) caster,(s -> !(s.getItem() instanceof IInscribed) || (s.getItem() instanceof IInscribed && ((IInscribed)s.getItem()).applyConditions())));
                    if (stack == null) return;
                    String condition = findWithin(conditions, "", next, next2, next3, next4);
                    if (!condition.equals("") && SpellcastingHandler.checkCondition(stack, condition)) {
                        NBTTagCompound tag = getOrCreateTagCompound(stack);
                        tag.setString("condition", condition);
                    }

                    Wizard.castParticles(world, element, caster.getPositionVector(), 18);
                    continue;
                }
            }

            if (findInSmart(shape, shape_inscribe) && caster instanceof EntityPlayer && original != null){
                try {
                    set.remove(shape_inscribe);
                }catch (Exception ignore){}

                ItemStack stack = Thief.getInHands((EntityPlayer) caster);
                if (stack == null) return;

                ItemStack reagent = Thief.getItem((EntityPlayer) caster, p -> p.getItem() == TalesItems.chanting_stone);
                if (reagent == null){
                    Aterna.messageBar((EntityPlayer) caster, "You need one Chanting Stone in inventory!");
                    return;
                }
                reagent.shrink(1);

                NBTTagCompound tag = getOrCreateTagCompound(stack);
                tag.setString("spell", original);

                Wizard.castParticles(world, element, caster.getPositionVector(), 18);
                return;
            }

            if (findIn(shape, shape_area)) {
                try {
                    set.remove(shape_area);
                }catch (Exception ignore){}
                EntityCircleAreaOnceCast entity = new EntityCircleAreaOnceCast(world, set);
                entity.setLocation("u_" + element.func_176610_l());
                if (attackAlly) entity.setCaster(caster);
                if (canAlly) entity.workOnAllies();
                entity.setLifetime(lifetime < 0.0F ? 1 : Solver.asTicks(lifetime));
                entity.setPosition(spellBlock.getX() + 0.5, spellBlock.getY(), spellBlock.getZ() + 0.5);
                entity.setSizeMultiplier((float) (2 + range));
                Myriad.create(world, entity);
                return;
            }

            if (findIn(shape, shape_sigil) && (blockRay != null && blockRay.sideHit == EnumFacing.UP)) {
                try {
                    set.remove(shape_sigil);
                }catch (Exception ignore){}
                EntityCustomSigil entity = new EntityCustomSigil(world, set);
                entity.setLocation(element.func_176610_l());
                if (attackAlly) entity.setCaster(caster);
                entity.setLifetime(Solver.asTicks(lifetime));
                if (canAlly) entity.workOnAllies();
                entity.setPosition(spellBlock.getX() + 0.5, spellBlock.getY(), spellBlock.getZ() + 0.5);
                entity.setSizeMultiplier((float) 2);
                if(world.getEntitiesWithinAABB(entity.getClass(), entity.getEntityBoundingBox()).isEmpty())
                    Myriad.create(world, entity);
                return;
            }

            if (findIn(shape, shape_projectile) && caster != null) {
                try {
                    set.remove(shape_projectile);
                }catch (Exception ignore){}
                EntityChantingArrow entity = new EntityChantingArrow(world, set);
                entity.setElement(element);
                entity.setLifetime(Solver.asTicks(lifetime));
                entity.damageMultiplier = (float) potency;
                entity.setCaster(caster);
                entity.aim(caster, entity.calculateVelocity(entity, (float) range, caster.getEyeHeight()));
                Myriad.create(world, entity);
                return;
            }

            if (shape.equals(shape_minion) && findIn(word, "summon") && isServer) {
                try {
                    set.remove(shape_minion);
                }catch (Exception ignore){}

                if (next.split(":").length <= 1) next= "ebwizardry:"+ next;

                EntityLiving entity = null;
                try {
                    entity = (EntityLiving) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(Tales.toResourceLocation(next)))
                                    .newInstance(world);
                    if (Arrays.asList(Tales.chanting.minionBlacklist).contains(next))
                        entity = null;

                }catch (Exception ignore){}

                if (entity instanceof ISummonedCreature) {
                    if (attackAlly) ((ISummonedCreature) entity).setCaster(caster);
                    ((ISummonedCreature) entity).setLifetime(lifetime < 0.0F ? 1 : Solver.asTicks(lifetime));
                    BlockPos pos = BlockUtils.findNearbyFloorSpace(focal, (int) (2 + range/4), 4);
                    if (pos != null && useMana(focal, ((lifetime/10F) * (health/2F) * potency)/5F
                            , true)) {
                        entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(pos)), null);
                        ((ISummonedCreature) entity).onSpawn();
                        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
                        entity.setHealth(entity.getMaxHealth()); // Need to set this because we may have just modified the value
                        entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(potency);
                        Myriad.create(world, entity);

                        focal.playSound(WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN, 0.7f, 1.0f);
                    }
                }
            }

            if(findIn(word, "ignite") && useMana(focal, 2 * duration, true)){
                target.setFire((int) duration);
                if(!isServer){ Wizard.castParticles(world, Element.FIRE, target.getPositionVector());}
            }

            if (findIn(word, "attack") && findIn(next, "with")){
                if (useMana(focal, potency * potency, true))
                    Sage.smartDamage(next2, caster, target, (float) potency);
            }

            if(findIn(word, "turn") && findIn(next, "around") && useMana(focal, potency, true)){
                target.turn((float) potency * 2, 0);
                if(!isServer){ Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                        target.getPositionVector(), new Vec3d(0, 0.02, 0), 18);}
            }

            if (findIn(word, "apply") && target instanceof EntityLivingBase){
                if (isServer){
                    if (next.split(":").length <= 1) next= "minecraft:"+next;
                    Potion potion = null;
                    try {
                        potion = Potion.getPotionFromResourceLocation(next);
                        Potion finalPotion = potion;
                        if (Arrays.stream(Tales.chanting.applyBlacklist)
                                .anyMatch(p -> p.equals(finalPotion != null ?
                                        Objects.requireNonNull(finalPotion.getRegistryName()).toString() : null))){
                            potion = null;
                        }

                    }catch (Exception ignore){}

                    if (potion != null && useMana(focal, potency * (duration/10), true)) {
                        ((EntityLivingBase) target).addPotionEffect(new PotionEffect(potion,
                                Solver.asTicks(duration), Math.max((int) (potency - 1), 0)));
                    }

                    //Wizard.conjureCircle(world, "construct", target.getPositionVector());
                }

            }

            if(findIn(word, "move")){
                float x;
                float y;
                float z;
                if(!findIn(next, ignore)) {
                    x = getParamNumber(next, 0.0F);
                    y = getParamNumber(next2, 0.0F);
                    z = getParamNumber(next3, 0.0F);
                }else{
                    x = getParamNumber(next2, 0.0F);
                    y = getParamNumber(next3, 0.0F);
                    z = getParamNumber(next4, 0.0F);
                }
                float distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                if(useMana(focal, distance * distance, true)) {
                    target.addVelocity(x * 0.1f, y * 0.1f, z * 0.1f);
                    target.velocityChanged = true;
                }

                if(!isServer){ Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                        target.getPositionVector(), new Vec3d(0, 0.02, 0), 18);}
            }

            // Depends on shape
            if (findIn(shape, shape_construct)){
                if(findIn(word, "teleport")){
                    float x;
                    float y;
                    float z;
                    if(!findIn(next, ignore)) {
                        x = getParamNumber(next, 0.0F);
                        y = getParamNumber(next2, 0.0F);
                        z = getParamNumber(next3, 0.0F);
                    }else{
                        x = getParamNumber(next2, 0.0F);
                        y = getParamNumber(next3, 0.0F);
                        z = getParamNumber(next4, 0.0F);
                    }

                    if (spellBlock != null && Math.abs(x + y + z) == 0.0F){
                        x = (float) ((spellBlock.getX() + 0.5) - target.getPositionVector().x);
                        y = (float) (spellBlock.getY() - target.getPositionVector().y);
                        z = (float) ((spellBlock.getZ() + 0.5) - target.getPositionVector().z);
                    }

                    Vec3d pos = target.getPositionVector();
                    Vec3d targetPos = target.getPositionVector().add(new Vec3d(x,y,z));

                    if(lifetime != 0.0F){
                        EntityCircleTeleportation circle1 = new EntityCircleTeleportation(world);
                        circle1.setLocation("u_sorcery");
                        circle1.setSizeMultiplier(5.0f);
                        EntityCircleTeleportation circle2 = new EntityCircleTeleportation(world);
                        circle2.setLocation("u_sorcery");
                        circle2.setSizeMultiplier(5.0f);

                        circle1.setCaster(caster);
                        circle2.setCaster(caster);

                        if (canAlly){ circle1.workOnAllies(); circle2.workOnAllies();}

                        circle1.setPosition(pos.x, pos.y, pos.z);
                        circle1.setStoredPosition(new Vec3d(targetPos.x, targetPos.y, targetPos.z));
                        circle1.setLifetime(Solver.asTicks(lifetime));

                        circle2.setPosition(targetPos.x, targetPos.y, targetPos.z);
                        circle2.setStoredPosition(new Vec3d(pos.x, pos.y, pos.z));
                        circle2.setLifetime(Solver.asTicks(lifetime));

                        Myriad.create(world, circle1);
                        //target.setPositionAndUpdate(targetPos.x, targetPos.y, targetPos.z);
                        Myriad.create(world, circle2);

                        if(!isServer){ Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                                new Vec3d(pos.x, pos.y, pos.z), new Vec3d(0, 0.02, 0), 18);

                            Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                                    new Vec3d(targetPos.x, targetPos.y, targetPos.z), new Vec3d(0, 0.2, 0), 18);
                        }
                    }
                }

                if (findIn(word, "summon") && isServer) {
                    try {
                        set.remove(shape_construct);
                    }catch (Exception ignore){}

                    if (next.split(":").length <= 1) next= "ebwizardry:"+ next;

                    Entity entity = null;
                    try {
                        entity = Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(Tales.toResourceLocation(next)))
                                .newInstance(world);
                        if (Arrays.asList(Tales.chanting.constructBlacklist).contains(next))
                            entity = null;

                    }catch (Exception ignore){}

                    if (entity != null) {
                        if (entity instanceof EntityMagicConstruct) {
                            if (attackAlly)((EntityMagicConstruct) entity).setCaster(caster);
                            ((EntityMagicConstruct) entity).lifetime = (lifetime < 0.0F ? 1 : Solver.asTicks(lifetime));
                            ((EntityMagicConstruct) entity).damageMultiplier = (float) potency / 100F;
                        }else if (entity instanceof EntityMagic){
                            if (attackAlly) ((EntityMagic) entity).setCaster(caster);
                            ((EntityMagic) entity).setLifetime(lifetime < 0.0F ? 1 : Solver.asTicks(lifetime));
                            ((EntityMagic) entity).damageMultiplier = (float) potency / 100F;
                        }
                        int type = 0;
                        if (entity instanceof EntityScaledConstruct){
                            type = 1;
                            ((EntityScaledConstruct) entity).setSizeMultiplier((float) (2 + range));
                        }else if (entity instanceof EntityMagicScaled){
                            type = 2;
                            ((EntityMagicScaled) entity).setSizeMultiplier((float) (2 + range));
                        }

                        entity.setPosition(spellBlock.getX() + 0.5, spellBlock.getY(), spellBlock.getZ() + 0.5);


                        if (useMana(focal, type > 0 ? lifetime * range * (potency/100F) : lifetime * (potency/100F), true)) {
                            Myriad.create(world, entity);
                            focal.playSound(WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN, 0.7f, 1.0f);
                        }
                    }
                }
            }else {
                if (findIn(word, "teleport")) {
                    float x;
                    float y;
                    float z;
                    if (!findIn(next, ignore)) {
                        x = getParamNumber(next, 0.0F);
                        y = getParamNumber(next2, 0.0F);
                        z = getParamNumber(next3, 0.0F);
                    } else {
                        x = getParamNumber(next2, 0.0F);
                        y = getParamNumber(next3, 0.0F);
                        z = getParamNumber(next4, 0.0F);
                    }

                    if (spellBlock != null && Math.abs(x + y + z) == 0.0F) {
                        x = (float) ((spellBlock.getX() + 0.5) - target.getPositionVector().x);
                        y = (float) (spellBlock.getY() - target.getPositionVector().y);
                        z = (float) ((spellBlock.getZ() + 0.5) - target.getPositionVector().z);
                    }

                    Vec3d pos = target.getPositionVector();
                    Vec3d targetPos = target.getPositionVector().add(new Vec3d(x, y, z));
                    float distance = (float) pos.distanceTo(targetPos);

                    if (lifetime > 0.0F && SpellCreation.useMana(focal, (distance * distance) / 100F)) {
                        EntityMagicCircle circle1 = new EntityMagicCircle(world);
                        circle1.setLocation("u_sorcery");
                        circle1.setSizeMultiplier(5.0f);
                        EntityMagicCircle circle2 = new EntityMagicCircle(world);
                        circle2.setLocation("u_sorcery");
                        circle2.setSizeMultiplier(5.0f);

                        circle1.setCaster(caster);
                        circle2.setCaster(caster);

                        circle1.setPosition(pos.x, pos.y, pos.z);
                        circle1.setLifetime(Solver.asTicks(2));

                        circle2.setPosition(targetPos.x, targetPos.y, targetPos.z);
                        circle2.setLifetime(Solver.asTicks(2));

                        Myriad.create(world, circle1);
                        target.setPositionAndUpdate(targetPos.x, targetPos.y, targetPos.z);
                        Myriad.create(world, circle2);

                        if (!isServer) {
                            Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                                    new Vec3d(pos.x, pos.y, pos.z), new Vec3d(0, 0.02, 0), 18);

                            Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                                    new Vec3d(targetPos.x, targetPos.y, targetPos.z), new Vec3d(0, 0.2, 0), 18);
                        }
                    }
                }
            }

            previous = word;
        }

        if (isServer && caster != null){
            Wizard.conjureCircle(world, "construct", caster.getPositionVector());
        }
    }
*/
}

package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Thief;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.data.PacketMagic;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.data.cap.StatIds;
import astramusfate.wizardry_tales.data.packets.PacketAbilityMode;
import astramusfate.wizardry_tales.data.packets.PacketCastingRing;
import astramusfate.wizardry_tales.data.packets.PacketIncantate;
import astramusfate.wizardry_tales.data.packets.PacketSyncStatToServer;
import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.ClientProxy;
import electroblob.wizardry.client.gui.GuiSpellDisplay;
import electroblob.wizardry.command.CommandCastSpell;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.packet.PacketControlInput;
import electroblob.wizardry.packet.PacketSpellQuickAccess;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;
import java.util.List;

/** Event handler class responsible for handling wizardry's controls. */
//@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class TalesControlHandler {

	static boolean NkeyPressed = false;
	static boolean BkeyPressed = false;
	static boolean[] quickAccessKeyPressed = new boolean[ClientProxy.SPELL_QUICK_ACCESS.length];

	static boolean spellCastRing = false;
	static boolean abilityMode = false;
	static boolean manualInscription = false;
	static boolean status = false;

	// Changed to a tick event to allow mouse button keybinds
	// The 'lag' that happened previously was actually because the code only fired when a keyboard key was pressed!
	@SubscribeEvent
	public static void onTickEvent(TickEvent.ClientTickEvent event){

		if(event.phase == TickEvent.Phase.END) return; // Only really needs to be once per tick

		if(Wizardry.proxy instanceof ClientProxy){

			EntityPlayer player = Minecraft.getMinecraft().player;

			if(player != null){

				ItemStack wand = getWandInUse(player);
				if(wand == null) return;

				if(ClientProxy.NEXT_SPELL.isKeyDown() && Minecraft.getMinecraft().inGameHasFocus){
					if(!NkeyPressed){
						NkeyPressed = true;
						selectNextSpell(wand);
					}
				}else{
					NkeyPressed = false;
				}

				if(ClientProxy.PREVIOUS_SPELL.isKeyDown() && Minecraft.getMinecraft().inGameHasFocus){
					if(!BkeyPressed){
						BkeyPressed = true;
						// Packet building
						selectPreviousSpell(wand);
					}
				}else{
					BkeyPressed = false;
				}

				for(int i = 0; i < ClientProxy.SPELL_QUICK_ACCESS.length; i++){
					if(ClientProxy.SPELL_QUICK_ACCESS[i].isKeyDown() && Minecraft.getMinecraft().inGameHasFocus){
						if(!quickAccessKeyPressed[i]){
							quickAccessKeyPressed[i] = true;
							// Packet building
							selectSpell(wand, i);
						}
					}else{
						quickAccessKeyPressed[i] = false;
					}
				}

			}
		}
	}

	// Changed to a tick event to allow mouse button keybinds
	// The 'lag' that happened previously was actually because the code only fired when a keyboard key was pressed!
	@SubscribeEvent
	public static void onTickEventTales(TickEvent.ClientTickEvent event){

		if(event.phase == TickEvent.Phase.END) return; // Only really needs to be once per tick

		if(WizardryTales.proxy instanceof astramusfate.wizardry_tales.proxy.ClientProxy){

			EntityPlayer player = Minecraft.getMinecraft().player;

			if(player != null){

				if(astramusfate.wizardry_tales.proxy.ClientProxy.CAST_SPELL.isKeyDown() && Minecraft.getMinecraft().inGameHasFocus){
					ISoul soul = Mana.getSoul(player);
					if (soul == null || soul.getCooldown() > 0) return;

					ItemStack wand = getWandInUseSmarter(player);
					if(wand == null) return;
					Spell spell = WandHelper.getCurrentSpell(wand);
					SpellModifiers modifiers = SpellModifiers.fromNBT(wand.getTagCompound() == null ? new NBTTagCompound() : wand.getTagCompound());
					if (WizardData.get(player) != null && !WizardData.get(player).hasSpellBeenDiscovered(spell)) return;

					if (!spellCastRing) {
						spellCastRing = true;
						spellCastRing(player.getEntityId(), spell, modifiers);
						soul.setCooldown(player, Tales.mp.casting_ring_cooldown);
					}
				}else{
					spellCastRing = false;
				}


				if(astramusfate.wizardry_tales.proxy.ClientProxy.ABILITY_1.isKeyDown() && Minecraft.getMinecraft().inGameHasFocus) {
					ISoul soul = Mana.getSoul(player);
					if (soul == null) return;

					if (!abilityMode){
						abilityMode = true;
						int mode = soul.getMode() + 1 > 1 ? 0 : 1;
						soul.setMode(player, mode);
						switchAbility(mode);
					}
				}else{
					abilityMode = false;
				}

				if(astramusfate.wizardry_tales.proxy.ClientProxy.MANUAL_CHANT.isKeyDown() && Minecraft.getMinecraft().inGameHasFocus){
					if (!manualInscription){
						manualInscription = true;
						setManualInscription(player);
					}
				}else{
					manualInscription = false;
				}
			}
		}
	}

	public static boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers){

		// Spells can only be cast if the casting events aren't cancelled...
		if(castingTick == 0){
			if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, spell, caster, modifiers))) return false;
		}else{
			if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Tick(SpellCastEvent.Source.OTHER, spell, caster, modifiers, castingTick))) return false;
		}
		IManaStoringItem mana = (IManaStoringItem) stack.getItem();

		int cost = (int)(spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding

		// As of wizardry 4.2 mana cost is only divided over two intervals each second
		if(spell.isContinuous) cost = getDistributedCost(cost, castingTick) * (CommandCastSpell.DEFAULT_CASTING_DURATION/20);

		// ...and the wand has enough mana to cast the spell...
		return cost <= mana.getMana(stack) // This comes first because it changes over time
				// ...and either the spell is not in cooldown or the player is in creative mode
				&& (WandHelper.getCurrentCooldown(stack) == 0 || caster.isCreative());
	}

	/** Distributes the given cost (which should be the per-second cost of a continuous spell) over a second and
	 * returns the appropriate cost to be applied for the given tick. Currently the cost is distributed over 2
	 * intervals per second, meaning the returned value is 0 unless {@code castingTick} is a multiple of 10.*/
	protected static int getDistributedCost(int cost, int castingTick){

		int partialCost;

		if(castingTick % 20 == 0){ // Whole number of seconds has elapsed
			partialCost = cost / 2 + cost % 2; // Make sure cost adds up to the correct value by adding the remainder here
		}else if(castingTick % 10 == 0){ // Something-and-a-half seconds has elapsed
			partialCost = cost/2;
		}else{ // Some other number of ticks has elapsed
			partialCost = 0; // Wands aren't damaged within half-seconds
		}

		return partialCost;
	}

	// Shift-scrolling to change spells
	@SubscribeEvent
	public static void onMouseEvent(MouseEvent event){

		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack wand = getWandInUse(player);
		if(wand == null) return;

		if(Minecraft.getMinecraft().inGameHasFocus && !wand.isEmpty() && event.getDwheel() != 0 && player.isSneaking()
				&& Wizardry.settings.shiftScrolling){

			event.setCanceled(true);
			
			int d = Wizardry.settings.reverseScrollDirection ? -event.getDwheel() : event.getDwheel();

			if(d > 0){
				selectNextSpell(wand);
			}else if(d < 0){
				selectPreviousSpell(wand);
			}
		}
	}

	private static ItemStack getWandInUse(EntityPlayer player){
		if(ItemArtefact.isArtefactActive(player, TalesItems.casting_ring)
				&& Thief.testPredicateHands(player, i -> !(i.getItem() instanceof ISpellCastingItem))) {
			return Thief.getItem(player, item -> item.getItem() instanceof ISpellCastingItem
					&& ((ISpellCastingItem) item.getItem()).getSpells(item).length >= 2 && item.getItem() instanceof IManaStoringItem);
		}
		return null;
	}

	private static ItemStack getWandInUseSmarter(EntityPlayer player){
		if(ItemArtefact.isArtefactActive(player, TalesItems.casting_ring)
				&& Thief.testPredicateHands(player, i -> !(i.getItem() instanceof ISpellCastingItem))) {
			return Thief.getItem(player, item -> item.getItem() instanceof ISpellCastingItem
					&& item.getItem() instanceof IManaStoringItem);
		}
		return null;
	}

	private static void spellCastRing(int casterID, Spell spell, SpellModifiers modifiers){
		// Packet building
		IMessage msg = new PacketCastingRing(casterID, spell, modifiers);
		PacketMagic.net.sendToServer(msg);
	}

	private static void openStatus(int status){
		// Packet building
		IMessage msg = new PacketSyncStatToServer(StatIds.status, status);
		PacketMagic.net.sendToServer(msg);
	}

	private static void setManualInscription(EntityPlayer player){
		List<ItemStack> stacks = SpellcastingHandler.getChantedItems(player);
		for (ItemStack stack : stacks) {
			if (stack != null && SpellcastingHandler.checkCondition(stack, Lexicon.condition_manual)
					&& !player.getCooldownTracker().hasCooldown(stack.getItem())){
				NBTTagCompound tag = stack.getTagCompound();
				if (tag != null) {
					if (tag.hasKey("spell") && tag.hasKey("condition")
							&& SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_manual)) {
						player.getCooldownTracker().setCooldown(stack.getItem(), 20);

						String msg = SpellCreation.getMsg(tag.getString("spell"));
						List<String> words = SpellCreation.getSpell(msg);
						String[] spell = words.toArray(new String[0]);
						List<String> set = Lists.newArrayList();
						try {
							set.addAll(Arrays.asList(spell));
							set.remove(Lexicon.par_shape);
							set.remove(Lexicon.shape_inscribe);
						} catch (Exception e) {
							Aterna.messageBar(player, "Problem when casting!");
						}

						SpellCreation.createSpell(set, player, player, false);
					}
				}
			}
		}

		// Packet building
		IMessage msg = new PacketIncantate();
		PacketMagic.net.sendToServer(msg);
	}

	private static void switchAbility(int mode){
		// Packet building
		IMessage msg = new PacketAbilityMode(mode);
		PacketMagic.net.sendToServer(msg);
	}
	
	private static void selectNextSpell(ItemStack wand){
		// Packet building
		IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.NEXT_SPELL_KEY);
		WizardryPacketHandler.net.sendToServer(msg);
		// GUI switch animation
		((ISpellCastingItem)wand.getItem()).selectNextSpell(wand); // Makes sure the spell is set immediately for the client
		GuiSpellDisplay.playSpellSwitchAnimation(true);
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(WizardrySounds.ITEM_WAND_SWITCH_SPELL, 1));
	}
	
	private static void selectPreviousSpell(ItemStack wand){
		// Packet building
		IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.PREVIOUS_SPELL_KEY);
		WizardryPacketHandler.net.sendToServer(msg);
		// GUI switch animation
		((ISpellCastingItem)wand.getItem()).selectPreviousSpell(wand); // Makes sure the spell is set immediately for the client
		GuiSpellDisplay.playSpellSwitchAnimation(false);
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(WizardrySounds.ITEM_WAND_SWITCH_SPELL, 1));
	}

	private static void selectSpell(ItemStack wand, int index){
		// GUI switch animation
		if(((ISpellCastingItem)wand.getItem()).selectSpell(wand, index)){ // Makes sure the spell is set immediately for the client
			// Packet building (no point sending it unless the client-side spell selection succeeded
			IMessage msg = new PacketSpellQuickAccess.Message(index);
			WizardryPacketHandler.net.sendToServer(msg);

			GuiSpellDisplay.playSpellSwitchAnimation(true); // This will do, it's only an animation
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(WizardrySounds.ITEM_WAND_SWITCH_SPELL, 1));
		}
	}

}

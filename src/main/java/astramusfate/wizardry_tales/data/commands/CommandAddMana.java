package astramusfate.wizardry_tales.data.commands;


import astramusfate.wizardry_tales.WizardryTales;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;

import javax.annotation.Nonnull;
import java.util.List;

public class CommandAddMana extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "addPlayerMana";
    }

    @Override
    public int getRequiredPermissionLevel(){
        // I *think* it's something like 0 = everyone, 1 = moderator, 2 = op/admin, 3 = op/console...
        return 3;
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender p_71519_1_){
        return true;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender p_71518_1_){
        // Not ideal, but the way this is implemented means I have no choice. Only used in the help command, so in there
        // the custom command name will not display.
        return "commands." + WizardryTales.MODID + ":addMana.usage";
        // return I18n.format("commands." + Wizardry.MODID + ":ally.usage", Wizardry.settings.allyCommandName);
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] arguments,
                                          BlockPos pos){
        if (arguments.length == 1) {
            return getListOfStringsMatchingLastWord(arguments, server.getOnlinePlayerNames());
        }
        return super.getTabCompletions(server, sender, arguments, pos);
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if(args.length != 2) {
            throw new WrongUsageException("commands." + WizardryTales.MODID + ":addMana.usage", "addMana");
        }

        EntityPlayerMP manaOf = getPlayer(server, sender, args[0]);

        ISoul soul = Mana.getSoul(manaOf);
        if(soul != null){
            double mana = soul.getMP();

            try {
                mana = Double.parseDouble(args[1]);
            } catch (Exception ignore){}

            soul.addMana(manaOf, mana);
        }

    }
}

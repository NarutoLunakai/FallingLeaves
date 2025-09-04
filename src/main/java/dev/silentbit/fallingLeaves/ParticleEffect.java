package dev.silentbit.fallingLeaves;

import java.util.Objects;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;

record ParticleEffect(Particle particle, BlockData blockData) {
    ParticleEffect(Particle particle, BlockData blockData) {
        Objects.requireNonNull(particle, "Particle cannot be null");
        Objects.requireNonNull(blockData, "BlockData cannot be null");
        this.particle = particle;
        this.blockData = blockData;
    }

    public Particle particle() {
        return this.particle;
    }

    public BlockData blockData() {
        return this.blockData;
    }
}
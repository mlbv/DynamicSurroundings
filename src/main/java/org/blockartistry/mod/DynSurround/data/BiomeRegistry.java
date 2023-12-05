/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.DynSurround.data;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.Module;
import org.blockartistry.mod.DynSurround.client.sound.SoundEffect;
import org.blockartistry.mod.DynSurround.client.sound.SoundEffect.SoundType;
import org.blockartistry.mod.DynSurround.data.config.BiomeConfig;
import org.blockartistry.mod.DynSurround.data.config.SoundConfig;
import org.blockartistry.mod.DynSurround.event.RegistryReloadEvent;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.MyUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public final class BiomeRegistry {
    public static final Map<String, BiomeRegistryEntry> registry = new HashMap<>();
	private static final Map<String, String> biomeAliases = new HashMap<>();

	public static final BiomeGenBase UNDERGROUND = new FakeBiome(-1, "Underground");
	public static final BiomeGenBase PLAYER = new FakeBiome(-2, "Player");
	public static final BiomeGenBase UNDERWATER = new FakeBiome(-3, "Underwater");
	public static final BiomeGenBase UNDEROCEAN = new FakeBiome(-4, "UnderOCN");
	public static final BiomeGenBase UNDERDEEPOCEAN = new FakeBiome(-5, "UnderDOCN");
	public static final BiomeGenBase UNDERRIVER = new FakeBiome(-6, "UnderRVR");
	public static final BiomeGenBase OUTERSPACE = new FakeBiome(-7, "OuterSpace");
	public static final BiomeGenBase CLOUDS = new FakeBiome(-8, "Clouds");

	public static final SoundEffect WATER_DRIP = new SoundEffect(Module.MOD_ID + ":waterdrops");

	// This is for cases when the biome coming in doesn't make sense
	// and should default to something to avoid crap.
	private static final BiomeGenBase WTF = new FakeBiome(-256, "(FooBar)");

	public static class BiomeRegistryEntry implements Comparable<BiomeRegistryEntry> {

		private static Class<?> bopBiome;
		private static Field bopBiomeFogDensity;
		private static Field bopBiomeFogColor;

		static {
			try {
				bopBiome = Class.forName("biomesoplenty.common.biome.BOPBiome");
				bopBiomeFogDensity = ReflectionHelper.findField(bopBiome, "fogDensity");
				bopBiomeFogColor = ReflectionHelper.findField(bopBiome, "fogColor");
			} catch (final Throwable t) {
				bopBiome = null;
				bopBiomeFogDensity = null;
				bopBiomeFogColor = null;
			}
		}

		public final BiomeGenBase biome;
		public boolean hasPrecipitation;
		public boolean hasDust;
		public boolean hasAurora;
		public boolean hasFog;

		public Color dustColor;
		public Color fogColor;
		public float fogDensity;

		public List<SoundEffect> sounds;

		public int spotSoundChance;
		public List<SoundEffect> spotSounds;

		public BiomeRegistryEntry(final BiomeGenBase biome) {
			this.biome = biome;
			this.hasPrecipitation = biome.canSpawnLightningBolt() || biome.getEnableSnow();
			this.sounds = new ArrayList<>();
			this.spotSounds = new ArrayList<>();
			this.spotSoundChance = 1200;

			// If it is a BOP biome initialize from the BoP Biome
			// instance. May be overwritten by DS config.
			if (bopBiome != null && bopBiome.isInstance(biome)) {
				try {
					final int color = bopBiomeFogColor.getInt(biome);
					if (color > 0) {
						this.hasFog = true;
						this.fogColor = new Color(color);
						this.fogDensity = bopBiomeFogDensity.getFloat(biome);
					}
				} catch (final Exception ignored) {
				}
			}
		}

		public SoundEffect findSoundMatch(final String conditions) {
			for (final SoundEffect sound : this.sounds)
				if (sound.matches(conditions))
					return sound;
			return null;
		}

		public List<SoundEffect> findSoundMatches(final String conditions) {
			final List<SoundEffect> results = new ArrayList<>();
			for (final SoundEffect sound : this.sounds)
				if (sound.matches(conditions))
					results.add(sound);
			return results;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append(String.format("Biome %d [%s]:", this.biome.biomeID, resolveName(this.biome)));
			if (this.hasPrecipitation)
				builder.append(" PRECIPITATION");
			if (this.hasDust)
				builder.append(" DUST");
			if (this.hasAurora)
				builder.append(" AURORA");
			if (this.hasFog)
				builder.append(" FOG");
			if (!this.hasPrecipitation && !this.hasDust && !this.hasAurora && !this.hasFog)
				builder.append(" NONE");
			if (this.dustColor != null)
				builder.append(" dustColor:").append(this.dustColor);
			if (this.fogColor != null) {
				builder.append(" fogColor:").append(this.fogColor);
				builder.append(" fogDensity:").append(this.fogDensity);
			}

			if (!this.sounds.isEmpty()) {
				builder.append("; sounds [");
				for (final SoundEffect sound : this.sounds)
					builder.append(sound.toString()).append(',');
				builder.append(']');
			}

			if (!this.spotSounds.isEmpty()) {
				builder.append("; spot sound chance:").append(this.spotSoundChance);
				builder.append(" spot sounds [");
				for (final SoundEffect sound : this.spotSounds)
					builder.append(sound.toString()).append(',');
				builder.append(']');
			}
			return builder.toString();
		}

        @Override
        public int compareTo(BiomeRegistryEntry e) {
            return this.biome.biomeName.compareToIgnoreCase(e.biome.biomeName);
        }
    }

	public static String resolveName(final BiomeGenBase biome) {
		if (biome == null)
			return "(Bad Biome)";
		if (StringUtils.isEmpty(biome.biomeName))
			return "#" + biome.biomeID;
		return biome.biomeName;
	}

	public static void initialize() {
		synchronized (registry) {
			biomeAliases.clear();
			for (final String entry : ModOptions.biomeAliases) {
				final String[] parts = StringUtils.split(entry, "=");
				if (parts.length == 2) {
					biomeAliases.put(parts[0], parts[1]);
				}
			}

			registry.clear();

            Module.LOTR_PROXY.registerLOTRBiomes();
            for (BiomeGenBase biomeGenBase : BiomeGenBase.getBiomeGenArray()) {
                if (biomeGenBase != null) {
                    registry.put(biomeGenBase.biomeName, new BiomeRegistryEntry(biomeGenBase));
                }
            }
			// Add our fake biomes
			registry.put(UNDERGROUND.biomeName, new BiomeRegistryEntry(UNDERGROUND));
			registry.put(UNDERWATER.biomeName, new BiomeRegistryEntry(UNDERWATER));
			registry.put(UNDEROCEAN.biomeName, new BiomeRegistryEntry(UNDEROCEAN));
			registry.put(UNDERDEEPOCEAN.biomeName, new BiomeRegistryEntry(UNDERDEEPOCEAN));
			registry.put(UNDERRIVER.biomeName, new BiomeRegistryEntry(UNDERRIVER));
			registry.put(OUTERSPACE.biomeName, new BiomeRegistryEntry(OUTERSPACE));
			registry.put(CLOUDS.biomeName, new BiomeRegistryEntry(CLOUDS));
			registry.put(PLAYER.biomeName, new BiomeRegistryEntry(PLAYER));
			registry.put(WTF.biomeName, new BiomeRegistryEntry(WTF));

			processConfig();

			if (ModOptions.enableDebugLogging) {
				ModLog.info("*** BIOME REGISTRY ***");
                registry
                    .values()
                    .stream()
                    .sorted()
                    .forEach(entry -> ModLog.info(entry.toString()))
                ;
			}

			// Free memory because we no longer need
			biomeAliases.clear();
		}

		MinecraftForge.EVENT_BUS.post(new RegistryReloadEvent.Biome());
	}

	private static BiomeRegistryEntry get(final BiomeGenBase biome) {
		synchronized (registry) {
			BiomeRegistryEntry entry = registry.get(biome == null ? WTF.biomeName : biome.biomeName);
			if (entry == null) {
				ModLog.warn("Biome [%s] was not detected during initial scan! Reloading config...", resolveName(biome));
				initialize();
				entry = registry.get(biome.biomeName);
				if (entry == null) {
					ModLog.warn("Still can't find biome [%s]! Explicitly adding at defaults", resolveName(biome));
					entry = new BiomeRegistryEntry(biome);
					registry.put(biome.biomeName, entry);
				}
			}
			return entry;
		}
	}

	public static boolean hasDust(final BiomeGenBase biome) {
		return get(biome).hasDust;
	}

	public static boolean hasPrecipitation(final BiomeGenBase biome) {
		return get(biome).hasPrecipitation;
	}

	public static boolean hasAurora(final BiomeGenBase biome) {
		return get(biome).hasAurora;
	}

	public static boolean hasFog(final BiomeGenBase biome) {
		return get(biome).hasFog;
	}

	public static Color getDustColor(final BiomeGenBase biome) {
		return get(biome).dustColor;
	}

	public static Color getFogColor(final BiomeGenBase biome) {
		return get(biome).fogColor;
	}

	public static float getFogDensity(final BiomeGenBase biome) {
		return get(biome).fogDensity;
	}

	public static SoundEffect getSound(final BiomeGenBase biome, final String conditions) {
		return get(biome).findSoundMatch(conditions);
	}

	public static List<SoundEffect> getSounds(final BiomeGenBase biome, final String conditions) {
		return get(biome).findSoundMatches(conditions);
	}

	public static SoundEffect getSpotSound(final BiomeGenBase biome, final String conditions, final Random random) {
		final BiomeRegistryEntry e = get(biome);
		if (e.spotSounds.isEmpty() || random.nextInt(e.spotSoundChance) != 0)
			return null;

		int totalWeight = 0;
		final List<SoundEffect> candidates = new ArrayList<>();
		for (final SoundEffect s : e.spotSounds)
			if (s.matches(conditions)) {
				candidates.add(s);
				totalWeight += s.weight;
			}
		if (totalWeight <= 0)
			return null;

		if (candidates.size() == 1)
			return candidates.get(0);

		int targetWeight = random.nextInt(totalWeight);
		int i;
		for (i = candidates.size(); (targetWeight -= candidates.get(i - 1).weight) >= 0; i--)
			;

		return candidates.get(i - 1);
	}

	private static void processConfig() {
		try {
			process(BiomeConfig.load(Module.MOD_ID));
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final String[] configFiles = ModOptions.biomeConfigFiles;
		for (final String file : configFiles) {
			final File theFile = new File(Module.dataDirectory(), file);
			if (theFile.exists()) {
				try {
					final BiomeConfig config = BiomeConfig.load(theFile);
					if (config != null)
						process(config);
					else
						ModLog.warn("Unable to process biome config file " + file);
				} catch (final Exception ex) {
					ModLog.error("Unable to process biome config file " + file, ex);
				}
			} else {
				ModLog.warn("Could not locate biome config file [%s]", file);
			}
		}
	}

	static boolean isBiomeMatch(final BiomeConfig.Entry entry, final String biomeName) {
		if (Pattern.matches(entry.biomeName, biomeName))
			return true;
		final String alias = biomeAliases.get(biomeName);
		return alias != null && Pattern.matches(entry.biomeName, alias);
	}

	private static void process(final BiomeConfig config) {
		for (final BiomeConfig.Entry entry : config.entries) {
			for (final BiomeRegistryEntry biomeEntry : registry.values()) {
				if (isBiomeMatch(entry, resolveName(biomeEntry.biome))) {
					if (entry.hasPrecipitation != null)
						biomeEntry.hasPrecipitation = entry.hasPrecipitation;
					if (entry.hasAurora != null)
						biomeEntry.hasAurora = entry.hasAurora;
					if (entry.hasDust != null)
						biomeEntry.hasDust = entry.hasDust;
					if (entry.hasFog != null)
						biomeEntry.hasFog = entry.hasFog;
					if (entry.fogDensity != null)
						biomeEntry.fogDensity = entry.fogDensity;
					if (entry.fogColor != null) {
						final int[] rgb = MyUtils.splitToInts(entry.fogColor, ',');
						if (rgb.length == 3)
							biomeEntry.fogColor = new Color(rgb[0], rgb[1], rgb[2]);
					}
					if (entry.dustColor != null) {
						final int[] rgb = MyUtils.splitToInts(entry.dustColor, ',');
						if (rgb.length == 3)
							biomeEntry.dustColor = new Color(rgb[0], rgb[1], rgb[2]);
					}
					if (entry.soundReset != null && entry.soundReset) {
						biomeEntry.sounds = new ArrayList<>();
						biomeEntry.spotSounds = new ArrayList<>();
					}

					if (entry.spotSoundChance != null)
						biomeEntry.spotSoundChance = entry.spotSoundChance;

					for (final SoundConfig sr : entry.sounds) {
						if (SoundRegistry.isSoundBlocked(sr.sound))
							continue;
						final SoundEffect s = new SoundEffect(sr);
						if (s.type == SoundType.SPOT)
							biomeEntry.spotSounds.add(s);
						else
							biomeEntry.sounds.add(s);
					}
				}
			}
		}
	}
}

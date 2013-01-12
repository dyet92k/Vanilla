/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Vanilla is licensed under the Spout License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.vanilla.plugin.world.generator.theend;

import java.util.Random;

import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.module.modifier.ScalePoint;
import net.royawesome.jlibnoise.module.source.Perlin;

import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;

import org.spout.vanilla.plugin.material.VanillaMaterials;
import org.spout.vanilla.plugin.material.block.Liquid;
import org.spout.vanilla.plugin.world.generator.VanillaSingleBiomeGenerator;
import org.spout.vanilla.plugin.world.generator.biome.VanillaBiomes;

public class TheEndGenerator extends VanillaSingleBiomeGenerator {
	private static final int SEA_LEVEL = 62;
	private static final int ISLAND_HEIGHT = 56;
	private static final int ISLAND_OFFSET = 8;
	private static final int ISLAND_RADIUS = 144;
	private static final double ISLAND_TOTAL_OFFSET = ISLAND_OFFSET + ISLAND_HEIGHT / 2d;
	private static final double ISLAND_HEIGHT_SCALE = ((double) ISLAND_RADIUS / (double) ISLAND_HEIGHT) * 2d;
	public static final int HEIGHT = ((ISLAND_HEIGHT + ISLAND_OFFSET + 1) / 4) * 4 + 4;
	// noise for generation
	private static final Perlin PERLIN = new Perlin();
	private static final ScalePoint NOISE = new ScalePoint();

	static {
		PERLIN.setFrequency(0.01);
		PERLIN.setLacunarity(2);
		PERLIN.setNoiseQuality(NoiseQuality.BEST);
		PERLIN.setPersistence(0.5);
		PERLIN.setOctaveCount(16);

		NOISE.SetSourceModule(0, PERLIN);
		NOISE.setxScale(0.7);
		NOISE.setyScale(1);
		NOISE.setzScale(0.7);
	}

	public TheEndGenerator() {
		super(VanillaBiomes.ENDSTONE);
	}

	@Override
	public void registerBiomes() {
		super.registerBiomes();
		register(VanillaBiomes.ENDSTONE);
	}

	@Override
	public String getName() {
		return "VanillaTheEnd";
	}

	@Override
	protected void generateTerrain(CuboidBlockMaterialBuffer blockData, int x, int y, int z, BiomeManager biomes, long seed) {
		PERLIN.setSeed((int) seed * 23);
		final Vector3 size = blockData.getSize();
		final int sizeX = size.getFloorX();
		final int sizeY = MathHelper.clamp(size.getFloorY(), 0, HEIGHT);
		final int sizeZ = size.getFloorZ();
		final double[][][] noise = WorldGeneratorUtils.fastNoise(NOISE, sizeX, sizeY, sizeZ, 4, x, y, z);
		for (int xx = 0; xx < sizeX; xx++) {
			for (int yy = 0; yy < sizeY; yy++) {
				for (int zz = 0; zz < sizeZ; zz++) {
					final int totalX = x + xx;
					final int totalY = y + yy;
					final int totalZ = z + zz;
					final double distanceY = (totalY - ISLAND_TOTAL_OFFSET) * ISLAND_HEIGHT_SCALE;
					final double distance = Math.sqrt(totalX * totalX + distanceY * distanceY + totalZ * totalZ);
					if (distance == 0) {
						blockData.set(totalX, totalY, totalZ, VanillaMaterials.END_STONE);
						continue;
					}
					final double density = ISLAND_RADIUS / distance * (noise[xx][yy][zz] * 0.5 + 0.5);
					if (density >= 1) {
						blockData.set(totalX, totalY, totalZ, VanillaMaterials.END_STONE);
					}
				}
			}
		}
	}

	@Override
	public Point getSafeSpawn(World world) {
		final Random random = new Random();
		for (byte attempts = 0; attempts < 10; attempts++) {
			final int x = random.nextInt(32) - 16;
			final int z = random.nextInt(32) - 16;
			final int y = getHighestSolidBlock(world, x, z);
			if (y != -1) {
				return new Point(world, x, y + 0.5f, z);
			}
		}
		return new Point(world, 0, 80, 0);
	}

	private int getHighestSolidBlock(World world, int x, int z) {
		int y = world.getHeight() - 1;
		while (world.getBlockMaterial(x, y, z) == VanillaMaterials.AIR) {
			y--;
			if (y == 0 || world.getBlockMaterial(x, y, z) instanceof Liquid) {
				return -1;
			}
		}
		return ++y;
	}

	@Override
	public int[][] getSurfaceHeight(World world, int chunkX, int chunkY) {
		int[][] heights = new int[Chunk.BLOCKS.SIZE][Chunk.BLOCKS.SIZE];
		for (int x = 0; x < Chunk.BLOCKS.SIZE; x++) {
			for (int z = 0; z < Chunk.BLOCKS.SIZE; z++) {
				heights[x][z] = SEA_LEVEL;
			}
		}
		return heights;
	}
}
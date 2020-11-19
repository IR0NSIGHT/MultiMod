package me.jakev.smprogression.asteroids;

import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.server.controller.world.factory.WorldCreatorFloatingRockFactory;
import org.schema.game.server.controller.world.factory.planet.structures.TerrainStructure;
import org.schema.game.server.controller.world.factory.planet.structures.TerrainStructureList;
import org.schema.game.server.controller.world.factory.terrain.GeneratorResourcePlugin;
import org.schema.game.server.controller.world.factory.terrain.TerrainDeco;

import java.util.Random;

/**
 * Created by Jake on 11/16/2020.
 * <insert description here>
 */
public class MetalAsteroidFactory extends WorldCreatorFloatingRockFactory {
    private final short oreType;
    public MetalAsteroidFactory(long seed, short oreId) {
        super(seed);
        oreType = oreId;
    }

    private final static int ICE = registerBlock(ElementKeyMap.TERRAIN_ROCK_WHITE);

    @Override
    protected int getRandomSolidType(float density, Random rand) {
        return ICE;
    }

    @Override
    public void setMinable(Random rand) {
        this.minable = new TerrainDeco[1];
        this.minable[0] = new GeneratorResourcePlugin(9, oreType, ElementKeyMap.TERRAIN_ROCK_WHITE);
    }

    @Override
    protected void terrainStructurePlacement(byte x, byte y, byte z, float depth, TerrainStructureList sl, Random rand) {
        if (rand.nextFloat() <= defaultResourceChance) {
            sl.add(x, y, z, TerrainStructure.Type.ResourceBlob, oreType, ElementKeyMap.TERRAIN_ROCK_WHITE, defaultResourceSize);
        }
    }
}

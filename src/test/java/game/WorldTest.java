package game;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldTest {

    @Test
    void addAtEmptyLocation_creature_placesAtOnlyEligibleFloorTile() {
        Tile[][][] tiles = wallMap(2, 2);
        tiles[1][0][0] = Tile.FLOOR;
        World world = new World(tiles);
        Creature creature = newCreature(world, "bat");

        world.addAtEmptyLocation(creature, 0);

        assertEquals(1, creature.x);
        assertEquals(0, creature.y);
        assertEquals(0, creature.z);
        assertSame(creature, world.creature(1, 0, 0));
    }

    @Test
    void addAtEmptyLocation_item_placesAtOnlyEligibleFloorTile() {
        Tile[][][] tiles = wallMap(2, 2);
        tiles[0][1][0] = Tile.FLOOR;
        World world = new World(tiles);
        Item item = newItem("potion");

        world.addAtEmptyLocation(item, 0);

        assertSame(item, world.item(0, 1, 0));
    }

    @Test
    void addAtEmptyLocation_creatureSkipsOtherCreaturesButMayShareWithItems() {
        Tile[][][] tiles = wallMap(2, 1);
        tiles[0][0][0] = Tile.FLOOR;
        tiles[1][0][0] = Tile.FLOOR;
        World world = new World(tiles);

        Creature existingCreature = newCreature(world, "existing");
        existingCreature.x = 0;
        existingCreature.y = 0;
        world.add(existingCreature);
        Item existingItem = newItem("rock");
        world.addAtEmptySpace(existingItem, 1, 0, 0);
        Creature newCreature = newCreature(world, "new");

        world.addAtEmptyLocation(newCreature, 0);

        assertSame(existingCreature, world.creature(0, 0, 0));
        assertSame(newCreature, world.creature(1, 0, 0));
        assertSame(existingItem, world.item(1, 0, 0));
    }

    @Test
    void addAtEmptyLocation_itemSkipsOtherItemsButMayShareWithCreatures() {
        Tile[][][] tiles = wallMap(2, 1);
        tiles[0][0][0] = Tile.FLOOR;
        tiles[1][0][0] = Tile.FLOOR;
        World world = new World(tiles);

        Item existingItem = newItem("rock");
        world.addAtEmptySpace(existingItem, 0, 0, 0);
        Creature existingCreature = newCreature(world, "bat");
        existingCreature.x = 1;
        existingCreature.y = 0;
        world.add(existingCreature);
        Item newItem = newItem("potion");

        world.addAtEmptyLocation(newItem, 0);

        assertSame(existingItem, world.item(0, 0, 0));
        assertSame(existingCreature, world.creature(1, 0, 0));
        assertSame(newItem, world.item(1, 0, 0));
    }

    @Test
    void addAtEmptyLocation_noGroundTiles_throwsWithoutChangingState() {
        World world = new World(wallMap(2, 2));
        Creature creature = newCreature(world, "bat");
        creature.x = 7;
        creature.y = 8;
        creature.z = 9;
        Item item = newItem("potion");

        IllegalStateException creatureException = assertThrows(
                IllegalStateException.class,
                () -> world.addAtEmptyLocation(creature, 0));
        IllegalStateException itemException = assertThrows(
                IllegalStateException.class,
                () -> world.addAtEmptyLocation(item, 0));

        assertTrue(creatureException.getMessage().contains("depth 0"));
        assertTrue(itemException.getMessage().contains("depth 0"));
        assertEquals(7, creature.x);
        assertEquals(8, creature.y);
        assertEquals(9, creature.z);
        assertNull(world.item(0, 0, 0));
        assertNull(world.creature(0, 0, 0));
    }

    @Test
    void addAtEmptyLocation_allGroundTilesOccupied_throwsForBothTypes() {
        Tile[][][] creatureTiles = wallMap(2, 1);
        creatureTiles[0][0][0] = Tile.FLOOR;
        creatureTiles[1][0][0] = Tile.FLOOR;
        World creatureWorld = new World(creatureTiles);
        Creature firstCreature = newCreature(creatureWorld, "first");
        firstCreature.x = 0;
        creatureWorld.add(firstCreature);
        Creature secondCreature = newCreature(creatureWorld, "second");
        secondCreature.x = 1;
        creatureWorld.add(secondCreature);
        Creature unplacedCreature = newCreature(creatureWorld, "third");

        Tile[][][] itemTiles = wallMap(2, 1);
        itemTiles[0][0][0] = Tile.FLOOR;
        itemTiles[1][0][0] = Tile.FLOOR;
        World itemWorld = new World(itemTiles);
        Item firstItem = newItem("first");
        Item secondItem = newItem("second");
        itemWorld.addAtEmptySpace(firstItem, 0, 0, 0);
        itemWorld.addAtEmptySpace(secondItem, 1, 0, 0);
        Item unplacedItem = newItem("third");

        assertThrows(IllegalStateException.class,
                () -> creatureWorld.addAtEmptyLocation(unplacedCreature, 0));
        assertThrows(IllegalStateException.class,
                () -> itemWorld.addAtEmptyLocation(unplacedItem, 0));

        assertSame(firstCreature, creatureWorld.creature(0, 0, 0));
        assertSame(secondCreature, creatureWorld.creature(1, 0, 0));
        assertSame(firstItem, itemWorld.item(0, 0, 0));
        assertSame(secondItem, itemWorld.item(1, 0, 0));
    }

    @Test
    void addAtEmptyLocation_invalidDepth_throwsWithoutChangingState() {
        Tile[][][] tiles = wallMap(1, 1);
        tiles[0][0][0] = Tile.FLOOR;
        World world = new World(tiles);
        Creature creature = newCreature(world, "bat");
        creature.x = 7;
        creature.y = 8;
        creature.z = 9;
        Item item = newItem("potion");

        IllegalStateException creatureException = assertThrows(
                IllegalStateException.class,
                () -> world.addAtEmptyLocation(creature, 1));
        IllegalStateException itemException = assertThrows(
                IllegalStateException.class,
                () -> world.addAtEmptyLocation(item, 1));

        assertTrue(creatureException.getMessage().contains("depth 1"));
        assertTrue(itemException.getMessage().contains("depth 1"));
        assertEquals(7, creature.x);
        assertEquals(8, creature.y);
        assertEquals(9, creature.z);
        assertSame(Tile.FLOOR, world.tile(0, 0, 0));
        assertNull(world.item(0, 0, 0));
    }

    private static Tile[][][] wallMap(int width, int height) {
        Tile[][][] tiles = new Tile[width][height][1];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y][0] = Tile.WALL;
            }
        }
        return tiles;
    }

    private static Creature newCreature(World world, String name) {
        return new Creature(world, '@', Color.GRAY, name, 100, 10, 5);
    }

    private static Item newItem(String name) {
        return new Item('!', Color.RED, name, null);
    }
}

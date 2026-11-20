package game;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreatureTest {

    private World world;
    private Creature creature;
    private Tile[][][] tiles;

    @BeforeEach
    void setUp() {
        tiles = new Tile[3][3][1];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                tiles[x][y][0] = Tile.FLOOR;
            }
        }

        world = new World(tiles);
        creature = new Creature(world, '@', Color.GRAY, "josh", 100, 10, 5);
        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
    }

    @Test
    void moveBy_openFloor_movesCreatureAndConsumesFood() {
        int initialFood = creature.food();
        new CreatureAi(creature);

        creature.moveBy(1, 0, 0);

        assertEquals(2, creature.x);
        assertEquals(1, creature.y);
        assertEquals(0, creature.z);
        assertEquals(initialFood - 1, creature.food());
    }

    @Test
    void moveBy_wall_doesNotMoveAndConsumesFood() {
        tiles[2][1][0] = Tile.WALL;
        int initialFood = creature.food();
        new CreatureAi(creature);

        creature.moveBy(1, 0, 0);

        assertEquals(1, creature.x);
        assertEquals(1, creature.y);
        assertEquals(0, creature.z);
        assertEquals(initialFood - 1, creature.food());
    }

    @Test
    void moveBy_worldBoundary_doesNotMoveAndConsumesFood() {
        creature.x = 0;
        int initialFood = creature.food();
        new CreatureAi(creature);

        creature.moveBy(-1, 0, 0);

        assertEquals(0, creature.x);
        assertEquals(1, creature.y);
        assertEquals(0, creature.z);
        assertEquals(initialFood - 1, creature.food());
    }

    @Test
    void moveBy_occupiedTileAttacksWithoutMoving() {
        Creature defender = new Creature(world, 'g', Color.RED, "goblin", 50, 0, 3);
        defender.x = 2;
        defender.y = 1;
        defender.z = 0;
        new CreatureAi(creature);
        new CreatureAi(defender);
        world.add(creature);
        world.add(defender);
        int initialFood = creature.food();
        int initialDefenderHp = defender.hp();

        creature.moveBy(1, 0, 0);

        int damage = initialDefenderHp - defender.hp();
        assertEquals(1, creature.x);
        assertEquals(1, creature.y);
        assertTrue(damage >= 1);
        assertTrue(damage <= creature.attackValue() - defender.defenseValue());
        assertEquals(initialFood - 3, creature.food());
    }

    @Test
    void meleeAttack_oneHpVictim_killsAwardsXpAndLeavesCorpse() {
        Creature victim = new Creature(world, 'v', Color.RED, "victim", 10, 0, 9);
        victim.x = 2;
        victim.y = 1;
        victim.z = 0;
        victim.modifyHp(-9, "Test setup");
        new CreatureAi(creature);
        new CreatureAi(victim);
        int initialFood = creature.food();

        creature.meleeAttack(victim);

        assertTrue(victim.hp() <= 0);
        assertEquals("Killed by a josh", victim.causeOfDeath());
        assertEquals(18, creature.xp());
        assertEquals(initialFood - 2, creature.food());
        assertNotNull(world.item(2, 1, 0));
        assertEquals("victim corpse", world.item(2, 1, 0).name());
    }

    @Test
    void testModifyHpNonFatalDamage() {
        creature.modifyHp(-10, "Nonfatal Damage");

        assertEquals(90, creature.hp());
        assertEquals("Nonfatal Damage", creature.causeOfDeath());
    }

    @Test
    void testModifyHpFatalDamage() {
        creature.modifyHp(-100, "Fatal Damage");

        assertEquals(0, creature.hp());
        assertEquals("Fatal Damage", creature.causeOfDeath());
    }

    @Test
    void testFatalDamageCreatesCorpse() {
        creature.modifyHp(-100, "Fatal Damage");

        Item corpse = world.item(1, 1, 0);

        assertNotNull(corpse, "Fatal damage should create a corpse");
        assertEquals("josh corpse", corpse.name());
    }
}

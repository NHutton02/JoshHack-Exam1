package game;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CreatureTest {

    private World world;
    private Creature creature;
@BeforeEach 

void setUp() { 

    Tile[][][] tiles = new Tile[3][3][1]; 

 

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
    void testFatalDamageCreatesCorpse(){

        creature.modifyHp(-100, "Fatal Damage");

        Item corpse = world.item(1,1,0);

        assertNotNull(corpse, "Fatal damage should create a corpse");

        assertEquals("josh corpse", corpse.name());




    }
    
    }
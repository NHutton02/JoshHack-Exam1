package game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CombatRulesTest {
    
@ParameterizedTest(name = "{index}: start{0}, amount={1}, expectedHP={2}, dead={3}")
@CsvSource ({

        "50,   0,  50, false",
        "40,  10,  50, false",
        "98,   1,  99, false",
        "98,   2, 100, false",
        "98,   3, 100, false",
        "10,  -8,   2, false",
        "10,  -9,   1, false",
        "10, -10,   0, true",
        "10, -11,  -1, true"
    })

    void testCombatRules(
        int currentHp,
        int amount,
        int expectedHp,
        boolean expectedDead) {

            int actualHp =
                CombatRules.calculateNewHp(currentHp, 100, amount);

                assertEquals(expectedHp, actualHp);
                assertEquals(expectedDead, CombatRules.isDead(actualHp));

        }
    }



























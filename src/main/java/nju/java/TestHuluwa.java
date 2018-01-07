package nju.java;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestHuluwa {
    @Test
    public void testDie() {
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < 7; ++i)
            players.add(new Calabash());
        players.add(new Grandpa());
        players.add(new Snake());
        players.add(new Frog());
        players.add(new Scorpion());

        Player good = new Grandpa();
        Player bad = new Scorpion();

        for (Player i : players)
            i.setMustDie(1); // must die if fights
        for (Player i : players) {
            if (i.isGoodKind()) {
                assertTrue(i.getDeadProb(bad));
                assertFalse(i.getDeadProb(good));
            } else {
                assertTrue(i.getDeadProb(good));
                assertFalse(i.getDeadProb(bad));
            }
        }

        for (Player i : players)
            i.setMustDie(-1); // never die
        for (Player i : players) {
            if (i.isGoodKind())
                assertFalse(i.getDeadProb(bad));
            else
                assertFalse(i.getDeadProb(good));
        }
    }

    @Test
    public void testPos() {
        Field field = new Field();
        field.initWorld();
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 5; ++j) {
                Player p = field.getWho(i, j, 1, 1);
                if (p != null) {
                    assertTrue(field.getWho(p.getGridX(), p.getGridY(), p.getWidth(), p.getHeight()) == p);
                }
            }
        }
    }
}

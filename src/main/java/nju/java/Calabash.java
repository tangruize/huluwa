package nju.java;

public class Calabash extends Player {
    private static int seq = 0;
    private static final String PREFIX = "calabash";
    private static final String SUFFIX = ".png";
    private int number;
    private static String[] color = {
            "Red", "Orange", "Yellow", "Green", "Cyan", "Blue", "Purple"
    };

    public Calabash() {
        super(PREFIX + (seq % 7 + 1) + SUFFIX, true);
        number = seq++ % 7 + 1;
    }

    public int getCalabashRank() {
        return number;
    }

    @Override
    public boolean getDeadProb(Player p) {
        if (isGoodKind() == p.isGoodKind())
            return false;
        if (getMustDie() < 0) {
            setMustDie(0);
            return false;
        } else if (getMustDie() > 0) {
            setMustDie(0);
            return true;
        }
        if (p instanceof Frog)
            return Math.random() < 0.15; //15%
        else if (p instanceof Snake)
            return Math.random() < 0.6; // 60%
        else if (p instanceof Scorpion)
            return Math.random() < 0.7; //70%
        return Math.random() < 0.4; //40%
    }

    @Override
    public String getName() {
        return "Calabash" + number + "(" + color[number - 1] + ")";
    }
}

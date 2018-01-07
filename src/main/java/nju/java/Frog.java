package nju.java;

public class Frog extends Player {
    Frog() {
        super("frog.png", false);
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
        if (p instanceof Grandpa)
            return Math.random() < 0.1; //10%
        else if (p instanceof Calabash)
            return Math.random() < 0.8; //80%
        return Math.random() < 0.4; //40%
    }

    @Override
    public String getName() {
        return "Frog";
    }
}

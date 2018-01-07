package nju.java;

public class Scorpion extends Player {
    Scorpion() {
        super("scorpion.png", false);
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
            return Math.random() < 0.05; //5%
        else if (p instanceof Calabash)
            return Math.random() < 0.15; //10%
        return Math.random() < 0.4; //40%
    }

    @Override
    public String getName() {
        return "Scorpion king";
    }
}

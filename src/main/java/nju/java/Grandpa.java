package nju.java;

public class Grandpa extends Player {
    Grandpa() {
        super("grandpa.png", true);
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
            return Math.random() < 0.7; //70%
        else if (p instanceof Snake)
            return Math.random() < 0.10; //10%
        else if (p instanceof Scorpion)
            return Math.random() < 0.50; //50%
        return Math.random() < 0.4; // 40%
    }

    @Override
    public String getName() {
        return "Grandpa";
    }
}

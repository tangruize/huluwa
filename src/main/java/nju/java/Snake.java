package nju.java;

public class Snake extends Player {
    Snake() {
        super("snake.png", false);
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
            return Math.random() < 0.7; //70%
        else if (p instanceof Calabash)
            return Math.random() < 0.1; //10%
        return Math.random() < 0.4; //40%
    }

    @Override
    public String getName() {
        return "Snake Queen";
    }
}

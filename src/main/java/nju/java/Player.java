package nju.java;

import java.awt.Image;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import javax.swing.ImageIcon;

public class Player extends Thing2D implements Runnable {
    private Field field;
    private final boolean isGoodKind;
    private static int nextBornNumber = 0;
    private int bornNumber;
    private int height, width;
    private boolean autoMode = true;
    private boolean isEnabled = false;
    private boolean isDead = false;
    private String face;
    private int mustDie = 0;

    private void initFace(String face) {
        this.face = face;
        URL loc = this.getClass().getClassLoader().getResource(face);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
        height = image.getHeight(null) / 100;
        width = image.getWidth(null) / 80;
    }

    public int getMustDie() {
        return mustDie;
    }

    public void setMustDie(int mustDie) {
        this.mustDie = mustDie;
    }

    public boolean getDeadProb(Player p) {
        return isGoodKind() != p.isGoodKind() && Math.random() < 0.5; //50%
    }

    public String getName() {
        return "Player";
    }

    public void setDead() {
        isDead = true;
        initFace("dead_" + face);
    }

    public void setAutoMode(boolean autoMode) {
        this.autoMode = autoMode;
    }

    public int getHeight() {
        return height;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setEnabled() {
        isEnabled = true;
    }

    public int getWidth() {
        return width;
    }

    public Player(int x, int y, Field field, String face, boolean isGoodKind) {
        super(x, y);

        this.field = field;
        this.isGoodKind = isGoodKind;

        bornNumber = nextBornNumber++;

        initFace(face);
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isGoodKind() {
        return isGoodKind;
    }

    public Player(String face, boolean isGoodKind) {
        super(LEFT_OFFSET, TOP_OFFSET);

        this.isGoodKind = isGoodKind;
        bornNumber = nextBornNumber++;

        initFace(face);
    }

    public void move(int x, int y) {
        int nx = this.getGridX() + x;
        int ny = this.getGridY() + y;
        boolean xOk = checkGridX(nx, width);
        boolean yOk = checkGridY(ny, height);
        if (!xOk && !yOk)
            return;
        if (!xOk)
            nx = getGridX();
        if (!yOk)
            ny = getGridY();
        Lock lock = field.getLock();
        lock.lock();
        try {
            if (isDead)
                return;
            field.setWho(getGridX(), getGridY(), width, height, null);
            Player p = field.getWho(nx, ny, width, height);
            field.beforeLog(this, p);
            if (p == null) {
                this.setGridX(nx, width);
                this.setGridY(ny, height);
                field.setWho(nx, ny, width, height, this);
            } else {
                field.setWho(getGridX(), getGridY(), width, height, this);
                field.fight(this, p);
            }
            field.log(x, y);
        } finally {
            lock.unlock();
        }
    }

    public int getBornNumber() {
        return bornNumber;
    }

    public void run() {
        Random rand = new Random();
        while (!Thread.interrupted()) {
            if (autoMode && !isDead)
                this.move(rand.nextInt(3) - 1, rand.nextInt(3) - 1);
            try {
                Thread.sleep(rand.nextInt(1000) + 80);
                this.field.repaint();
            } catch (Exception e) {
                return;
            }
        }
    }
}
package nju.java;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;

public class Field extends JPanel {
    private static final int OFFSET = 30;

    private Tile tile;
    private List<Player> players = new ArrayList<Player>();
    private List<Thread> threads = new ArrayList<Thread>();

    private int w = 1349 + OFFSET;
    private int h = 673 + OFFSET;
    private int level = 0;

    private boolean completed = false;
    private Lock lock = new ReentrantLock();
    private String fightInfo = "No fight";

    private Record record = new Record();
    private boolean aDie, bDie;
    private boolean isPlaybackMode = false;
    private int aIndex, bIndex;
    private Thread playback = null;

    private String[] levels =
            {
                    ".c......f.\n" +
                            "c.......f.\n" +
                            ".g......f.\n" +
                            "c.......f.\n" +
                            ".c......f.\n",

                    "....c...f.\n" +
                            "...c....s.\n" +
                            "..g.......\n" +
                            ".c......f.\n" +
                            "c.......f.\n",

                    "........f.\n" +
                            "..c....s..\n" +
                            "cgc.......\n" +
                            "cccc....S.\n" +
                            "..........\n",

                    "..c..f..S.\n" +
                            ".c.c..f...\n" +
                            "c.g.c..f..\n" +
                            "..c...f.s.\n" +
                            "..c..f....\n",

                    "..c..f..s.\n" +
                            ".c.c.f....\n" +
                            "g...c..S..\n" +
                            ".c.c.s....\n" +
                            "..c.....f.\n"

            };
    private int goodLeft = 0, badLeft = 0;

    public void fight(Player a, Player b) {
        aDie = bDie = false;
        if (a.isGoodKind() == b.isGoodKind())
            return;
        fightInfo = a.getName() + " VS " + b.getName();
        aDie = !doFight(a, b);
        bDie = !doFight(b, a);
        if (aDie && bDie)
            fightInfo += ", both are dead";
        else if (aDie)
            fightInfo += ", " + a.getName() + " is dead";
        else if (bDie)
            fightInfo += ", " + b.getName() + " is dead";
        else
            fightInfo += ", nobody is dead";
        if (aDie || bDie)
            repaint();
    }

    public void beforeLog(Player a, Player b) {
        if (!isPlaybackMode) {
            aIndex = players.lastIndexOf(a);
            bIndex = players.lastIndexOf(b);
            if (b == null)
                aDie = bDie = false;
        }
    }

    public void log(int x, int y) {
        if (!isPlaybackMode) {
            record.write(aIndex + " " + bIndex + " "
                    + x + " " + y + " "
                    + aDie + " " + bDie);
        }
    }

    private boolean doFight(Player a, Player b) {
        if (a.getDeadProb(b)) {
            a.setDead();
            setWho(a.getGridX(), a.getGridY(), a.getWidth(), a.getHeight(), null);
            if (a.isGoodKind())
                --goodLeft;
            else
                --badLeft;
            players.remove(a);
            int i = 0;
            for (; i < players.size(); ++i) {
                if (players.get(i).isDead())
                    continue;
                players.add(i, a);
                break;
            }
            if (i == players.size())
                players.add(i - 1, a);
            return false;
        }
        return true;
    }

    private Player[][] position = new Player[10][5];

    public Player getWho(int x, int y, int width, int height) {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (position[i + x][j + y] != null)
                    return position[i + x][j + y];
            }
        }
        return null;
    }

    public void setWho(int x, int y, int width, int height, Player w) {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                position[i + x][j + y] = w;
            }
        }
    }

    public Lock getLock() {
        return lock;
    }

    public Field() {

        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();
    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }

    public void playBack(final boolean isContinue) {
        if (!record.initInput())
            return;
        if (isPlaybackMode) {
            if (playback != null) {
                playback.interrupt();
                try {
                    playback.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        record.readInt();
        int l = record.readInt();
        if (l == -1) {
            return;
        }
        fightInfo = "No fight";
        clearThread();
        isPlaybackMode = true;
        level = l;
        tile = null;
        players.clear();
        initWorld();
        repaint();
        class Playback extends Thread {
            @Override
            public void run() {
                int time, a, b, x, y;
                boolean aDie = false, bDie = false;
                while (record.hasNext() && !Thread.interrupted()) {
                    time = record.readInt();
                    a = record.readInt();
                    b = record.readInt();
                    x = record.readInt();
                    y = record.readInt();
                    try {
                        aDie = record.readBoolean();
                        bDie = record.readBoolean();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (time > 0 && !isContinue)
                            TimeUnit.MILLISECONDS.sleep(time);
                    } catch (InterruptedException e) {
                        break;
                    }
                    Player aPlayer = players.get(a);
                    Player bPlayer = null;
                    if (b >= 0)
                        bPlayer = players.get(b);
                    aPlayer.setMustDie(aDie ? 1 : -1);
                    if (bPlayer != null)
                        bPlayer.setMustDie(bDie ? 1 : -1);
                    aPlayer.move(x, y);
                    repaint();
                }
                isPlaybackMode = false;
            }
        }
        playback = new Playback();
        playback.start();
    }

    private void initPos() {
        int x = 0, y = 0, c = 0, s = 7, f = 10;
        goodLeft = badLeft = 0;
        String l = levels[level];
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 5; ++j) {
                position[i][j] = null;
            }
        }
        for (int i = 0; i < l.length(); ++i) {
            char w = l.charAt(i);
            if (w == '\n') {
                x = 0;
                ++y;
                continue;
            }
            Player curr = null;
            switch (w) {
                case 'c':
                    curr = players.get(c++);
                    break;
                case 'g':
                    curr = players.get(players.size() - 1);
                    break;
                case 'f':
                    curr = players.get(f++);
                    break;
                case 's':
                    curr = players.get(s++);
                    break;
                case 'S':
                    curr = players.get(9);
                    break;
            }
            if (curr != null) {
                curr.setGridX(x, curr.getWidth());
                curr.setGridY(y, curr.getHeight());
                curr.setEnabled();
                if (curr.isGoodKind())
                    ++goodLeft;
                else
                    ++badLeft;
                curr.move(0, 0);
                threads.add(new Thread(curr));
            }
            ++x;
        }
    }

    public final void initWorld() {

        tile = new Tile(OFFSET, OFFSET);

        for (int i = 0; i < 7; ++i)
            players.add(new Calabash());

        players.add(new Snake());
        players.add(new Snake());
        players.add(new Scorpion());
        for (int i = 0; i < 5; ++i)
            players.add(new Frog());
        players.add(new Grandpa());

        for (Player i : players) {
            i.setField(this);
        }
        initPos();
    }

    public void start() {
        if (completed)
            return;
        if (goodLeft == 0 || badLeft == 0)
            return;
        completed = true;
        if (record.init(true)) {
            record.write(String.valueOf(level));
        }
        for (Thread i : threads) {
            i.start();
        }
    }

    public void buildWorld(Graphics g) {

        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        ArrayList world = new ArrayList();
        world.add(tile);

        world.addAll(players);


        for (int i = 0; i < world.size(); i++) {

            Thing2D item = (Thing2D) world.get(i);

            if (item instanceof Player && !((Player) item).isEnabled())
                continue;

            g.drawImage(item.getImage(), item.x(), item.y(), this);
        }
        g.setColor(new Color(0, 0, 0));
        if (completed) {
            String msg = "Left: Calabash: " + goodLeft + ", Evil: " + badLeft;
            if (goodLeft == 0)
                msg = "Evil wins ... ";
            else if (badLeft == 0)
                msg = "Calabash wins!";
            g.drawString("Level " + (level + 1) + ", " + msg, 25, 20);
            if (goodLeft == 0 || badLeft == 0) {
                clearThread();
                completed = false;
            }
        } else if (goodLeft == 0) {
            g.drawString("Press 'r' to restart game!", 25, 20);
        } else if (badLeft == 0) {
            g.drawString("Press 'r' to go to next level!", 25, 20);
        } else {
            g.drawString("Press 's' to start game!", 25, 20);
        }
//        fightInfo = "No fight";
        g.drawString(fightInfo, 25, 720);
        if (isPlaybackMode) {
            g.drawString("Playback", 1330, 20);
        }
    }

    public boolean loadRecordFile() {
        FileDialog d1 = new FileDialog(new JFrame("FileDialog"), "Open File", FileDialog.LOAD);
        d1.setVisible(true);
        record.setInputFileName(d1.getDirectory(), d1.getFile());
        return d1.getFile() != null;
    }

    public void saveRecordFile() {
        FileDialog d1 = new FileDialog(new JFrame("FileDialog"), "Save File", FileDialog.SAVE);
        d1.setVisible(true);
        record.setFileName(d1.getDirectory(), d1.getFile());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        buildWorld(g);
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            Player player = players.get(players.size() - 1);

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_R) {
                restartLevel();
            } else if (!isPlaybackMode && (key == KeyEvent.VK_S || key == KeyEvent.VK_SPACE)) {
                start();
                if (key == KeyEvent.VK_S)
                    player.setAutoMode(true);
            } else if (!completed) {
                if (key == KeyEvent.VK_L) {
                    if (loadRecordFile())
                        playBack(false);
                } else if (key == KeyEvent.VK_C) {
                    if (loadRecordFile())
                        playBack(true);
                } else if (!isPlaybackMode && key == KeyEvent.VK_W) {
                    saveRecordFile();
                }
            } else if (player instanceof Grandpa) {
                if (key == KeyEvent.VK_LEFT) {
                    player.setAutoMode(false);
                    player.move(-1, 0);
                } else if (key == KeyEvent.VK_RIGHT) {
                    player.setAutoMode(false);
                    player.move(1, 0);
                } else if (key == KeyEvent.VK_UP) {
                    player.setAutoMode(false);
                    player.move(0, -1);
                } else if (key == KeyEvent.VK_DOWN) {
                    player.setAutoMode(false);
                    player.move(0, 1);
                }
            }

            repaint();
        }
    }

    private void clearThread() {
        for (Thread i : threads)
            i.interrupt();
        threads.clear();
    }


    public void restartLevel() {
        if (goodLeft == 0)
            level = 0;
        else if (badLeft == 0)
            level = (level + 1) % levels.length;
        if (isPlaybackMode && playback != null)
            playback.interrupt();
        fightInfo = "No fight";
        isPlaybackMode = false;
        tile = null;
        clearThread();
        players.clear();
        initWorld();
        record.init();
        if (completed) {
            completed = false;
        }
    }
}

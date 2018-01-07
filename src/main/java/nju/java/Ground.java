package nju.java;


import javax.swing.JFrame;

public final class Ground extends JFrame {

    private final int OFFSET = 30;


    public Ground() {
        InitUI();
    }

    public void InitUI() {
        Field field = new Field();
        add(field);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(field.getBoardWidth() + OFFSET,
                field.getBoardHeight() + 2 * OFFSET);
        setLocationRelativeTo(null);
        setTitle("Calabash Brothers Fight Evils");
    }

}
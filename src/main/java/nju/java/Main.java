package nju.java;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 1)
            Record.setArgFileName(args[0]);
        Ground ground = new Ground();
        ground.setVisible(true);
    }
}

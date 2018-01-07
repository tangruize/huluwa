package nju.java;

import java.awt.Image;

public class Thing2D {
    protected static final int OFFSET = 30;

    protected static final int LEFT_OFFSET = 260 + OFFSET;
    protected static final int TOP_OFFSET = 100 + OFFSET;
    protected static final int RIGHT_OFFSET = 1070 + OFFSET;
    protected static final int BOTTOM_OFFSET = 600 + OFFSET;

    protected static final int HORIZONTAL_SPACE = 81;
    protected static final int VERTICAL_SPACE = 100;
    protected static final int GRID_HORIZONTAL = 80;
    protected static final int GRID_VERTICAL = 100;

    private int x;
    private int y;
    private Image image;

    public Thing2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image img) {
        image = img;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean checkGridX(int x, int width) {
        int nx = x * HORIZONTAL_SPACE + LEFT_OFFSET;
        return nx + width * GRID_HORIZONTAL <= RIGHT_OFFSET && nx >= LEFT_OFFSET;
    }

    public boolean checkGridY(int y, int height) {
        int ny = y * VERTICAL_SPACE + TOP_OFFSET;
        return ny + height * GRID_VERTICAL <= BOTTOM_OFFSET && ny >= TOP_OFFSET;
    }

    public void setGridX(int x, int width) {
        if (checkGridX(x, width))
            setX(x * HORIZONTAL_SPACE + LEFT_OFFSET);
    }

    public void setGridY(int y, int height) {
        if (checkGridY(y, height))
            setY(y * VERTICAL_SPACE + TOP_OFFSET);
    }


    public int getGridX() {
        return (x - LEFT_OFFSET) / HORIZONTAL_SPACE;
    }

    public int getGridY() {
        return (y - TOP_OFFSET) / VERTICAL_SPACE;
    }
}
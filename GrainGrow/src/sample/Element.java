package sample;



public class Element {
    private int colorNum = 0;
    private double x = 0;
    private double y = 0;

    public Element(int colorNum, double x, double y) {
        this.colorNum = colorNum;
        this.x = x;
        this.y = y;
    }

    public Element() {
    }


    public int getColorNum() {
        return colorNum;
    }

    public void setColorNum(int colorNum) {
        this.colorNum = colorNum;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}

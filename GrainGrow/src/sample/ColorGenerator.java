package sample;

import java.util.Random;


public class ColorGenerator {
    private Random rand = new Random();

    public ColorGenerator() {
    }


    public String generateColor(int r1, int r2, int g1, int g2, int b1, int b2) {
        String newColor = "";
        int color = rand.nextInt(r1);
        newColor += Integer.toHexString(color);
        color = rand.nextInt(r2);
        newColor += Integer.toHexString(color);
        color = rand.nextInt(g1);
        newColor += Integer.toHexString(color);
        color = rand.nextInt(g2);
        newColor += Integer.toHexString(color);
        color = rand.nextInt(b1);
        newColor += Integer.toHexString(color);
        color = rand.nextInt(b2);
        newColor += Integer.toHexString(color);

        return newColor;
    }

}

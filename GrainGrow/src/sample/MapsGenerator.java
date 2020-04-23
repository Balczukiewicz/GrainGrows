package sample;

import java.util.Map;


public class MapsGenerator {

    public MapsGenerator() {
    }
// http://zasoby.open.agh.edu.pl/~10smgzyl/index98b6.html?module=articles&action=show&name=dyskretne-ca
//    blad z pentagonalnymi

    public void generateMapOfRules(Map mapOfRules){
        Integer[][] vonNeumann = new Integer[][]{{0, 1, 0}, {1, 0, 1}, {0, 1, 0}};
        mapOfRules.put("vonNeumann", vonNeumann);

        Integer[][] moore = new Integer[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        mapOfRules.put("Moore", moore);

        Integer[][] pent1 = new Integer[][]{{1, 1, 0}, {1, 0, 0}, {1, 1, 0}};
        mapOfRules.put("Pent1", pent1);

        Integer[][] pent2 = new Integer[][]{{0, 1, 1}, {0, 0, 1}, {0, 1, 1}};
        mapOfRules.put("Pent2", pent2);

        Integer[][] pent3 = new Integer[][]{{0, 0, 0}, {1, 0, 1}, {1, 1, 1}};
        mapOfRules.put("Pent3", pent3);

        Integer[][] pent4 = new Integer[][]{{1, 1, 1}, {1, 0, 1}, {0, 0, 0}};
        mapOfRules.put("Pent4", pent4);

        Integer[][] hex1 = new Integer[][]{{0, 1, 1}, {1, 0, 1}, {1, 1, 0}};
        mapOfRules.put("Hex1", hex1);

        Integer[][] hex2 = new Integer[][]{{1, 1, 0}, {1, 0, 1}, {0, 1, 1}};
        mapOfRules.put("Hex2", hex2);
    }

}

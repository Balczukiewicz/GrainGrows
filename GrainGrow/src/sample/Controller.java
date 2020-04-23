package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.*;

import java.util.stream.Collectors;

import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class Controller {
    private static Controller controller=null;
    private ColorGenerator colorGenerator = new ColorGenerator();
    private MapsGenerator mapGenerator = new MapsGenerator();
    public Canvas canvas;
    public TextField width, length;
    public TextField rowGrain, colGrain, radiusField, nogField;
    public CheckBox periodicCB, absorbingCB;
    public CheckBox vonNeumannCB, mooreCB, hexCB, pentCB;
    private Timeline timeline;
    private ErrorDisplay errorDisplay = new ErrorDisplay();
    private int wSize, lSize;
    private double sizeX, sizeY;
    private Random rand = new Random();
    public Map<String, Integer> mapOfColors = new HashMap<>();
    private Map<String, Integer[][]> mapOfRules = new HashMap<>();
    private Integer mapKey = 1;
    private Element[][] arrayCurr, arrayNext;



    public Controller() {

    }

    public void loadData() {
        try {
            wSize = Integer.parseInt(width.getText());
            lSize = Integer.parseInt(length.getText());
            if (Integer.parseInt(radiusField.getText()) < 0 || Integer.parseInt(radiusField.getText()) > wSize / 2) {
                throw new Exception();
            }
            mapGenerator.generateMapOfRules(mapOfRules);
        } catch (Exception e) {
            errorDisplay.showAlert("Enter correct values!!");
        }
        if (wSize < 0 || lSize < 0) {
            errorDisplay.showAlert("Width and lenght must be greater than 0");
            if (wSize < 0) {
                wSize = 1;
            }
            if (lSize < 0) {
                lSize = 1;
            }
        }

        if (wSize < lSize) {
            sizeX =  (canvas.getWidth() / lSize);
            sizeY =  (canvas.getWidth() / lSize);
        } else if (wSize > lSize) {
            sizeX =  (canvas.getHeight() / wSize);
            sizeY =  (canvas.getHeight() / wSize);
        } else if (wSize == lSize) {
            sizeX =  (canvas.getHeight() / wSize);
            sizeY =  (canvas.getWidth() / lSize);
        }

        arrayCurr = new Element[lSize][wSize];
        arrayNext = new Element[lSize][wSize];

        for (int i = 0; i < lSize; i++) {
            for (int j = 0; j < wSize; j++) {
                arrayCurr[i][j] = new Element();
                arrayNext[i][j] = new Element();
            }
        }
    }
    // rysowanie

    public void drawOneGrain(String hexColor, Map mapOfColors, GraphicsContext gc, Element[][] arrayCurr,
                             int x1, int y1, double xElSize, double yElSize) {
        mapOfColors.putIfAbsent(hexColor, mapKey++);
        gc.setFill(Color.web(hexColor));
        arrayCurr[y1][x1].setColorNum(Integer.parseInt(String.valueOf(mapOfColors.get(hexColor))));
        gc.fillRect(x1 * xElSize, y1 * yElSize, xElSize, yElSize);
    }

    private void drawGrains(int xSizeGrid, int ySizeGrid, Element[][] arrayCurr) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1.0);
        for (int i = 0; i < ySizeGrid; i++) {
            for (int j = 0; j < xSizeGrid; j++) {
                int tmpVal = arrayCurr[i][j].getColorNum();
                if (tmpVal > 0) {
                    String s = mapOfColors.keySet().stream().filter(key -> mapOfColors.get(key) == tmpVal
                    ).collect(Collectors.joining());
                    gc.setFill(Paint.valueOf(s));
                    gc.fillRect(j * sizeX, i * sizeY, sizeX, sizeY);
                }
            }
        }
    }

    private void coloringArrayNext(int i, int j, Map<Integer, Integer> tmpMap) {
        if (tmpMap.size() != 0) {
            int numberOfColor = Integer.parseInt(String.valueOf(tmpMap
                    .entrySet()
                    .stream()
                    .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
                    .get()
                    .getKey()));

            arrayNext[i][j].setColorNum(numberOfColor);
        }
    }


    // obsluga przyciskow i CB

    public void showbtn() {
        loadData();
    }


    public void startBtn() {

        try {
            if (wSize < 1 || lSize < 1)
                throw new Exception();
            setGrowthTimeline();
            timeline.play();

        } catch (Exception e) {
            ErrorDisplay.showAlert("Enter width and height");
        }
    }

    public void stopBtn() {
        timeline.stop();
    }

    public void clearBtn() {
        try {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setLineWidth(1.0);
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            for (Element[] elements : arrayCurr) {
                for (Element e : elements) {
                    e.setColorNum(0);
                    e.setY(0);
                    e.setX(0);
                }
            }
        } catch (Exception e) {
            errorDisplay.showAlert("It's empty");
        }

    }

    public void radiusBtn() {

        try {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setLineWidth(1.0);
            int radius = Integer.parseInt(radiusField.getText());
            int elements = Integer.parseInt(nogField.getText());
            if (radius < 0 || elements < 0) {
                throw new Exception();
            }
            for (int i = 0; i < elements; i++) {
                int x1 = rand.nextInt(wSize - 2 * radius) + radius;
                int y1 = rand.nextInt(lSize - 2 * radius) + radius;

                if (arrayCurr[y1][x1].getColorNum() == 0 && arrayCurr[y1][x1].getX() == 0 && arrayCurr[y1][x1].getY() == 0) {

                    for (int y = y1 - radius; y <= y1 + radius; y++) {
                        for (int x = x1 - radius; x <= x1 + radius; x++) {
                            if (Math.sqrt(Math.abs(((x - x1) * (x - x1)) + (y - y1) * (y - y1))) <= (double) radius) {
                                arrayCurr[y][x].setColorNum(-1);
                            }
                        }
                    }

                    String hexColor = colorGenerator.generateColor(16, 16, 16, 16, 16, 16);
                    drawOneGrain(hexColor, mapOfColors, gc, arrayCurr, x1, y1, sizeX, sizeY);
                }
            }
        } catch (Exception e) {
            errorDisplay.showAlert("Enter radius and elements");
        }
    }

    public void homoBtn() {
        try {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setLineWidth(1.0);
            int inRow = Integer.parseInt(rowGrain.getText());
            int inCol = Integer.parseInt(colGrain.getText());
            if (inCol < 0 || inRow < 0)
                throw new Exception();
            int diffX =  wSize / inRow;
            int diffY =  lSize / inCol;

            for (int i = 0; i < inCol; i++) {
                for (int j = 0; j < inRow; j++) {

                    if (arrayCurr[i * diffY][j * diffX].getColorNum() <= 0) {
                        String hexColor = colorGenerator.generateColor(16, 16, 16, 16, 16, 16);
                        drawOneGrain(hexColor, mapOfColors, gc, arrayCurr, j * diffX, i * diffY, sizeX, sizeY);
                    }
                }
            }
        } catch (Exception e) {
            errorDisplay.showAlert("Enter columns and rows!");
        }
    }

    public void randomBtn() {

        try {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            int elements = Integer.parseInt(nogField.getText());
            if (elements < 0) {
                throw new Exception();
            }
            for (int i = 0; i < elements; i++) {


                int x1 = rand.nextInt(wSize);
                int y1 = rand.nextInt(lSize);

                if (arrayCurr[y1][x1].getColorNum() <= 0) {

                    String hexColor = colorGenerator.generateColor(16, 16, 16, 16, 16, 16);
                    drawOneGrain(hexColor, mapOfColors, gc, arrayCurr, x1, y1, sizeX, sizeY);
                    mapOfColors.putIfAbsent(hexColor, mapKey++);
                }
            }
        } catch (Exception e) {
            errorDisplay.showAlert("Enter columns and rows!");
        }
    }

    public void onMouseClickedListener() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(event -> {
            double tmpSizeX = 0;
            double tmpSizeY = 0;

            if (wSize > lSize) {
                tmpSizeX = canvas.getHeight() / wSize;
                tmpSizeY = canvas.getHeight() / wSize;
            } else if (wSize < lSize) {
                tmpSizeX = canvas.getWidth() / lSize;
                tmpSizeY = canvas.getWidth() / lSize;
            } else if (tmpSizeX == tmpSizeY) {
                tmpSizeX = canvas.getHeight() / wSize;
                tmpSizeY = canvas.getWidth() / lSize;
            }

            double finalX = tmpSizeX;
            double finalY = tmpSizeY;
            String hexColor = colorGenerator.generateColor(16, 16, 16, 16, 16, 16);
            gc.setLineWidth(1.0);
            gc.setFill(Color.web(hexColor));
            mapOfColors.putIfAbsent(hexColor, mapKey++);
            int tmpX = (int) (event.getX() / finalX);
            int tmpY = (int) (event.getY() / finalY);
            try {
                if (arrayCurr[tmpY][tmpX].getColorNum() != 0) {
                    gc.setFill(Color.WHITE);
                    arrayCurr[tmpY][tmpX].setColorNum(0);
                } else if (arrayCurr[tmpY][tmpX].getColorNum() == 0) {
                    gc.setFill(Color.web(hexColor));
                    arrayCurr[tmpY][tmpX].setColorNum(Integer.parseInt(String.valueOf(mapOfColors.get(hexColor))));
                }
                gc.fillRect(finalX * tmpX, finalY * tmpY, finalX, finalY);

            } catch (Exception e) {
                errorDisplay.showAlert("Ups! Smth happened");
            }
        });


    }



    private void selectPeriodicRule() {
        if (vonNeumannCB.isSelected()) {
            rewriteArray(arrayCurr, arrayNext, wSize,lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    calculatePeriodic(mapOfRules.get("vonNeumann"),arrayCurr, wSize, lSize, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);

            drawGrains(wSize, lSize, arrayNext);
            mooreCB.setSelected(false);
            pentCB.setSelected(false);
            hexCB.setSelected(false);
        } else if (mooreCB.isSelected()) {
            rewriteArray(arrayCurr, arrayNext, wSize,lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    calculatePeriodic(mapOfRules.get("Moore"),arrayCurr, wSize, lSize, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);
            drawGrains(wSize, lSize, arrayNext);
            vonNeumannCB.setSelected(false);
            pentCB.setSelected(false);
            hexCB.setSelected(false);
        } else if (pentCB.isSelected()) {


            rewriteArray(arrayCurr, arrayNext, wSize, lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    String nameTmp = "Pent" + String.valueOf(rand.nextInt(4) + 1);
                    calculatePeriodic(mapOfRules.get(nameTmp), arrayCurr, wSize, lSize, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);
            drawGrains(wSize, lSize, arrayNext);
            mooreCB.setSelected(false);
            vonNeumannCB.setSelected(false);
            hexCB.setSelected(false);
        } else if (hexCB.isSelected()) {
            rewriteArray(arrayCurr, arrayNext, wSize, lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    String nameTmp = "Hex" + String.valueOf(rand.nextInt(2) + 1);
                    calculatePeriodic(mapOfRules.get(nameTmp), arrayCurr,wSize, lSize, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);
            drawGrains(wSize, lSize, arrayNext);
            mooreCB.setSelected(false);
            pentCB.setSelected(false);
            vonNeumannCB.setSelected(false);

        }
    }

    private void selectAbsorbingRule() {
        if (vonNeumannCB.isSelected()) {
            rewriteArray(arrayCurr, arrayNext, wSize,lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    calculateAbsorbing(mapOfRules.get("vonNeumann"), arrayCurr, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);

            drawGrains(wSize, lSize, arrayNext);
            mooreCB.setSelected(false);
            pentCB.setSelected(false);
            hexCB.setSelected(false);
        } else if (mooreCB.isSelected()) {
            rewriteArray(arrayCurr, arrayNext, wSize,lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    calculateAbsorbing(mapOfRules.get("Moore"), arrayCurr, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);
            drawGrains(wSize, lSize, arrayNext);
            vonNeumannCB.setSelected(false);
            pentCB.setSelected(false);
            hexCB.setSelected(false);
        } else if (pentCB.isSelected()) {

            rewriteArray(arrayCurr, arrayNext, wSize, lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    String nameTmp = "Pent" + String.valueOf(rand.nextInt(4) + 1);
                    calculateAbsorbing(mapOfRules.get(nameTmp), arrayCurr, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);

            drawGrains(wSize, lSize, arrayNext);
            mooreCB.setSelected(false);
            vonNeumannCB.setSelected(false);
            hexCB.setSelected(false);
        } else if (hexCB.isSelected()) {
            rewriteArray(arrayCurr, arrayNext, wSize, lSize);
            for (int i = 0; i < lSize; i++) {
                for (int j = 0; j < wSize; j++) {
                    String nameTmp = "Hex" + String.valueOf(rand.nextInt(2) + 1);
                    calculateAbsorbing(mapOfRules.get(nameTmp), arrayCurr, i, j);
                }
            }
            rewriteArray(arrayNext, arrayCurr, wSize, lSize);

            drawGrains(wSize, lSize, arrayNext);
            mooreCB.setSelected(false);
            pentCB.setSelected(false);
            vonNeumannCB.setSelected(false);
        }
    }

// os czasu

    private void setGrowthTimeline() {
        EventHandler<ActionEvent> eventHandler = event -> {
            if (absorbingCB.isSelected()) {
                periodicCB.setSelected(false);
                try {
                    selectAbsorbingRule();
                } catch (IllegalStateException e) {
                    timeline.stop();
                    errorDisplay.showAlert("Ups! Smth happened");

                }

            } else if (periodicCB.isSelected()) {
                absorbingCB.setSelected(false);
                selectPeriodicRule();
            }
        };
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
    }



// tworzenie i zamiana tablic


    private void rewriteArray(Element[][] A, Element[][] B, int sizeX, int sizeY) {
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                B[i][j].setColorNum(A[i][j].getColorNum());
                B[i][j].setX(A[i][j].getX());
                B[i][j].setY(A[i][j].getY());
            }
        }
    }



    private void createArrayNext(Element[][] tabOfStates, int i, int j, Map tmpMap) {
        if (tabOfStates[i][j].getColorNum() > 0) {
            if (tmpMap.get(tabOfStates[i][j].getColorNum()) == null) {
                tmpMap.put(tabOfStates[i][j].getColorNum(), 1);
            } else {
                tmpMap.put(tabOfStates[i][j].getColorNum(), 1 + (int) tmpMap.get(tabOfStates[i][j].getColorNum()));
            }
        }
    }

    // obliczanie dla periodycznego i absorbujacego


    private void calculateAbsorbing(Integer[][] ruleArr, Element[][] curr, int i, int j) {


        if (curr[i][j].getColorNum() <= 0) {
            Map<Integer, Integer> tmpMap = new HashMap<>();
            for (int k = 0; k < 3; k++) {
                for (int l = 0; l < 3; l++) {
                    if (ruleArr[k][l] == 1) {
                        int iTmp = -1;
                        int jTmp = -1;
                        if ((i - 1 + k) >= 0 && (i - 1 + k) < this.lSize) {
                            iTmp = i - 1 + k;
                        }
                        if ((j - 1 + l) >= 0 && (j - 1 + l) < this.wSize) {
                            jTmp = j - 1 + l;
                        }
                        if (iTmp > -1 && jTmp > -1) {
                            createArrayNext(curr, iTmp, jTmp, tmpMap);
                        }
                    }
                }
            }

            coloringArrayNext(i, j, tmpMap);
        }
    }

    private void calculatePeriodic(Integer[][] ruleArr, Element[][] curr, int sizeX, int sizeY, int i, int j) {

        if (curr[i][j].getColorNum() <= 0) {
            Map<Integer, Integer> tmpMap = new HashMap<>();
            for (int k = 0; k < 3; k++) {
                for (int l = 0; l < 3; l++) {
                    if (ruleArr[k][l] == 1) {
                        int iTmp = -1;
                        int jTmp = -1;
                        if ((i - 1 + k) < 0) {
                            iTmp = sizeY - 1;
                        } else if ((i - 1 + k) >= this.lSize) {
                            iTmp = k;
                        } else {
                            iTmp = i - 1 + k;
                        }
                        if ((j - 1 + l) < 0) {
                            jTmp = sizeX - 1;
                        } else if ((j - 1 + l) >= this.wSize) {
                            jTmp = l;
                        } else {
                            jTmp = j - 1 + l;
                        }
                        if (iTmp > -1 && jTmp > -1) {
                            createArrayNext(curr, iTmp, jTmp, tmpMap);
                        }
                    }
                }
            }

            coloringArrayNext(i, j, tmpMap);
        }
    }


}


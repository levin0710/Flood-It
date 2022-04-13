import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import javalib.worldcanvas.WorldCanvas;

//import javalib.worldcanvas.WorldCanvas;
import java.awt.Color;
import javalib.worldimages.*;

//random colors 
//printing the board 
//printing separate pieces
//represents a single cell
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;

  int size;

  // the color of the cell
  Color color;

  // if its flooded or not
  boolean flooded;

  // used to generate random number
  Random random;

  Color[] listOfColor = { Color.GREEN, Color.yellow, Color.pink, Color.blue, Color.cyan,
      Color.orange, Color.black, Color.red };

  Cell(int x, int y) {

    this(x, y, false);

  }

  Cell(int x, int y, boolean flooded) {

    this.x = x;
    this.y = y;
    this.size = 25;
    this.color = randomColor();
    this.flooded = flooded;

  }

  Cell(int x, int y, Color color) {

    this.x = x;
    this.y = y;
    this.color = color;
  }

  Cell(int x, int y, Color color, boolean flooded) {
    this(x, y, flooded);
    this.color = color;
  }

  // returns a color at a random index of the array of colors
  Color randomColor() {
    Random rand = new Random();
    return this.listOfColor[rand.nextInt(FloodItWorld.NUM_COLORS)];
  }

  // draws a single cell
  public WorldImage draw() {
    return new RectangleImage(this.size, this.size, OutlineMode.SOLID, this.color);
  }

  // draws a cell at a specific x and y coordinates
  public void drawAt(int col, int row, WorldScene background) {
    int xCord = (col * this.size) + (this.size / 2);
    int yCord = (row * this.size) + (this.size / 2);
    background.placeImageXY(this.draw(), xCord, yCord);

  }

}

//represents the game FloodIt that expends world
class FloodItWorld extends World {
  // here array list of array list of cells
  // the inside one is the columns and the outer one is the array of rows
  // doing double .get to get a cell

  // All the cells of the game

  static int BOARD_SIZE;
  static int NUM_COLORS;
  Board board;
  Deque<Cell> flood;
  int currentTick;
  Color currentColor;
  int score;
  boolean win;
  int maxClicks;

  FloodItWorld() {
    FloodItWorld.BOARD_SIZE = 14;
    FloodItWorld.NUM_COLORS = 5;
    this.board = new Board();
    this.flood = new ArrayDeque<>();

    this.currentTick = 1;
    this.score = 0;
    this.currentColor = this.board.board.get(0).get(0).color;
    this.win = false;
    this.maxClicks = 25;

  }

  FloodItWorld(int size) {
    this();
    FloodItWorld.BOARD_SIZE = size;
    this.maxClicks = size + 7;

  }

  // checks if the user has won the game by checking all cells are flooded
  public boolean checkWin() {
    for (ArrayList<Cell> list : this.board.board) {
      for (Cell c : list) {
        if (!c.flooded) {
          return false;
        }
      }
    }
    return true;
  }

  // makes the scene with the entire board
  @Override
  public WorldScene makeScene() {

    int size = this.board.board.get(0).get(0).size;

    WorldScene scene = this.getEmptyScene();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        this.board.board.get(i).get(j).drawAt(j, i, scene);
      }
    }

    scene
        .placeImageXY(
            (new TextImage("Clicks: " + Integer.toString(this.score) + " / "
                + Integer.toString(this.maxClicks), 20, Color.BLACK)),
            FloodItWorld.BOARD_SIZE * size / 2,
            FloodItWorld.BOARD_SIZE * (size + 5) + FloodItWorld.BOARD_SIZE);

    if (this.win) {
      scene.placeImageXY((new TextImage("You Win!", 20 ,Color.BLACK)), FloodItWorld.BOARD_SIZE * size / 2,
          FloodItWorld.BOARD_SIZE * (size + 5) + FloodItWorld.BOARD_SIZE + 20);
    }

    if (this.score > this.maxClicks) {
      scene.placeImageXY((new TextImage("You Lose!", 20 ,Color.BLACK)), FloodItWorld.BOARD_SIZE * size / 2,
          FloodItWorld.BOARD_SIZE * (size + 5) + FloodItWorld.BOARD_SIZE + 20);
    }

    return scene;

  }

  // makes the scene with the entire board
  public WorldScene makeScene(Board board) {

    WorldScene scene = this.getEmptyScene();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        this.board.board.get(i).get(j).drawAt(j, i, scene);
      }
    }

    return scene;

  }

  // makes a new board when the key "r" is pressed
  public void onKeyEvent(String k) {
    if (k.equals("r")) {
      this.board = new Board();
      this.currentTick = 1;
      this.currentColor = this.board.board.get(0).get(0).color;
      this.flood = new ArrayDeque<>();
      this.score = 0;
    }
  }

  // Floods the current level on the board using breadth first search
  public void floodIt() {

    Deque<Cell> tempdeque = new ArrayDeque<>();
    this.flood.add(this.board.board.get(0).get(0));

    for (Cell c : this.flood) {

      c.color = this.currentColor;
      c.flooded = true;

      if (c.x + 1 < FloodItWorld.BOARD_SIZE) {
        if (!this.board.board.get(c.x + 1).get(c.y).flooded
            && this.board.board.get(c.x + 1).get(c.y).color == c.color
            || this.board.board.get(c.x + 1).get(c.y).flooded
                && this.board.board.get(c.x + 1).get(c.y).color != c.color) {

          tempdeque.add(this.board.board.get(c.x + 1).get(c.y));
        }
      }

      if (c.y + 1 < FloodItWorld.BOARD_SIZE) {
        if (!this.board.board.get(c.x).get(c.y + 1).flooded
            && this.board.board.get(c.x).get(c.y + 1).color == c.color
            || this.board.board.get(c.x).get(c.y + 1).flooded
                && this.board.board.get(c.x).get(c.y + 1).color != c.color

        ) {

          tempdeque.add(this.board.board.get(c.x).get(c.y + 1));
        }
      }

      if (c.x - 1 >= 0) {
        if (!this.board.board.get(c.x - 1).get(c.y).flooded
            && this.board.board.get(c.x - 1).get(c.y).color == c.color

            || this.board.board.get(c.x - 1).get(c.y).flooded
                && this.board.board.get(c.x - 1).get(c.y).color != c.color) {

          tempdeque.add(this.board.board.get(c.x - 1).get(c.y));
        }
      }

      if (c.y - 1 >= 0) {
        if (!this.board.board.get(c.x).get(c.y - 1).flooded
            && this.board.board.get(c.x).get(c.y - 1).color == c.color

            || this.board.board.get(c.x).get(c.y - 1).flooded
                && this.board.board.get(c.x).get(c.y - 1).color != c.color

        ) {

          tempdeque.add(this.board.board.get(c.x).get(c.y - 1));
        }
      }

    }
    this.flood = tempdeque;
  }

  // sets the current flooding color to the color of the click cell. Also updates
  // the score.
  public void onMouseClicked(Posn pos) {

    int size = this.board.board.get(0).get(0).size;

    // Make sure it's within bounds
    if (pos.x < 0 || pos.y < 0 || pos.x > size * size || pos.y > size * size) {
      return;
    }

    int x = pos.x / (size);
    int y = pos.y / (size);
    Cell cell = this.board.board.get(y).get(x);

    Color temp = this.currentColor;
    this.currentColor = this.board.board.get(y).get(x).color;

    if (temp != this.currentColor) {
      this.score++;
    }

  }

  @Override
  // Floods the board and checks for the winner on every tick
  public void onTick() {
    this.currentTick = this.currentTick + 1;
    floodIt();
    this.win = this.checkWin();

  }
}

//represents the board that consists of cells
class Board {

  ArrayList<ArrayList<Cell>> board;

  Board() {
    this.board = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      // first for loop is for columns
      ArrayList<Cell> row = new ArrayList<Cell>();
      board.add(row);
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        row.add(new Cell(i, j));
      }
    }
    this.board.get(0).get(0).flooded = false;

  }

  Board(Color color) {
    this.board = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE; i++) {
      // first for loop is for columns
      ArrayList<Cell> row = new ArrayList<Cell>();
      board.add(row);
      for (int j = 0; j < FloodItWorld.BOARD_SIZE; j++) {
        row.add(new Cell(i, j, color));
      }
    }
    this.board.get(0).get(0).flooded = false;

  }

}

//examples
class ExamplesMyWorldProgram {
  WorldCanvas canvas;
  WorldScene scene;
  WorldScene scene1;
  WorldScene scene2;
  WorldScene scene3;
  Cell cell;
  Cell cell2;
  Board board;
  FloodItWorld w1 = new FloodItWorld(25);

  void initConditions() {
    canvas = new WorldCanvas(400, 400);
    scene = new WorldScene(400, 400);
    scene1 = new WorldScene(400, 400);
    scene2 = new WorldScene(400, 400);
    scene3 = new WorldScene(400, 400);
    cell = new Cell(100, 100, Color.red, true);
    cell2 = new Cell(50, 50, Color.orange, true);
    board = new Board(Color.blue);

  }

  // testing bigbang
  void testBigBang(Tester t) {

    FloodItWorld world = new FloodItWorld(14);

    int size = this.board.board.get(0).get(0).size;

    world.bigBang(FloodItWorld.BOARD_SIZE * 25, 500 , 0.1);

  }

  // testing draw method
  boolean testDraw(Tester t) {
    initConditions();
    scene.placeImageXY(cell.draw(), 50, 50);
    scene2.placeImageXY(cell2.draw(), 150, 150);
    scene1.placeImageXY(new RectangleImage(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE,
        OutlineMode.SOLID, Color.red), 50, 50);
    scene3.placeImageXY(new RectangleImage(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE,
        OutlineMode.SOLID, Color.orange), 150, 150);
    return t.checkExpect(scene, scene1) && t.checkExpect(scene2, scene3);

  }

  // testing drawAt method
  boolean testDrawAt(Tester t) {
    initConditions();
    // cell = new Cell(100, 100, Color.red, true);
    cell.drawAt(50, 50, scene);

    scene1.placeImageXY(new RectangleImage(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE,
        OutlineMode.SOLID, Color.red), 1111 + 100, 1111 + 100);
    cell2.drawAt(50, 50, scene2);

    scene3.placeImageXY(new RectangleImage(FloodItWorld.BOARD_SIZE, FloodItWorld.BOARD_SIZE,
        OutlineMode.SOLID, Color.orange), 1111 + 100, 1111 + 100);
    return t.checkExpect(scene, scene1) && t.checkExpect(scene2, scene3);

  }

  // testing randColor method
  boolean testRandColor(Tester t) {
    Random rand = new Random();
    int num = rand.nextInt(FloodItWorld.NUM_COLORS);
    Color color1 = cell.listOfColor[num];

    return t.checkExpect(color1, cell.listOfColor[num]);
  }

  boolean testMakeScene(Tester t) {

    scene.placeImageXY(new RectangleImage(1, 1, OutlineMode.SOLID, Color.blue), 150, 150);

    return t.checkExpect(w1.makeScene(board), scene);

  }

  // testing floodIt method
  boolean testFloodIt(Tester t) {
    w1.floodIt();
    scene.placeImageXY(new RectangleImage(1, 1, OutlineMode.SOLID, Color.blue), 150, 150);

    return t.checkExpect(w1.makeScene(board), scene);

  }

}


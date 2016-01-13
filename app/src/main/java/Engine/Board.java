package Engine;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Engine.Events.OnGameOverListener;
import Engine.Events.OnMineOpenListener;
import Engine.Events.OnSquareFlagListener;
import Engine.Events.OnSquareOpenListener;

public class Board {
    public static char imageBomb;
    public static char imageFlag;

    private int sizeX;
    private int sizeY;
    private int difficulty;
    private int numberOfMines;
    private int totalSquares;
    private Square[][] squares;

    private int openSquares;
    private int flagedSquares;

    private final List<OnMineOpenListener> onMineOpenListeners = new ArrayList<>();
    private final List<OnSquareOpenListener> onSquareOpenListeners = new ArrayList<>();
    private final List<OnSquareFlagListener> onSquareFlagListeners = new ArrayList<>();
    private final List<OnGameOverListener> onGameOverListeners = new ArrayList<>();

    public Board(int sizeX, int sizeY, int difficulty) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.difficulty = difficulty;
        this.squares = new Square[sizeY][sizeX];
        this.totalSquares = this.sizeX * this.sizeY;
        this.numberOfMines = (int)Math.ceil(totalSquares * (this.difficulty / 10f)) - 15;
        this.openSquares = 0;
        this.flagedSquares = 0;

        this.generateInitBoard();
    }

    public void addOnSquareOpenListener(OnSquareOpenListener listener) {
        this.onSquareOpenListeners.add(listener);
    }

    public void addOnMineOpenListener(OnMineOpenListener listener) {
        this.onMineOpenListeners.add(listener);
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public int getTotalMines() {
        return this.numberOfMines;
    }

    public int getSizeX() {
        return this.sizeX;
    }

    public int getSizeY() {
        return this.sizeY;
    }

    public void setDifficulty(int aDifficulty) {
        this.difficulty = aDifficulty;
    }

    public void setSizeX(int aSizeX) {
        this.sizeX = aSizeX;
    }

    public void setSizeY(int aSizeY) {
        this.sizeY = aSizeY;
    }

    private void generateInitBoard() {
        for (int j = 0; j < this.sizeY; j++) {
            for (int i = 0; i < this.sizeX; i++) {
                this.squares[j][i] = new Square(0, Square.Type.Empty, Square.State.Close);

                this.squares[j][i].addOnFlagListener(new OnSquareFlagListener() {
                    @Override
                    public void onFlag(Square square) {
                        squareFlagLister(square);
                    }

                    @Override
                    public void onUnflag(Square square) {
                        squareUnflagLister(square);
                    }
                });
            }
        }

        this.fillWithMines();
        this.generateBoard();
    }

    public void squareFlagLister(Square square) {
        this.increaseFlagedSquares();

        for (OnSquareFlagListener listener : this.onSquareFlagListeners) {
            listener.onFlag(square);
        }
    }

    public void squareUnflagLister(Square square) {
        this.decreaseFlagedSquares();

        for (OnSquareFlagListener listener : this.onSquareFlagListeners) {
            listener.onUnflag(square);
        }
    }

    private void increaseOpenSquares() {
        this.openSquares++;        Log.e("--->", this.openSquares + " " + this.flagedSquares + " --> " + (this.sizeX * this.sizeY) );

        if((this.flagedSquares + this.openSquares) >= (this.sizeX * this.sizeY)) {

            boolean win = true;

            for(int c = 0; c < this.squares.length; c++) {
                for(int x = 0; x < this.squares[c].length; x++) {
                    if((this.squares[c][x].getFlag() == Square.Flag.Yes && this.squares[c][x].getType() != Square.Type.Mine) ||
                            (this.squares[c][x].getFlag() == Square.Flag.No && this.squares[c][x].getType() == Square.Type.Mine)) {
                        win = false;
                    }
                }
            }

            for (OnGameOverListener listener : this.onGameOverListeners) {
                if(win) {
                    listener.onWin();
                } else {
                    listener.onLose();
                }
            }
        }
    }

    private void decreaseOpenSquares() {
        this.openSquares++;
    }

    private void increaseFlagedSquares() {
        this.flagedSquares++;

        if((this.flagedSquares + this.openSquares) >= (this.sizeX * this.sizeY)) {

            boolean win = true;

            for(int c = 0; c < this.squares.length; c++) {
                for(int x = 0; x < this.squares[c].length; x++) {
                    if((this.squares[c][x].getFlag() == Square.Flag.Yes && this.squares[c][x].getType() != Square.Type.Mine) ||
                            (this.squares[c][x].getFlag() == Square.Flag.No && this.squares[c][x].getType() == Square.Type.Mine)) {

                        win = false;
                    }
                }
            }

            for (OnGameOverListener listener : this.onGameOverListeners) {
                if(win) {
                    listener.onWin();
                } else {
                    listener.onLose();
                }
            }
        }
    }

    private void decreaseFlagedSquares() {
        if((this.flagedSquares - 1) >= 0) {
            this.flagedSquares--;
        }
    }

    public void addOnSquareFlagListener(OnSquareFlagListener listener) {
        this.onSquareFlagListeners.add(listener);
    }

    public void addOnGameOverListeners(OnGameOverListener listener) {
        this.onGameOverListeners.add(listener);
    }

    public Square[][] getBoardMatrix() {
        return this.squares;
    }

    public void generateBoard() {
        for (int j = 0; j < this.squares.length; j++) {
            for (int i = 0; i < this.squares[0].length; i++) {
                if(this.squares[j][i].getType() == Square.Type.Mine) {
                    Pos neighbors[] = (new Pos(i, j)).getNeighbors();

                    for(int z = 0; z < neighbors.length; z++) {
                        if(this.isNotMineSquare(neighbors[z])) {
                            this.squares[neighbors[z].y][neighbors[z].x].incrementValueHint();
                        }
                    }
                }
            }
        }
    }

    class Pos {
        public int x;
        public int y;

        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Pos getTop() {
            return new Pos(this.x, this.y - 1);
        }

        public Pos getBottom() {
            return new Pos(this.x, this.y + 1);
        }

        public Pos getLeft() {
            return new Pos(this.x - 1, this.y);
        }

        public Pos getRight() {
            return new Pos(this.x + 1, this.y);
        }

        public Pos getTopLeft() {
            return new Pos(this.x - 1, this.y - 1);
        }

        public Pos getTopRight() {
            return new Pos(this.x + 1, this.y - 1);
        }

        public Pos getBottomLeft() {
            return new Pos(this.x - 1, this.y + 1);
        }

        public Pos getBottomRight() {
            return new Pos(this.x + 1, this.y + 1);
        }

        public Pos[] getNeighbors() {
            return new Pos[] {
                    this.getTop(), this.getBottom(), this.getLeft(), this.getRight(),
                    this.getTopLeft(), this.getTopRight(),
                    this.getBottomLeft(), this.getBottomRight()
            };
        }
    }

    public void fillWithMines() {

        ArrayList<Pos> pos = new ArrayList<>();

        for (int i = 0; i < this.squares.length; i++) {
            for (int j = 0; j < this.squares[0].length; j++) {
                pos.add(new Pos(j, i));
            }
        }

        for (int i = 0; i < this.numberOfMines; i++) {
            Random rand = new Random();
            int position = rand.nextInt(pos.size());
            Pos currentPos = pos.get(position);

            this.squares[currentPos.y][currentPos.x].setType(Square.Type.Mine);
            pos.remove(position);
        }
    }

    public boolean positionInBounds(int y, int x) {
        // Check bounds
        return !(y < 0 || x < 0 || !(y < this.squares.length) || !(x < this.squares[0].length));
    }

    public boolean isEmptySquare(int y, int x) {
        return this.positionInBounds(y, x) && (this.squares[y][x].getType() == Square.Type.Empty);
    }

    public boolean isNotMineSquare(int y, int x) {
        return this.positionInBounds(y, x) && (this.squares[y][x].getType() != Square.Type.Mine);
    }


    public boolean isNotMineSquare(Pos pos) {
        return this.positionInBounds(pos.y, pos.x) && (this.squares[pos.y][pos.x].getType() != Square.Type.Mine);
    }

    public void openSquare(int j,  int i) {

        java.util.ArrayList<Pos> queue = new java.util.ArrayList<Pos>();
        queue.add(new Pos(i, j));

        do {
            Pos current = (Pos) queue.get(0);

            if(this.squares[current.y][current.x].getFlag() == Square.Flag.No) {
                Pos neighbors[] = current.getNeighbors();

                if (this.isEmptySquare(current.y, current.x) && this.squares[current.y][current.x].getState() != Square.State.Open) { // If the square is already open, ignore it

                    for (int z = 0; z < neighbors.length; z++) {
                        if (this.isNotMineSquare(neighbors[z])) {
                            queue.add(neighbors[z]);
                        }
                    }

                    this.squares[current.y][current.x].setState(Square.State.Open);
                    this.increaseOpenSquares();
                } else if (this.positionInBounds(current.y, current.x) && this.squares[current.y][current.x].getType() == Square.Type.Hint) {
                    if(this.squares[current.y][current.x].getState() != Square.State.Open) {
                        this.increaseOpenSquares();
                        this.squares[current.y][current.x].setState(Square.State.Open);
                    }
                } else if (this.positionInBounds(current.y, current.x) && this.squares[current.y][current.x].getType() == Square.Type.Mine) {
                    this.squares[current.y][current.x].setState(Square.State.Open);

                    if(this.squares[current.y][current.x].getState() != Square.State.Open) {
                        this.increaseOpenSquares();
                    }
                }
            }

            queue.remove(0);
        } while (queue.size() > 0);

        if(this.squares[j][i].getFlag() == Square.Flag.No) {
            if (!this.isNotMineSquare(new Pos(i, j))) {
                for (OnMineOpenListener listener : this.onMineOpenListeners) {
                    listener.mineOpen(j, i, this, this.squares[j][i]);
                }
            }

            if (this.positionInBounds(i, j)) {
                for (OnSquareOpenListener listener : this.onSquareOpenListeners) {
                    listener.squareOpen(j, i, this, this.squares[j][i]);
                }
            }
        }

    }

    public void openAll() {
        boolean win = true;

        for (int j = 0; j < this.squares.length; j++) {
            for (int i = 0; i < this.squares[0].length; i++) {
                this.squares[j][i].setState(Square.State.Open);
            }
        }

        for(int c = 0; c < this.squares.length; c++) {
            for(int x = 0; x < this.squares[c].length; x++) {
                if((this.squares[c][x].getFlag() == Square.Flag.Yes && this.squares[c][x].getType() != Square.Type.Mine) ||
                        (this.squares[c][x].getFlag() == Square.Flag.No && this.squares[c][x].getType() == Square.Type.Mine)) {
                    win = false;
                }
            }
        }

        for (OnGameOverListener listener : this.onGameOverListeners) {
            if(win) {
                listener.onWin();
            } else {
                listener.onLose();
            }
        }
    }

    public void closeAll() {
        for (int j = 0; j < this.squares.length; j++) {
            for (int i = 0; i < this.squares[0].length; i++) {
                this.squares[j][i].setState(Square.State.Close);
                this.squares[j][i].setFlag(Square.Flag.No);
                this.squares[j][i].setFinishOpenState(false);
            }
        }

        this.flagedSquares = 0;
        this.openSquares = 0;
    }
}

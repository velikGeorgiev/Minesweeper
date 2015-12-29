package Engine.Events;

import Engine.Board;

public interface OnSquareOpenListener {
    void squareOpen(int y, int x, Board board);
}

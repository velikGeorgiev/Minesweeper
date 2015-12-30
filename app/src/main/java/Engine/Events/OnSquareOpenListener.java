package Engine.Events;

import Engine.Board;
import Engine.Square;

public interface OnSquareOpenListener {
    void squareOpen(int y, int x, Board board, Square square);
}

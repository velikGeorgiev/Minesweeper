package Engine.Events;

import Engine.Board;
import Engine.Square;

public interface OnMineOpenListener {
    void mineOpen(int y, int x, Board board, Square square);
}
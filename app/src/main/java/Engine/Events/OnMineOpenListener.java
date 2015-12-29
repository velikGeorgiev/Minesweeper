package Engine.Events;

import Engine.Board;

public interface OnMineOpenListener {
    void mineOpen(int y, int x, Board board);
}

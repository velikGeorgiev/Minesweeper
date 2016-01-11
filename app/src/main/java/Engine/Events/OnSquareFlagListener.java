package Engine.Events;

import Engine.Square;

/**
 * Created by velko on 30/12/15.
 */
public interface OnSquareFlagListener {
    void onFlag(Square square);
    void onUnflag(Square square);
}

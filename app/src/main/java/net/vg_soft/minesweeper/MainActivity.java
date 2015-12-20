package net.vg_soft.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import Engine.Board;
import Engine.Square;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridBoard;
    private boolean boardUiDrawed = false;
    private HashMap<String, Button> uiSquares;
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Board.imageBomb = '*';
        Board.imageFlag = 'f';

        this.board = new Board(16, 16, 1);
        this.uiSquares = new HashMap<>();

        final Square[][] boardMatrix = this.board.getBoardMatrix();

        final MainActivity _this = this;
        this.gridBoard = (GridLayout) findViewById(R.id.gridBoard);
        this.gridBoard.setColumnCount(16);

        this.gridBoard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(_this.boardUiDrawed == false) {
                    int sizeBoard = _this.gridBoard.getWidth();

                    for (int j = 0; j < boardMatrix.length; j++) {
                        for (int i = 0; i < boardMatrix[0].length; i++) {

                            final Button btn = new Button(new ContextThemeWrapper(_this, R.style.gameBoardSquare), null, 0);
                            btn.setBackground(getResources().getDrawable(R.drawable.button_dark_gradient));
                            btn.setTextAppearance(_this, R.style.gameBoardSquare);

                            _this.uiSquares.put(j + "-" + i, btn);

                            btn.setWidth(sizeBoard / 16);
                            btn.setHeight(sizeBoard / 16);

                            final int _posX = i;
                            final int _posY = j;

                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    _this.openSquare(btn, _posY, _posX);
                                }
                            });

                            _this.gridBoard.addView(btn);
                        }
                    }
                    _this.boardUiDrawed = true;
                }
            }
        });


    }

    public void openSquare(Button target, int y, int x) {
        this.board.openSquare(y, x);

        final Square[][] boardMatrix = this.board.getBoardMatrix();

        for (int j = 0; j < boardMatrix.length; j++) {
            for (int i = 0; i < boardMatrix[0].length; i++) {
                if(boardMatrix[j][i].getState() == Square.State.Open) {
                    if (boardMatrix[j][i].getType() == Square.Type.Mine) {
                        this.uiSquares.get(j + "-" + i).setText(String.valueOf(Board.imageBomb));
                    } else if (boardMatrix[j][i].getType() == Square.Type.Empty) {
                        this.uiSquares.get(j + "-" + i).setBackground(getResources().getDrawable(R.drawable.button_light_gradient));
                    } else {
                        this.uiSquares.get(j + "-" + i).setText(String.valueOf(boardMatrix[j][i].getValue()));
                    }
                }
            }
        }
    }
}

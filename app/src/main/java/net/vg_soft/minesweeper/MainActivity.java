package net.vg_soft.minesweeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import Engine.Board;
import Engine.Square;

public class MainActivity extends AppCompatActivity {

    // Layout inflate view elements
    private GridLayout gridBoard;
    private TextView mineNumberLbl;

    private boolean boardUiDrawed = false;
    private HashMap<String, ImageButton> uiSquares;
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Board.imageBomb = '*';
        Board.imageFlag = 'f';

        this.uiSquares = new HashMap<>();

        this.gridBoard = (GridLayout) findViewById(R.id.gridBoard);
        this.mineNumberLbl = (TextView) findViewById(R.id.mineNumberLbl);
        this.newGrid(10, 10, 2);
    }

    public void restartGameAction(View v) {
        restartGrid();
    }

    public void newGameAction(View v) {
        newGrid(this.board.getSizeX(), this.board.getSizeY(), this.board.getDifficulty());
    }

    private void newGrid(int gameSettingsX, int gameSettingsY, int gameSettingsLevel) {
        this.buildGrid(gameSettingsX, gameSettingsY, gameSettingsLevel, true);
    }

    private void restartGrid() {
        this.buildGrid(this.board.getSizeX(), this.board.getSizeY(), this.board.getDifficulty(), false);
    }

    private void buildGrid(int gameSettingsX, int gameSettingsY, int gameSettingsLevel, boolean restart) {

        if(restart || this.board == null) {
            this.board = new Board(gameSettingsX, gameSettingsY, gameSettingsLevel);

            this.board.addOnMineOpenListener(new Engine.Events.OnMineOpenListener() {

                @Override
                public void mineOpen(int y, int x) {
                    Log.e("OPEN SQUARE", "BUUUM Pos: " + y + " - " + x);
                }
            });

            this.board.addOnSquareOpenListener(new Engine.Events.OnSquareOpenListener() {

                @Override
                public void squareOpen(int y, int x) {
                    Log.e("OPEN SQUARE", "NEW SQUARE OPENED  Pos: " + y + " - " + x);
                }
            });
        }

        this.board.closeAll();

        final MainActivity _this = this;
        this.gridBoard.removeAllViews();

        this.uiSquares.clear();
        _this.boardUiDrawed = false;

        this.gridBoard.setColumnCount(this.board.getSizeX());
        this.mineNumberLbl.setText("Minas: " + this.board.getTotalMines());

        this.gridBoard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Square[][] boardMatrix = _this.board.getBoardMatrix();

                if (_this.boardUiDrawed == false) {
                    int sizeBoardX = _this.gridBoard.getWidth();
                    int sizeBoardY = (_this.gridBoard.getHeight() < _this.gridBoard.getWidth()) ? _this.gridBoard.getHeight() : _this.gridBoard.getWidth();

                    for (int j = 0; j < boardMatrix.length; j++) {
                        for (int i = 0; i < boardMatrix[0].length; i++) {

                            final Animation animScale = AnimationUtils.loadAnimation(_this, R.anim.anim_scale);

                            final ImageButton btn = new ImageButton(new ContextThemeWrapper(_this, R.style.gameBoardSquare), null, 0);
                            btn.setBackground(getResources().getDrawable(R.drawable.button_dark_gradient));
                            btn.setLayoutParams(new ViewGroup.LayoutParams((sizeBoardX / _this.board.getSizeX()), (sizeBoardY / _this.board.getSizeY())));

                            _this.uiSquares.put((j + "-" + i), btn);

                            final int _posX = i;
                            final int _posY = j;

                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Square[][] boardMatrix = _this.board.getBoardMatrix();

                                    //btn.startAnimation(animScale);

                                    if (boardMatrix[_posY][_posX].getState() == Square.State.Close && boardMatrix[_posY][_posX].getFlag() == Square.Flag.No) {
                                        _this.openSquare(btn, _posY, _posX);
                                    }
                                }
                            });

                            btn.setOnLongClickListener(new View.OnLongClickListener() {

                                @Override
                                public boolean onLongClick(View v) {
                                    Square[][] boardMatrix = _this.board.getBoardMatrix();

                                    if (boardMatrix[_posY][_posX].getState() == Square.State.Close && boardMatrix[_posY][_posX].getFlag() == Square.Flag.No) {
                                        btn.setImageResource(R.drawable.ic_flag);
                                        boardMatrix[_posY][_posX].setFlag(Square.Flag.Yes);
                                    } else if (boardMatrix[_posY][_posX].getState() == Square.State.Close && boardMatrix[_posY][_posX].getFlag() == Square.Flag.Yes) {
                                        btn.setImageResource(android.R.color.transparent);
                                        boardMatrix[_posY][_posX].setFlag(Square.Flag.No);
                                    }

                                    return true;
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

    public void openSquare(ImageButton target, int y, int x) {
        this.board.openSquare(y, x);

        final Square[][] boardMatrix = this.board.getBoardMatrix();

        if(boardMatrix[y][x].getType() == Square.Type.Mine) {
            this.board.openAll();
        }

        for (int j = 0; j < boardMatrix.length; j++) {
            for (int i = 0; i < boardMatrix[0].length; i++) {
                if(boardMatrix[j][i].getState() == Square.State.Open) {
                    if (boardMatrix[j][i].getType() == Square.Type.Mine) {
                        if (boardMatrix[j][i].getFlag() == Square.Flag.No) {
                            this.uiSquares.get(j + "-" + i).setBackground(getResources().getDrawable(R.drawable.button_bomb_gradient));
                        } else {
                            this.uiSquares.get(j + "-" + i).setBackground(getResources().getDrawable(R.drawable.button_green_gradient));
                        }

                        this.uiSquares.get(j + "-" + i).setImageResource(R.mipmap.bomb);
                    } else if (boardMatrix[j][i].getType() == Square.Type.Empty) {
                        this.uiSquares.get(j + "-" + i).setBackground(getResources().getDrawable(R.drawable.button_light_gradient));
                    } else {

                        switch(boardMatrix[j][i].getValue()) {
                            case 1:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num1);
                                break;
                            case 2:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num2);
                                break;
                            case 3:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num3);
                                break;
                            case 4:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num4);
                                break;
                            case 5:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num5);
                                break;
                            case 6:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num6);
                                break;
                            case 7:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num7);
                                break;
                            case 8:
                                this.uiSquares.get(j + "-" + i).setImageResource(R.drawable.ic_num8);
                                break;
                        }
                        if (boardMatrix[j][i].getFlag() == Square.Flag.No) {
                            this.uiSquares.get(j + "-" + i).setBackground(getResources().getDrawable(R.drawable.button_yellow_gradient));
                        } else {
                            this.uiSquares.get(j + "-" + i).setBackground(getResources().getDrawable(R.drawable.button_bomb_gradient));
                        }

                    }

                    final int _posX = i;
                    final int _posY = j;

                    if(!boardMatrix[j][i].getFinishOpenState()) {
                        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

                        animScale.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                boardMatrix[_posY][_posX].setFinishOpenState(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        this.uiSquares.get(j + "-" + i).startAnimation(animScale);

                    }
                }
            }
        }
    }
}

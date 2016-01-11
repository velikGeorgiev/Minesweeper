package net.vg_soft.minesweeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import Engine.Board;
import Engine.Events.OnGameOverListener;
import Engine.Events.OnSquareFlagListener;
import Engine.Square;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Layout inflate view elements
    private GridLayout gridBoard;
    private TextView mineNumberLbl;

    private boolean boardUiDrawed = false;
    private int[] numberImageResourceId;

    private HashMap<String, ImageButton> uiSquares;
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Board.imageBomb = '*'; // TODO: Implement
        Board.imageFlag = 'f'; // TODO: Implement

        this.uiSquares = new HashMap<>();

        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLinerLayout);

        this.gridBoard = (GridLayout) mainLayout.findViewById(R.id.gridBoard);
        this.mineNumberLbl = (TextView) findViewById(R.id.mineNumberLbl);
        this.newGrid(10, 10, 2);

        // Numeric image resource ids
        this.numberImageResourceId = new int[] { R.drawable.ic_num0, R.drawable.ic_num1, R.drawable.ic_num2, R.drawable.ic_num3,
                R.drawable.ic_num4, R.drawable.ic_num5, R.drawable.ic_num6,
                R.drawable.ic_num7, R.drawable.ic_num8, R.drawable.ic_num9 };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_restart) {
            newGrid(this.board.getSizeX(), this.board.getSizeY(), this.board.getDifficulty());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_howto) {

        } else if (id == R.id.nav_difficulty) {

        } else if (id == R.id.nav_boardsize) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            final MainActivity _this = this;

            this.board.addOnMineOpenListener(new Engine.Events.OnMineOpenListener() {

                @Override
                public void mineOpen(int y, int x, Board targetBoard, Square targetSquare) {
                    if (targetSquare.getType() == Square.Type.Mine) {
                        targetBoard.openAll();
                    }
                }
            });

            this.board.addOnSquareOpenListener(new Engine.Events.OnSquareOpenListener() {

                @Override
                public void squareOpen(int y, int x, Board targetBoard, Square targetSquare) {
                    // When a square is opened the GUI board will be repainted
                    repaintGuiGrid();
                }
            });

            this.board.addOnGameOverListeners(new OnGameOverListener() {
                @Override
                public void onWin() {
                    Log.e("STATE", "WIN");
                }

                @Override
                public void onLose() {
                    Log.e("STATE", "LOSE");
                }
            });

            this.board.addOnSquareFlagListener(new OnSquareFlagListener() {
                @Override
                public void onFlag(Square square) {
                    int currentNumber = 0;

                    if (!_this.mineNumberLbl.getText().toString().equals("")) {
                        currentNumber = Integer.parseInt(_this.mineNumberLbl.getText().toString());
                    }

                    if (_this.mineNumberLbl != null) {
                        _this.mineNumberLbl.setText(--currentNumber + "");
                    }
                }

                @Override
                public void onUnflag(Square square) {
                    int currentNumber = 0;

                    if (!_this.mineNumberLbl.getText().toString().equals("")) {
                        currentNumber = Integer.parseInt(_this.mineNumberLbl.getText().toString());
                    }

                    if (_this.mineNumberLbl != null) {
                        _this.mineNumberLbl.setText(++currentNumber + "");
                    }
                }
            });
        }

        this.board.closeAll();

        final MainActivity _this = this;
        this.gridBoard.removeAllViews();

        this.uiSquares.clear();
        _this.boardUiDrawed = false;

        this.gridBoard.setColumnCount(this.board.getSizeX());
        this.mineNumberLbl.setText(this.board.getTotalMines() + "");

        ((LinearLayout) findViewById(R.id.mainLinerLayout)).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (_this.boardUiDrawed == false) {
                    ((LinearLayout) findViewById(R.id.mainLinerLayout)).setMinimumWidth(((RelativeLayout) findViewById(R.id.scrollViewLayout)).getWidth());
                    ((LinearLayout) findViewById(R.id.mainLinerLayout)).setMinimumHeight(((RelativeLayout) findViewById(R.id.scrollViewLayout)).getHeight());
                }

                _this.gridBoard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (_this.boardUiDrawed == false) {

                            _this.gridBoard.setMinimumWidth(((RelativeLayout) findViewById(R.id.scrollViewLayout)).getWidth());
                            _this.gridBoard.setMinimumHeight(((RelativeLayout) findViewById(R.id.scrollViewLayout)).getHeight() - 200);
                        }
                        Square[][] boardMatrix = _this.board.getBoardMatrix();

                        if (_this.boardUiDrawed == false) {
                            int sizeBoardX = _this.gridBoard.getWidth();
                            int tempHeight = ((RelativeLayout) findViewById(R.id.scrollViewLayout)).getHeight();
                            int sizeBoardY = (tempHeight < _this.gridBoard.getWidth()) ? tempHeight : _this.gridBoard.getWidth();

                            for (int j = 0; j < boardMatrix.length; j++) {
                                for (int i = 0; i < boardMatrix[0].length; i++) {

                                    final ImageButton btn = new ImageButton(new ContextThemeWrapper(_this, R.style.gameBoardSquare), null, 0);

                                    btn.setBackground(getResources().getDrawable(R.drawable.button_dark_gradient));

                                    int heightBtn = (sizeBoardX / _this.board.getSizeX());
                                    int widthBtn = (sizeBoardY / _this.board.getSizeY());

                                    heightBtn = (heightBtn < 65) ? 65 : heightBtn;
                                    widthBtn = (widthBtn < 65) ? 65 : widthBtn;
Log.d("SIZE ", widthBtn + " - " + heightBtn);
                                    btn.setLayoutParams(new ViewGroup.LayoutParams(heightBtn, widthBtn));

                                    _this.uiSquares.put((j + "-" + i), btn);

                                    final int _posX = i;
                                    final int _posY = j;

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Square[][] boardMatrix = _this.board.getBoardMatrix();

                                            if (boardMatrix[_posY][_posX].getState() == Square.State.Close && boardMatrix[_posY][_posX].getFlag() == Square.Flag.No) {
                                                _this.board.openSquare(_posY, _posX);
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
        });
    }

    public void repaintGuiGrid() {
        final Square[][] boardMatrix = this.board.getBoardMatrix();

        for (int j = 0; j < boardMatrix.length; j++) {
            for (int i = 0; i < boardMatrix[0].length; i++) {

                ImageButton currentImageButton = this.uiSquares.get(j + "-" + i);

                if(boardMatrix[j][i].getState() == Square.State.Open) {
                    if (boardMatrix[j][i].getType() == Square.Type.Mine) {
                        if (boardMatrix[j][i].getFlag() == Square.Flag.No) {
                            currentImageButton.setBackground(getResources().getDrawable(R.drawable.button_bomb_gradient));
                        } else {
                            currentImageButton.setBackground(getResources().getDrawable(R.drawable.button_green_gradient));
                        }

                        currentImageButton.setImageResource(R.mipmap.bomb);
                    } else if (boardMatrix[j][i].getType() == Square.Type.Empty) {
                        currentImageButton.setBackground(getResources().getDrawable(R.drawable.button_light_gradient));
                    } else {

                        currentImageButton.setImageResource(this.numberImageResourceId[boardMatrix[j][i].getValue()]);

                        if (boardMatrix[j][i].getFlag() == Square.Flag.No) {
                            currentImageButton.setBackground(getResources().getDrawable(R.drawable.button_yellow_gradient));
                        } else {
                            currentImageButton.setBackground(getResources().getDrawable(R.drawable.button_bomb_gradient));
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

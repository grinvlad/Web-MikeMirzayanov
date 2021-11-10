package ru.itmo.wp.web.page;

import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {

    private void action(HttpServletRequest request, Map<String, Object> view) {
        State state = (State) request.getSession().getAttribute("state");
        if (state == null) {
            newGame(request, view);
        } else {
            view.put("state", state);
        }
    }

    private void onMove(HttpServletRequest request, Map<String, Object> view) {
        State state = (State) request.getSession().getAttribute("state");
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] tokens = entry.getKey().split("_");
            if (tokens.length == 2 && tokens[0].equals("cell")) {
                int row = Integer.parseInt(String.valueOf(tokens[1].charAt(0)));
                int col = Integer.parseInt(String.valueOf(tokens[1].charAt(1)));
                state.setMove(row, col);
                break;
            }
        }
        view.put("state", state);
        throw new RedirectException("/ticTacToe");
    }

    private void newGame(HttpServletRequest request, Map<String, Object> view) {
        State state = new State();
        request.getSession().setAttribute("state", state);
        view.put("state", state);
        throw new RedirectException("/ticTacToe");
    }
    
    public static class State {
        private int countMove;
        private final int SIZE = 3;
        private boolean crossesMove;
        public String phase;
        String[][] cells;


        private State() {
            cells = new String[SIZE][SIZE];
            countMove = 0;
            crossesMove = true;
            phase = "RUNNING";
        }

        public boolean getCrossesMove() {
            return crossesMove;
        }

        public String[][] getCells() {
            return cells;
        }

        public int getSize() {
            return SIZE;
        }

        public String getPhase() {
            return phase;
        }

        private void setMove(int row, int col) {
            if (phase.equals("RUNNING") && isValid(row, col, null)) {
                countMove++;
                cells[row][col] = crossesMove ? "X" : "O";
                checkWin(new Move(row, col, cells[row][col]));
                crossesMove = !crossesMove;
            }
        }

        void checkWin(Move move) {
            if (count(1, 0, move) + count(-1, 0, move) + 1 >= SIZE ||
                count(0, 1, move) + count(0, -1, move) + 1 >= SIZE ||
                count(1, 1, move) + count(-1, -1, move) + 1 >= SIZE ||
                count(-1, 1, move) + count(1, -1, move) + 1 >= SIZE) {
                phase = crossesMove ? "WON_X" : "WON_O";
            } else if (countMove == SIZE*SIZE) {
                phase = "DRAW";
            }
        }

        private int count(int x, int y, Move move) {
            int cnt = 0;
            int xx = 0;
            int yy = 0;
            for (int i = 1; i < SIZE; i++) {
                xx += x;
                yy += y;
                if (move != null && isValid(move.getRow() + xx, move.getCol() + yy, move.getValue())) {
                    cnt++;
                } else {
                    break;
                }
            }
            return cnt;
        }

        private boolean isValid(int i, int j, Object compare) {
            return i >= 0 && j >= 0 &&
                    i < SIZE && j < SIZE &&
                    cells[i][j] == compare;
        }
    }

    private static class Move {
        private final int row;
        private final int col;
        private final String value;

        private Move(int row, int col, String value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        private int getRow() {
            return row;
        }

        private int getCol() {
            return col;
        }

        private String getValue() {
            return value;
        }
    }
}

package chess;

public class BoardLooper {
    public interface PositionChecker<T> {
        T check(ChessPosition pos);
    }

    public <T> T findPosition(PositionChecker<T> checker) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);

                T result = checker.check(pos);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}

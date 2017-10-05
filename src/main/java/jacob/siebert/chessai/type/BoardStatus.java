package jacob.siebert.chessai.type;

/**
 * Represents the current status of the board.
 */
public enum BoardStatus {
    INIT,               // game yet to begin
    IN_PROGRESS,        // game has started
    TAN_CHECKMATED,     // white has won
    WHITE_CHECKMATED,   // tan has won
    STALEMATE           // game concluded in stalemate
}

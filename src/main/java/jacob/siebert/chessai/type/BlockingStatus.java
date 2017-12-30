package jacob.siebert.chessai.type;

/**
 * The blocking status of a piece. This refers to whether or not a piece
 * is blocking a check on its king and, if so, can the piece either eliminate
 * the threat or move while continuing to prevent the check.
 * TODO: do i need all 4 of these? likely not, finish the algo first
 */
public enum BlockingStatus {
    NOT_BLOCKING,               // not preventing any check
    BLOCKING_CANNOT_MOVE,       // blocking the check with no option to move
    BLOCKING_CAN_MOVE,          // blocking the check but can move along the threat path
    BLOCKING_CAN_ELIMINATE      // blocking the check but can remove the threat
}

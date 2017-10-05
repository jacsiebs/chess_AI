package jacob.siebert.chessai.type;

/**
 * User inputs the type of new game to be played which is sent to the
 * Board and used to either begin a fresh game, load an existing game,
 * or spawn an empty Board (generally for testing).
 */
public enum NewGameType {
    NEW,
    LOAD,
    EMPTY
}

package model.enums;

/**
 * Some Description
 *
 * @author Maciej Niemczyk (Maciej@gmx.de)
 */
public enum CommentLevel {
    QUIET(0),
    NORMAL(1),
    VERBOSE(2);

    int level;

    CommentLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

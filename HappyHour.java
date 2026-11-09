// Pricing policy that can be activated once per game by the manager (Stage 3)
// Happy Hour lasts for the turn it is activated and the next turn (2 turns total)
public class HappyHour {
    private static final int DURATION_TURNS = 2;
    private static final double DISCOUNT_MULTIPLIER = 0.8; // 20% off
    private static final int SATISFACTION_RECOVERY = 15;

    private boolean used = false; // its either used or not, and can only be used once so we use boolean
    private int remainingTurns = 0; // why 0?
    // because if 2, then remaining turns >0 would be true form turn 1
    // automatically, rather than
    // the manager activating it.

    public boolean canActivate() { // Checks if the manager is allowed to activate Happy Hour
        return !used && remainingTurns == 0;
    }

    public void activate() { // Activates Happy Hour for 2 turns and marks it as used for the rest of the
                             // game
        if (!canActivate()) {
            throw new IllegalStateException("Happy Hour can only be activated once per game.");
        }
        used = true;
        remainingTurns = DURATION_TURNS; // now we add 2 to the remaining turns, and it will count down to 0
    }

    public boolean isActive() { // Returns true if Happy Hour is currently active on this turn
        return remainingTurns > 0;
    }

    public boolean isUsed() { // Returns true if Happy Hour has already been triggered in this game
        return used;
    }

    public int getRemainingTurns() { // Returns how many turns are left for Happy Hour
        return remainingTurns;
    }

    public void endTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    public static double getDiscountMultiplier() {
        return DISCOUNT_MULTIPLIER;
    }

    public static int getSatisfactionRecovery() {
        return SATISFACTION_RECOVERY;
    }
}

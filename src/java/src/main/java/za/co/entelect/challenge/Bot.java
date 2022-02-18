package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.*;

import java.util.*;

import java.security.SecureRandom;

import static java.lang.Math.*;

/**
 * Represents a bot that implements the Offense Strategy:
 * Try to disrupt the opponent as much as possible
 */
public class Bot {

    private static final int maxSpeed = 9;
    private List<Command> directionList = new ArrayList<>();
    private List<Boolean> canTurn = new ArrayList<>();

    private Random random;
    private GameState gameState;
    private Car opponent;
    private Car myCar;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);
    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);

    public Bot(Random random, GameState gameState) {
        this.random = new SecureRandom();
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;

        this.directionList.add(TURN_LEFT);
        this.directionList.add(TURN_RIGHT);
    }

    public Command run() {
        int myLane = this.myCar.position.lane;
        int myBlock = this.myCar.position.block;
        int opponentLane = this.opponent.position.lane;
        int opponentBlock = this.opponent.position.block;

        List<Object> blocks = getBlocksInFront(myLane, myBlock);

        // List possible turn(s)
        if (myLane == 1) {
            this.canTurn.add(false);
            this.canTurn.add(true);
        } else if (myLane == 4) {
            this.canTurn.add(true);
            this.canTurn.add(false);
        } else {
            this.canTurn.add(true);
            this.canTurn.add(true);
        }

        // Fix logic
        if (this.myCar.damage >= 2 && !myCar.boosting) {
            return FIX;
        }

        // Restart logic
        if (this.myCar.speed == 0) {
            return ACCELERATE;
        }

        // Collision avoidance logic
        if (blocks.contains(Terrain.MUD) || blocks.contains(Terrain.OIL_SPILL) || blocks.contains(Terrain.WALL)) {
            List<Object> leftBlocks = new ArrayList<>();
            List<Object> rightBlocks = new ArrayList<>();

            if (this.canTurn.get(0)) {
                leftBlocks = getBlocksInFront(myLane - 1, myBlock);
            }
            if (this.canTurn.get(1)) {
                rightBlocks = getBlocksInFront(myLane + 1, myBlock);
            }

            if (this.canTurn.get(0) && this.canTurn.get(1)) {
                if ((leftBlocks.contains(Terrain.MUD) || leftBlocks.contains(Terrain.OIL_SPILL) || leftBlocks.contains(Terrain.WALL))
                        && (rightBlocks.contains(Terrain.MUD) || rightBlocks.contains(Terrain.OIL_SPILL) || rightBlocks.contains(Terrain.WALL))
                        && hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    return LIZARD;
                } else if (leftBlocks.contains(Terrain.MUD) || leftBlocks.contains(Terrain.OIL_SPILL) || leftBlocks.contains(Terrain.WALL)) {
                    return TURN_RIGHT;
                } else if (rightBlocks.contains(Terrain.MUD) || rightBlocks.contains(Terrain.OIL_SPILL) || rightBlocks.contains(Terrain.WALL)) {
                    return TURN_LEFT;
                } else if (leftBlocks.contains(Terrain.BOOST) || leftBlocks.contains(Terrain.OIL_POWER) || leftBlocks.contains(Terrain.EMP)
                        || leftBlocks.contains(Terrain.TWEET) || leftBlocks.contains(Terrain.LIZARD)) {
                    return TURN_LEFT;
                } else if (rightBlocks.contains(Terrain.BOOST) || rightBlocks.contains(Terrain.OIL_POWER) || rightBlocks.contains(Terrain.EMP)
                        || rightBlocks.contains(Terrain.TWEET) || rightBlocks.contains(Terrain.LIZARD)) {
                    return TURN_RIGHT;
                } else {
                    int i = this.random.nextInt(directionList.size());
                    return this.directionList.get(i);
                }
            } else if (this.canTurn.get(0)) {
                if ((leftBlocks.contains(Terrain.MUD) || leftBlocks.contains(Terrain.OIL_SPILL) || leftBlocks.contains(Terrain.WALL))
                        && hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    return LIZARD;
                } else {
                    return TURN_LEFT;
                }
            } else if (this.canTurn.get(1)) {
                if ((rightBlocks.contains(Terrain.MUD) || rightBlocks.contains(Terrain.OIL_SPILL) || rightBlocks.contains(Terrain.WALL))
                        && hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    return LIZARD;
                } else {
                    return TURN_RIGHT;
                }
            }
        }

        // Accelerate logic
        if (this.myCar.speed <= 5) {
            return ACCELERATE;
        }

        // Finish boost logic
        if (blocks.contains(Terrain.FINISH) && hasPowerUp(PowerUps.BOOST, this.myCar.powerups)) {
            return BOOST;
        }

        // EMP logic
        if (hasPowerUp(PowerUps.EMP, this.myCar.powerups)
                && opponentBlock > myBlock
                && abs(opponentLane - myLane) <= 1) {
            return EMP;
        }

        // Tweet logic
        if (hasPowerUp(PowerUps.TWEET, this.myCar.powerups) && this.myCar.speed >= Bot.maxSpeed
                && !blocks.contains(Terrain.FINISH)) {
            switch (this.opponent.speed) {
                case 0 : return new TweetCommand(opponentLane, opponentBlock + 4);
                case 3 : return new TweetCommand(opponentLane, opponentBlock + 7);
                case 6 : return new TweetCommand(opponentLane, opponentBlock + 9);
                case 8 : // Merged with case 9
                case 9 : return new TweetCommand(opponentLane, opponentBlock + 10);
                case 15 : return new TweetCommand(opponentLane, opponentBlock + 16);
            }
        }

        // Oil logic
        if (hasPowerUp(PowerUps.OIL, this.myCar.powerups)
                && opponentBlock < myBlock
                && abs(opponentLane - myLane) <= 1) {
            return OIL;
        }

        // Boost logic
        if (hasPowerUp(PowerUps.BOOST, this.myCar.powerups) && !this.myCar.boosting) {
            return BOOST;
        }

        return ACCELERATE;
    }

    /**
     * Checks whether the car has a certain power-up
     * @param powerUpToCheck the power-up being checked
     * @param available list of power-up(s) the car has
     * @return {@code true} if the power-up is found, {@code false} otherwise
     */
    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp: available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     * @param lane y-position of car
     * @param block x-position of car
     * @return list of blocks in front of car
     */
    private List<Object> getBlocksInFront(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

}

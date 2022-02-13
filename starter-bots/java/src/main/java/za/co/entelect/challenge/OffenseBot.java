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
public class OffenseBot {

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

    public OffenseBot(GameState gameState) {
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
                if (leftBlocks.contains(Terrain.OIL_POWER) || leftBlocks.contains(Terrain.EMP)
                        || leftBlocks.contains(Terrain.TWEET)) {
                    return TURN_LEFT;
                } else if (rightBlocks.contains(Terrain.OIL_POWER) || rightBlocks.contains(Terrain.EMP)
                        || rightBlocks.contains(Terrain.TWEET)) {
                    return TURN_RIGHT;
                } else {
                    int i = this.random.nextInt(directionList.size());
                    return this.directionList.get(i);
                }
            } else if (this.canTurn.get(0)) {
                return TURN_LEFT;
            } else if (this.canTurn.get(1)) {
                return TURN_RIGHT;
            } else {
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    return LIZARD;
                } else {
                    return ACCELERATE;
                }
            }
        }

        // Tweet logic
        if (hasPowerUp(PowerUps.TWEET, this.myCar.powerups) && this.myCar.speed == OffenseBot.maxSpeed
                && !blocks.contains(Terrain.FINISH)) {
            return new TweetCommand(opponentLane, opponentBlock + this.opponent.speed);
        }

        // Oil logic
        if (hasPowerUp(PowerUps.OIL, this.myCar.powerups)
                && opponentBlock < myBlock
                && abs(opponentLane - myLane) <= 1) {
            return OIL;
        }

        // EMP logic
        // Opponent's coordinate must satisfy y > x && y > -x
        if (hasPowerUp(PowerUps.EMP, this.myCar.powerups)
                && opponentBlock - myBlock > negateExact(opponentLane - myLane)
                && opponentBlock - myBlock > opponentLane - myLane) {
            return EMP;
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
        for (int i = max(block - startBlock, 0); i <= block - startBlock + OffenseBot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

}

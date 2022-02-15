package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Integer> directionList = new ArrayList<>();
    private Random random;
    private Car myCar;
    private Car opponent;
    private GameState gamestate;
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gamestate = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
        directionList.add(1);
        directionList.add(-1);
    }

    public Command run() {
        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block);
        List<Object> track1 = getBlocksInFront(1, myCar.position.block);
        List<Object> track2 = getBlocksInFront(2, myCar.position.block);
        List<Object> track3 = getBlocksInFront(3, myCar.position.block);
        List<Object> track4 = getBlocksInFront(4, myCar.position.block);

        List<Object> nextBlocks = blocks.subList(0,1);//ambil elemen pertama, taruh di list

        // LIHAT KONDISI DIRI SENDIRI

        if (myCar.damage == 5) {
            return FIX;
        }

        if (myCar.speed < 4){
            return ACCELERATE;
        }

        if (myCar.damage > 3) {
            return FIX;
        }

        // LIHAT KONDISI LAPANGAN DI DEPANNYA

        // MUD/WALL/OIL SPILL -> ganti arah
        if(obstacleOnLine(blocks)) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) { // pake lizard kalo ada powerup biar bisa loncat
                return LIZARD;
            } else {
                if (myCar.position.lane == 1){
                    return TURN_RIGHT;
                } else if(myCar.position.lane == 4){
                    return TURN_LEFT;
                } else {
                    // at this point bisa belok kiri belok kanan, pilih yang gaada obstacle & ada boost
                    List<Object> RightBlocks = getBlocksInFront(myCar.position.lane+1, myCar.position.block);
                    List<Object> LeftBlocks = getBlocksInFront(myCar.position.lane-1, myCar.position.block);
                    if(obstacleOnLine(RightBlocks)){
                        return TURN_LEFT;
                    }
                    if(obstacleOnLine(LeftBlocks)){
                        return TURN_RIGHT;
                    }
                    // kalo kanan kiri aman, pilih yang ada boostnya
                    if(powerUpOnLine(RightBlocks)){
                        return TURN_RIGHT;
                    }
                    return TURN_LEFT;
                }
            }

        }

        // BOOST -> ambil, langsung pake
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return BOOST;
        }

        // TODO: pikirin juga buat nguliin semua kemungkinan getBlocksInFront(<1 sampe 4>, myCar.position.block)
        // buat nyari semua boost

        // KALO KONDISI DIRI SENDIRI DAN LAPANGAN LANCAR

        return ACCELERATE;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block) {
        List<Lane[]> map = this.gamestate.lanes;
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

    private boolean obstacleOnLine(List<Object> blocks){
        return blocks.contains(Terrain.MUD) || blocks.contains(Terrain.WALL) || blocks.contains(Terrain.OIL_SPILL);
    }

    private boolean powerUpOnLine(List<Object> blocks){
        return blocks.contains(Terrain.OIL_POWER) || blocks.contains(Terrain.BOOST) ||
        blocks.contains(Terrain.LIZARD) || blocks.contains(Terrain.TWEET) || blocks.contains(Terrain.EMP);
    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp: available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

}

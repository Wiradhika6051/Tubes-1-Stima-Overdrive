package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

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
        List<Object> nextBlock = blocks.subList(0,1);//ambil elemen pertama, taruh di list
        if (myCar.damage >= 5) {
            return FIX;
        }
        if (blocks.contains(Terrain.MUD)||blocks.contains(Terrain.WALL)) {
            int i=0;//menentukan arah belok(default ke kanan)
            //dapetin list block di sebelah kanan
            if(this.myCar.position.lane !=4) {//masih bisa belok kanan
                List<Object> RightBlocks = getBlocksInFront(myCar.position.lane+1, myCar.position.block);
                if((!RightBlocks.contains(Terrain.MUD)&&!RightBlocks.contains(Terrain.WALL))||this.myCar.position.lane == 1){
                    //kalau di lane 1 mau gak mau harus ke kanan
                    i = 0;
                }
            }
            //dapetin list block di sebelah kiri
            if(this.myCar.position.lane !=1) {//masih bisa belok kiri
                List<Object> LeftBlocks = getBlocksInFront(myCar.position.lane-1, myCar.position.block);
                if((!LeftBlocks.contains(Terrain.MUD)&& !LeftBlocks.contains(Terrain.WALL))||this.myCar.position.lane == 4){
                    //kalau di lane 4 mau gak mau ke kiri
                    i = 1;
                }
            }

            return new ChangeLaneCommand(directionList.get(i));
        }
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

}

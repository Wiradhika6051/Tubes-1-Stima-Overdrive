package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {

    //private static final int maxSpeed = 9;
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
    private final static Command DO_NOTHING = new DoNothingCommand();

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gamestate = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
        directionList.add(1);
        directionList.add(-1);
    }

    public Command run() {
        List<Object> lane1 = getBlocksInFront(1, myCar.position.block,myCar.speed);
        List<Object> lane2 = getBlocksInFront(2, myCar.position.block,myCar.speed);
        List<Object> lane3 = getBlocksInFront(3, myCar.position.block,myCar.speed);
        List<Object> lane4 = getBlocksInFront(4, myCar.position.block,myCar.speed);
        int currLane = myCar.position.lane-1;
        //List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block,myCar.speed);
        //List<Object> nextBlock = blocks.subList(0,1);//ambil elemen pertama, taruh di list
        if (myCar.damage >= 5) {
            return FIX;
        }
        if (myCar.speed==0){
            return ACCELERATE;
        }
        int[] lanesrisk = new int[4];
        lanesrisk[0] = getRiskValue(lane1);
        lanesrisk[1] = getRiskValue(lane2);
        lanesrisk[2] = getRiskValue(lane3);
        lanesrisk[3] = getRiskValue(lane4);
        int decision = 0;//-1 berarti ke kiri, 0 tetep, 1 ke kanan
        if(lanesrisk[currLane]>0) {
            if(currLane==0) {//cuma bisa lurus atau ke kanan
                if (lanesrisk[currLane] > lanesrisk[currLane + 1]) {
                    decision = 1;
                }
            }
            else if(currLane==3) {//cuma bisa lurus sama ke kiri
                if (lanesrisk[currLane] > lanesrisk[currLane - 1]) {
                    decision = -1;
                }
            }
            else {
                int lowestRiskSideLanes = lanesrisk[currLane-1] < lanesrisk[currLane+1] ? currLane - 1 : currLane + 1;
                int finalDecision = lanesrisk[lowestRiskSideLanes] < lanesrisk[currLane] ? lowestRiskSideLanes : currLane;
                decision = finalDecision-currLane;
            }
            if(decision!=0){
                return new ChangeLaneCommand(decision);
            }
            else if(hasPowerUps(myCar.powerups,PowerUps.LIZARD)){
                return LIZARD;
            }
        }
        /*
        if((blocks.contains(Terrain.MUD)||blocks.contains(Terrain.WALL))) {//kalau lagi berhenti accelerate dulu
            int i=0;//menentukan arah belok(default ke kanan)
            //dapetin list block di sebelah kanan
            if(this.myCar.position.lane !=4) {//masih bisa belok kanan
                List<Object> RightBlocks = getBlocksInFront(myCar.position.lane+1, myCar.position.block, myCar.speed);
                if((!RightBlocks.contains(Terrain.MUD)&&!RightBlocks.contains(Terrain.WALL))||this.myCar.position.lane == 1){
                    //kalau di lane 1 mau gak mau harus ke kanan
                    i = 0;
                }
            }
            //dapetin list block di sebelah kiri
            if(this.myCar.position.lane !=1) {//masih bisa belok kiri
                List<Object> LeftBlocks = getBlocksInFront(myCar.position.lane-1, myCar.position.block, myCar.speed);
                if((!LeftBlocks.contains(Terrain.MUD)&& !LeftBlocks.contains(Terrain.WALL))||this.myCar.position.lane == 4){
                    //kalau di lane 4 mau gak mau ke kiri
                    i = 1;
                }
            }
            */
            if(myCar.damage>0){
                return FIX;
            }
            if(hasPowerUps(myCar.powerups,PowerUps.BOOST)&& lanesrisk[currLane]==0&&!myCar.boosting){
                return BOOST;
            }
            if(!myCar.boosting) {
                return ACCELERATE;
            }
            return DO_NOTHING;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block,int speed) {
        List<Lane[]> map = this.gamestate.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }
    private boolean hasPowerUps(PowerUps[] list,PowerUps powerup){
        if(list==null || list.length==0){
            return false;
        }
        if(Arrays.asList(list).contains(powerup)){
            return true;
        }
        //for(PowerUps elmt: list){
        //    if(elmt.equals(powerup)){
         //       return true;
        //    }
        //}
        return false;
    }
    private int getRiskValue(List<Object> lanes){
        int riskValues = 0;
        for(int i=0;i<lanes.size();i++){
            if(lanes.get(i)==Terrain.OIL_SPILL){
                    riskValues += 1;
            }
            else if(lanes.get(i)==Terrain.MUD){
                riskValues += 2;
            }
            else if(lanes.get(i)==Terrain.WALL){
                riskValues += 3;
            }
        }
        return riskValues;
    }

}

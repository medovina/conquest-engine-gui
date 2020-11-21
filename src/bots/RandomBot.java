package bots;

import java.util.*;

import engine.Bot;
import game.*;
import game.move.*;

public class RandomBot implements Bot
{
    Random rand = new Random(0);
    
    // Move randomly.

    @Override
    public void init(long timeoutMillis) {
    }
    
    // Choose a starting region.
    
    @Override
    public MapRegion chooseRegion(Game state) {
        ArrayList<Region> choosable = state.getPickableRegions();
        return choosable.get(rand.nextInt(choosable.size())).getMapRegion();
    }

    // Decide where to place armies this turn.
    // state.armiesPerTurn(state.me()) is the number of armies available to place.
    
    @Override
    public List<PlaceArmiesMove> placeArmies(Game state) {
        int me = state.currentPlayer();
        List<Region> mine = state.regionsOwnedBy(me);
        int numRegions = mine.size();
        
        int[] count = new int[numRegions];
        for (int i = 0 ; i < state.armiesPerTurn(me) ; ++i) {
            int r = rand.nextInt(numRegions);
            count[r]++;
        }
        
        List<PlaceArmiesMove> ret = new ArrayList<PlaceArmiesMove>();
        for (int i = 0 ; i < numRegions ; ++i)
            if (count[i] > 0)
                ret.add(new PlaceArmiesMove(mine.get(i), count[i]));
        return ret;
    }
    
    // Decide where to move armies this turn.
    
    @Override
    public List<AttackTransferMove> moveArmies(Game state) {
        List<AttackTransferMove> ret = new ArrayList<AttackTransferMove>();
        
        for (Region rd : state.regionsOwnedBy(state.currentPlayer())) {
            int count = rand.nextInt(rd.getArmies());
            if (count > 0) {
                List<Region> neighbors = rd.getNeighbors();
                Region to = neighbors.get(rand.nextInt(neighbors.size()));
                ret.add(new AttackTransferMove(rd, to, count));
            }
        }
        return ret;        
    }
}

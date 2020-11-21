package game.move;

import java.util.List;

import game.Game;

public class PlaceArmiesAction implements Action {
    public List<PlaceArmiesMove> commands;
    
    public PlaceArmiesAction(List<PlaceArmiesMove> commands) { this.commands = commands; }
    
    public void apply(Game state) {
        state.placeArmies(commands);
    }
}

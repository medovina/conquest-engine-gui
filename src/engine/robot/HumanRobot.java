package engine.robot;

import java.util.List;

import engine.Bot;
import game.*;
import game.move.AttackTransferMove;
import game.move.PlaceArmiesMove;
import game.world.MapRegion;
import view.GUI;

public class HumanRobot implements Bot {
    private GUI gui;
    
    public HumanRobot(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void init(long timeoutMillis) {
    }

    @Override
    public MapRegion chooseRegion(GameState state) {
        return gui.chooseRegionHuman().getWorldRegion();
    }

    @Override
    public List<PlaceArmiesMove> placeArmies(GameState state) {
        return gui.placeArmiesHuman();
    }

    @Override
    public List<AttackTransferMove> moveArmies(GameState state) {
        return gui.moveArmiesHuman();
    }
}

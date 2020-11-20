// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//    
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package engine;

import java.io.IOException;

import engine.replay.FileGameLog;
import engine.replay.GameLog;
import engine.robot.HumanRobot;
import engine.robot.InternalRobot;
import game.*;
import view.GUI;

public class RunGame
{
    Config config;
    
    Engine engine;
    GameState game;
    
    public RunGame(Config config)
    {
        this.config = config;        
    }
    
    public GameResult go()
    { 
        try {
            GameLog log = null;
            if (config.replayLog != null) {
                log = new FileGameLog(config.replayLog);
            }
            
            String[] playerNames = new String[2];
            Robot[] robots = new Robot[2];
            
            robots[0] = setupRobot(1, config.bot1Init);
            robots[1] = setupRobot(2, config.bot2Init);
                    
            playerNames[0] = config.player1Name;
            playerNames[1] = config.player2Name;
                        
            return go(log, playerNames, robots);
        } catch (Exception e) {
            throw new RuntimeException("Failed to run/finish the game.", e);
        }
    }

    private GameResult go(GameLog log, String[] playerNames, Robot[] robots) throws InterruptedException {
        game = new GameState(config.game, null);

        GUI gui;
        if (config.visualize) {
            gui = new GUI(game, robots, config);
            if (config.visualizeContinual != null) {
                gui.setContinual(config.visualizeContinual);
            }
            if (config.visualizeContinualFrameTimeMillis != null) {
                gui.setContinualFrameTime(config.visualizeContinualFrameTimeMillis);
            }
            game.setGUI(gui);
        } else gui = null;
        
        //start the engine
        this.engine = new Engine(game, robots, gui, config.botCommandTimeoutMillis);
        
        if (log != null) {
            log.start(config);
        }
        
        for (int i = 1 ; i <= 2 ; ++i) {
            RobotConfig robotCfg =
                    new RobotConfig(i, playerNames[i - 1],
                            config.botCommandTimeoutMillis, log, config.logToConsole, gui);
            robots[i - 1].setup(robotCfg);
        }
        
        //send the bots the info they need to start
        engine.distributeStartingRegions(); //decide the players' starting regions
        
        //play the game
        while(!game.isDone())
        {
            if (log != null) {
                log.logComment(0, "Round " + game.getRoundNumber());
            }
            engine.playRound();
        }

        GameResult result = finish(game.getMap(), robots);
        
        if (log != null) {
            log.finish(result);
        }
        
        return result;
    }

    private Robot setupRobot(int player, String botInit) throws IOException {
        if (botInit.startsWith("internal:")) {
            String botFQCN = botInit.substring(9);
            return new InternalRobot(player, botFQCN);
        }
        if (botInit.startsWith("human")) {
            config.visualize = true;
            return new HumanRobot();
        }
        throw new RuntimeException("Invalid init string for player '" + player +
                "', must start either with 'process:' or 'internal:' or 'human', passed value was: " + botInit);
    }

    private GameResult finish(GameMap map, Robot[] bots) throws InterruptedException
    {
        return this.saveGame(map);        
    }
    
    public GameResult saveGame(GameMap map) {

        GameResult result = new GameResult();
        
        result.config = config;
        result.player1Regions = map.numberRegionsOwned(1);
        result.player1Armies = map.numberArmiesOwned(1);
        result.player2Regions = map.numberRegionsOwned(2);
        result.player2Armies = map.numberArmiesOwned(2);

        result.winner = game.winningPlayer();
        result.round = game.getRoundNumber();
        
        return result;
    }
}

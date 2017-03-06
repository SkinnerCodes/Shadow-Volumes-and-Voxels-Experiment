package client;

import optic.framework.GameState;

/**
 * Created with IntelliJ IDEA.
 * User: Joseph
 * Date: 1/25/14
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameStateSwitcher {

    public GameState activeState;

    public void initState() {
        activeState.init();
    }

    public void updateState(float lastFrameDuration) {

        activeState.update(lastFrameDuration);
    }

    public void displayState(float lastFrameDuration) {

        activeState.display(lastFrameDuration);

    }

    public void reshapeWindow() {
        activeState.reshapeWindow();
    }
}

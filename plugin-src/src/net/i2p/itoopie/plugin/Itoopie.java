package net.i2p.itoopie.plugin;
/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import javax.swing.UIManager;

import net.i2p.I2PAppContext;
import net.i2p.app.*;
import static net.i2p.app.ClientAppState.*;
import net.i2p.util.Log;

import net.i2p.itoopie.gui.GUIHelper;
import net.i2p.itoopie.gui.TrayManager;
import net.i2p.itoopie.gui.WindowHandler;

/**
 *
 * @author zzz
 */
public class Itoopie implements ClientApp {
    private final I2PAppContext _context;
    private final Log _log;
    private final ClientAppManager _mgr;
    private TrayManager trayManager;
    private ClientAppState _state = UNINITIALIZED;

    public Itoopie(I2PAppContext ctx, ClientAppManager mgr, String args[]) {
        _context = ctx;
        _log = ctx.logManager().getLog(Itoopie.class);
        _mgr = mgr;
        _state = INITIALIZED;
    }

    /**
     *  Not supported
     */
    public synchronized static void main(String args[]) {
        throw new UnsupportedOperationException("Must use ClientApp interface");
    }

    /////// ClientApp methods

    public synchronized void startup() throws Exception {
        if (_state != STOPPED && _state != INITIALIZED && _state != START_FAILED) {
            _log.error("Start while state = " + _state);
            return;
        }
        trayManager = TrayManager.getInstance();
        trayManager.startManager();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            GUIHelper.setDefaultStyle();
        } catch (Exception ex) {}
        // Popup Main window.
        WindowHandler.toggleFrames();
        changeState(RUNNING);
    }

    public synchronized void shutdown(String[] args) {
        if (_state == STOPPED)
            return;
        changeState(STOPPING);
        trayManager.stopManager();
        // TODO stop it
        trayManager = null;
        changeState(STOPPED);
    }

    public ClientAppState getState() {
        return _state;
    }

    public String getName() {
        return "itoopie";
    }

    public String getDisplayName() {
        return "Itoopie";
    }

    /////// end ClientApp methods

    private synchronized void changeState(ClientAppState state) {
        if (state == _state)
            return;
        _state = state;
        _mgr.notify(this, state, null, null);
    }

    private synchronized void changeState(ClientAppState state, String msg, Exception e) {
        if (state == _state)
            return;
        _state = state;
        _mgr.notify(this, state, msg, e);
    }
}

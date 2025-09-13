import type {CapacitorElectronConfig} from '@capacitor-community/electron';
import {getCapacitorElectronConfig, setupElectronDeepLinking} from '@capacitor-community/electron';
import type {MenuItemConstructorOptions} from 'electron';
import {app, MenuItem} from 'electron';
import electronIsDev from 'electron-is-dev';
import unhandled from 'electron-unhandled';
import {autoUpdater} from 'electron-updater';

import {ElectronCapacitorApp, setupContentSecurityPolicy, setupReloadWatcher} from './setup';
import {BrowserWindow, ipcMain, dialog} from 'electron';
import {join} from "path";

let mainWindow: BrowserWindow | null = null;

app.on('ready', () => {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    webPreferences: {
      preload: join(__dirname, 'preload.js'), // atenție: .js, nu .ts
      contextIsolation: true,
      nodeIntegration: false
    }
  });

  // Încarcă Angular-ul
  mainWindow.loadURL('http://localhost:4200'); // dev
  // mainWindow.loadFile(join(__dirname, '../app/index.html')); // build
});

// 🔹 IPC pentru alegerea folderului
ipcMain.handle('select-folder', async () => {
  const result = await dialog.showOpenDialog({
    properties: ['openDirectory'] // doar foldere
  });

  if (result.canceled || result.filePaths.length === 0) {
    return null;
  }

  return result.filePaths[0]; // returnează path-ul complet
});


// Graceful handling of unhandled errors.
unhandled();

// Define our menu templates (these are optional)
const trayMenuTemplate: (MenuItemConstructorOptions | MenuItem)[] = [new MenuItem({label: 'Quit App', role: 'quit'})];
const appMenuBarMenuTemplate: (MenuItemConstructorOptions | MenuItem)[] = [
  {role: process.platform === 'darwin' ? 'appMenu' : 'fileMenu'},
  {role: 'viewMenu'},
];

// Get Config options from capacitor.config
const capacitorFileConfig: CapacitorElectronConfig = getCapacitorElectronConfig();

// Initialize our app. You can pass menu templates into the app here.
// const myCapacitorApp = new ElectronCapacitorApp(capacitorFileConfig);
const myCapacitorApp = new ElectronCapacitorApp(capacitorFileConfig, trayMenuTemplate, appMenuBarMenuTemplate);

// If deeplinking is enabled then we will set it up here.
if (capacitorFileConfig.electron?.deepLinkingEnabled) {
  setupElectronDeepLinking(myCapacitorApp, {
    customProtocol: capacitorFileConfig.electron.deepLinkingCustomProtocol ?? 'mycapacitorapp',
  });
}

// If we are in Dev mode, use the file watcher components.
if (electronIsDev) {
  setupReloadWatcher(myCapacitorApp);
}

// Run Application
(async () => {
  // Wait for electron app to be ready.
  await app.whenReady();
  // Security - Set Content-Security-Policy based on whether or not we are in dev mode.
  setupContentSecurityPolicy(myCapacitorApp.getCustomURLScheme());
  // Initialize our app, build windows, and load content.
  await myCapacitorApp.init();
  // Check for updates if we are in a packaged app.
  autoUpdater.checkForUpdatesAndNotify();
})();

// Handle when all of our windows are close (platforms have their own expectations).
app.on('window-all-closed', function () {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// When the dock icon is clicked.
app.on('activate', async function () {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (myCapacitorApp.getMainWindow().isDestroyed()) {
    await myCapacitorApp.init();
  }
});

// Place all ipc or other electron api calls and custom functionality under this line

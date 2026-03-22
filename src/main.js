const path = require('path');
const { app, BrowserWindow, ipcMain, shell, WebContentsView } = require('electron');

const HOME_URL = 'https://www.google.com';
const ALLOWED_WINDOW_OPEN_PROTOCOLS = new Set(['http:', 'https:']);

let mainWindow;
let browserView;

function normalizeUrl(input) {
  const value = String(input || '').trim();
  if (!value) {
    return HOME_URL;
  }

  const hasProtocol = /^[a-zA-Z][a-zA-Z\d+.-]*:/.test(value);
  const candidate = hasProtocol ? value : `https://${value}`;

  try {
    const parsed = new URL(candidate);
    if (parsed.protocol === 'http:' || parsed.protocol === 'https:') {
      return parsed.toString();
    }
  } catch {
    const search = new URL('https://www.google.com/search');
    search.searchParams.set('q', value);
    return search.toString();
  }

  const search = new URL('https://www.google.com/search');
  search.searchParams.set('q', value);
  return search.toString();
}

function sendState() {
  if (!mainWindow || !browserView) {
    return;
  }

  const webContents = browserView.webContents;
  mainWindow.webContents.send('browser:state', {
    canGoBack: webContents.navigationHistory.canGoBack(),
    canGoForward: webContents.navigationHistory.canGoForward(),
    isLoading: webContents.isLoading(),
    title: webContents.getTitle() || 'Browshere',
    url: webContents.getURL() || HOME_URL
  });
}

function layoutBrowserView() {
  if (!mainWindow || !browserView) {
    return;
  }

  const bounds = mainWindow.getContentBounds();
  const toolbarHeight = 92;
  browserView.setBounds({
    x: 0,
    y: toolbarHeight,
    width: bounds.width,
    height: Math.max(bounds.height - toolbarHeight, 0)
  });
}

function attachBrowserView() {
  browserView = new WebContentsView({
    webPreferences: {
      devTools: true,
      javascript: true,
      webSecurity: true,
      allowRunningInsecureContent: false,
      nodeIntegration: false,
      contextIsolation: true,
      sandbox: true
    }
  });

  mainWindow.contentView.addChildView(browserView);
  layoutBrowserView();

  browserView.webContents.setWindowOpenHandler(({ url }) => {
    try {
      const parsed = new URL(url);
      if (!ALLOWED_WINDOW_OPEN_PROTOCOLS.has(parsed.protocol)) {
        return { action: 'deny' };
      }
    } catch {
      return { action: 'deny' };
    }

    shell.openExternal(url);
    return { action: 'deny' };
  });

  browserView.webContents.on('did-start-loading', sendState);
  browserView.webContents.on('did-stop-loading', sendState);
  browserView.webContents.on('page-title-updated', sendState);
  browserView.webContents.on('did-navigate', sendState);
  browserView.webContents.on('did-navigate-in-page', sendState);

  browserView.webContents.on('will-navigate', (event, url) => {
    try {
      const parsed = new URL(url);
      if (!ALLOWED_WINDOW_OPEN_PROTOCOLS.has(parsed.protocol)) {
        event.preventDefault();
      }
    } catch {
      event.preventDefault();
    }
  });

  browserView.webContents.loadURL(HOME_URL);
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1440,
    height: 960,
    minWidth: 960,
    minHeight: 680,
    title: 'Browshere',
    backgroundColor: '#081120',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: false
    }
  });

  mainWindow.loadFile(path.join(__dirname, '..', 'renderer', 'index.html'));
  attachBrowserView();

  mainWindow.on('resize', layoutBrowserView);
  mainWindow.on('enter-full-screen', layoutBrowserView);
  mainWindow.on('leave-full-screen', layoutBrowserView);
  mainWindow.webContents.on('did-finish-load', sendState);
}

ipcMain.handle('browser:navigate', async (_event, input) => {
  if (!browserView) {
    return;
  }

  await browserView.webContents.loadURL(normalizeUrl(input));
  sendState();
});

ipcMain.handle('browser:back', async () => {
  if (browserView?.webContents.navigationHistory.canGoBack()) {
    browserView.webContents.navigationHistory.goBack();
  }
  sendState();
});

ipcMain.handle('browser:forward', async () => {
  if (browserView?.webContents.navigationHistory.canGoForward()) {
    browserView.webContents.navigationHistory.goForward();
  }
  sendState();
});

ipcMain.handle('browser:reload', async () => {
  browserView?.webContents.reload();
  sendState();
});

ipcMain.handle('browser:home', async () => {
  if (!browserView) {
    return;
  }

  await browserView.webContents.loadURL(HOME_URL);
  sendState();
});

app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

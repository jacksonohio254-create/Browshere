# Browshere

Browshere is now a runnable desktop browser shell built with Electron's Chromium runtime instead of a static landing page. It provides a real address bar, back/forward/reload controls, a home action, and a live browser surface managed by Electron's `WebContentsView`.

## What it does

- Opens real websites in a Chromium-powered browser view.
- Supports search-or-navigate behavior from the address bar.
- Exposes back, forward, reload, and home controls in a native desktop window.
- Surfaces lightweight security status by distinguishing HTTPS pages from non-HTTPS pages.
- Blocks unsupported external protocols from automatic in-app navigation and opens allowed popup targets in the system browser.

## Project structure

- `package.json` — Electron app metadata and scripts.
- `src/main.js` — main-process window creation, browser view layout, navigation, and safety guards.
- `src/preload.js` — secure IPC bridge for renderer controls.
- `renderer/index.html` — browser chrome UI.
- `renderer/app.css` — desktop browser styling.
- `renderer/app.js` — toolbar interactions and live status updates.
- `assets/logo.svg` — Browshere logo.

## Run locally

1. Install dependencies:

   ```bash
   npm install
   ```

2. Start the browser:

   ```bash
   npm start
   ```

## Validation

Run the lightweight code checks with:

```bash
npm run check
```

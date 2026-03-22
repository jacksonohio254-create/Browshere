const addressForm = document.getElementById('addressForm');
const addressInput = document.getElementById('addressInput');
const backButton = document.getElementById('backButton');
const forwardButton = document.getElementById('forwardButton');
const reloadButton = document.getElementById('reloadButton');
const homeButton = document.getElementById('homeButton');
const pageTitle = document.getElementById('pageTitle');
const securityBadge = document.getElementById('securityBadge');

addressForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  await window.browserAPI.navigate(addressInput.value);
});

backButton.addEventListener('click', async () => {
  await window.browserAPI.back();
});

forwardButton.addEventListener('click', async () => {
  await window.browserAPI.forward();
});

reloadButton.addEventListener('click', async () => {
  await window.browserAPI.reload();
});

homeButton.addEventListener('click', async () => {
  await window.browserAPI.home();
});

window.browserAPI.onState((state) => {
  if (!state) {
    return;
  }

  addressInput.value = state.url || '';
  pageTitle.textContent = state.isLoading ? 'Loading…' : state.title || 'Browshere';
  backButton.disabled = !state.canGoBack;
  forwardButton.disabled = !state.canGoForward;

  try {
    const url = new URL(state.url);
    securityBadge.textContent = url.protocol === 'https:' ? 'HTTPS' : 'Caution';
    securityBadge.dataset.level = url.protocol === 'https:' ? 'secure' : 'warning';
  } catch {
    securityBadge.textContent = 'Protected';
    securityBadge.dataset.level = 'secure';
  }
});

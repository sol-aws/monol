const TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function isLoggedIn() {
  return Boolean(getToken());
}

function logout() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  window.location.href = '/login.html';
}

async function apiFetch(url, options = {}) {
  const headers = new Headers(options.headers || {});
  const token = getToken();
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(url, { ...options, headers });
  if (response.status === 401) {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  }
  return response;
}

function showMessage(element, text, type = 'info') {
  element.textContent = text;
  element.className = `message ${type}`;
}

const form = document.getElementById('loginForm');
const message = document.getElementById('message');

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(form).entries());

  try {
    const response = await fetch('/member/doLogin', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      throw new Error(await response.text() || '로그인에 실패했습니다.');
    }

    const result = await response.json();
    localStorage.setItem(TOKEN_KEY, result.token);
    localStorage.setItem(REFRESH_TOKEN_KEY, result.refreshToken);
    showMessage(message, '로그인되었습니다.', 'success');
    setTimeout(() => window.location.href = '/', 500);
  } catch (error) {
    showMessage(message, error.message, 'error');
  }
});

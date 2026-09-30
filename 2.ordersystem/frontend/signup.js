const form = document.getElementById('signupForm');
const message = document.getElementById('message');

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(form).entries());

  try {
    const response = await fetch('/member/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      throw new Error(await response.text() || '회원가입에 실패했습니다.');
    }

    showMessage(message, '회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.', 'success');
    setTimeout(() => window.location.href = '/login.html', 900);
  } catch (error) {
    showMessage(message, error.message, 'error');
  }
});

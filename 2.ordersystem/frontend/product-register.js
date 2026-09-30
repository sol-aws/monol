if (!isLoggedIn()) {
  window.location.href = '/login.html';
}

document.getElementById('logoutButton').addEventListener('click', logout);

const form = document.getElementById('productForm');
const message = document.getElementById('message');

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(form);

  try {
    const response = await apiFetch('/product/create', {
      method: 'POST',
      body: formData
    });

    if (response.status === 401 || response.status === 403) {
      throw new Error('로그인이 필요합니다.');
    }
    if (!response.ok) {
      throw new Error(await response.text() || '상품등록에 실패했습니다.');
    }

    showMessage(message, '상품이 등록되었습니다. 상품 페이지로 이동합니다.', 'success');
    setTimeout(() => window.location.href = '/', 700);
  } catch (error) {
    showMessage(message, error.message, 'error');
  }
});

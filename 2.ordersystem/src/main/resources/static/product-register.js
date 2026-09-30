if (!isLoggedIn()) {
  window.location.href = '/login.html';
}

document.getElementById('logoutButton').addEventListener('click', logout);

const form = document.getElementById('productForm');
const message = document.getElementById('message');
const imageInput = document.getElementById('imageInput');
const imagePreview = document.getElementById('imagePreview');
const submitButton = document.getElementById('submitButton');
const MAX_IMAGE_SIZE = 15 * 1024 * 1024;
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

function validateImage(file) {
  if (!file) return '상품 이미지를 선택하세요.';
  if (!ALLOWED_TYPES.includes(file.type)) return 'JPG, PNG, GIF, WEBP 이미지만 업로드할 수 있습니다.';
  if (file.size > MAX_IMAGE_SIZE) return '이미지는 15MB 이하만 업로드할 수 있습니다.';
  return null;
}

imageInput.addEventListener('change', () => {
  const file = imageInput.files[0];
  const error = validateImage(file);
  if (error) {
    imageInput.value = '';
    imagePreview.textContent = 'IMAGE PREVIEW';
    showMessage(message, error, 'error');
    return;
  }

  message.className = 'message hidden';
  const url = URL.createObjectURL(file);
  imagePreview.innerHTML = `<img src="${url}" alt="선택한 상품 이미지 미리보기" />`;
});

form.addEventListener('submit', async (event) => {
  event.preventDefault();

  const file = imageInput.files[0];
  const error = validateImage(file);
  if (error) {
    showMessage(message, error, 'error');
    return;
  }

  const formData = new FormData(form);
  submitButton.disabled = true;
  submitButton.textContent = 'S3 업로드 및 상품등록 중...';

  try {
    const response = await apiFetch('/product/create', {
      method: 'POST',
      body: formData
    });

    if (response.status === 401 || response.status === 403) {
      throw new Error('로그인 정보가 만료되었습니다. 다시 로그인하세요.');
    }
    if (!response.ok) {
      throw new Error(await response.text() || '상품등록에 실패했습니다.');
    }

    showMessage(message, '상품과 이미지가 정상 등록되었습니다. 메인 페이지로 이동합니다.', 'success');
    setTimeout(() => window.location.href = '/', 800);
  } catch (error) {
    showMessage(message, error.message, 'error');
    submitButton.disabled = false;
    submitButton.textContent = '상품등록';
  }
});

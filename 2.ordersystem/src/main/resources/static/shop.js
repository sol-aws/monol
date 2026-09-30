const productGrid = document.getElementById('productGrid');
const message = document.getElementById('message');
const loginState = document.getElementById('loginState');
const loginLink = document.getElementById('loginLink');
const signupLink = document.getElementById('signupLink');
const logoutButton = document.getElementById('logoutButton');

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function updateLoginMenu() {
  if (isLoggedIn()) {
    loginState.textContent = '로그인 상태 · 상품등록과 주문이 가능합니다.';
    loginLink.classList.add('hidden');
    signupLink.classList.add('hidden');
    logoutButton.classList.remove('hidden');
  } else {
    loginState.textContent = '로그인 후 상품등록과 주문 기능을 사용할 수 있습니다.';
  }
}

logoutButton.addEventListener('click', logout);
document.getElementById('refreshButton').addEventListener('click', loadProducts);

function productCard(product) {
  const name = escapeHtml(product.name);
  const category = escapeHtml(product.category || '기타');
  const imageUrl = product.imageUrl ? escapeHtml(product.imageUrl) : '';
  const image = imageUrl
    ? `<img src="${imageUrl}" alt="${name}" loading="lazy" onerror="this.parentElement.innerHTML='<div class=\'no-image\'>IMAGE LOAD FAILED</div>'" />`
    : `<div class="no-image">NO IMAGE</div>`;

  const stock = Number(product.stockQuantity || 0);
  const soldOut = stock <= 0;
  return `
    <article class="product-card">
      <div class="image-box">${image}</div>
      <div class="product-body">
        <span class="category">${category}</span>
        <h3>${name}</h3>
        <p class="price">${Number(product.price || 0).toLocaleString('ko-KR')}원</p>
        <p class="stock">재고 ${stock.toLocaleString('ko-KR')}개</p>
        <button class="order-button" data-id="${Number(product.id)}" ${soldOut ? 'disabled' : ''}>
          ${soldOut ? '품절' : '주문하기'}
        </button>
      </div>
    </article>`;
}

async function loadProducts() {
  productGrid.innerHTML = '<div class="empty">상품을 불러오는 중입니다...</div>';
  message.className = 'message hidden';

  try {
    const response = await fetch('/product/list', { cache: 'no-store' });
    if (!response.ok) throw new Error('상품 목록을 불러오지 못했습니다.');

    const products = await response.json();
    if (!Array.isArray(products) || products.length === 0) {
      productGrid.innerHTML = '<div class="empty">등록된 상품이 없습니다. 로그인 후 첫 상품을 등록해보세요.</div>';
      return;
    }

    productGrid.innerHTML = products.map(productCard).join('');
    document.querySelectorAll('.order-button').forEach(button => {
      button.addEventListener('click', () => orderProduct(button.dataset.id));
    });
  } catch (error) {
    productGrid.innerHTML = '';
    showMessage(message, error.message, 'error');
  }
}

async function orderProduct(productId) {
  if (!isLoggedIn()) {
    alert('주문하려면 먼저 로그인해야 합니다.');
    window.location.href = '/login.html';
    return;
  }

  const countText = prompt('주문 수량을 입력하세요.', '1');
  if (countText === null) return;
  const productCount = Number(countText);
  if (!Number.isInteger(productCount) || productCount <= 0) {
    alert('주문 수량은 1 이상의 정수여야 합니다.');
    return;
  }

  try {
    const response = await apiFetch('/ordering/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productId: Number(productId), productCount })
    });

    if (response.status === 401 || response.status === 403) {
      alert('로그인이 만료되었습니다. 다시 로그인하세요.');
      window.location.href = '/login.html';
      return;
    }
    if (!response.ok) throw new Error(await response.text() || '주문에 실패했습니다.');

    alert('주문이 완료되었습니다.');
    await loadProducts();
  } catch (error) {
    alert(error.message);
  }
}

updateLoginMenu();
loadProducts();

(function () {
    const state = {
        merchant: null,
        categories: [],
        foods: [],
        editingFood: null
    };

    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        bindEvents();

        state.merchant = await MerchantApp.requireMerchantLogin();

        await loadCategories();
        await loadFoods();
    }

    function bindEvents() {
        MerchantApp.$('#logoutBtn').addEventListener('click', logout);
        MerchantApp.$('#addFoodBtn').addEventListener('click', () => openFoodModal());
        MerchantApp.$('#closeFoodModalBtn').addEventListener('click', closeFoodModal);
        MerchantApp.$('#saveFoodBtn').addEventListener('click', saveFood);
        MerchantApp.$('#searchBtn').addEventListener('click', loadFoods);

        MerchantApp.$('#keywordInput').addEventListener('keydown', event => {
            if (event.key === 'Enter') {
                loadFoods();
            }
        });

        MerchantApp.$('#categoryFilter').addEventListener('change', loadFoods);
        MerchantApp.$('#statusFilter').addEventListener('change', loadFoods);
    }

    async function loadCategories() {
        try {
            state.categories = await MerchantApp.request('/merchant/categories');
        } catch (e) {
            state.categories = [];
            MerchantApp.toast(e.message || '分类加载失败', 'error');
        }

        renderCategorySelects();
    }

    function renderCategorySelects() {
        const filter = MerchantApp.$('#categoryFilter');
        const input = MerchantApp.$('#foodCategoryInput');

        const options = state.categories.map(category => {
            const id = MerchantApp.getField(category, ['categoryId', 'category_id'], '');
            const name = MerchantApp.getField(category, ['categoryName', 'category_name'], '分类');
            return `<option value="${MerchantApp.escapeHtml(id)}">${MerchantApp.escapeHtml(name)}</option>`;
        }).join('');

        filter.innerHTML = `<option value="">全部分类</option>${options}`;
        input.innerHTML = `<option value="">请选择分类</option>${options}`;
    }

    async function loadFoods() {
        const keyword = MerchantApp.$('#keywordInput').value.trim();
        const categoryId = MerchantApp.$('#categoryFilter').value;
        const status = MerchantApp.$('#statusFilter').value;

        const params = new URLSearchParams();

        if (keyword) {
            params.set('keyword', keyword);
        }

        if (categoryId) {
            params.set('categoryId', categoryId);
        }

        if (status !== '') {
            params.set('status', status);
        }

        const box = MerchantApp.$('#foodList');
        box.innerHTML = '<div class="empty-state">正在加载菜品...</div>';

        try {
            state.foods = await MerchantApp.request(`/merchant/foods/list?${params.toString()}`);
            renderFoods();
        } catch (e) {
            box.innerHTML = `<div class="empty-state">${MerchantApp.escapeHtml(e.message || '菜品加载失败')}</div>`;
        }
    }

    function renderFoods() {
        const box = MerchantApp.$('#foodList');

        if (!state.foods.length) {
            box.innerHTML = '<div class="empty-state">暂无菜品，请新增菜品</div>';
            return;
        }

        box.innerHTML = state.foods.map(food => {
            const id = MerchantApp.getField(food, ['productId', 'product_id', 'id'], '');
            const name = MerchantApp.getField(food, ['productName', 'product_name', 'name'], '未命名菜品');
            const categoryName = MerchantApp.getField(food, ['categoryName', 'category_name'], '未分类');
            const desc = MerchantApp.getField(food, ['description', 'desc'], '暂无描述');
            const price = MerchantApp.getField(food, ['price'], 0);
            const sales = MerchantApp.getField(food, ['monthlySales', 'monthly_sales', 'orderCount', 'order_count'], 0);
            const uploadDate = MerchantApp.getField(food, ['uploadDate', 'upload_date'], '');
            const status = Number(MerchantApp.getField(food, ['status'], 1));
            const isOn = status === 1;

            return `
        <article class="manage-food-card" data-id="${MerchantApp.escapeHtml(id)}">
          <div class="food-cover">${isOn ? '🍽️' : '📦'}</div>

          <div class="food-main">
            <div class="food-head">
              <div>
                <h3>${MerchantApp.escapeHtml(name)}</h3>
                <p>${MerchantApp.escapeHtml(desc || '暂无描述')}</p>
              </div>
              <span class="status-pill ${isOn ? 'done' : 'cancel'}">${isOn ? '上架中' : '已下架'}</span>
            </div>

            <div class="food-meta">
              <span>分类：${MerchantApp.escapeHtml(categoryName)}</span>
              <span>销量：${sales}</span>
              <span>上传：${MerchantApp.escapeHtml(uploadDate || '未知')}</span>
            </div>

            <div class="food-foot">
              <strong class="price">${MerchantApp.formatMoney(price)}</strong>

              <div class="order-actions">
                <button data-action="edit" data-id="${MerchantApp.escapeHtml(id)}">编辑</button>
                <button class="${isOn ? '' : 'main'}" data-action="status" data-status="${isOn ? 0 : 1}" data-id="${MerchantApp.escapeHtml(id)}">
                  ${isOn ? '下架' : '上架'}
                </button>
              </div>
            </div>
          </div>
        </article>
      `;
        }).join('');

        box.querySelectorAll('button[data-action]').forEach(btn => {
            btn.addEventListener('click', handleFoodAction);
        });
    }

    function handleFoodAction(event) {
        const action = event.currentTarget.dataset.action;
        const id = event.currentTarget.dataset.id;

        if (action === 'edit') {
            const food = state.foods.find(item => {
                const productId = MerchantApp.getField(item, ['productId', 'product_id', 'id'], '');
                return String(productId) === String(id);
            });

            openFoodModal(food);
            return;
        }

        if (action === 'status') {
            const status = Number(event.currentTarget.dataset.status);
            updateFoodStatus(id, status);
        }
    }

    function openFoodModal(food) {
        state.editingFood = food || null;

        MerchantApp.$('#foodModalTitle').textContent = food ? '修改菜品' : '新增菜品';

        MerchantApp.$('#productIdInput').value = food
            ? MerchantApp.getField(food, ['productId', 'product_id', 'id'], '')
            : '';

        MerchantApp.$('#foodNameInput').value = food
            ? MerchantApp.getField(food, ['name', 'productName', 'product_name'], '')
            : '';

        MerchantApp.$('#foodCategoryInput').value = food
            ? MerchantApp.getField(food, ['categoryId', 'category_id'], '')
            : '';

        MerchantApp.$('#foodPriceInput').value = food
            ? MerchantApp.getField(food, ['price'], '')
            : '';

        MerchantApp.$('#foodImageInput').value = food
            ? MerchantApp.getField(food, ['imageUrl', 'image_url'], '')
            : '';

        MerchantApp.$('#foodDescInput').value = food
            ? MerchantApp.getField(food, ['description', 'desc'], '')
            : '';

        MerchantApp.$('#foodModalMask').classList.remove('hidden');
    }

    function closeFoodModal() {
        MerchantApp.$('#foodModalMask').classList.add('hidden');
        state.editingFood = null;
    }

    async function saveFood() {
        const body = {
            productId: MerchantApp.$('#productIdInput').value,
            name: MerchantApp.$('#foodNameInput').value.trim(),
            categoryId: MerchantApp.$('#foodCategoryInput').value,
            price: MerchantApp.$('#foodPriceInput').value,
            imageUrl: MerchantApp.$('#foodImageInput').value.trim(),
            description: MerchantApp.$('#foodDescInput').value.trim()
        };

        if (!body.name) {
            MerchantApp.toast('请填写菜品名称', 'error');
            return;
        }

        if (!body.categoryId) {
            MerchantApp.toast('请选择菜品分类', 'error');
            return;
        }

        if (!body.price || Number(body.price) <= 0) {
            MerchantApp.toast('请填写正确的菜品价格', 'error');
            return;
        }

        const isEdit = Boolean(body.productId);
        const url = isEdit ? '/merchant/foods/update' : '/merchant/foods/add';

        try {
            await MerchantApp.request(url, {
                method: 'POST',
                body
            });

            MerchantApp.toast(isEdit ? '修改成功' : '新增成功', 'success');
            closeFoodModal();
            await loadFoods();
        } catch (e) {
            MerchantApp.toast(e.message || '保存失败', 'error');
        }
    }

    async function updateFoodStatus(productId, status) {
        const actionText = status === 1 ? '上架' : '下架';

        if (!confirm(`确定${actionText}该菜品吗？`)) {
            return;
        }

        try {
            await MerchantApp.request('/merchant/foods/status', {
                method: 'POST',
                body: { productId, status }
            });

            MerchantApp.toast(`${actionText}成功`, 'success');
            await loadFoods();
        } catch (e) {
            MerchantApp.toast(e.message || `${actionText}失败`, 'error');
        }
    }

    async function logout() {
        try {
            await MerchantApp.request('/merchant/logout', {
                method: 'POST'
            });
        } catch (e) {
            // 退出时即使后端失败，也清理本地缓存。
        }

        MerchantApp.clearMerchant();
        location.href = '/merchant/login.html';
    }
})();
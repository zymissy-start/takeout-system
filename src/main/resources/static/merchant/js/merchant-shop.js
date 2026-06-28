(function () {
    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        await MerchantApp.requireMerchantLogin();
        await loadShopInfo();

        MerchantApp.$('#shopForm').addEventListener('submit', saveShopInfo);

        // 图片上传
        MerchantApp.$('#logoUploadBtn').addEventListener('click', () => {
            MerchantApp.$('#logoFileInput').click();
        });
        MerchantApp.$('#logoFileInput').addEventListener('change', handleLogoUpload);
    }

    function updateLogoPreview(url) {
        const preview = MerchantApp.$('#logoPreview');
        if (url) {
            preview.innerHTML = `<img src="${MerchantApp.escapeHtml(url)}" alt="店铺Logo" onerror="this.parentElement.innerHTML='<span class=\\'image-placeholder-text\\'>加载失败</span>'">`;
        } else {
            preview.innerHTML = '<span class="image-placeholder-text">暂无图片</span>';
        }
    }

    async function handleLogoUpload(event) {
        const file = event.target.files[0];
        if (!file) return;

        if (file.size > 5 * 1024 * 1024) {
            MerchantApp.toast('图片大小不能超过5MB', 'error');
            event.target.value = '';
            return;
        }

        const statusEl = MerchantApp.$('#logoUploadStatus');
        statusEl.textContent = '正在上传...';

        const formData = new FormData();
        formData.append('file', file);

        try {
            const resp = await fetch('/api/upload/image', {
                method: 'POST',
                credentials: 'same-origin',
                body: formData
            });
            const json = await resp.json();

            if (json.code === 0 && json.data && json.data.url) {
                const url = json.data.url;
                MerchantApp.$('#storeLogoInput').value = url;
                updateLogoPreview(url);
                statusEl.textContent = '上传成功';
                MerchantApp.toast('图片上传成功', 'success');
            } else {
                statusEl.textContent = '上传失败';
                MerchantApp.toast(json.message || '上传失败', 'error');
            }
        } catch (e) {
            statusEl.textContent = '上传失败';
            MerchantApp.toast('上传失败：' + e.message, 'error');
        }

        event.target.value = '';
    }

    async function loadShopInfo() {
        try {
            const shop = await MerchantApp.request('/merchant/shop/current');

            MerchantApp.$('#usernameInput').value = MerchantApp.getField(shop, ['username'], '');
            MerchantApp.$('#storeNameInput').value = MerchantApp.getField(shop, ['storeName', 'store_name'], '');
            MerchantApp.$('#contactPhoneInput').value = MerchantApp.getField(shop, ['contactPhone', 'contact_phone'], '');
            MerchantApp.$('#storeAddressInput').value = MerchantApp.getField(shop, ['storeAddress', 'store_address'], '');

            const logoUrl = MerchantApp.getField(shop, ['storeLogo', 'store_logo'], '');
            MerchantApp.$('#storeLogoInput').value = logoUrl;
            updateLogoPreview(logoUrl);

            MerchantApp.$('#businessStatusInput').value = String(MerchantApp.getField(shop, ['businessStatus', 'status'], 1));
            MerchantApp.$('#minOrderAmountInput').value = MerchantApp.getField(shop, ['minOrderAmount', 'min_order_amount'], 0);
            MerchantApp.$('#storeNoticeInput').value = MerchantApp.getField(shop, ['storeNotice', 'store_notice'], '');

            MerchantApp.$('#ratingText').textContent = MerchantApp.getField(shop, ['rating'], '5.0');
            MerchantApp.$('#monthlySalesText').textContent = MerchantApp.getField(shop, ['monthlySales', 'monthly_sales'], 0);
        } catch (e) {
            MerchantApp.toast(e.message || '店铺信息加载失败', 'error');
        }
    }

    async function saveShopInfo(event) {
        event.preventDefault();

        const storeName = value('#storeNameInput');
        const contactPhone = value('#contactPhoneInput');
        const storeAddress = value('#storeAddressInput');
        const storeLogo = value('#storeLogoInput');
        const businessStatus = value('#businessStatusInput');
        const minOrderAmount = value('#minOrderAmountInput') || '0';
        const storeNotice = value('#storeNoticeInput');

        if (!storeName) {
            MerchantApp.toast('店铺名称不能为空', 'error');
            return;
        }

        if (contactPhone && !/^1\d{10}$/.test(contactPhone)) {
            MerchantApp.toast('手机号格式不正确', 'error');
            return;
        }

        if (Number(minOrderAmount) < 0) {
            MerchantApp.toast('起送价不能小于0', 'error');
            return;
        }

        try {
            await MerchantApp.request('/merchant/shop/update', {
                method: 'POST',
                body: {
                    storeName,
                    contactPhone,
                    storeAddress,
                    storeLogo,
                    businessStatus,
                    minOrderAmount,
                    storeNotice
                }
            });

            MerchantApp.toast('店铺信息保存成功', 'success');
            await loadShopInfo();
        } catch (e) {
            MerchantApp.toast(e.message || '保存失败', 'error');
        }
    }

    function value(selector) {
        const el = MerchantApp.$(selector);
        return el ? el.value.trim() : '';
    }
})();
(function () {
    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        await MerchantApp.requireMerchantLogin();
        await loadShopInfo();

        MerchantApp.$('#shopForm').addEventListener('submit', saveShopInfo);
    }

    async function loadShopInfo() {
        try {
            const shop = await MerchantApp.request('/merchant/shop/current');

            MerchantApp.$('#usernameInput').value = MerchantApp.getField(shop, ['username'], '');
            MerchantApp.$('#storeNameInput').value = MerchantApp.getField(shop, ['storeName', 'store_name'], '');
            MerchantApp.$('#contactPhoneInput').value = MerchantApp.getField(shop, ['contactPhone', 'contact_phone'], '');
            MerchantApp.$('#storeLogoInput').value = MerchantApp.getField(shop, ['storeLogo', 'store_logo'], '');
            MerchantApp.$('#businessStatusInput').value = String(MerchantApp.getField(shop, ['businessStatus', 'status'], 1));
            MerchantApp.$('#minOrderAmountInput').value = MerchantApp.getField(shop, ['minOrderAmount', 'min_order_amount'], 0);
            MerchantApp.$('#deliveryFeeInput').value = MerchantApp.getField(shop, ['deliveryFee', 'delivery_fee'], 3);
            MerchantApp.$('#deliveryTimeInput').value = MerchantApp.getField(shop, ['deliveryTime', 'delivery_time'], 30);
            MerchantApp.$('#distanceKmInput').value = MerchantApp.getField(shop, ['distanceKm', 'distance_km'], 1);
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
        const storeLogo = value('#storeLogoInput');
        const businessStatus = value('#businessStatusInput');
        const minOrderAmount = value('#minOrderAmountInput') || '0';
        const deliveryFee = value('#deliveryFeeInput') || '3';
        const deliveryTime = value('#deliveryTimeInput') || '30';
        const distanceKm = value('#distanceKmInput') || '1';
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

        if (Number(deliveryFee) < 0) {
            MerchantApp.toast('配送费不能小于0', 'error');
            return;
        }

        if (Number(deliveryTime) <= 0) {
            MerchantApp.toast('预计配送时间必须大于0', 'error');
            return;
        }

        if (Number(distanceKm) < 0) {
            MerchantApp.toast('距离不能小于0', 'error');
            return;
        }

        try {
            await MerchantApp.request('/merchant/shop/update', {
                method: 'POST',
                body: {
                    storeName,
                    contactPhone,
                    storeLogo,
                    businessStatus,
                    minOrderAmount,
                    deliveryFee,
                    deliveryTime,
                    distanceKm,
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
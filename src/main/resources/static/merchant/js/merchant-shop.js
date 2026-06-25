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
            MerchantApp.$('#shopNameInput').value = MerchantApp.getField(shop, ['shopName', 'shop_name'], '');
            MerchantApp.$('#contactPhoneInput').value = MerchantApp.getField(shop, ['contactPhone', 'contact_phone'], '');
            MerchantApp.$('#shopAddressInput').value = MerchantApp.getField(shop, ['shopAddress', 'shop_address'], '');
            MerchantApp.$('#businessHoursInput').value = MerchantApp.getField(shop, ['businessHours', 'business_hours'], '');
            MerchantApp.$('#businessStatusInput').value = String(MerchantApp.getField(shop, ['businessStatus', 'business_status'], 1));
            MerchantApp.$('#shopNoticeInput').value = MerchantApp.getField(shop, ['shopNotice', 'shop_notice'], '');
            MerchantApp.$('#deliveryDescriptionInput').value = MerchantApp.getField(shop, ['deliveryDescription', 'delivery_description'], '');
        } catch (e) {
            MerchantApp.toast(e.message || '店铺信息加载失败', 'error');
        }
    }

    async function saveShopInfo(event) {
        event.preventDefault();

        const shopName = MerchantApp.$('#shopNameInput').value.trim();
        const contactPhone = MerchantApp.$('#contactPhoneInput').value.trim();
        const shopAddress = MerchantApp.$('#shopAddressInput').value.trim();
        const businessHours = MerchantApp.$('#businessHoursInput').value.trim();
        const businessStatus = MerchantApp.$('#businessStatusInput').value;
        const shopNotice = MerchantApp.$('#shopNoticeInput').value.trim();
        const deliveryDescription = MerchantApp.$('#deliveryDescriptionInput').value.trim();

        if (!shopName) {
            MerchantApp.toast('店铺名称不能为空', 'error');
            return;
        }

        if (contactPhone && !/^1\d{10}$/.test(contactPhone)) {
            MerchantApp.toast('手机号格式不正确', 'error');
            return;
        }

        if (!shopAddress) {
            MerchantApp.toast('店铺地址不能为空', 'error');
            return;
        }

        try {
            const shop = await MerchantApp.request('/merchant/shop/update', {
                method: 'POST',
                body: {
                    shopName,
                    contactPhone,
                    shopAddress,
                    businessHours,
                    businessStatus,
                    shopNotice,
                    deliveryDescription
                }
            });

            const merchant = MerchantApp.getMerchant() || {};
            merchant.realName = MerchantApp.getField(shop, ['shopName'], shopName);
            merchant.phone = MerchantApp.getField(shop, ['contactPhone'], contactPhone);
            MerchantApp.setMerchant(merchant);

            MerchantApp.toast('店铺信息保存成功', 'success');
        } catch (e) {
            MerchantApp.toast(e.message || '保存失败', 'error');
        }
    }
})();
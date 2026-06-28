(function () {
    if (window.__aiAgentLauncherLoaded) return;
    window.__aiAgentLauncherLoaded = true;

    function addStyle() {
        if (document.getElementById('ai-agent-launcher-style')) return;
        var style = document.createElement('style');
        style.id = 'ai-agent-launcher-style';
        style.textContent = '' +
            '.ai-agent-menu-entry{background:linear-gradient(135deg,#fff4ee,#fff);border:1px solid rgba(255,106,42,.18);box-shadow:0 8px 18px rgba(255,106,42,.10)}' +
            '.ai-agent-menu-entry span{background:linear-gradient(135deg,#ff7a45,#ff4d4f);color:#fff}' +
            '.ai-agent-float{position:fixed;right:18px;bottom:88px;z-index:80;display:flex;align-items:center;gap:8px;padding:11px 14px;border-radius:999px;background:linear-gradient(135deg,#ff7a45,#ff4d4f);color:#fff;text-decoration:none;font-weight:800;box-shadow:0 12px 28px rgba(255,77,79,.28)}' +
            '.ai-agent-float small{font-size:12px;opacity:.9;font-weight:600}';
        document.head.appendChild(style);
    }

    function createEntry() {
        var a = document.createElement('a');
        a.href = '/user/ai-agent.html';
        a.className = 'quick-item ai-agent-menu-entry';
        a.innerHTML = '<span>AI</span><b>智能助手</b>';
        return a;
    }

    function createFloat() {
        var a = document.createElement('a');
        a.href = '/user/ai-agent.html';
        a.className = 'ai-agent-float';
        a.innerHTML = '<strong>AI 点餐</strong><small>推荐/优惠/骑手</small>';
        return a;
    }

    function mount() {
        addStyle();
        if (document.querySelector('.ai-agent-menu-entry') || document.querySelector('.ai-agent-float')) return;
        var menu = document.querySelector('.quick-grid, .quick-actions, .quick-list, .category-grid, .shortcut-grid');
        if (menu) {
            menu.appendChild(createEntry());
            return;
        }
        document.body.appendChild(createFloat());
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', mount);
    } else {
        mount();
    }
})();
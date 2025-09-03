// 🚀 Java Learning App - Frontend JavaScript
// あなたの得意分野！JavaからAPIを呼び出すJavaScript

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Java Learning App フロントエンド開始');
    
    // イベントリスナーの設定
    setupEventListeners();
    
    // 初期表示でAPIテストを実行
    testApiConnection();
});

// イベントリスナーの設定
function setupEventListeners() {
    // タブ切り替え（既にHTMLで設定済み）
    
    // フォームのイベントリスナー
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('registerForm').addEventListener('submit', handleRegister);
    
    // ボタンのイベントリスナー
    document.getElementById('testApiBtn').addEventListener('click', testApi);
    document.getElementById('loadUsersBtn').addEventListener('click', loadUsers);
}

// タブ切り替え機能
function showTab(tabName) {
    // 全てのタブを非アクティブにする
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-button').forEach(button => {
        button.classList.remove('active');
    });
    
    // 指定されたタブをアクティブにする
    document.getElementById(tabName).classList.add('active');
    event.target.classList.add('active');
    
    console.log(`📄 タブ切り替え: ${tabName}`);
}

// API接続テスト
async function testApiConnection() {
    try {
        console.log('🔗 API接続テスト開始...');
        
        const response = await fetch('/api/test');
        const data = await response.json();
        
        if (response.ok) {
            console.log('✅ API接続テスト成功:', data);
            showToast('API接続成功！', 'success');
        } else {
            console.error('❌ API接続テストエラー:', data);
        }
    } catch (error) {
        console.error('❌ API接続テストエラー:', error);
    }
}

// API テスト関数
async function testApi() {
    const button = document.getElementById('testApiBtn');
    const resultDiv = document.getElementById('apiResult');
    
    try {
        button.disabled = true;
        button.classList.add('loading');
        
        console.log('🧪 API テスト開始...');
        
        const response = await fetch('/api/test');
        const data = await response.json();
        
        resultDiv.innerHTML = `
            <h3>✅ API テスト結果:</h3>
            <pre>${JSON.stringify(data, null, 2)}</pre>
        `;
        resultDiv.className = 'result success';
        
        console.log('✅ API テスト成功:', data);
        showToast('API テスト成功！', 'success');
        
    } catch (error) {
        console.error('❌ API テストエラー:', error);
        
        resultDiv.innerHTML = `
            <h3>❌ API テストエラー:</h3>
            <p style="color: var(--error-color);">${error.message}</p>
        `;
        resultDiv.className = 'result error';
        
        showToast('API テストエラー', 'error');
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
}

// ユーザー一覧読み込み関数
async function loadUsers() {
    const button = document.getElementById('loadUsersBtn');
    const listDiv = document.getElementById('usersList');
    
    try {
        button.disabled = true;
        button.classList.add('loading');
        
        console.log('👥 ユーザー一覧読み込み開始...');
        
        const response = await fetch('/api/users');
        const data = await response.json();
        
        if (response.ok && data.success) {
            listDiv.innerHTML = `
                <h3>👥 ユーザー一覧 (${data.count}件):</h3>
                <div class="users-container">
                    ${data.users.map(user => `
                        <div class="user-card">
                            <div class="user-name">${user.fullName} (@${user.username})</div>
                            <div class="user-details">
                                📧 ${user.email}<br>
                                🆔 ID: ${user.id} | 
                                🟢 ${user.isActive ? 'アクティブ' : '無効'}
                            </div>
                            <div class="user-meta">
                                📅 作成日: ${formatDateTime(user.createdAt)}<br>
                                🔐 最終ログイン: ${user.lastLoginAt ? formatDateTime(user.lastLoginAt) : '未ログイン'}
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
            
            console.log('✅ ユーザー一覧読み込み成功:', data.users);
            showToast(`${data.count}件のユーザーを読み込みました`, 'success');
            
        } else {
            throw new Error(data.error || 'ユーザー一覧の読み込みに失敗しました');
        }
        
    } catch (error) {
        console.error('❌ ユーザー一覧読み込みエラー:', error);
        
        listDiv.innerHTML = `
            <h3>❌ エラー:</h3>
            <p style="color: var(--error-color);">${error.message}</p>
        `;
        
        showToast('ユーザー一覧の読み込みに失敗', 'error');
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
}

// ログイン処理関数
async function handleLogin(event) {
    event.preventDefault();
    
    const form = event.target;
    const resultDiv = document.getElementById('loginResult');
    const submitButton = form.querySelector('button[type="submit"]');
    
    const formData = {
        username: form.username.value.trim(),
        password: form.password.value
    };
    
    try {
        submitButton.disabled = true;
        submitButton.classList.add('loading');
        
        console.log('🔐 ログイン試行:', formData.username);
        
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            resultDiv.innerHTML = `
                <h3>✅ ログイン成功！</h3>
                <p>ようこそ、${result.user.fullName}さん</p>
                <div class="user-details">
                    <p>👤 ユーザー名: ${result.user.username}</p>
                    <p>📧 メール: ${result.user.email}</p>
                    <p>🔐 最終ログイン: ${formatDateTime(result.user.lastLoginAt)}</p>
                </div>
            `;
            resultDiv.className = 'result success';
            
            console.log('✅ ログイン成功:', result.user);
            showToast(`ログイン成功！ようこそ${result.user.fullName}さん`, 'success');
            
            // フォームをリセット
            form.reset();
            
        } else {
            resultDiv.innerHTML = `
                <h3>❌ ログイン失敗</h3>
                <p>${result.error || 'ユーザー名またはパスワードが間違っています'}</p>
            `;
            resultDiv.className = 'result error';
            
            console.error('❌ ログイン失敗:', result);
            showToast(result.error || 'ログインに失敗しました', 'error');
        }
        
    } catch (error) {
        console.error('❌ ログインエラー:', error);
        
        resultDiv.innerHTML = `
            <h3>❌ ログインエラー</h3>
            <p>サーバーとの通信に失敗しました</p>
        `;
        resultDiv.className = 'result error';
        
        showToast('サーバーエラーが発生しました', 'error');
    } finally {
        submitButton.disabled = false;
        submitButton.classList.remove('loading');
    }
}

// ユーザー登録処理関数
async function handleRegister(event) {
    event.preventDefault();
    
    const form = event.target;
    const resultDiv = document.getElementById('registerResult');
    const submitButton = form.querySelector('button[type="submit"]');
    
    const formData = {
        username: form.username.value.trim(),
        email: form.email.value.trim(),
        password: form.password.value,
        firstName: form.firstName.value.trim(),
        lastName: form.lastName.value.trim()
    };
    
    // クライアントサイドバリデーション
    if (!validateRegistrationForm(formData)) {
        return;
    }
    
    try {
        submitButton.disabled = true;
        submitButton.classList.add('loading');
        
        console.log('📝 ユーザー登録試行:', formData.username);
        
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            resultDiv.innerHTML = `
                <h3>✅ 登録成功！</h3>
                <p>${result.user.fullName}さん、登録が完了しました</p>
                <div class="user-details">
                    <p>👤 ユーザー名: ${result.user.username}</p>
                    <p>📧 メール: ${result.user.email}</p>
                    <p>📅 登録日: ${formatDateTime(result.user.createdAt)}</p>
                </div>
            `;
            resultDiv.className = 'result success';
            
            console.log('✅ ユーザー登録成功:', result.user);
            showToast(`登録成功！ようこそ${result.user.fullName}さん`, 'success');
            
            // フォームをリセット
            form.reset();
            
            // 3秒後にログインタブに切り替え
            setTimeout(() => {
                showTab('login');
                document.querySelector('.tab-button[onclick="showTab(\'login\')"]').classList.add('active');
            }, 3000);
            
        } else {
            resultDiv.innerHTML = `
                <h3>❌ 登録失敗</h3>
                <p>${result.error || '登録に失敗しました'}</p>
            `;
            resultDiv.className = 'result error';
            
            console.error('❌ ユーザー登録失敗:', result);
            showToast(result.error || '登録に失敗しました', 'error');
        }
        
    } catch (error) {
        console.error('❌ ユーザー登録エラー:', error);
        
        resultDiv.innerHTML = `
            <h3>❌ 登録エラー</h3>
            <p>サーバーとの通信に失敗しました</p>
        `;
        resultDiv.className = 'result error';
        
        showToast('サーバーエラーが発生しました', 'error');
    } finally {
        submitButton.disabled = false;
        submitButton.classList.remove('loading');
    }
}

// 登録フォームのバリデーション
function validateRegistrationForm(formData) {
    // ユーザー名のバリデーション
    if (!formData.username || formData.username.length < 3) {
        showToast('ユーザー名は3文字以上で入力してください', 'warning');
        return false;
    }
    
    // メールのバリデーション
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
        showToast('正しいメールアドレスを入力してください', 'warning');
        return false;
    }
    
    // パスワードのバリデーション
    if (formData.password.length < 6) {
        showToast('パスワードは6文字以上で入力してください', 'warning');
        return false;
    }
    
    // 名前のバリデーション
    if (!formData.firstName || !formData.lastName) {
        showToast('姓名を入力してください', 'warning');
        return false;
    }
    
    return true;
}

// 日時フォーマット関数
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '未設定';
    
    const date = new Date(dateTimeString);
    return date.toLocaleString('ja-JP', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// トースト通知表示関数
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    
    toast.textContent = message;
    toast.className = `toast ${type}`;
    
    // 表示
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);
    
    // 3秒後に非表示
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// デバッグ用：グローバル関数として公開
window.JavaLearningApp = {
    testApi,
    loadUsers,
    showTab,
    showToast
};

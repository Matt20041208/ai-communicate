<template>
  <div class="login-container">
    <div class="login-form">
      <h2>AI 编程小助手</h2>
      <h3>用户登录</h3>
      
      <div class="form-group">
        <label for="username">用户名</label>
        <input 
          type="text" 
          id="username" 
          v-model="loginForm.username" 
          placeholder="请输入用户名"
          required
        >
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="loginForm.password" 
          placeholder="请输入密码"
          required
        >
      </div>
      
      <div class="form-actions">
        <button @click="handleLogin" :disabled="isLoading">
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
      </div>
      
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
      
      <div class="register-link">
        <p>还没有账号？<a href="#" @click.prevent="$emit('switch-to-register')">立即注册</a></p>
      </div>
      
      <div class="test-accounts">
        <h4>测试账号：</h4>
        <p>管理员：admin / admin123</p>
        <p>普通用户：user / user123</p>
      </div>
    </div>
  </div>
</template>

<script>
import authApi from '../api/authApi';

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      isLoading: false,
      error: ''
    };
  },
  methods: {
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        this.error = '请输入用户名和密码';
        return;
      }
      
      this.isLoading = true;
      this.error = '';
      
      try {
        const response = await authApi.login(this.loginForm.username, this.loginForm.password);
        const token = response.token;
        
        // 确保token是字符串
        if (typeof token === 'string') {
          // 清除旧的token
          localStorage.removeItem('token');
          // 存储新的token
          localStorage.setItem('token', token);
          console.log('登录成功，token已存储:', token.substring(0, 20) + '...');
          // 跳转到主页面
          this.$emit('login-success', this.loginForm.username);
        } else {
          throw new Error('无效的token格式');
        }
      } catch (error) {
        console.error('登录失败:', error);
        this.error = error.response?.data?.message || '登录失败，请检查用户名和密码';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.login-form {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 10px;
}

h3 {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
  font-weight: normal;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;
}

input:focus {
  outline: none;
  border-color: #4CAF50;
}

.form-actions {
  margin-top: 30px;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover:not(:disabled) {
  background-color: #45a049;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #ffebee;
  color: #d32f2f;
  border-radius: 4px;
  font-size: 14px;
}

.register-link {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}

.register-link a {
  color: #4CAF50;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.test-accounts {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

h4 {
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

p {
  color: #888;
  margin: 5px 0;
  font-size: 14px;
}
</style>

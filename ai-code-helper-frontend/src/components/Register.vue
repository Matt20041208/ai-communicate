<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label for="username">用户名</label>
        <input 
          type="text" 
          id="username" 
          v-model="form.username" 
          required 
          placeholder="请输入用户名"
        >
      </div>
      
      <div class="form-group">
        <label for="password">密码</label>
        <input 
          type="password" 
          id="password" 
          v-model="form.password" 
          required 
          placeholder="请输入密码"
        >
      </div>
      
      <div class="form-group">
        <label for="nickname">昵称</label>
        <input 
          type="text" 
          id="nickname" 
          v-model="form.nickname" 
          placeholder="请输入昵称"
        >
      </div>
      
      <div class="form-group">
        <label for="email">邮箱</label>
        <input 
          type="email" 
          id="email" 
          v-model="form.email" 
          placeholder="请输入邮箱"
        >
      </div>
      
      <div class="form-group">
        <label for="phone">电话</label>
        <input 
          type="tel" 
          id="phone" 
          v-model="form.phone" 
          placeholder="请输入电话"
        >
      </div>
      
      <button type="submit" class="register-btn" :disabled="loading">
        {{ loading ? '注册中...' : '注册' }}
      </button>
      
      <p class="login-link">
        已有账号？<a href="#" @click.prevent="$emit('switch-to-login')">去登录</a>
      </p>
    </form>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      form: {
        username: '',
        password: '',
        nickname: '',
        email: '',
        phone: ''
      },
      loading: false
    };
  },
  methods: {
    async handleRegister() {
      try {
        this.loading = true;
        const authApi = await import('../api/authApi');
        const result = await authApi.default.register(this.form);
        if (result.success) {
          alert('注册成功，请登录');
          this.$emit('switch-to-login');
        } else {
          alert('注册失败：' + result.message);
        }
      } catch (error) {
        console.error('注册失败:', error);
        alert('注册失败：' + (error.response?.data?.message || error.message));
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #666;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.register-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

.register-btn:hover {
  background: #66b1ff;
}

.register-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>

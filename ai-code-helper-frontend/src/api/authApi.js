import axios from 'axios';

// 创建axios实例
const authApi = axios.create({
  baseURL: 'http://localhost:53705/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 登录
const login = async (username, password) => {
  try {
    const response = await authApi.post('/auth/login', {
      username,
      password
    });
    return response.data;
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
};

// 注册
const register = async (user) => {
  try {
    const response = await authApi.post('/auth/register', user);
    return response.data;
  } catch (error) {
    console.error('注册失败:', error);
    throw error;
  }
};

export default {
  login,
  register
};
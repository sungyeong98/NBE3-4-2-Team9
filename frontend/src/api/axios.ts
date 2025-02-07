import axios from 'axios';

// 인증이 필요한 요청을 위한 인스턴스
export const privateApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  withCredentials: true
});

// 인증이 필요없는 요청을 위한 인스턴스
export const publicApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  withCredentials: true
});

// 인증이 필요한 요청에만 토큰 추가
privateApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('adminToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

privateApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('adminToken');
      document.cookie = 'adminToken=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
      const isAdminPage = window.location.pathname.startsWith('/admin');
      if (isAdminPage) {
        window.location.href = '/admin/login';
      }
    }
    return Promise.reject(error);
  }
);

export default publicApi; // 기본 export는 인증이 필요없는 인스턴스 
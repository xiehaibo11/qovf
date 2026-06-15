import axios from "axios";
import type { AxiosInstance } from "axios";

// 统一的 HTTP 客户端，开发环境通过 vite proxy 转发到后端 /api
const http: AxiosInstance = axios.create({
  baseURL: "/api",
  timeout: 15000
});

http.interceptors.request.use(config => {
  // TODO: 注入 token 等鉴权信息
  return config;
});

http.interceptors.response.use(
  response => response.data,
  error => Promise.reject(error)
);

export default http;

import { ApiResponse } from '@/types/common/ApiResponse';
import publicApi from './axios';

export const adminLogin = async (email: string, password: string) => {
  const response = await publicApi.post('/api/v1/adm/login', {
    email,
    password
  });
  return response.data;
}; 
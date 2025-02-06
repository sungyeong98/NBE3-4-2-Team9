import { ApiResponse } from '@/types/common/ApiResponse';
import { Category } from '@/types/post/Category';
import publicApi from './axios';

export const getCategories = async (): Promise<ApiResponse<Category[]>> => {
  const response = await publicApi.get('/api/v1/category');
  return response.data;
}; 
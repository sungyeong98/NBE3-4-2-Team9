import { ApiResponse } from '@/types/common/ApiResponse';
import { PageResponse } from '@/types/common/PageResponse';
import { PostResponse } from '@/types/post/PostResponse';
import axiosInstance from './axios';

interface GetPostsParams {
  categoryId?: number;
  keyword?: string;
  sort?: 'latest' | 'popular';
  page?: number;
  size?: number;
}

export const getPosts = async (params: GetPostsParams = {}): Promise<ApiResponse<PageResponse<PostResponse>>> => {
  const response = await axiosInstance.get('/api/v1/post', { params });
  return response.data;
};

export const getPost = async (id: number): Promise<ApiResponse<PostResponse>> => {
  const response = await axiosInstance.get(`/api/v1/post/${id}`);
  return response.data;
}; 
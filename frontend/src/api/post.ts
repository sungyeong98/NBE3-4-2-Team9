import { ApiResponse } from '@/types/common/ApiResponse';
import { PageResponse } from '@/types/common/PageResponse';
import { PostResponse } from '@/types/post/PostResponse';
import publicApi from './axios';

interface GetPostsParams {
  categoryId?: number;
  keyword?: string;
  sort?: 'latest' | 'popular';
  page?: number;
  size?: number;
}

export const getPosts = async (params: GetPostsParams = {}): Promise<ApiResponse<PageResponse<PostResponse>>> => {
  const response = await publicApi.get('/api/v1/posts', { params });
  return response.data;
};

export const getPost = async (id: number): Promise<ApiResponse<PostResponse>> => {
  const response = await publicApi.get(`/api/v1/posts/${id}`);
  return response.data;
};

interface CreatePostRequest {
  subject: string;
  content: string;
  categoryId: number;
}

export const createPost = async (data: CreatePostRequest): Promise<ApiResponse<PostResponse>> => {
  const response = await publicApi.post('/api/v1/posts', data);
  return response.data;
}; 
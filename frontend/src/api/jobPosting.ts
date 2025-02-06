import { ApiResponse } from '@/types/common/ApiResponse';
import { JobPostingSearchCondition } from '@/types/job-posting/JobPostingSearchCondition';
import { PageResponse } from '@/types/common/PageResponse';
import { JobPostingPageResponse } from '@/types/job-posting/JobPostingPageResponse';
import axiosInstance from './axios';

export const getJobPostings = async (params: JobPostingSearchCondition): Promise<ApiResponse<PageResponse<JobPostingPageResponse>>> => {
  const response = await axiosInstance.get('/api/v1/job-posting', { params });
  return response.data;
};

export const getJobPosting = async (id: number): Promise<ApiResponse<JobPostingPageResponse>> => {
  const response = await axiosInstance.get(`/api/v1/job-posting/${id}`);
  return response.data;
}; 
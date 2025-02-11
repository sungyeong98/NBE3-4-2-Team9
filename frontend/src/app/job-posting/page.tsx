'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { HeartIcon } from '@heroicons/react/24/solid';
import privateApi from '@/api/axios';

type JobPosting = {
  id: number;
  subject: string;
  openDate: string;
  closeDate: string;
  experienceLevel: {
    code: number;
    name: string;
  };
  requireEducate: {
    code: number;
    name: string;
  };
  jobPostingStatus: 'ACTIVE' | 'END';
  salary: {
    code: number;
    name: string;
  };
  applyCnt: number;
  isVoter?: boolean;
};

type SearchCondition = {
  salaryCode?: number;
  kw?: string;
  experienceLevel?: number;
  requireEducateCode?: number;
  sort?: string;
  order?: string;
  pageNum: number;
  pageSize: number;
};

export default function JobPostingList() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [posts, setPosts] = useState<JobPosting[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const currentTab = searchParams.get('tab') || 'all';
  const currentPage = Number(searchParams.get('page')) || 0;

  useEffect(() => {
    fetchPosts();
  }, [currentTab, currentPage]);

  const fetchPosts = async () => {
    try {
      setIsLoading(true);
      const searchCondition: SearchCondition = {
        pageNum: currentPage,
        pageSize: 9,
        order: 'DESC',
        sort: 'openDate'
      };

      const endpoint = currentTab === 'voted' 
        ? '/api/v1/job-posting/voter'
        : '/api/v1/job-posting';
      
      const response = await privateApi.get(endpoint, {
        params: searchCondition
      });

      if (response.data.success) {
        setPosts(response.data.data.content);
        setTotalPages(response.data.data.totalPages);
      }
    } catch (error) {
      console.error('Failed to fetch job postings:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleTabChange = (tab: string) => {
    router.push(`/job-posting?tab=${tab}&page=0`);
  };

  const handlePageChange = (page: number) => {
    router.push(`/job-posting?tab=${currentTab}&page=${page}`);
  };

  if (isLoading) return <div>로딩중...</div>;

  return (
    <div className="max-w-6xl mx-auto p-6">
      {/* 탭 메뉴 */}
      <div className="flex gap-4 mb-6">
        <button
          onClick={() => handleTabChange('all')}
          className={`px-4 py-2 rounded-lg ${
            currentTab === 'all'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          전체 공고
        </button>
        <button
          onClick={() => handleTabChange('voted')}
          className={`px-4 py-2 rounded-lg flex items-center gap-2 ${
            currentTab === 'voted'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          <HeartIcon className="w-5 h-5" />
          관심 공고
        </button>
      </div>

      {/* 공고 목록 */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {posts.map((posting) => (
          <Link
            key={posting.id}
            href={`/job-posting/${posting.id}`}
            className="block bg-white rounded-xl shadow-md hover:shadow-lg transition-all duration-200"
          >
            <div className="p-6 flex flex-col h-full justify-between">
              <div>
                <h2 className="text-lg font-semibold mb-2 line-clamp-2">
                  {posting.subject}
                </h2>
                <div className="flex flex-wrap gap-2 mb-4">
                  <span className="px-3 py-1 text-sm font-medium text-blue-600 bg-blue-50 rounded-full">
                    {posting.experienceLevel.name}
                  </span>
                  <span className="px-3 py-1 text-sm font-medium text-green-600 bg-green-50 rounded-full">
                    {posting.salary.name}
                  </span>
                </div>
                <div className="space-y-2 text-sm text-gray-600">
                  <p>학력: {posting.requireEducate.name}</p>
                  <p>지원자 수: {posting.applyCnt}명</p>
                </div>
              </div>
              <div className="flex items-center justify-between text-sm text-gray-500 mt-4">
                <span>마감일: {new Date(posting.closeDate).toLocaleDateString()}</span>
                <span className={`px-2 py-1 rounded ${
                  posting.jobPostingStatus === 'ACTIVE' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {posting.jobPostingStatus === 'ACTIVE' ? '진행중' : '마감'}
                </span>
              </div>
            </div>
          </Link>
        ))}
      </div>

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-8">
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={() => handlePageChange(i)}
              className={`px-4 py-2 rounded ${
                currentPage === i
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 hover:bg-gray-200'
              }`}
            >
              {i + 1}
            </button>
          ))}
        </div>
      )}
    </div>
  );
} 
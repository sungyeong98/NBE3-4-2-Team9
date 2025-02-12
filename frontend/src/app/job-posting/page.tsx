'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { HeartIcon } from '@heroicons/react/24/solid';
import { 
  BuildingOfficeIcon, 
  AcademicCapIcon,
  BriefcaseIcon,
  CurrencyDollarIcon,
  UserGroupIcon,
  CalendarIcon
} from '@heroicons/react/24/outline';
import privateApi from '@/api/axios';
import { JobPostingResponse } from '@/types/job-posting/JobPostingResponse';

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
  const [posts, setPosts] = useState<JobPostingResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const currentTab = searchParams.get('tab') || 'all';
  const currentPage = Number(searchParams.get('page')) || 0;
  const pageSize = 10;

  useEffect(() => {
    const fetchJobPostings = async () => {
      try {
        setIsLoading(true);
        const searchCondition: SearchCondition = {
          pageNum: currentPage,
          pageSize: pageSize,
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
          setTotalElements(response.data.data.totalElements);
        }
      } catch (error) {
        console.error('Failed to fetch job postings:', error);
        alert('채용공고를 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchJobPostings();
  }, [currentTab, currentPage]);

  const handleTabChange = (tab: string) => {
    router.push(`/job-posting?tab=${tab}&page=0`);
  };

  const handlePageChange = (newPage: number) => {
    router.push(`/job-posting?tab=${currentTab}&page=${newPage}`);
    window.scrollTo(0, 0);  // 페이지 변경 시 맨 위로 스크롤
  };

  // 페이지 번호 계산 함수 수정
  const getPageNumbers = () => {
    const MAX_VISIBLE_PAGES = 5; // 한 번에 보여줄 최대 페이지 버튼 수
    const pages = [];
    
    let startPage: number;
    let endPage: number;

    if (totalPages <= MAX_VISIBLE_PAGES) {
      // 전체 페이지가 MAX_VISIBLE_PAGES 이하인 경우
      startPage = 0;
      endPage = totalPages - 1;
    } else {
      // 현재 페이지가 중앙에 오도록 계산
      const middlePage = Math.floor(MAX_VISIBLE_PAGES / 2);
      
      if (currentPage <= middlePage) {
        // 현재 페이지가 시작 부분에 가까운 경우
        startPage = 0;
        endPage = MAX_VISIBLE_PAGES - 1;
      } else if (currentPage >= totalPages - middlePage - 1) {
        // 현재 페이지가 끝 부분에 가까운 경우
        startPage = totalPages - MAX_VISIBLE_PAGES;
        endPage = totalPages - 1;
      } else {
        // 현재 페이지가 중간인 경우
        startPage = currentPage - middlePage;
        endPage = currentPage + middlePage;
      }
    }

    // 첫 페이지로 이동 버튼 (현재가 첫 페이지가 아닐 때)
    if (startPage > 0) {
      pages.push(
        <button
          key="first"
          onClick={() => handlePageChange(0)}
          className="px-2 py-1 border rounded-lg hover:bg-gray-100"
        >
          처음
        </button>,
        <span key="ellipsis1" className="px-1">...</span>
      );
    }

    // 페이지 번호 버튼
    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          onClick={() => handlePageChange(i)}
          className={`px-3 py-1 border rounded-lg ${
            currentPage === i 
              ? 'bg-blue-500 text-white' 
              : 'hover:bg-gray-100'
          }`}
        >
          {i + 1}
        </button>
      );
    }

    // 마지막 페이지로 이동 버튼 (현재가 마지막 페이지가 아닐 때)
    if (endPage < totalPages - 1) {
      pages.push(
        <span key="ellipsis2" className="px-1">...</span>,
        <button
          key="last"
          onClick={() => handlePageChange(totalPages - 1)}
          className="px-2 py-1 border rounded-lg hover:bg-gray-100"
        >
          마지막
        </button>
      );
    }

    return pages;
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 상단 배너 */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="max-w-7xl mx-auto py-12 px-8">
          <h1 className="text-4xl font-bold mb-4">채용정보</h1>
          <p className="text-blue-100">다양한 개발자 채용 정보를 확인하세요</p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-8 -mt-8">
        {/* 검색 및 필터 카드 */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <div className="flex gap-2 overflow-x-auto pb-2">
            <button
              onClick={() => handleTabChange('all')}
              className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors
                ${currentTab === 'all' 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}
            >
              <span className="flex items-center gap-2">
                <BriefcaseIcon className="w-4 h-4" />
                전체 공고
              </span>
            </button>
            <button
              onClick={() => handleTabChange('voted')}
              className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors
                ${currentTab === 'voted' 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}
            >
              <span className="flex items-center gap-2">
                <HeartIcon className="w-4 h-4" />
                관심 공고
              </span>
            </button>
          </div>
        </div>

        {/* 공고 목록 */}
        {isLoading ? (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="bg-white rounded-lg shadow-md p-6 animate-pulse">
                <div className="flex gap-2 mb-4">
                  <div className="h-6 w-20 bg-gray-200 rounded-full"></div>
                  <div className="h-6 w-24 bg-gray-200 rounded-full"></div>
                </div>
                <div className="h-7 bg-gray-200 rounded w-3/4 mb-4"></div>
                <div className="space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-full"></div>
                  <div className="h-4 bg-gray-200 rounded w-2/3"></div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <>
            {/* 공고 목록 */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {posts.map((posting) => (
                <Link
                  key={posting.id}
                  href={`/job-posting/${posting.id}`}
                  className="group bg-white rounded-lg shadow-sm hover:shadow-md transition-all duration-200 overflow-hidden border border-gray-200/75"
                >
                  <div className="p-6">
                    <div className="flex items-start justify-between">
                      <h2 className="text-lg font-semibold text-gray-900 group-hover:text-blue-600 transition-colors duration-200 line-clamp-2">
                        {posting.subject}
                      </h2>
                      {posting.isVoter && (
                        <HeartIcon className="w-5 h-5 text-red-500 flex-shrink-0" />
                      )}
                    </div>

                    <div className="mt-4 space-y-3">
                      <div className="flex items-center text-sm text-gray-600">
                        <BuildingOfficeIcon className="w-4 h-4 mr-2" />
                        <span>{posting.experienceLevel.name}</span>
                      </div>
                      <div className="flex items-center text-sm text-gray-600">
                        <AcademicCapIcon className="w-4 h-4 mr-2" />
                        <span>{posting.requireEducate.name}</span>
                      </div>
                      <div className="flex items-center text-sm text-gray-600">
                        <CurrencyDollarIcon className="w-4 h-4 mr-2" />
                        <span>{posting.salary.name}</span>
                      </div>
                    </div>

                    <div className="mt-4 pt-4 border-t border-gray-100">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center text-sm text-gray-500">
                          <UserGroupIcon className="w-4 h-4 mr-1" />
                          <span>지원자 {posting.applyCnt}명</span>
                        </div>
                        <div className="flex items-center text-sm">
                          <CalendarIcon className="w-4 h-4 mr-1" />
                          <span className={posting.jobPostingStatus === 'ACTIVE' ? 'text-blue-600' : 'text-red-600'}>
                            {posting.jobPostingStatus === 'ACTIVE' 
                              ? `D-${Math.ceil((new Date(posting.closeDate).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24))}` 
                              : '마감'}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </Link>
              ))}
            </div>

            {/* 데이터가 없을 때 */}
            {posts.length === 0 && (
              <div className="bg-white rounded-xl shadow-md p-12 text-center">
                <div className="bg-gray-50 rounded-full w-20 h-20 flex items-center justify-center mx-auto mb-6">
                  <BriefcaseIcon className="h-10 w-10 text-gray-400" />
                </div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">등록된 공고가 없습니다</h3>
                <p className="text-gray-500">
                  {currentTab === 'voted' 
                    ? '관심있는 공고를 등록해보세요!' 
                    : '곧 새로운 공고가 등록될 예정입니다.'}
                </p>
              </div>
            )}

            {/* 페이지네이션 */}
            <div className="flex justify-center items-center gap-1 mt-8">
              <button
                onClick={() => handlePageChange(Math.max(0, currentPage - 1))}
                disabled={currentPage === 0}
                className="px-2 py-1 border rounded-lg disabled:opacity-50 hover:bg-gray-100"
              >
                이전
              </button>
              
              <div className="flex items-center gap-1 mx-2">
                {getPageNumbers()}
              </div>

              <button
                onClick={() => handlePageChange(Math.min(totalPages - 1, currentPage + 1))}
                disabled={currentPage === totalPages - 1}
                className="px-2 py-1 border rounded-lg disabled:opacity-50 hover:bg-gray-100"
              >
                다음
              </button>
            </div>

            {/* 페이지 정보 */}
            <div className="text-center mt-4 text-sm text-gray-600">
              {totalElements}개 중 {currentPage * pageSize + 1}-{Math.min((currentPage + 1) * pageSize, totalElements)}
            </div>
          </>
        )}
      </div>
    </div>
  );
} 
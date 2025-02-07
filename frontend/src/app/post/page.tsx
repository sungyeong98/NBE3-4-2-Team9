'use client';

import { useEffect, useState } from 'react';
import { PostResponse } from '@/types/post/PostResponse';
import { getPosts } from '@/api/post';
import Link from 'next/link';
import { formatDate } from '@/utils/dateUtils';
import { 
  PencilIcon, 
  MagnifyingGlassIcon,
  FunnelIcon,
  ChatBubbleLeftIcon 
} from '@heroicons/react/24/outline';
import { Category } from '@/types/post/Category';
import { getCategories } from '@/api/category';

export default function PostList() {
  const [posts, setPosts] = useState<PostResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('latest');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await getPosts({ 
          page: currentPage, 
          size: 10,
          sort: sortBy,
          categoryId: selectedCategory !== 'all' ? selectedCategory : undefined,
          keyword: searchTerm
        });
        if (response.success) {
          setPosts(response.data.content);
          setTotalPages(response.data.totalPages);
        }
      } catch (error) {
        console.error('Failed to fetch posts:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchPosts();
  }, [currentPage, sortBy, selectedCategory, searchTerm]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getCategories();
        if (response.success) {
          setCategories(response.data);
        }
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };

    fetchCategories();
  }, []);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 상단 배너 */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="max-w-7xl mx-auto py-12 px-8">
          <h1 className="text-4xl font-bold mb-4">커뮤니티</h1>
          <p className="text-blue-100">개발자들과 다양한 이야기를 나눠보세요</p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-8 -mt-8">
        {/* 검색 및 필터 카드 */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <div className="flex flex-col lg:flex-row gap-4 items-stretch">
            {/* 카테고리 필터 */}
            <div className="flex gap-2 overflow-x-auto pb-2 lg:pb-0">
              <button
                onClick={() => setSelectedCategory('all')}
                className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors
                  ${selectedCategory === 'all' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}
              >
                전체
              </button>
              {categories.map((category) => (
                <button
                  key={category.id}
                  onClick={() => setSelectedCategory(category.id)}
                  className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors
                    ${selectedCategory === category.id 
                      ? 'bg-blue-600 text-white' 
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}
                >
                  {category.name}
                </button>
              ))}
            </div>

            <div className="flex flex-1 gap-4">
              {/* 검색바 */}
              <div className="relative flex-1">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="관심있는 내용을 검색해보세요"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                />
              </div>

              {/* 정렬 옵션 */}
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none text-sm appearance-none pr-10 relative"
                style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%236B7280'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E")`, backgroundPosition: 'right 0.5rem center', backgroundRepeat: 'no-repeat', backgroundSize: '1.5em 1.5em' }}
              >
                <option value="latest">최신순</option>
                <option value="popular">인기순</option>
                <option value="comments">댓글순</option>
              </select>

              {/* 글쓰기 버튼 */}
              <Link
                href="/post/write"
                className="inline-flex items-center gap-2 px-6 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
              >
                <PencilIcon className="h-5 w-5" />
                글쓰기
              </Link>
            </div>
          </div>
        </div>

        {/* 게시글 목록 */}
        <div className="space-y-4">
          {isLoading ? (
            [...Array(6)].map((_, index) => (
              <div
                key={`skeleton-${index}`}
                className="bg-white p-6 rounded-xl shadow-md animate-pulse"
              >
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
            ))
          ) : (
            posts.map((post) => (
              <Link
                key={`post-${post.id}`}
                href={`/post/${post.id}`}
                className="block bg-white rounded-xl shadow-md hover:shadow-lg transition-all duration-200"
              >
                <div className="p-6">
                  <div className="flex flex-wrap items-center gap-3 mb-3">
                    <span className="px-3 py-1 text-sm font-medium text-blue-600 bg-blue-50 rounded-full">
                      {categories.find(cat => cat.id === String(post.categoryId))?.name}
                    </span>
                    <div className="flex items-center gap-2 text-sm text-gray-500">
                      <span>익명</span>
                      <span>•</span>
                      <span>{formatDate(post.createdAt)}</span>
                    </div>
                  </div>
                  
                  <h2 className="text-xl font-bold text-gray-900 mb-3 hover:text-blue-600 transition-colors line-clamp-1">
                    {post.subject}
                  </h2>
                  
                  <p className="text-gray-600 mb-4 line-clamp-2">
                    {post.content}
                  </p>
                  
                  <div className="flex items-center gap-4 text-sm text-gray-500">
                    <div className="flex items-center gap-2">
                      <ChatBubbleLeftIcon className="h-5 w-5" />
                      <span>댓글 0</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <FunnelIcon className="h-5 w-5" />
                      <span>조회 0</span>
                    </div>
                  </div>
                </div>
              </Link>
            ))
          )}
        </div>

        {/* 게시글 없을 때 */}
        {posts.length === 0 && !isLoading && (
          <div className="bg-white rounded-xl shadow-md p-12 text-center">
            <div className="bg-gray-50 rounded-full w-20 h-20 flex items-center justify-center mx-auto mb-6">
              <ChatBubbleLeftIcon className="h-10 w-10 text-gray-400" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">게시글이 없습니다</h3>
            <p className="text-gray-500 mb-6">첫 게시글의 주인공이 되어보세요!</p>
            <Link
              href="/post/write"
              className="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
            >
              <PencilIcon className="h-5 w-5" />
              새 글 작성하기
            </Link>
          </div>
        )}

        {/* 페이지네이션 추가 */}
        {!isLoading && posts.length > 0 && (
          <div className="mt-8 flex justify-center items-center gap-2">
            <button
              onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
              disabled={currentPage === 0}
              className={`p-2 rounded-lg transition-colors ${
                currentPage === 0
                  ? 'text-gray-400 cursor-not-allowed'
                  : 'text-gray-600 hover:bg-gray-100'
              }`}
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
            </button>

            <div className="flex items-center gap-1">
              {[...Array(totalPages || 10)].map((_, index) => {
                if (
                  index === 0 ||
                  index === (totalPages - 1) ||
                  (index >= currentPage - 2 && index <= currentPage + 2)
                ) {
                  return (
                    <button
                      key={index}
                      onClick={() => setCurrentPage(index)}
                      className={`min-w-[2.5rem] h-10 rounded-lg transition-colors ${
                        currentPage === index
                          ? 'bg-blue-600 text-white'
                          : 'text-gray-600 hover:bg-gray-100'
                      }`}
                    >
                      {index + 1}
                    </button>
                  );
                } else if (
                  index === currentPage - 3 ||
                  index === currentPage + 3
                ) {
                  return <span key={index} className="px-1">...</span>;
                }
                return null;
              })}
            </div>

            <button
              onClick={() => setCurrentPage(Math.min((totalPages || 10) - 1, currentPage + 1))}
              disabled={currentPage === (totalPages || 10) - 1}
              className={`p-2 rounded-lg transition-colors ${
                currentPage === (totalPages || 10) - 1
                  ? 'text-gray-400 cursor-not-allowed'
                  : 'text-gray-600 hover:bg-gray-100'
              }`}
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        )}
      </div>
    </div>
  );
} 
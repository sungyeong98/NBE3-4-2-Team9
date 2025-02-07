'use client';

import { useEffect, useState } from 'react';
import { PostResponse } from '@/types/post/PostResponse';
import { getPosts } from '@/api/post';
import Link from 'next/link';
import { formatDate } from '@/utils/dateUtils';
import { ChatBubbleLeftIcon, FunnelIcon, SearchIcon } from '@heroicons/react/24/outline';
import { Category } from '@/types/post/Category';
import { getCategories } from '@/api/category';

export default function PostsPage({ searchParams }: { searchParams: { [key: string]: string | undefined } }) {
  const [posts, setPosts] = useState<PostResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [categories, setCategories] = useState<Category[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [sortBy, setSortBy] = useState('latest');

  // 카테고리 데이터 가져오기
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

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await getPosts({ 
          page: currentPage, 
          size: 10,
          categoryId: selectedCategory !== 'all' ? selectedCategory : undefined,
          sort: sortBy,
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
  }, [currentPage, selectedCategory, sortBy, searchTerm]);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 상단 배너 */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="max-w-7xl mx-auto py-12 px-8">
          <h1 className="text-4xl font-bold mb-4">커뮤니티</h1>
          <p className="text-blue-100">개발자들과 다양한 이야기를 나눠보세요</p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-8 py-8">
        {/* 게시글 목록 */}
        <div className="grid gap-6">
          {isLoading ? (
            // 로딩 스켈레톤
            [...Array(5)].map((_, index) => (
              <div key={index} className="bg-white p-6 rounded-lg shadow animate-pulse">
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/2"></div>
              </div>
            ))
          ) : (
            // 실제 게시글 목록
            posts.map((post) => (
              <Link
                key={post.id}
                href={`/posts/${post.id}`}
                className="block bg-white rounded-lg shadow hover:shadow-md transition-shadow"
              >
                <div className="p-6">
                  <h2 className="text-xl font-semibold mb-2">{post.subject}</h2>
                  <div className="flex items-center justify-between text-sm text-gray-500">
                    <span>{categories.find(cat => cat.id === String(post.categoryId))?.name}</span>
                    <span>{formatDate(post.createdAt)}</span>
                  </div>
                </div>
              </Link>
            ))
          )}
        </div>

        {/* 페이지네이션 */}
        {!isLoading && posts.length > 0 && (
          <div className="mt-8 flex justify-center gap-2">
            {[...Array(totalPages)].map((_, index) => (
              <button
                key={index}
                onClick={() => setCurrentPage(index)}
                className={`px-4 py-2 rounded ${
                  currentPage === index ? 'bg-blue-600 text-white' : 'bg-white'
                }`}
              >
                {index + 1}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
} 
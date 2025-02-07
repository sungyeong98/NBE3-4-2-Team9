'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { getPost } from '@/api/post';
import { PostResponse } from '@/types/post/PostResponse';
import { formatDate } from '@/utils/dateUtils';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';
import { Category } from '@/types/post/Category';
import { getCategories } from '@/api/category';
import Link from 'next/link';
import privateApi from '@/api/axios';

export default function PostDetail() {
  const params = useParams();
  const router = useRouter();
  const [post, setPost] = useState<PostResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isAuthor, setIsAuthor] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getCategories();
        if (response.success) {
          console.log('Categories:', response.data);
          setCategories(response.data);
        }
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };

    fetchCategories();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [postResponse, categoriesResponse] = await Promise.all([
          privateApi.get(`/api/v1/posts/${params.id}`),
          privateApi.get('/api/v1/category')
        ]);

        if (postResponse.data.success) {
          setPost(postResponse.data.data);
          const currentUserId = localStorage.getItem('userId');
          setIsAuthor(currentUserId === String(postResponse.data.data.authorId));
        }
        if (categoriesResponse.data.success) {
          setCategories(categoriesResponse.data.data);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [params.id]);

  const handleDelete = async () => {
    if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
      return;
    }

    try {
      setIsDeleting(true);
      const response = await privateApi.delete(`/api/v1/posts/${params.id}`);
      
      if (response.data.success) {
        alert('게시글이 삭제되었습니다.');
        router.push('/post');
        router.refresh();
      }
    } catch (error: any) {
      if (error.response?.status === 403) {
        alert('본인이 작성한 게시글만 삭제할 수 있습니다.');
      } else {
        alert('게시글 삭제 중 오류가 발생했습니다.');
      }
    } finally {
      setIsDeleting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded w-3/4 mb-4"></div>
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-8"></div>
          <div className="space-y-4">
            <div className="h-4 bg-gray-200 rounded w-full"></div>
            <div className="h-4 bg-gray-200 rounded w-full"></div>
            <div className="h-4 bg-gray-200 rounded w-3/4"></div>
          </div>
        </div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        <div className="text-center py-12 text-gray-500">
          게시글을 찾을 수 없습니다.
        </div>
      </div>
    );
  }

  const categoryName = categories.find(cat => Number(cat.id) === post.categoryId)?.name;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b">
        <div className="max-w-4xl mx-auto px-4">
          <div className="flex items-center py-4 text-sm">
            <button
              onClick={() => router.back()}
              className="flex items-center gap-2 text-gray-600 hover:text-blue-600 transition-colors"
            >
              <ArrowLeftIcon className="h-5 w-5" />
              목록으로
            </button>
            <div className="mx-4 text-gray-300">|</div>
            <div className="flex items-center text-gray-600">
              현재 게시판:
              <Link 
                href={`/post?category=${post.categoryId}`}
                className="ml-2 text-blue-600 hover:text-blue-700 transition-colors font-medium"
              >
                {categoryName}
              </Link>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 py-8">
        <article className="bg-white rounded-xl shadow-lg overflow-hidden">
          <div className="p-8">
            <div className="mb-8">
              <h1 className="text-2xl font-bold text-gray-900 mb-3">
                {post.subject}
              </h1>
              <div className="flex items-center gap-4 text-sm text-gray-500">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 bg-gray-100 rounded-full flex items-center justify-center">
                    <span className="text-gray-600">익</span>
                  </div>
                  <span>익명</span>
                </div>
                <span>•</span>
                <span>{formatDate(post.createdAt)}</span>
                <div className="flex-1"></div>
                <button className="text-gray-500 hover:text-blue-600 transition-colors">
                  공유하기
                </button>
                <button className="text-gray-500 hover:text-red-600 transition-colors">
                  신고하기
                </button>
              </div>
            </div>

            <div className="prose max-w-none text-gray-800 leading-relaxed mb-8">
              {post.content}
            </div>

            <div className="flex items-center justify-between pt-6 border-t">
              <div className="flex items-center gap-4">
                <button className="flex items-center gap-2 text-gray-500 hover:text-blue-600 transition-colors">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
                  </svg>
                  <span>추천</span>
                </button>
                <button className="flex items-center gap-2 text-gray-500 hover:text-blue-600 transition-colors">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                  <span>댓글</span>
                </button>
              </div>
              <div className="flex items-center gap-2">
                <button 
                  className="px-4 py-2 text-sm text-gray-600 hover:text-red-600 transition-colors"
                  onClick={handleDelete}
                  disabled={isDeleting}
                >
                  {isDeleting ? '삭제 중...' : '삭제'}
                </button>
              </div>
            </div>
          </div>
        </article>

        <div className="mt-8">
          <h3 className="text-lg font-semibold mb-6">댓글</h3>
          
          <div className="bg-white rounded-xl shadow-lg p-6 mb-6">
            <textarea
              placeholder="댓글을 입력하세요..."
              className="w-full p-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
              rows={2}
            />
            <div className="flex justify-end mt-2">
              <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                댓글 작성
              </button>
            </div>
          </div>

          <div className="space-y-4">
            {true && (
              <div className="bg-white rounded-xl shadow-lg p-8 text-center">
                <div className="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                  <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                </div>
                <p className="text-gray-500">아직 작성된 댓글이 없습니다</p>
                <p className="text-sm text-gray-400 mt-1">첫 댓글을 작성해보세요!</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
} 
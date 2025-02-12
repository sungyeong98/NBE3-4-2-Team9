'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import privateApi from '@/api/axios';
import { Post } from '@/types/post';
import Link from 'next/link';
import { formatDate } from '@/utils/dateUtils';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';

export default function PostDetail() {
  const router = useRouter();
  const params = useParams();
  const [post, setPost] = useState<Post | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchPost = async () => {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        alert('로그인이 필요한 서비스입니다.');
        router.push('/login');
        return;
      }

      if (!params.id) {
        console.error('게시글 ID가 없습니다.');
        router.push('/post');
        return;
      }

      try {
        console.log('Fetching post with ID:', params.id);
        const response = await privateApi.get(`/api/v1/free/posts/${params.id}`, {
          headers: { Authorization: `Bearer ${token}` }
        });

        if (response.data.success) {
          console.log('Post data:', response.data.data);
          setPost(response.data.data);
        }
      } catch (error: any) {
        console.error('Error fetching post:', error.response);
        if (error.response?.status === 401) {
          alert('세션이 만료되었습니다. 다시 로그인해주세요.');
          router.push('/login');
        } else {
          alert('게시글을 불러오는데 실패했습니다.');
          router.push('/post');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchPost();
  }, [params.id]);

  if (isLoading) {
    return <div className="max-w-4xl mx-auto p-8">로딩중...</div>;
  }

  if (!post) {
    return <div className="max-w-4xl mx-auto p-8">게시글을 찾을 수 없습니다.</div>;
  }

  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="mb-6">
        <button
          onClick={() => router.back()}
          className="flex items-center gap-2 text-gray-600 hover:text-blue-600"
        >
          <ArrowLeftIcon className="h-5 w-5" />
          목록으로
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-lg p-8">
        <h1 className="text-2xl font-bold mb-4">{post.subject}</h1>
        <div className="flex items-center text-gray-600 mb-6">
          <span>작성자: {post.authorName}</span>
          <span className="mx-2">•</span>
          <span>작성일: {formatDate(post.createdAt)}</span>
        </div>
        <div className="prose max-w-none mb-8">
          {post.content}
        </div>

        {/* 댓글 섹션 UI (기능 비활성화) */}
        <div className="mt-8 border-t pt-8">
          <h3 className="text-lg font-bold mb-4">댓글</h3>
          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-gray-500 text-center">댓글 기능은 준비 중입니다.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import privateApi from '@/api/axios';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';
import { 
  ArrowLeftIcon,
  ChatBubbleLeftIcon,
  HeartIcon,
  FunnelIcon 
} from '@heroicons/react/24/outline';
import { formatDate } from '@/utils/dateUtils';
import Link from 'next/link';
import { Category } from '@/types/category';

export default function PostDetail() {
  const router = useRouter();
  const params = useParams();
  const { user } = useSelector((state: RootState) => state.auth);
  const [post, setPost] = useState<any>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await privateApi.get('/api/v1/category');
        if (response.data.success) {
          setCategories(response.data.data);
        }
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };

    fetchCategories();
  }, []);

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await privateApi.get(`/api/v1/free/posts/${params.id}`);
        if (response.data.success) {
          const postData = response.data.data;
          const category = categories.find(cat => cat.id === postData.categoryId);
          
          if (category?.name === "모집 게시판") {
            const recruitmentResponse = await privateApi.get(`/api/v1/recruitment/posts/${params.id}`);
            if (recruitmentResponse.data.success) {
              setPost({
                ...recruitmentResponse.data.data,
                type: 'RECRUITMENT'
              });
            }
          } else {
            setPost({
              ...postData,
              type: 'FREE'
            });
          }
        }
      } catch (error: any) {
        console.error('Error:', error.response);
        if (error.response?.status === 401) {
          alert('세션이 만료되었습니다. 다시 로그인해주세요.');
          router.push('/login');
        }
      } finally {
        setIsLoading(false);
      }
    };

    if (categories.length > 0) {
      fetchPost();
    }
  }, [params.id, user, categories]);

  const isRecruitmentPost = () => {
    if (!post || !categories.length) return false;
    const category = categories.find(cat => cat.id === post.categoryId);
    return category?.name === '모집 게시판';
  };

  const handleEdit = () => {
    if (!post) return;
    
    if (isRecruitmentPost()) {
      router.push(`/post/recruitment/${params.id}/edit`);
    } else {
      router.push(`/post/${params.id}/edit`);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
      return;
    }

    setIsDeleting(true);
    try {
      const endpoint = isRecruitmentPost()
        ? `/api/v1/recruitment/posts/${params.id}`
        : `/api/v1/free/posts/${params.id}`;

      const response = await privateApi.delete(endpoint);
      if (response.data.success) {
        alert('게시글이 삭제되었습니다.');
        router.push('/post');
      }
    } catch (error: any) {
      console.error('Error:', error);
      alert('게시글 삭제에 실패했습니다.');
    } finally {
      setIsDeleting(false);
    }
  };

  if (isLoading) {
    return <div className="max-w-4xl mx-auto p-8">로딩중...</div>;
  }

  if (!post) {
    return <div className="max-w-4xl mx-auto p-8">게시글을 찾을 수 없습니다.</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto p-8">
        {/* 상단 네비게이션 */}
        <div className="mb-8 flex items-center justify-between">
          <Link
            href="/post"
            className="flex items-center text-gray-600 hover:text-blue-600 transition-colors"
          >
            <ArrowLeftIcon className="h-5 w-5 mr-2" />
            게시글 목록으로
          </Link>
          {post.isAuthor && (
            <div className="flex gap-2">
              <button
                onClick={handleEdit}
                className="px-4 py-2 text-sm text-gray-600 hover:text-blue-600 transition-colors"
              >
                수정
              </button>
              <button
                onClick={handleDelete}
                className="px-4 py-2 text-sm text-gray-600 hover:text-red-600 transition-colors"
                disabled={isDeleting}
              >
                {isDeleting ? '삭제 중...' : '삭제'}
              </button>
            </div>
          )}
        </div>

        {/* 게시글 카드 */}
        <div className="bg-white rounded-xl shadow-lg p-8">
          {/* 게시글 타입 및 메타 정보 */}
          <div className="flex flex-wrap items-center gap-3 mb-6">
            <span className={`px-3 py-1 text-sm font-medium rounded-full ${
              isRecruitmentPost()
                ? 'text-green-600 bg-green-50'
                : 'text-blue-600 bg-blue-50'
            }`}>
              {isRecruitmentPost() ? '모집글' : '자유글'}
            </span>
            <div className="flex items-center gap-2 text-sm text-gray-500">
              <div className="flex items-center gap-2">
                {post.authorImg ? (
                  <img 
                    src={post.authorImg} 
                    alt={post.authorName}
                    className="w-6 h-6 rounded-full object-cover"
                  />
                ) : (
                  <div className="w-6 h-6 rounded-full bg-gray-200 flex items-center justify-center">
                    <span className="text-xs text-gray-500">익명</span>
                  </div>
                )}
                <span>{post.authorName}</span>
              </div>
              <span>•</span>
              <span>{formatDate(post.createdAt)}</span>
            </div>
          </div>

          {/* 제목 */}
          <h1 className="text-2xl font-bold mb-6">{post.subject}</h1>

          {/* 모집글 추가 정보 */}
          {isRecruitmentPost() && (
            <div className="bg-gray-50 p-4 rounded-lg mb-6">
              <p className="text-gray-700">모집 인원: {post.numOfApplicants}명</p>
              {post.jobPostingTitle && (
                <p className="text-gray-700">채용공고: {post.jobPostingTitle}</p>
              )}
              <p className="text-gray-700">
                상태: {post.recruitmentStatus === 'CLOSED' ? '모집 마감' : '모집 중'}
              </p>
            </div>
          )}

          {/* 본문 내용 */}
          <div className="prose max-w-none mb-8">
            {post.content}
          </div>

          {/* 하단 정보 */}
          <div className="flex items-center gap-4 text-sm text-gray-500 border-t pt-6">
            <div className="flex items-center gap-2">
              <HeartIcon className="h-5 w-5" />
              <span>좋아요 {post.voterCount || 0}</span>
            </div>
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
      </div>
    </div>
  );
}
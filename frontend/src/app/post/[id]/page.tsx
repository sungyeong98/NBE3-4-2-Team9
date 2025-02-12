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
  FunnelIcon,
  ArrowRightIcon
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

    if (post.type === 'RECRUITMENT') {
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
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 상단 네비게이션 */}
      <div className="mb-6 flex items-center space-x-4">
        <button
          onClick={() => router.back()}
          className="inline-flex items-center text-gray-600 hover:text-blue-600 transition-colors"
        >
          <ArrowLeftIcon className="h-5 w-5 mr-2" />
          목록으로
        </button>
        {post.isAuthor && (
          <div className="flex items-center space-x-2 ml-auto">
            <button
              onClick={handleEdit}
              className="px-4 py-2 text-sm text-blue-600 hover:bg-blue-50 rounded-md transition-colors"
            >
              수정
            </button>
            <button
              onClick={handleDelete}
              disabled={isDeleting}
              className="px-4 py-2 text-sm text-red-600 hover:bg-red-50 rounded-md transition-colors"
            >
              {isDeleting ? '삭제 중...' : '삭제'}
            </button>
          </div>
        )}
      </div>

      {/* 메인 컨텐츠 */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200">
        {/* 게시글 헤더 */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-4">
              {post.authorImg ? (
                <img
                  src={post.authorImg}
                  alt={post.authorName}
                  className="w-10 h-10 rounded-full object-cover"
                />
              ) : (
                <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">
                  <span className="text-gray-500 text-lg">{post.authorName?.charAt(0)}</span>
                </div>
              )}
              <div>
                <div className="font-medium text-gray-900">{post.authorName}</div>
                <div className="text-sm text-gray-500">{formatDate(post.createdAt)}</div>
              </div>
            </div>
            <div className="text-sm text-gray-500">
              조회 {post.viewCount || 0}
            </div>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">{post.subject}</h1>
        </div>

        {/* 모집글 추가 정보 */}
        {isRecruitmentPost() && (
          <div className="px-6 py-4 bg-blue-50 border-b border-blue-100">
            <div className="flex flex-col space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-blue-800">모집 인원</span>
                <span className="font-medium text-blue-900">{post.numOfApplicants}명</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-blue-800">모집 상태</span>
                <span className={`font-medium ${post.recruitmentStatus === 'CLOSED' ? 'text-red-600' : 'text-green-600'}`}>
                  {post.recruitmentStatus === 'CLOSED' ? '모집 마감' : '모집 중'}
                </span>
              </div>
              {post.jobPostingId && (
                <div className="pt-2 mt-2 border-t border-blue-200">
                  <div className="flex items-center justify-between">
                    <span className="text-blue-800">연관된 채용공고</span>
                    <Link
                      href={`/job-posting/${post.jobPostingId}`}
                      className="text-blue-600 hover:text-blue-800 font-medium text-sm inline-flex items-center"
                    >
                      {post.jobPostingTitle}
                      <ArrowRightIcon className="h-4 w-4 ml-1" />
                    </Link>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}

        {/* 본문 내용 */}
        <div className="p-6 prose max-w-none">
          {post.content}
        </div>

        {/* 하단 정보 */}
        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50 rounded-b-xl">
          <div className="flex items-center space-x-6 text-sm">
            <button className="flex items-center space-x-2 text-gray-600 hover:text-blue-600 transition-colors">
              <HeartIcon className="h-5 w-5" />
              <span>좋아요 {post.voterCount || 0}</span>
            </button>
            <button className="flex items-center space-x-2 text-gray-600 hover:text-blue-600 transition-colors">
              <ChatBubbleLeftIcon className="h-5 w-5" />
              <span>댓글 {post.commentCount || 0}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
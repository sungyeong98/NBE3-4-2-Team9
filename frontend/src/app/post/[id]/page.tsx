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

interface Comment {
  id: number;
  content: string;
  createdAt: string;
  modifiedAt: string;
  profileImageUrl: string | null;
  authorName: string;
  author: boolean;
}

export default function PostDetail() {
  const router = useRouter();
  const params = useParams();
  const { user } = useSelector((state: RootState) => state.auth);
  const [post, setPost] = useState<any>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDeleting, setIsDeleting] = useState(false);
  const [comments, setComments] = useState<Comment[]>([]);
  const [commentContent, setCommentContent] = useState('');
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState('');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

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

  // 댓글 목록 조회
  const fetchComments = async (pageNum: number) => {
    try {
      const response = await privateApi.get(`/api/v1/posts/${params.id}/comments`, {
        params: {
          page: pageNum,
          size: 10
        }
      });
      if (response.data.success) {
        const newComments = response.data.data.content;
        console.log('댓글 목록:', newComments); // 디버깅용 로그
        setComments(pageNum === 0 ? newComments : prev => [...prev, ...newComments]);
        setHasMore(!response.data.data.last);
      }
    } catch (error) {
      console.error('댓글 조회 실패:', error);
    }
  };

  // 댓글 작성
  const handleCommentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentContent.trim()) return;

    try {
      const response = await privateApi.post(`/api/v1/posts/${params.id}/comments`, {
        content: commentContent
      });
      if (response.data.success) {
        setCommentContent('');
        setPage(0);
        setComments([]);
        fetchComments(0);
      }
    } catch (error) {
      console.error('댓글 작성 실패:', error);
    }
  };

  // 댓글 수정
  const handleCommentEdit = async (commentId: number) => {
    if (!editContent.trim()) return;
    
    try {
      const response = await privateApi.patch(`/api/v1/posts/${params.id}/comments/${commentId}`, {
        content: editContent
      });
      if (response.data.success) {
        setEditingCommentId(null);
        setComments(prev => 
          prev.map(comment => 
            comment.id === commentId 
              ? { ...comment, content: editContent, modifiedAt: new Date().toISOString() }
              : comment
          )
        );
      }
    } catch (error) {
      console.error('댓글 수정 실패:', error);
      alert('댓글 수정에 실패했습니다.');
    }
  };

  // 댓글 삭제
  const handleCommentDelete = async (commentId: number) => {
    if (!confirm('댓글을 삭제하시겠습니까?')) return;

    try {
      const response = await privateApi.delete(`/api/v1/posts/${params.id}/comments/${commentId}`);
      if (response.data.success) {
        setComments(prev => prev.filter(comment => comment.id !== commentId));
      }
    } catch (error) {
      console.error('댓글 삭제 실패:', error);
      alert('댓글 삭제에 실패했습니다.');
    }
  };

  // 댓글 목록 초기 로딩
  useEffect(() => {
    if (params.id) {
      fetchComments(page);
    }
  }, [params.id, page]);

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

      {/* 댓글 섹션 */}
      <div className="mt-8">
        <h3 className="text-lg font-bold mb-4">댓글</h3>
        
        {/* 댓글 작성 폼 */}
        <form onSubmit={handleCommentSubmit} className="mb-6">
          <textarea
            value={commentContent}
            onChange={(e) => setCommentContent(e.target.value)}
            placeholder="댓글을 작성하세요..."
            className="w-full p-3 border rounded-lg resize-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            rows={3}
          />
          <button
            type="submit"
            className="mt-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            댓글 작성
          </button>
        </form>

        {/* 댓글 목록 */}
        <div className="space-y-4">
          {comments.map((comment) => (
            <div key={comment.id} className="bg-gray-50 p-4 rounded-lg">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center space-x-2">
                  {comment.profileImageUrl ? (
                    <img
                      src={comment.profileImageUrl}
                      alt={comment.authorName}
                      className="w-8 h-8 rounded-full"
                    />
                  ) : (
                    <div className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center">
                      <span className="text-gray-600">{comment.authorName[0]}</span>
                    </div>
                  )}
                  <span className="font-medium">{comment.authorName}</span>
                </div>
                <span className="text-sm text-gray-500">
                  {formatDate(comment.createdAt)}
                </span>
              </div>

              {editingCommentId === comment.id ? (
                <div>
                  <textarea
                    value={editContent}
                    onChange={(e) => setEditContent(e.target.value)}
                    className="w-full p-2 border rounded-lg resize-none"
                    rows={3}
                  />
                  <div className="flex justify-end space-x-2 mt-2">
                    <button
                      onClick={() => handleCommentEdit(comment.id)}
                      className="px-3 py-1 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
                    >
                      수정 완료
                    </button>
                    <button
                      onClick={() => setEditingCommentId(null)}
                      className="px-3 py-1 bg-gray-500 text-white rounded-lg hover:bg-gray-600"
                    >
                      취소
                    </button>
                  </div>
                </div>
              ) : (
                <div>
                  <p className="text-gray-700">{comment.content}</p>
                  {comment.author && (
                    <div className="flex justify-end space-x-2 mt-2">
                      <button
                        onClick={() => {
                          setEditingCommentId(comment.id);
                          setEditContent(comment.content);
                        }}
                        className="text-sm text-blue-600 hover:text-blue-800"
                      >
                        수정
                      </button>
                      <button
                        onClick={() => handleCommentDelete(comment.id)}
                        className="text-sm text-red-600 hover:text-red-800"
                      >
                        삭제
                      </button>
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}

          {/* 더보기 버튼 */}
          {hasMore && (
            <button
              onClick={() => setPage(prev => prev + 1)}
              className="w-full py-2 text-blue-600 hover:text-blue-800"
            >
              더보기
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
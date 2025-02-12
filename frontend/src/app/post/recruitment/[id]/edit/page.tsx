'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import privateApi from '@/api/axios';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';

export default function EditRecruitmentPost() {
  const router = useRouter();
  const params = useParams();
  const { user } = useSelector((state: RootState) => state.auth);
  const [subject, setSubject] = useState('');
  const [content, setContent] = useState('');
  const [numOfApplicants, setNumOfApplicants] = useState<number>(1);
  const [jobPostingId, setJobPostingId] = useState<number | null>(null);
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await privateApi.get(`/api/v1/recruitment/posts/${params.id}`);
        if (response.data.success) {
          const post = response.data.data;
          if (!post.isAuthor) {
            alert('수정 권한이 없습니다.');
            router.back();
            return;
          }
          setSubject(post.subject);
          setContent(post.content);
          setNumOfApplicants(post.numOfApplicants);
          setJobPostingId(post.jobPostingId);
        }
      } catch (error: any) {
        console.error('Error fetching post:', error.response);
        if (error.response?.status === 401) {
          alert('세션이 만료되었습니다. 다시 로그인해주세요.');
          router.push('/login');
        } else {
          alert('게시글을 불러오는데 실패했습니다.');
          router.back();
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchPost();
  }, [params.id, router]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await privateApi.get('/api/v1/category');
        if (response.data.success) {
          const categories = response.data.data;
          const recruitmentCategory = categories.find(cat => cat.name === "모집 게시판");
          if (recruitmentCategory) {
            setCategoryId(recruitmentCategory.id);
          }
        }
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };

    fetchCategories();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!subject.trim() || !content.trim()) {
      alert('제목과 내용을 모두 입력해주세요.');
      return;
    }

    if (numOfApplicants < 1) {
      alert('모집 인원은 1명 이상이어야 합니다.');
      return;
    }

    if (!jobPostingId) {
      alert('채용공고 정보가 필요합니다.');
      return;
    }

    if (!categoryId) {
      alert('카테고리 정보를 불러오는데 실패했습니다.');
      return;
    }

    try {
      const response = await privateApi.patch(`/api/v1/recruitment/posts/${params.id}`, {
        subject,
        content,
        numOfApplicants: Number(numOfApplicants),
        jobPostingId: Number(jobPostingId),
        categoryId: categoryId
      });

      if (response.data.success) {
        alert('게시글이 수정되었습니다.');
        router.push(`/post/${params.id}`);
      }
    } catch (error: any) {
      if (error.response?.status === 401) {
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        router.push('/login');
      } else if (error.response?.data?.code === 1005) {
        alert('수정 권한이 없습니다.');
        router.back();
      } else {
        alert(error.response?.data?.message || '게시글 수정에 실패했습니다.');
      }
    }
  };

  if (isLoading) {
    return <div className="max-w-4xl mx-auto p-8">로딩중...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto p-8">
      <div className="mb-8 flex items-center justify-between">
        <button
          onClick={() => router.back()}
          className="flex items-center text-gray-600 hover:text-blue-600 transition-colors"
        >
          <ArrowLeftIcon className="h-5 w-5 mr-2" />
          뒤로가기
        </button>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="subject" className="block text-sm font-medium text-gray-700 mb-2">
            제목
          </label>
          <input
            type="text"
            id="subject"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            required
          />
        </div>

        <div>
          <label htmlFor="numOfApplicants" className="block text-sm font-medium text-gray-700 mb-2">
            모집 인원
          </label>
          <input
            type="number"
            id="numOfApplicants"
            value={numOfApplicants}
            onChange={(e) => setNumOfApplicants(Math.max(1, parseInt(e.target.value)))}
            min="1"
            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            required
          />
        </div>

        <div>
          <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-2">
            내용
          </label>
          <textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={10}
            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            required
          />
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            className="px-6 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            수정하기
          </button>
        </div>
      </form>
    </div>
  );
} 
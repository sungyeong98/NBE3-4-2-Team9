'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import privateApi from '@/api/axios';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';

export default function WriteRecruitmentPost() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const jobPostingId = searchParams.get('jobPostingId');
  const { user } = useSelector((state: RootState) => state.auth);

  const [subject, setSubject] = useState('');
  const [content, setContent] = useState('');
  const [numOfApplicants, setNumOfApplicants] = useState('');
  const [isValidJobPosting, setIsValidJobPosting] = useState(false);

  useEffect(() => {
    const validateJobPosting = async () => {
      if (!jobPostingId) return;
      
      try {
        const response = await privateApi.get(`/api/v1/job-posting/${jobPostingId}`);
        setIsValidJobPosting(response.data.success);
      } catch (error) {
        console.error('채용공고 검증 실패:', error);
        setIsValidJobPosting(false);
      }
    };

    validateJobPosting();
  }, [jobPostingId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const token = user?.isAdmin 
        ? localStorage.getItem('adminToken')
        : localStorage.getItem('accessToken');

      if (!token) {
        alert('로그인이 필요한 서비스입니다.');
        router.push('/login');
        return;
      }

      if (!jobPostingId) {
        alert('채용공고 정보가 없습니다.');
        return;
      }

      if (!isValidJobPosting) {
        alert('유효하지 않은 채용공고입니다.');
        router.push('/job-posting');
        return;
      }

      const requestData = {
        subject,
        content,
        jobPostingId: Number(jobPostingId),
        numOfApplicants: Number(numOfApplicants)
      };

      console.log('Request Data:', requestData);

      const response = await privateApi.post('/api/v1/recruitment/posts', 
        requestData,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (response.data.success) {
        alert('모집글이 작성되었습니다.');
        router.push('/post');
      }
    } catch (error: any) {
      console.error('Error details:', error.response?.data);
      if (error.response?.status === 401) {
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        router.push('/login');
      } else if (error.response?.status === 404) {
        alert('채용공고를 찾을 수 없습니다. 올바른 채용공고를 선택해주세요.');
        router.push('/job-posting');
      } else {
        alert(error.response?.data?.message || '모집글 작성에 실패했습니다.');
      }
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-2xl font-bold mb-6">모집 게시글 작성</h1>
      
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
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
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
            onChange={(e) => setNumOfApplicants(e.target.value)}
            min="1"
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
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
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
            required
          />
        </div>

        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-4 py-2 text-gray-600 hover:text-gray-800"
          >
            취소
          </button>
          <button
            type="submit"
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            작성하기
          </button>
        </div>
      </form>
    </div>
  );
} 
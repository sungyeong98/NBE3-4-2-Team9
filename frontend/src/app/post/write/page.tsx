'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import privateApi from '@/api/axios';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';

export default function WritePost() {
  const router = useRouter();
  const [subject, setSubject] = useState('');
  const [content, setContent] = useState('');
  const { user } = useSelector((state: RootState) => state.auth);

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

      const response = await privateApi.post('/api/v1/free/posts', 
        {
          subject,
          content
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (response.data.success) {
        router.push('/post');
      }
    } catch (error: any) {
      console.error('Error:', error.response);
      alert(error.response?.data?.message || '게시글 작성에 실패했습니다.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-2xl font-bold mb-6">자유 게시글 작성</h1>
      
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
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
            required
          />
        </div>

        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
          >
            취소
          </button>
          <button
            type="submit"
            className="px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700"
          >
            작성하기
          </button>
        </div>
      </form>
    </div>
  );
}
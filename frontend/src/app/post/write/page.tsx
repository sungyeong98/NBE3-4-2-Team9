'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { privateApi } from '@/api/axios';

interface Category {
  id: number;
  name: string;
}

export default function WritePost() {
  const router = useRouter();
  const [subject, setSubject] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('');
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // 카테고리 목록 가져오기
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setIsLoading(true);
        const response = await privateApi.get('/api/v1/category');
        console.log('카테고리 응답:', response.data);

        if (response.data.success) {
          const categoryData = response.data.data;
          setCategories(categoryData);
          if (categoryData.length > 0) {
            setCategory(categoryData[0].id.toString());
          }
        }
      } catch (error) {
        console.error('카테고리 조회 실패:', error);
        alert('카테고리 목록을 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchCategories();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!subject.trim() || !content.trim() || !category) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    try {
      const postData = {
        subject: subject.trim(),
        content: content.trim(),
        categoryId: parseInt(category)
      };

      console.log('전송할 데이터:', postData);

      const response = await privateApi.post('/api/v1/posts', postData);

      console.log('게시글 작성 응답:', response.data);

      if (response.data.success) {
        alert('게시글이 작성되었습니다.');
        router.push('/post');
      } else {
        throw new Error(response.data.message || '게시글 작성에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('게시글 작성 실패:', error);
      alert(error.response?.data?.message || '게시글 작성에 실패했습니다.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">게시글 작성</h1>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            카테고리
          </label>
          {isLoading ? (
            <div className="w-full px-3 py-2 border border-gray-300 rounded-md">
              로딩중...
            </div>
          ) : (
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            >
              <option value="">카테고리를 선택하세요</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            제목
          </label>
          <input
            type="text"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="제목을 입력하세요"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            내용
          </label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={15}
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="내용을 입력하세요"
            required
          />
        </div>

        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-4 py-2 text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
          >
            취소
          </button>
          <button
            type="submit"
            className="px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700"
            disabled={isLoading}
          >
            작성하기
          </button>
        </div>
      </form>
    </div>
  );
} 
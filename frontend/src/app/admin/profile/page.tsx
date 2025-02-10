'use client';

import { useSelector } from 'react-redux';
import { useRouter } from 'next/navigation';
import { RootState } from '@/store/store';
import { useState, useEffect } from 'react';
import { privateApi } from '@/api/axios';
import { UserCircleIcon, PlusIcon, TrashIcon } from '@heroicons/react/24/outline';

interface Category {
  id: string;
  name: string;
}

export default function AdminProfile() {
  const router = useRouter();
  const { user } = useSelector((state: RootState) => state.auth);
  const [newCategory, setNewCategory] = useState('');
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    const adminToken = localStorage.getItem('adminToken');
    if (!user?.email?.includes('admin') || !adminToken) {
      router.push('/admin/login');
      return;
    }
    fetchCategories();
  }, [user, router]);

  const fetchCategories = async () => {
    try {
      const response = await privateApi.get('/api/v1/category');
      if (response.data.success) {
        setCategories(response.data.data);
      }
    } catch (error) {
      console.error('카테고리 조회 실패:', error);
    }
  };

  const handleAddCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCategory.trim()) return;

    try {
      const response = await privateApi.post('/api/v1/category', {
        name: newCategory.trim()
      });

      if (response.data.success) {
        alert('카테고리가 추가되었습니다.');
        setNewCategory('');
        fetchCategories(); // 카테고리 목록 새로고침
      }
    } catch (error) {
      console.error('카테고리 추가 실패:', error);
      alert('카테고리 추가에 실패했습니다.');
    }
  };

  const handleDeleteCategory = async (categoryId: string) => {
    if (!confirm('정말로 이 카테고리를 삭제하시겠습니까?')) {
      return;
    }

    try {
      const response = await privateApi.delete(`/api/v1/category/${categoryId}`);
      if (response.data.success) {
        alert('카테고리가 삭제되었습니다.');
        fetchCategories(); // 카테고리 목록 새로고침
      }
    } catch (error) {
      console.error('카테고리 삭제 실패:', error);
      alert('카테고리 삭제에 실패했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        <div className="bg-white rounded-xl shadow-lg p-6 mb-6">
          <div className="flex items-center gap-4 mb-6">
            <UserCircleIcon className="w-16 h-16 text-gray-400" />
            <div>
              <h1 className="text-2xl font-bold text-gray-900">관리자 프로필</h1>
              <p className="text-gray-500">{user?.email}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-lg p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">카테고리 관리</h2>
          
          <form onSubmit={handleAddCategory} className="mb-6">
            <div className="flex gap-2">
              <input
                type="text"
                value={newCategory}
                onChange={(e) => setNewCategory(e.target.value)}
                placeholder="새 카테고리 이름"
                className="flex-1 p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <button
                type="submit"
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
              >
                <PlusIcon className="w-5 h-5" />
                추가
              </button>
            </div>
          </form>

          <div className="space-y-2">
            {categories.map((category) => (
              <div
                key={category.id}
                className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
              >
                <span className="text-gray-700">{category.name}</span>
                <button
                  onClick={() => handleDeleteCategory(category.id)}
                  className="p-2 text-gray-500 hover:text-red-600 transition-colors"
                >
                  <TrashIcon className="w-5 h-5" />
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
} 
'use client';

import { useSelector } from 'react-redux';
import { useRouter } from 'next/navigation';
import { RootState } from '@/store/store';
import { useState, useEffect } from 'react';
import { privateApi } from '@/api/axios';
import { UserCircleIcon, PlusIcon } from '@heroicons/react/24/outline';

export default function AdminProfile() {
  const router = useRouter();
  const { user } = useSelector((state: RootState) => state.auth);
  const [newCategory, setNewCategory] = useState('');

  useEffect(() => {
    const adminToken = localStorage.getItem('adminToken');
    if (!user?.email?.includes('admin') || !adminToken) {
      router.push('/admin/login');
      return;
    }
  }, [user, router]);

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
      }
    } catch (error) {
      console.error('카테고리 추가 실패:', error);
      alert('카테고리 추가에 실패했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* 프로필 카드 */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="p-6">
              <div className="flex items-center justify-center mb-6">
                <UserCircleIcon className="h-24 w-24 text-gray-400" />
              </div>
              <div className="text-center">
                <h2 className="text-xl font-bold text-gray-900">{user.name}</h2>
                <p className="text-gray-500">{user.email}</p>
                <div className="mt-3">
                  <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                    관리자
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* 카테고리 관리 카드 */}
          <div className="md:col-span-2 bg-white rounded-lg shadow">
            <div className="p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-6">카테고리 관리</h3>
              
              {/* 카테고리 추가 폼 */}
              <form onSubmit={handleAddCategory} className="space-y-4">
                <div className="flex items-center gap-4">
                  <div className="flex-1">
                    <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-1">
                      새 카테고리
                    </label>
                    <input
                      id="category"
                      type="text"
                      value={newCategory}
                      onChange={(e) => setNewCategory(e.target.value)}
                      placeholder="카테고리 이름을 입력하세요"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    />
                  </div>
                  <button
                    type="submit"
                    className="inline-flex items-center px-4 py-2 mt-6 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    <PlusIcon className="h-5 w-5 mr-2" />
                    추가
                  </button>
                </div>
              </form>

              <div className="mt-6">
                <p className="text-sm text-gray-500">
                  * 추가된 카테고리는 게시판에서 즉시 사용할 수 있습니다.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 
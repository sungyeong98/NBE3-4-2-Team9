'use client';

import { useState, useEffect } from 'react';
import { Category } from '@/types/post/Category';
import { getCategories } from '@/api/category';
import { createCategory } from '@/api/category';
import { 
  PlusIcon, 
  PencilSquareIcon, 
  TrashIcon,
  ChartBarIcon,
  UsersIcon,
  ChatBubbleLeftIcon,
  TagIcon
} from '@heroicons/react/24/outline';

export default function AdminProfile() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isAddingCategory, setIsAddingCategory] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await getCategories();
      if (response.success) {
        setCategories(response.data);
      }
    } catch (error) {
      console.error('Failed to fetch categories:', error);
    }
  };

  const handleAddCategory = async () => {
    try {
      if (!newCategoryName.trim()) {
        alert('카테고리 이름을 입력해주세요.');
        return;
      }

      const response = await createCategory(newCategoryName);
      if (response.success) {
        setIsAddingCategory(false);
        setNewCategoryName('');
        // 카테고리 목록 새로고침
        fetchCategories();
      } else {
        alert('카테고리 추가에 실패했습니다.');
      }
    } catch (error) {
      console.error('Failed to add category:', error);
      alert('카테고리 추가 중 오류가 발생했습니다.');
    }
  };

  const handleEditCategory = async (category: Category) => {
    // TODO: API 연동
    console.log('Editing category:', category);
    setEditingCategory(null);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 관리자 헤더 */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="py-6">
            <h1 className="text-2xl font-bold text-gray-900">관리자 대시보드</h1>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          {/* 사이드바 */}
          <div className="space-y-4">
            <div className="bg-white rounded-lg shadow p-4">
              <div className="flex items-center gap-3 p-2 bg-blue-50 text-blue-700 rounded-md">
                <TagIcon className="h-5 w-5" />
                <span className="font-medium">카테고리 관리</span>
              </div>
              <div className="mt-2 space-y-1">
                <button className="w-full p-2 text-left text-gray-600 hover:bg-gray-50 rounded-md">
                  게시글 관리
                </button>
                <button className="w-full p-2 text-left text-gray-600 hover:bg-gray-50 rounded-md">
                  회원 관리
                </button>
                <button className="w-full p-2 text-left text-gray-600 hover:bg-gray-50 rounded-md">
                  통계
                </button>
              </div>
            </div>

            {/* 통계 카드 */}
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-white rounded-lg shadow p-4">
                <div className="flex items-center justify-between">
                  <ChartBarIcon className="h-6 w-6 text-blue-600" />
                  <span className="text-lg font-semibold">150</span>
                </div>
                <div className="mt-2 text-sm text-gray-600">총 게시글</div>
              </div>
              <div className="bg-white rounded-lg shadow p-4">
                <div className="flex items-center justify-between">
                  <UsersIcon className="h-6 w-6 text-green-600" />
                  <span className="text-lg font-semibold">42</span>
                </div>
                <div className="mt-2 text-sm text-gray-600">총 회원수</div>
              </div>
              <div className="bg-white rounded-lg shadow p-4">
                <div className="flex items-center justify-between">
                  <ChatBubbleLeftIcon className="h-6 w-6 text-purple-600" />
                  <span className="text-lg font-semibold">89</span>
                </div>
                <div className="mt-2 text-sm text-gray-600">총 댓글</div>
              </div>
            </div>
          </div>

          {/* 메인 컨텐츠 */}
          <div className="md:col-span-3">
            <div className="bg-white rounded-lg shadow">
              <div className="p-6">
                <div className="flex items-center justify-between mb-6">
                  <h2 className="text-lg font-semibold text-gray-900">카테고리 관리</h2>
                  <button
                    onClick={() => setIsAddingCategory(true)}
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                  >
                    <PlusIcon className="h-5 w-5" />
                    카테고리 추가
                  </button>
                </div>

                {/* 카테고리 목록 */}
                <div className="space-y-4">
                  {isAddingCategory && (
                    <div className="flex items-center gap-4 p-4 bg-blue-50 rounded-lg">
                      <input
                        type="text"
                        placeholder="새 카테고리 이름"
                        value={newCategoryName}
                        onChange={(e) => setNewCategoryName(e.target.value)}
                        className="flex-1 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                      />
                      <button
                        onClick={handleAddCategory}
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                      >
                        추가
                      </button>
                      <button
                        onClick={() => setIsAddingCategory(false)}
                        className="px-4 py-2 text-gray-600 hover:text-gray-800 transition-colors"
                      >
                        취소
                      </button>
                    </div>
                  )}

                  {categories.map((category) => (
                    <div
                      key={category.id}
                      className="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
                    >
                      {editingCategory?.id === category.id ? (
                        <input
                          type="text"
                          value={editingCategory.name}
                          onChange={(e) => setEditingCategory({ ...editingCategory, name: e.target.value })}
                          className="flex-1 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                        />
                      ) : (
                        <span className="text-gray-800">{category.name}</span>
                      )}
                      
                      <div className="flex items-center gap-2">
                        {editingCategory?.id === category.id ? (
                          <>
                            <button
                              onClick={() => handleEditCategory(editingCategory)}
                              className="p-2 text-blue-600 hover:text-blue-800 transition-colors"
                            >
                              저장
                            </button>
                            <button
                              onClick={() => setEditingCategory(null)}
                              className="p-2 text-gray-600 hover:text-gray-800 transition-colors"
                            >
                              취소
                            </button>
                          </>
                        ) : (
                          <>
                            <button
                              onClick={() => setEditingCategory(category)}
                              className="p-2 text-gray-600 hover:text-blue-600 transition-colors"
                            >
                              <PencilSquareIcon className="h-5 w-5" />
                            </button>
                            <button
                              onClick={() => console.log('Delete category:', category.id)}
                              className="p-2 text-gray-600 hover:text-red-600 transition-colors"
                            >
                              <TrashIcon className="h-5 w-5" />
                            </button>
                          </>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 
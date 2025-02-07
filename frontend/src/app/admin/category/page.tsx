'use client';

import { useState, useEffect } from 'react';
import { PencilIcon, TrashIcon, XMarkIcon } from '@heroicons/react/24/outline';
import privateApi from '@/api/axios';
import { Category } from '@/types/category';

export default function CategoryManagement() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editName, setEditName] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await privateApi.get('/api/v1/category');
      if (response.data.success) {
        setCategories(response.data.data);
      }
    } catch (error) {
      console.error('카테고리 로딩 실패:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (category: Category) => {
    setEditingId(category.id);
    setEditName(category.name);
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditName('');
  };

  const handleUpdate = async (id: string) => {
    if (!editName.trim()) {
      alert('카테고리 이름을 입력해주세요.');
      return;
    }

    try {
      const response = await privateApi.patch(`/api/v1/category/${id}`, {
        name: editName.trim()
      });

      if (response.data.success) {
        setCategories(categories.map(cat => 
          cat.id === id ? { ...cat, name: editName.trim() } : cat
        ));
        handleCancelEdit();
      }
    } catch (error: any) {
      console.error('카테고리 수정 실패:', error);
      alert(error.response?.data?.message || '카테고리 수정에 실패했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">카테고리 관리</h1>
        
        <div className="bg-white rounded-xl shadow-sm">
          <div className="divide-y">
            {isLoading ? (
              [...Array(3)].map((_, index) => (
                <div key={index} className="p-4 animate-pulse">
                  <div className="h-6 bg-gray-200 rounded w-1/3"></div>
                </div>
              ))
            ) : (
              categories.map((category) => (
                <div key={category.id} className="p-4">
                  {editingId === category.id ? (
                    <div className="flex items-center gap-4">
                      <input
                        type="text"
                        value={editName}
                        onChange={(e) => setEditName(e.target.value)}
                        className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                        placeholder="카테고리 이름"
                      />
                      <button
                        onClick={() => handleUpdate(category.id)}
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                      >
                        저장
                      </button>
                      <button
                        onClick={handleCancelEdit}
                        className="p-2 text-gray-500 hover:text-gray-700"
                      >
                        <XMarkIcon className="h-5 w-5" />
                      </button>
                    </div>
                  ) : (
                    <div className="flex items-center justify-between">
                      <span className="text-gray-900">{category.name}</span>
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => handleEdit(category)}
                          className="p-2 text-gray-500 hover:text-blue-600 transition-colors"
                        >
                          <PencilIcon className="h-5 w-5" />
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
} 
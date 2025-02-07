'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useSelector } from 'react-redux';
import { 
  TagIcon, 
  UserGroupIcon, 
  DocumentTextIcon, 
  ChartBarIcon,
  UserCircleIcon,
  PencilIcon,
  TrashIcon
} from '@heroicons/react/24/outline';
import { RootState } from '@/store/store';
import { privateApi } from '@/api/axios';

interface Category {
  id: number;
  name: string;
}

export default function AdminDashboard() {
  const router = useRouter();
  const { user } = useSelector((state: RootState) => state.auth);
  const [newCategory, setNewCategory] = useState('');
  const [activeTab, setActiveTab] = useState('카테고리');
  const [categories, setCategories] = useState<Category[]>([]);

  // 카테고리 목록 조회
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

  // 컴포넌트 마운트 시 카테고리 목록 조회
  useEffect(() => {
    fetchCategories();
  }, []);

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
        // 카테고리 추가 후 목록 새로고침
        fetchCategories();
      }
    } catch (error) {
      console.error('카테고리 추가 실패:', error);
      alert('카테고리 추가에 실패했습니다.');
    }
  };

  // 카테고리 삭제 핸들러
  const handleDeleteCategory = async (categoryId: number) => {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    try {
      const response = await privateApi.delete(`/api/v1/category/${categoryId}`);
      if (response.data.success) {
        alert('카테고리가 삭제되었습니다.');
        // 카테고리 삭제 후 목록 새로고침
        fetchCategories();
      }
    } catch (error) {
      console.error('카테고리 삭제 실패:', error);
      alert('카테고리 삭제에 실패했습니다.');
    }
  };

  // 카테고리 수정 핸들러
  const handleEditCategory = async (category: Category) => {
    const newName = prompt('새로운 카테고리 이름을 입력하세요:', category.name);
    if (!newName || newName === category.name) return;

    try {
      const response = await privateApi.patch(`/api/v1/category/${category.id}`, {
        name: newName.trim()
      });

      if (response.data.success) {
        alert('카테고리가 수정되었습니다.');
        // 카테고리 수정 후 목록 새로고침
        fetchCategories();
      }
    } catch (error) {
      console.error('카테고리 수정 실패:', error);
      alert('카테고리 수정에 실패했습니다.');
    }
  };

  const menuItems = [
    { name: '카테고리', icon: TagIcon },
    { name: '게시글', icon: DocumentTextIcon },
    { name: '회원', icon: UserGroupIcon },
    { name: '통계', icon: ChartBarIcon },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* 관리자 헤더 */}
      <div className="bg-white border-b shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="py-6">
            <div className="flex items-center justify-between">
              <h1 className="text-3xl font-bold text-gray-900">관리자 대시보드</h1>
              {/* 관리자 프로필 미니 카드 */}
              <div className="flex items-center space-x-4 bg-blue-50 px-4 py-2 rounded-lg">
                <UserCircleIcon className="h-10 w-10 text-blue-600" />
                <div>
                  <p className="font-medium text-gray-900">{user?.name}</p>
                  <p className="text-sm text-gray-500">{user?.email}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          {/* 사이드바 */}
          <div className="space-y-4">
            <div className="bg-white rounded-xl shadow-lg overflow-hidden">
              {menuItems.map((item) => (
                <button
                  key={item.name}
                  onClick={() => setActiveTab(item.name)}
                  className={`w-full flex items-center gap-3 p-4 text-left transition-colors duration-150
                    ${activeTab === item.name 
                      ? 'bg-blue-50 text-blue-700 border-l-4 border-blue-700' 
                      : 'text-gray-600 hover:bg-gray-50'}`}
                >
                  <item.icon className="h-5 w-5" />
                  <span className="font-medium">{item.name} 관리</span>
                </button>
              ))}
            </div>

            {/* 통계 카드 */}
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-gradient-to-br from-blue-500 to-blue-600 p-4 rounded-xl shadow-lg">
                <p className="text-white text-sm">총 게시글</p>
                <p className="text-white text-2xl font-bold">150</p>
              </div>
              <div className="bg-gradient-to-br from-purple-500 to-purple-600 p-4 rounded-xl shadow-lg">
                <p className="text-white text-sm">총 회원수</p>
                <p className="text-white text-2xl font-bold">42</p>
              </div>
            </div>
          </div>

          {/* 메인 컨텐츠 영역 */}
          <div className="md:col-span-3">
            <div className="bg-white rounded-xl shadow-lg">
              <div className="p-6">
                <h2 className="text-xl font-bold text-gray-900 mb-6">
                  {activeTab} 관리
                </h2>

                {activeTab === '카테고리' && (
                  <div className="space-y-6">
                    <form onSubmit={handleAddCategory} className="flex gap-3">
                      <input
                        type="text"
                        value={newCategory}
                        onChange={(e) => setNewCategory(e.target.value)}
                        placeholder="새 카테고리 이름"
                        className="flex-1 rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                      />
                      <button
                        type="submit"
                        className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 
                                 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2
                                 transition-colors duration-150 shadow-md"
                      >
                        추가
                      </button>
                    </form>

                    {/* 카테고리 목록 */}
                    <div className="space-y-2">
                      {categories.map((category) => (
                        <div key={category.id} 
                             className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors duration-150">
                          <span className="font-medium text-gray-700">{category.name}</span>
                          <div className="flex gap-2">
                            <button 
                              onClick={() => handleEditCategory(category)}
                              className="p-1 text-gray-400 hover:text-blue-600">
                              <PencilIcon className="h-5 w-5" />
                            </button>
                            <button 
                              onClick={() => handleDeleteCategory(category.id)}
                              className="p-1 text-gray-400 hover:text-red-600">
                              <TrashIcon className="h-5 w-5" />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 
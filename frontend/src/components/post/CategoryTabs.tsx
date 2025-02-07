'use client';

import { Category } from '@/types/post/Category';
import Link from 'next/link';
import clsx from 'clsx';
import { SearchIcon } from '@heroicons/react/24/outline';

interface CategoryTabsProps {
  categories: Category[];
  currentCategory?: string;
}

export default function CategoryTabs({ categories, currentCategory }: CategoryTabsProps) {
  return (
    <div className="bg-white">
      {/* 상단 카테고리 탭 */}
      <div className="border-b">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center h-12 overflow-x-auto scrollbar-hide">
            <Link 
              href="/posts"
              className={clsx(
                "shrink-0 h-full px-6 inline-flex items-center",
                !currentCategory ? "text-blue-600 border-b-2 border-blue-600" : "text-gray-600"
              )}
            >
              전체
            </Link>
            {categories.map((category) => (
              <Link
                key={category.id}
                href={`/posts?category=${category.id}`}
                className={clsx(
                  "shrink-0 h-full px-6 inline-flex items-center text-[15px] font-medium whitespace-nowrap",
                  currentCategory === category.id.toString()
                    ? "text-blue-600 border-b-2 border-blue-600"
                    : "text-gray-600 hover:text-gray-900"
                )}
              >
                {category.name}
              </Link>
            ))}
          </div>
        </div>
      </div>

      {/* 검색/필터 영역 */}
      <div className="border-b">
        <div className="max-w-7xl mx-auto px-4 py-3">
          <div className="flex items-center gap-3">
            <select className="h-10 pl-3 pr-8 text-sm border rounded-lg min-w-[150px]">
              <option value="">전체 카테고리</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
            <div className="relative flex-1">
              <input
                type="text"
                placeholder="검색어를 입력하세요"
                className="w-full h-10 pl-10 pr-4 text-sm border rounded-lg"
              />
              <SearchIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
            </div>
            <select className="h-10 pl-3 pr-8 text-sm border rounded-lg min-w-[100px]">
              <option>최신순</option>
              <option>인기순</option>
              <option>조회순</option>
            </select>
            <button className="h-10 px-5 text-sm text-white bg-blue-600 rounded-lg hover:bg-blue-700">
              글쓰기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
} 
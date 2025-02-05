'use client';

import { useSelector } from 'react-redux';
import { useRouter } from 'next/navigation';
import { RootState } from '@/store/store';

export default function AdminProfile() {
  const router = useRouter();
  const { user, isAuthenticated } = useSelector((state: RootState) => state.auth);

  if (!isAuthenticated || !user?.email?.includes('admin')) {
    router.push('/login');
    return null;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <div className="p-6">
            <h1 className="text-2xl font-bold mb-6">관리자 프로필</h1>
            <div className="space-y-4">
              <div>
                <h2 className="text-lg font-semibold">이메일</h2>
                <p className="mt-1 text-gray-600">{user.email}</p>
              </div>
              <div>
                <h2 className="text-lg font-semibold">이름</h2>
                <p className="mt-1 text-gray-600">{user.name}</p>
              </div>
              <div>
                <h2 className="text-lg font-semibold">권한</h2>
                <p className="mt-1 text-gray-600">관리자</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 
'use client';

import { useState, useEffect, useRef } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '@/store/features/authSlice';
import type { RootState } from '@/store/store';
import { 
  UserCircleIcon,
  UserIcon,
  ArrowRightOnRectangleIcon,
  Cog6ToothIcon,
} from '@heroicons/react/24/outline';

export default function Header() {
  const router = useRouter();
  const dispatch = useDispatch();
  const { isAuthenticated, user } = useSelector((state: RootState) => state.auth);
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleProfileClick = () => {
    if (!user) return;
    
    if (user.email?.includes('admin')) {
      router.push('/admin/profile');
    } else {
      router.push(`/users/${user.id}`);
    }
  };

  const handleLogout = async () => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/logout`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        dispatch(logout());
        router.push('/login');
      }
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <header className="bg-white shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link href="/" className="text-2xl font-bold text-primary">
            IT-hub
          </Link>

          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <div className="relative" ref={dropdownRef}>
                <button 
                  onClick={() => setShowDropdown(!showDropdown)}
                  className="flex items-center focus:outline-none"
                >
                  {user?.email?.includes('admin') ? (
                    <div className="w-9 h-9 rounded-full border-2 border-primary flex items-center justify-center bg-primary/10">
                      <UserCircleIcon className="w-7 h-7 text-primary" />
                    </div>
                  ) : (
                    <div className="w-9 h-9 rounded-full overflow-hidden border-2 border-primary">
                      <img
                        src={user?.profileImage || user?.profileImg || '/default-profile.png'}
                        alt="Profile"
                        className="w-full h-full object-cover"
                      />
                    </div>
                  )}
                </button>

                {showDropdown && (
                  <div className="absolute right-0 mt-2 w-44 bg-white rounded-lg shadow-lg overflow-hidden border border-gray-100">
                    {user?.email?.includes('admin') ? (
                      <Link
                        href="/admin/profile"
                        className="flex items-center px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 transition-colors duration-150"
                      >
                        <UserIcon className="w-4 h-4 mr-3 text-gray-500" />
                        관리자 프로필
                      </Link>
                    ) : (
                      <>
                        <Link
                          href={`/users/${user?.id}`}
                          className="flex items-center px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 transition-colors duration-150"
                        >
                          <UserIcon className="w-4 h-4 mr-3 text-gray-500" />
                          프로필
                        </Link>
                        <Link
                          href={`/users/${user?.id}/edit`}
                          className="flex items-center px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 transition-colors duration-150"
                        >
                          <Cog6ToothIcon className="w-4 h-4 mr-3 text-gray-500" />
                          설정
                        </Link>
                      </>
                    )}
                    <div className="h-px bg-gray-100" />
                    <button
                      onClick={handleLogout}
                      className="flex items-center w-full px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 transition-colors duration-150"
                    >
                      <ArrowRightOnRectangleIcon className="w-4 h-4 mr-3 text-gray-500" />
                      로그아웃
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <Link href="/login">
                <button className="bg-primary text-white hover:bg-primary/90 px-4 py-2 rounded-md text-sm font-medium">
                  로그인
                </button>
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  );
} 
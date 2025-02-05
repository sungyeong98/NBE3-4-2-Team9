'use client';

import Link from 'next/link';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '@/store/features/authSlice';
import type { RootState } from '@/store/store';
import { UserCircleIcon } from '@heroicons/react/24/solid';

export default function Header() {
  const router = useRouter();
  const dispatch = useDispatch();
  const { isAuthenticated, user } = useSelector((state: RootState) => state.auth);

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
    <header className="bg-white shadow-md">
      <nav className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 justify-between items-center">
          <Link href="/">
            <div className="flex-shrink-0 cursor-pointer">
              <h1 className="text-2xl font-bold text-primary">IT-hub</h1>
            </div>
          </Link>
          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <div className="relative group">
                  <button 
                    className="flex items-center"
                    onClick={handleProfileClick}
                  >
                    {user?.email?.includes('admin') ? (
                      <div className="w-10 h-10 rounded-full border-2 border-primary flex items-center justify-center bg-primary/10">
                        <UserCircleIcon className="w-8 h-8 text-primary" />
                      </div>
                    ) : (
                      <div className="w-10 h-10 rounded-full overflow-hidden border-2 border-primary">
                        <img
                          src={user?.profileImage || user?.profileImg || '/default-profile.png'}
                          alt="Profile"
                          className="w-full h-full object-cover"
                        />
                      </div>
                    )}
                  </button>
                </div>
                <button
                  onClick={handleLogout}
                  className="bg-primary text-white hover:bg-primary/90 px-4 py-2 rounded-md text-sm font-medium"
                >
                  로그아웃
                </button>
              </>
            ) : (
              <Link href="/login">
                <button className="bg-primary text-white hover:bg-primary/90 px-4 py-2 rounded-md text-sm font-medium">
                  로그인
                </button>
              </Link>
            )}
          </div>
        </div>
      </nav>
    </header>
  );
} 
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '@/store/features/authSlice';
import type { RootState } from '@/store/store';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { UserCircleIcon } from '@heroicons/react/24/outline';

export default function Navbar() {
  const dispatch = useDispatch();
  const { isAuthenticated, isAdmin, user } = useSelector((state: RootState) => state.auth);
  const router = useRouter();

  const handleLogout = () => {
    dispatch(logout());
    router.push('/');
  };

  return (
    <nav className="bg-white border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <Link href="/" className="flex items-center">
              IT-hub
            </Link>
          </div>

          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                {isAdmin ? (
                  <div className="flex items-center gap-4">
                    <Link 
                      href="/admin" 
                      className="text-gray-600 hover:text-blue-600 flex items-center gap-2"
                    >
                      <UserCircleIcon className="h-8 w-8" />
                      <span>관리자</span>
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="text-gray-600 hover:text-blue-600"
                    >
                      로그아웃
                    </button>
                  </div>
                ) : (
                  <div className="flex items-center gap-4">
                    <Link 
                      href={`/users/${user?.id}`}
                      className="text-gray-600 hover:text-blue-600"
                    >
                      프로필
                    </Link>
                    <Link 
                      href={`/users/${user?.id}/edit`}
                      className="text-gray-600 hover:text-blue-600"
                    >
                      설정
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="text-gray-600 hover:text-blue-600"
                    >
                      로그아웃
                    </button>
                  </div>
                )}
              </>
            ) : (
              <Link href="/admin/login" className="text-gray-600 hover:text-blue-600">
                관리자 로그인
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
} 
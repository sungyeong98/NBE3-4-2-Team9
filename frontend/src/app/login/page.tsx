'use client';

import { useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useDispatch } from 'react-redux';
import KakaoIcon from '@/components/KakaoIcon';
import { setCredentials } from '@/store/features/authSlice';

export default function Login() {
  const router = useRouter();
  const dispatch = useDispatch();
  const searchParams = useSearchParams();
  
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const accessToken = params.get('token');
    const userInfo = params.get('user');

    if (accessToken && userInfo) {
      try {
        const user = JSON.parse(decodeURIComponent(userInfo));
        dispatch(setCredentials({ user, token: accessToken }));
        localStorage.setItem('accessToken', accessToken);
        localStorage.removeItem('isAdmin');
        router.push('/');
      } catch (error) {
        console.error('Failed to parse user info:', error);
      }
    }
  }, [searchParams, dispatch, router]);

  const handleKakaoLogin = () => {
    window.location.href = `${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/kakao`;
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow-lg">
        <div className="text-center">
          <h2 className="mt-6 text-3xl font-bold text-gray-900">로그인</h2>
          <p className="mt-2 text-sm text-gray-600">
            IT-hub에 오신 것을 환영합니다
          </p>
        </div>
        
        <div className="mt-8 space-y-6">
          <button
            onClick={handleKakaoLogin}
            className="w-full flex items-center justify-center gap-3 px-4 py-3 border border-transparent text-base font-medium rounded-md text-[#391B1B] bg-[#FEE500] hover:bg-[#FEE500]/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#FEE500]"
          >
            <KakaoIcon />
            카카오로 시작하기
          </button>
          
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-300" />
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-white text-gray-500">또는</span>
            </div>
          </div>
          
          <div className="text-sm text-center text-gray-600">
            <p>관리자이신가요?</p>
            <button
              onClick={() => router.push('/admin/login')}
              className="font-medium text-primary hover:text-primary/80"
            >
              관리자 로그인
            </button>
          </div>
        </div>
      </div>
    </div>
  );
} 
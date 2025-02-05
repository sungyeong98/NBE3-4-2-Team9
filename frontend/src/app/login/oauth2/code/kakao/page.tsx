'use client';

import { useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useDispatch } from 'react-redux';
import { setCredentials } from '@/store/features/authSlice';

export default function KakaoCallback() {
  const router = useRouter();
  const dispatch = useDispatch();
  const searchParams = useSearchParams();

  useEffect(() => {
    const token = searchParams.get('token');
    const userInfo = searchParams.get('user');

    if (token && userInfo) {
      try {
        const user = JSON.parse(decodeURIComponent(userInfo));
        const cleanToken = token.replace('Bearer ', '');
        dispatch(setCredentials({ 
          user,
          token: cleanToken
        }));
        router.push(`/users/${user.id}`);
      } catch (error) {
        console.error('Failed to parse user info:', error);
        router.push('/login');
      }
    } else {
      router.push('/login');
    }
  }, [searchParams, dispatch, router]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <h2 className="text-xl">로그인 처리중...</h2>
        <p className="mt-2 text-gray-600">잠시만 기다려주세요...</p>
      </div>
    </div>
  );
} 
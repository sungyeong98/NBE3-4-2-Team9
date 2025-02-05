'use client';

import { useSelector } from 'react-redux';
import { useRouter } from 'next/navigation';
import { RootState } from '@/store/store';
import { useEffect, useState } from 'react';

interface UserProfile {
  id: number;
  email: string;
  name: string;
  profileImg?: string;
  introduction?: string;
  job?: string;
  jobSkills?: Array<{
    name: string;
    code: number;
  }>;
}

export default function UserProfile({ params }: { params: { id: string } }) {
  const router = useRouter();
  const { isAuthenticated, token, user } = useSelector((state: RootState) => state.auth);
  const [profile, setProfile] = useState<UserProfile | null>(null);

  useEffect(() => {
    if (!isAuthenticated || !user) {
      router.push('/login');
      return;
    }

    // 관리자는 admin/profile로 리다이렉트
    if (user.email?.includes('admin')) {
      router.push('/admin/profile');
      return;
    }

    // 일반 유저는 id 검증
    if (Number(user.id) !== Number(params.id)) {
      router.push('/');
      return;
    }

    fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/users/${params.id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    })
    .then(async res => {
      const data = await res.json();
      if (!res.ok || !data.success) {
        throw new Error(data.message || 'Failed to fetch profile');
      }
      setProfile(data.data);
    })
    .catch(error => {
      console.error('Failed to fetch profile:', error);
      router.push('/');
    });
  }, [isAuthenticated, user, params.id, router, token]);

  if (!profile) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <div className="p-6">
            <div className="flex items-center space-x-4">
              <div className="w-20 h-20 rounded-full overflow-hidden">
                <img
                  src={profile.profileImg || '/default-profile.png'}
                  alt={profile.name}
                  className="w-full h-full object-cover"
                />
              </div>
              <div>
                <h1 className="text-2xl font-bold">{profile.name}</h1>
                <p className="text-gray-600">{profile.email}</p>
              </div>
            </div>
            
            <div className="mt-6">
              <h2 className="text-lg font-semibold">직업</h2>
              <p className="mt-2 text-gray-600">
                {profile.job || '등록된 직업이 없습니다.'}
              </p>
            </div>
            
            <div className="mt-6">
              <h2 className="text-lg font-semibold">소개</h2>
              <p className="mt-2 text-gray-600">
                {profile.introduction || '등록된 소개가 없습니다.'}
              </p>
            </div>
            
            <div className="mt-6">
              <h2 className="text-lg font-semibold">보유 기술</h2>
              {profile.jobSkills && profile.jobSkills.length > 0 ? (
                <div className="mt-2 flex flex-wrap gap-2">
                  {profile.jobSkills.map((skill) => (
                    <span 
                      key={skill.code}
                      className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm"
                    >
                      {skill.name}
                    </span>
                  ))}
                </div>
              ) : (
                <p className="mt-2 text-gray-600">등록된 기술이 없습니다.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 
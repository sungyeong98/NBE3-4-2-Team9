'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';

interface UserProfile {
  id: number;
  email: string;
  name: string;
  introduction?: string;
  job?: string;
}

interface JobSkill {
  name: string;
  code?: number;
}

export default function EditProfile({ params }: { params: { id: string } }) {
  const router = useRouter();
  const { user, token } = useSelector((state: RootState) => state.auth);
  
  const [formData, setFormData] = useState({
    introduction: '',
    job: '',
    skillInput: '',  // 기술 스택 입력을 위한 필드
    jobSkills: [] as JobSkill[]  // 기술 스택 배열
  });

  // 현재 유저의 프로필 정보 불러오기
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/users/${params.id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        const data = await response.json();
        if (data.success) {
          setFormData({
            introduction: data.data.introduction || '',
            job: data.data.job || '',
            skillInput: '',
            jobSkills: data.data.jobSkills || []
          });
        }
      } catch (error) {
        console.error('Failed to fetch profile:', error);
      }
    };

    fetchProfile();
  }, [params.id, token]);

  // 권한 체크
  if (user?.id !== parseInt(params.id)) {
    router.push(`/users/${params.id}`);
    return null;
  }

  // 기술 스택 추가 핸들러
  const handleSkillAdd = () => {
    if (formData.skillInput.trim()) {
      setFormData(prev => ({
        ...prev,
        jobSkills: [...prev.jobSkills, { name: formData.skillInput.trim() }],
        skillInput: ''
      }));
    }
  };

  // 기술 스택 제거 핸들러
  const handleSkillRemove = (skillToRemove: string) => {
    setFormData(prev => ({
      ...prev,
      jobSkills: prev.jobSkills.filter(skill => skill.name !== skillToRemove)
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/users/${params.id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          introduction: formData.introduction,
          job: formData.job,
          jobSkills: formData.jobSkills
        })
      });

      const data = await response.json();
      if (data.success) {
        router.push(`/users/${params.id}`);
      } else {
        alert('프로필 수정에 실패했습니다.');
      }
    } catch (error) {
      console.error('Failed to update profile:', error);
      alert('프로필 수정 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-3xl mx-auto px-4">
        <div className="bg-white rounded-xl shadow-sm">
          {/* 상단 배경 */}
          <div className="h-24 bg-gradient-to-r from-primary/20 to-primary/10 rounded-t-xl" />
          
          <div className="px-8 pb-8">
            {/* 헤더 */}
            <div className="flex items-center -mt-12 mb-8">
              <div className="w-20 h-20 rounded-xl overflow-hidden ring-4 ring-white shadow-lg">
                <img
                  src={user?.profileImage || user?.profileImg || '/default-profile.png'}
                  alt="Profile"
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="ml-6">
                <h1 className="text-2xl font-bold text-gray-900">프로필 설정</h1>
                <p className="text-sm text-gray-500 mt-1">{user?.email}</p>
              </div>
            </div>
            
            {/* 폼 */}
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid gap-6">
                {/* 직무 입력 */}
                <div className="bg-gray-50 p-6 rounded-xl">
                  <label className="block text-sm font-medium text-gray-900 mb-4">
                    직무
                  </label>
                  <input
                    type="text"
                    value={formData.job}
                    onChange={(e) => setFormData(prev => ({ ...prev, job: e.target.value }))}
                    className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors"
                    placeholder="예: 백엔드 개발자"
                  />
                </div>

                {/* 소개 입력 */}
                <div className="bg-gray-50 p-6 rounded-xl">
                  <label className="block text-sm font-medium text-gray-900 mb-4">
                    소개
                  </label>
                  <textarea
                    value={formData.introduction}
                    onChange={(e) => setFormData(prev => ({ ...prev, introduction: e.target.value }))}
                    className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors h-32 resize-none"
                    placeholder="자기소개를 입력하세요..."
                  />
                </div>

                {/* 보유 기술 입력 */}
                <div className="bg-gray-50 p-6 rounded-xl">
                  <label className="block text-sm font-medium text-gray-900 mb-4">
                    보유 기술
                  </label>
                  <div className="space-y-4">
                    <div className="flex flex-wrap gap-2">
                      {formData.jobSkills.map((skill, index) => (
                        <span
                          key={index}
                          className="inline-flex items-center px-3 py-1.5 bg-white rounded-lg border border-gray-200 shadow-sm group"
                        >
                          <span className="text-sm text-gray-700">{skill.name}</span>
                          <button
                            type="button"
                            onClick={() => handleSkillRemove(skill.name)}
                            className="ml-2 text-gray-400 hover:text-gray-600"
                          >
                            ×
                          </button>
                        </span>
                      ))}
                    </div>
                    <div className="flex gap-2">
                      <input
                        type="text"
                        value={formData.skillInput}
                        onChange={(e) => setFormData(prev => ({ ...prev, skillInput: e.target.value }))}
                        className="flex-1 px-4 py-2.5 bg-white border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-colors"
                        placeholder="기술 스택을 입력하세요"
                        onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleSkillAdd())}
                      />
                      <button
                        type="button"
                        onClick={handleSkillAdd}
                        className="px-6 py-2.5 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors focus:outline-none focus:ring-2 focus:ring-primary/20"
                      >
                        추가
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              {/* 하단 버튼 */}
              <div className="flex justify-end gap-3 pt-6 mt-8 border-t">
                <button
                  type="button"
                  onClick={() => router.back()}
                  className="px-6 py-2.5 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-200"
                >
                  취소
                </button>
                <button
                  type="submit"
                  className="px-6 py-2.5 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors focus:outline-none focus:ring-2 focus:ring-primary/20"
                >
                  저장
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
} 
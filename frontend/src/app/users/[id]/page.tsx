'use client';

import { useSelector } from 'react-redux';
import { useRouter } from 'next/navigation';
import { RootState } from '@/store/store';
import { useEffect, useState } from 'react';
import Link from 'next/link';
import privateApi from '@/api/axios';
import { UserGroupIcon, ClipboardDocumentListIcon, ChatBubbleLeftIcon } from '@heroicons/react/24/outline';

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
  posts?: Array<{
    postId: number;
    subject: string;
    createdAt: string;
  }>;
  comments?: Array<{
    commentId: number;
    content: string;
    postId: number;
    createdAt: string;
  }>;
}

interface RecruitmentUserResponse {
  recruitmentUserList: {
    content: Array<{
      userId: number;
      userProfile: {
        name: string;
        job?: string;
        jobSkills?: Array<{ name: string }>;
      };
      status: 'APPLIED' | 'ACCEPTED' | 'REJECTED';
      createdAt: string;
    }>;
    last: boolean;
  };
}

interface RecruitmentPostResponse {
  posts: {
    content: Array<{
      postId: number;
      subject: string;
      status: string;
      createdAt: string;
    }>;
    last: boolean;
  };
}

export default function UserProfile({ params }: { params: { id: string } }) {
  const router = useRouter();
  const { isAuthenticated, token, user } = useSelector((state: RootState) => state.auth);
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [myRecruitments, setMyRecruitments] = useState<RecruitmentUserResponse['recruitmentUserList']['content']>([]);
  const [acceptedPosts, setAcceptedPosts] = useState<RecruitmentPostResponse['posts']['content']>([]);

  useEffect(() => {
    if (!isAuthenticated || !user) {
      router.push('/login');
      return;
    }

    const fetchData = async () => {
      try {
        // 프로필 정보 조회 (작성한 모집글 포함)
        const profileResponse = await privateApi.get(`/api/v1/users/${params.id}`);
        if (profileResponse.data.success) {
          setProfile(profileResponse.data.data);
        }

        // 내 프로젝트 지원자 목록 조회
        const recruitmentResponse = await privateApi.get<{ data: RecruitmentUserResponse }>(`/api/v1/recruitment/${params.id}/applied-users`);
        if (recruitmentResponse.data.success) {
          setMyRecruitments(recruitmentResponse.data.data.recruitmentUserList.content);
        }

        // 내가 지원한 프로젝트 중 승인된 목록 조회
        const acceptedResponse = await privateApi.get('/api/v1/recruitment/accepted-posts', {
          params: {
            status: 'ACCEPTED',
            pageNum: 0,
            pageSize: 10
          }
        });
        if (acceptedResponse.data.success) {
          setAcceptedPosts(acceptedResponse.data.data.posts.content);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      }
    };

    fetchData();
  }, [isAuthenticated, user, params.id, router]);

  if (!profile) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 왼쪽 사이드바: 프로필 정보 */}
        <div className="lg:col-span-1 space-y-6">
          <div className="bg-white shadow-md rounded-xl overflow-hidden">
            {/* 프로필 헤더 */}
            <div className="h-32 bg-gradient-to-r from-primary/20 to-primary/10" />
            
            <div className="px-6 pb-6">
              {/* 프로필 이미지와 기본 정보 */}
              <div className="relative -mt-16 mb-4">
                <div className="w-24 h-24 rounded-xl overflow-hidden ring-4 ring-white shadow-lg">
                  <img
                    src={profile.profileImg || '/default-profile.png'}
                    alt={profile.name}
                    className="w-full h-full object-cover"
                  />
                </div>
              </div>
              
              <div className="space-y-2">
                <h1 className="text-2xl font-bold text-gray-900">{profile.name}</h1>
                <p className="text-gray-500">{profile.email}</p>
              </div>
            </div>
          </div>

          {/* 직업 정보 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">직업</h2>
            <p className="text-gray-700">
              {profile.job || '등록된 직업이 없습니다.'}
            </p>
          </div>

          {/* 소개 정보 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">소개</h2>
            <p className="text-gray-700 whitespace-pre-line">
              {profile.introduction || '등록된 소개가 없습니다.'}
            </p>
          </div>

          {/* 보유 기술 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">보유 기술</h2>
            {profile.jobSkills && profile.jobSkills.length > 0 ? (
              <div className="flex flex-wrap gap-2">
                {profile.jobSkills.map((skill) => (
                  <span 
                    key={skill.code}
                    className="px-3 py-1 bg-gray-100 text-gray-700 rounded-lg text-sm"
                  >
                    {skill.name}
                  </span>
                ))}
              </div>
            ) : (
              <p className="text-gray-700">등록된 기술이 없습니다.</p>
            )}
          </div>
        </div>

        {/* 오른쪽 메인 컨텐츠 */}
        <div className="lg:col-span-2 space-y-6">
          {/* 내가 작성한 게시글 목록 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
              <UserGroupIcon className="h-6 w-6 text-blue-600" />
              내가 작성한 게시글
            </h3>
            {profile?.posts && profile.posts.length > 0 ? (
              <div className="divide-y divide-gray-100">
                {profile.posts.map((post) => (
                  <Link
                    key={post.postId}
                    href={`/post/${post.postId}`}
                    className="block py-4 hover:bg-gray-50 rounded transition-colors"
                  >
                    <div className="flex justify-between items-center">
                      <div>
                        <p className="font-medium">{post.subject}</p>
                        <p className="text-sm text-gray-500">
                          작성일: {new Date(post.createdAt).toLocaleDateString('ko-KR')}
                        </p>
                      </div>
                      <span className="text-sm text-blue-600">
                        자세히 보기 →
                      </span>
                    </div>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="text-center py-8">
                <p className="text-gray-500">작성한 게시글이 없습니다</p>
              </div>
            )}
          </div>

          {/* 승인된 프로젝트 목록 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
              <ClipboardDocumentListIcon className="h-6 w-6 text-blue-600" />
              참여 확정된 프로젝트
            </h3>
            {acceptedPosts.length > 0 ? (
              <div className="divide-y divide-gray-100">
                {acceptedPosts.map((post) => (
                  <Link
                    key={post.postId}
                    href={`/post/${post.postId}`}
                    className="block py-4 hover:bg-gray-50 rounded transition-colors"
                  >
                    <div className="flex justify-between items-center">
                      <div>
                        <p className="font-medium">{post.subject}</p>
                        <p className="text-sm text-gray-500">
                          승인일: {new Date(post.createdAt).toLocaleDateString('ko-KR')}
                        </p>
                      </div>
                      <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm">
                        참여 확정
                      </span>
                    </div>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="text-center py-8">
                <p className="text-gray-500">참여 확정된 프로젝트가 없습니다</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
} 
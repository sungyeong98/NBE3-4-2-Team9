'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import privateApi from '@/api/axios';

export default function VotedJobPostings() {
  const router = useRouter();
  const [votedPosts, setVotedPosts] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchVotedPosts = async () => {
      try {
        const response = await privateApi.get('/api/v1/job-posting/voter');
        if (response.data.success) {
          setVotedPosts(response.data.data);
        }
      } catch (error) {
        console.error('Failed to fetch voted posts:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchVotedPosts();
  }, []);

  if (isLoading) return <div>로딩중...</div>;

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">관심 공고 목록</h1>
      <div className="grid gap-4">
        {votedPosts.map((post) => (
          <Link 
            key={post.id} 
            href={`/job-posting/${post.id}`}
            className="block bg-white rounded-lg shadow p-4 hover:shadow-md transition-shadow"
          >
            <h2 className="text-xl font-semibold mb-2">{post.title}</h2>
            <div className="text-gray-600">
              <p>{post.company}</p>
              <p>{post.location}</p>
            </div>
          </Link>
        ))}
        {votedPosts.length === 0 && (
          <p className="text-gray-500 text-center py-8">
            관심 등록한 공고가 없습니다.
          </p>
        )}
      </div>
    </div>
  );
} 
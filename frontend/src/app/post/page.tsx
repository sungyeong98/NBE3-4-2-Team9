'use client';

import { useEffect, useState } from 'react';
import { PostResponse } from '@/types/post/PostResponse';
import { getPosts } from '@/api/post';
import Link from 'next/link';
import { formatDate } from '@/utils/dateUtils';

export default function PostList() {
  const [posts, setPosts] = useState<PostResponse[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await getPosts({ page, size: 10 });
        if (response.success) {
          setPosts(response.data.content);
          setTotalPages(response.data.totalPages);
        }
      } catch (error) {
        console.error('Failed to fetch posts:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchPosts();
  }, [page]);

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="animate-pulse mb-6">
            <div className="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
            <div className="h-4 bg-gray-200 rounded w-1/4"></div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-8">커뮤니티</h1>
      
      <div className="space-y-6">
        {posts.map((post) => (
          <Link 
            key={post.postId}
            href={`/post/${post.postId}`}
            className="block bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow"
          >
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-blue-600">{post.categoryName}</span>
              <span className="text-sm text-gray-500">{formatDate(post.createdAt)}</span>
            </div>
            <h2 className="text-xl font-semibold mb-2">{post.title}</h2>
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                {post.authorProfileImage && (
                  <img 
                    src={post.authorProfileImage} 
                    alt={post.authorName}
                    className="w-6 h-6 rounded-full mr-2"
                  />
                )}
                <span className="text-gray-600">{post.authorName}</span>
              </div>
              <div className="text-sm text-gray-500">
                조회 {post.viewCount}
              </div>
            </div>
          </Link>
        ))}
      </div>

      {totalPages > 1 && (
        <div className="flex justify-center mt-8">
          <nav className="flex gap-2">
            {[...Array(totalPages)].map((_, i) => (
              <button
                key={i}
                onClick={() => setPage(i)}
                className={`px-4 py-2 rounded ${
                  page === i 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 hover:bg-gray-200'
                }`}
              >
                {i + 1}
              </button>
            ))}
          </nav>
        </div>
      )}
    </div>
  );
} 
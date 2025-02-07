'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { PostResponse } from '@/types/post/PostResponse';
import { Category } from '@/types/post/Category';
import { getPost } from '@/api/post';
import { getCategories } from '@/api/category';
import { formatDate } from '@/utils/dateUtils';

export default function PostDetail({ params }: { params: { id: string } }) {
  const router = useRouter();
  const [post, setPost] = useState<PostResponse | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [postResponse, categoriesResponse] = await Promise.all([
          getPost(parseInt(params.id)),
          getCategories()
        ]);

        if (postResponse.success) {
          setPost(postResponse.data);
        }
        if (categoriesResponse.success) {
          setCategories(categoriesResponse.data);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [params.id]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!post) {
    return <div>Post not found</div>;
  }

  const categoryName = categories.find(cat => cat.id === String(post.categoryId))?.name;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        <article className="bg-white rounded-xl shadow-lg overflow-hidden">
          <div className="p-8">
            <div className="flex items-center gap-2 text-sm text-gray-500 mb-4">
              <Link href="/posts" className="hover:text-blue-600">
                게시판
              </Link>
              <span>›</span>
              <Link 
                href={`/posts?category=${post.categoryId}`}
                className="text-blue-600 hover:text-blue-700"
              >
                {categoryName}
              </Link>
            </div>

            <h1 className="text-3xl font-bold text-gray-900 mb-4">
              {post.subject}
            </h1>

            <div className="flex items-center justify-between text-sm text-gray-500 mb-8">
              <div className="flex items-center gap-4">
                <span>익명</span>
                <span>{formatDate(post.createdAt)}</span>
              </div>
            </div>

            <div className="prose max-w-none">
              {post.content}
            </div>
          </div>
        </article>
      </div>
    </div>
  );
} 
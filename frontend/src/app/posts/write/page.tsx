'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function WritePost() {
  const router = useRouter();  // useRouter 훅 추가
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('1');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // TODO: API 연동
    console.log({
      title,
      content,
      categoryId: parseInt(category)
    });

    // 작성 완료 후 목록으로 이동
    router.push('/posts');
  };

  // ... 나머지 JSX 부분
} 
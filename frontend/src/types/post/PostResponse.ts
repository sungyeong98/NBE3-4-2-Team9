export interface PostResponse {
  postId: number;
  title: string;
  content: string;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
  categoryId: number;
  categoryName: string;
  authorId: number;
  authorName: string;
  authorProfileImage?: string;
} 
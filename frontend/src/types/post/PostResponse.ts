export interface PostResponse {
  id: number;
  subject: string;
  content: string;
  categoryId: number;
  categoryName: string;
  authorId: number;
  authorName: string;
  viewCount: number;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
} 
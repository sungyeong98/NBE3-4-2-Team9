export interface Category {
  id: number;
  name: string;
  createdAt: string;
  modifiedAt: string;
}

export const CATEGORY = {
  FREE: '자유 게시판',
  RECRUITMENT: '모집 게시판'
} as const; 
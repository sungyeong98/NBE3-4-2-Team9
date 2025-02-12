import { RecruitmentStatus } from './recruitmentStatus';

export interface Post {
  id: number;
  subject: string;
  content: string;
  categoryId: number;
  isAuthor: boolean;
  authorName: string;
  authorImg: string;
  voterCount: number;
  isVoter: boolean;
  createdAt: string;
  jobPostingId?: number;
  numOfApplicants?: number;
  recruitmentStatus?: RecruitmentStatus;
}

export interface PostPageResponse {
  postId: number;
  subject: string;
  categoryName: string;
  authorName: string;
  authorProfileImage: string;
  commentCount: number;
  voterCount: number;
  createdAt: string;
} 
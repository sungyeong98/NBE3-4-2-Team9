export interface JobPosting {
  id: number;
  title: string;
  companyName: string;
  jobType: string;
  location: string;
  experienceLevel: string;
  createdAt: string;
  // 필요한 다른 필드들도 추가할 수 있습니다
}

export interface JobPostingPageResponse {
  id: number;
  subject: string;
  openDate: string;
  closeDate: string;
  experienceLevel: {
    code: number;
    min: number;
    max: number;
    name: string;
  };
  requireEducate: {
    code: number;
    name: string;
  };
  jobPostingStatus: 'ACTIVE' | 'END';
  salary: {
    code: number;
    name: string;
  };
  applyCnt: number;
}

export interface JobPostingPage {
  content: JobPostingPageResponse[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  empty: boolean;
} 
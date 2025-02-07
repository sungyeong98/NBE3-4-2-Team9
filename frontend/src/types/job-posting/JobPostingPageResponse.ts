import { JobSkill } from '../job-skill/JobSkill';

export interface JobPostingPageResponse {
  jobPostingId: number;
  subject: string;
  url: string;
  companyName: string;
  companyLink: string;
  experienceLevel: {
    code: number;
    name: string;
    min: number;
    max: number;
  };
  requireEducate: {
    code: number;
    name: string;
  };
  salary: {
    code: number;
    name: string;
  };
  jobSkillList: JobSkill[];
  closeDate: string;
  applyCnt: number;
} 
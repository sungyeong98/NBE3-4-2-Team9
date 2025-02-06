import { JobPostingPageResponse } from '@/types/job-posting/JobPostingPageResponse';
import { formatDate } from '@/utils/dateUtils';
import Link from 'next/link';

interface JobPostingCardProps {
  posting: JobPostingPageResponse;
}

export default function JobPostingCard({ posting }: JobPostingCardProps) {
  console.log('Posting data:', posting); // 각 posting 객체의 구조 확인

  return (
    <Link 
      href={`/job-posting/${posting.jobPostingId}`}
      className="block p-6 h-full hover:bg-gray-50 transition-colors duration-200"
    >
      <h2 className="text-lg font-semibold mb-2 line-clamp-2">
        {posting.subject}
      </h2>
      
      <p className="text-gray-600 mb-3">
        {posting.companyName}
      </p>

      <div className="flex flex-wrap gap-2 mb-4">
        <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">
          {posting.experienceLevel?.name || '경력 정보 없음'}
        </span>
        <span className="px-2 py-1 bg-green-100 text-green-800 text-sm rounded-full">
          {posting.salary?.name || '급여 정보 없음'}
        </span>
      </div>

      <div className="flex flex-wrap gap-2 mb-4">
        {posting.jobSkillList?.map((skill) => (
          <span 
            key={skill.jobSkillId}
            className="px-2 py-1 border border-gray-200 text-gray-600 text-sm rounded-full"
          >
            {skill.name}
          </span>
        )) || (
          <span className="text-gray-500 text-sm">기술 스택 정보 없음</span>
        )}
      </div>

      <p className="text-sm text-gray-500 text-right">
        마감일: {formatDate(posting.closeDate)}
      </p>
    </Link>
  );
} 
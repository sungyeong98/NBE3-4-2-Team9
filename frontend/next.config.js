/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    domains: [
      'k.kakaocdn.net',  // 카카오 프로필 이미지 도메인 추가
    ],
  },
  // app 디렉토리 사용 명시
  experimental: {
    appDir: true
  }
}

module.exports = nextConfig 
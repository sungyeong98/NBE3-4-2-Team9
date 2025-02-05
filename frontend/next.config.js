/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    domains: [
      'k.kakaocdn.net',  // 카카오 프로필 이미지 도메인 추가
    ],
  },
}

module.exports = nextConfig 
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const cookieToken = request.cookies.get('accessToken') || request.cookies.get('adminToken');
  const isAdminPage = request.nextUrl.pathname.startsWith('/admin');
  const isAuthPage = request.nextUrl.pathname.startsWith('/profile') || 
                    request.nextUrl.pathname.startsWith('/settings');

  if (isAdminPage && !request.cookies.get('adminToken')) {
    return NextResponse.redirect(new URL('/admin/login', request.url));
  }

  if (isAuthPage && !cookieToken) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/admin/:path*', '/profile/:path*', '/settings/:path*'],
}; 
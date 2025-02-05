'use client';

import { Provider } from 'react-redux';
import { store } from '@/store/store';
import Header from '@/components/Header';

export default function RootLayoutClient({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <Provider store={store}>
      <Header />
      <main className="bg-gray-50 min-h-screen">{children}</main>
    </Provider>
  );
} 
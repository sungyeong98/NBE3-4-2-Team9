'use client';

import { useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { useRouter } from 'next/navigation';
import { useSelector } from 'react-redux';
import type { RootState } from '@/store/store';

interface ChatMessage {
  id: number;
  userId: number;
  username: string;
  profileImg: string;
  type: 'CHAT' | 'JOIN' | 'LEAVE';
  createdAt: string;
  content: string;
}

export default function ChatPage() {
  const router = useRouter();
  const { isAuthenticated, user, token } = useSelector((state: RootState) => state.auth);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const clientRef = useRef<Client | null>(null);
  const [postId, setPostId] = useState<number>(1);

  useEffect(() => {
    if (!isAuthenticated || !user || !token) {
      return;
    }

    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        console.log('Connected to WebSocket');
        client.subscribe(`/topic/${postId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, receivedMessage]);
        });
      },
    });

    clientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
    };
  }, [postId, isAuthenticated, user, token]);

  const sendMessage = () => {
    if (!newMessage.trim() || !clientRef.current) return;

    const chatMessage = {
      type: 'CHAT',
      content: newMessage,
    };

    clientRef.current.publish({
      destination: `/app/msg/${postId}`,
      body: JSON.stringify(chatMessage),
    });

    setNewMessage('');
  };

  if (!isAuthenticated || !user) {
    return (
      <div className="max-w-4xl mx-auto p-8 text-center">
        <p className="text-gray-500 mb-4">채팅을 이용하시려면 로그인이 필요합니다.</p>
        <button
          onClick={() => router.push('/login')}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          로그인하기
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-4">
      <div className="bg-white rounded-lg shadow-lg">
        <div className="h-[600px] overflow-y-auto p-4">
          {messages.map((msg, index) => (
            <div
              key={index}
              className={`flex items-start gap-2 mb-4 ${
                msg.userId === user.id ? 'flex-row-reverse' : ''
              }`}
            >
              <img
                src={msg.profileImg || '/default-profile.png'}
                alt={msg.username}
                className="w-8 h-8 rounded-full"
              />
              <div
                className={`max-w-[70%] p-3 rounded-lg ${
                  msg.userId === user.id
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-100'
                }`}
              >
                <p className="text-sm font-medium mb-1">{msg.username}</p>
                <p>{msg.content}</p>
              </div>
            </div>
          ))}
        </div>
        <div className="p-4 border-t">
          <div className="flex gap-2">
            <input
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
              className="flex-1 p-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="메시지를 입력하세요..."
            />
            <button
              onClick={sendMessage}
              className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
            >
              전송
            </button>
          </div>
        </div>
      </div>
    </div>
  );
} 
// package com.backend.domain.chat.util;
//
// import java.util.List;
// import java.util.stream.IntStream;
//
// import com.backend.domain.chat.entity.Chat;
// import com.backend.domain.chat.entity.MessageType;
//
// public class ChatObjectProvider {
//
//     public static Chat getChat(String postId, String userId, String username, String content) {
//         return Chat.builder()
//                 .postId(postId)
//                 .userId(userId)
//                 .username(username)
//                 .content(content)
//                 .type(MessageType.CHAT)
//                 .build();
//     }
//
//     public static List<Chat> getChatList(String postId, String userId) {
//
//         return IntStream.range(0, 10)
//                 .mapToObj(i -> Chat.builder()
//                         .userId(userId)
//                         .postId(postId)
//                         .content("content" + i)
//                         .type(MessageType.CHAT)
//                         .build()).toList();
//     }
// }
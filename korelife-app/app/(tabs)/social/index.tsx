import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput, Modal } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Users, MessageSquare, HelpCircle, Heart, Plus, X, Send, Sparkles, Trophy } from 'lucide-react-native';
import { useSocialStore, Post } from '../../../src/store/socialStore';

export default function SocialIndexScreen() {
  const router = useRouter();
  const { posts, likePost, addComment, addPost } = useSocialStore();

  const [activePostForComments, setActivePostForComments] = useState<Post | null>(null);
  const [commentText, setCommentText] = useState('');
  const [showCreatePostModal, setShowCreatePostModal] = useState(false);
  const [newPostContent, setNewPostContent] = useState('');
  const [newPostCategory, setNewPostCategory] = useState('Ahorro cumplido');

  const categories = ['Ahorro cumplido', 'Productividad', 'Inversiones', 'Negocios & Ventas', 'General'];

  const handleAddPost = () => {
    if (!newPostContent.trim()) return;
    addPost(newPostContent.trim(), newPostCategory);
    setNewPostContent('');
    setShowCreatePostModal(false);
  };

  const handleSendComment = () => {
    if (!commentText.trim() || !activePostForComments) return;
    addComment(activePostForComments.id, commentText.trim());
    setCommentText('');
    const updated = posts.find(p => p.id === activePostForComments.id);
    if (updated) setActivePostForComments(updated);
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-4">
      {/* Header */}
      <View className="py-4 flex-row justify-between items-center mb-3">
        <View>
          <Text style={{ color: '#94a3b8' }} className="text-xs font-bold uppercase tracking-wider">Comunidad KoreLife</Text>
          <Text style={{ color: '#ffffff' }} className="text-2xl font-black">Feed & Logros 🌐</Text>
        </View>
        <View className="flex-row items-center space-x-2">
          <TouchableOpacity 
            onPress={() => setShowCreatePostModal(true)}
            style={{ backgroundColor: '#10b981' }}
            className="p-2.5 rounded-full shadow-lg active:bg-emerald-600 mr-2"
          >
            <Plus size={20} color="#022c22" strokeWidth={2.5} />
          </TouchableOpacity>
          <TouchableOpacity 
            onPress={() => router.push('/social/support')}
            style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
            className="p-2.5 rounded-2xl border"
          >
            <HelpCircle size={20} color="#34d399" />
          </TouchableOpacity>
        </View>
      </View>

      <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
        {/* Acceso Rápido a Grupos y Mensajes */}
        <View className="flex-row space-x-3 mb-5">
          <TouchableOpacity 
            onPress={() => router.push('/social/groups')}
            style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
            className="flex-1 p-4 rounded-2xl flex-row items-center shadow-sm active:opacity-80 mr-2"
          >
            <View style={{ backgroundColor: 'rgba(56, 189, 248, 0.15)', borderColor: '#38bdf8' }} className="p-2.5 rounded-xl border mr-3">
              <Users size={20} color="#38bdf8" />
            </View>
            <View>
              <Text style={{ color: '#ffffff' }} className="font-black text-sm">Grupos</Text>
              <Text style={{ color: '#94a3b8' }} className="text-[11px] font-medium">Debates en vivo</Text>
            </View>
          </TouchableOpacity>

          <TouchableOpacity 
            onPress={() => router.push('/social/messages')}
            style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
            className="flex-1 p-4 rounded-2xl flex-row items-center shadow-sm active:opacity-80"
          >
            <View style={{ backgroundColor: 'rgba(129, 140, 248, 0.15)', borderColor: '#818cf8' }} className="p-2.5 rounded-xl border mr-3">
              <MessageSquare size={20} color="#818cf8" />
            </View>
            <View>
              <Text style={{ color: '#ffffff' }} className="font-black text-sm">Mensajes</Text>
              <Text style={{ color: '#94a3b8' }} className="text-[11px] font-medium">Chat directo</Text>
            </View>
          </TouchableOpacity>
        </View>

        {/* Publicaciones de Logros en la Comunidad */}
        <Text style={{ color: '#ffffff' }} className="text-base font-black mb-3.5">
          Logros de la Comunidad ({posts.length})
        </Text>

        {posts.map((p) => (
          <View 
            key={p.id}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
            className="p-5 rounded-3xl mb-4 shadow-lg"
          >
            <View className="flex-row items-center mb-3">
              <View 
                style={{ backgroundColor: '#1e1b4b', borderColor: '#6366f1' }} 
                className="w-11 h-11 border rounded-2xl items-center justify-center mr-3"
              >
                <Text style={{ color: '#a5b4fc' }} className="text-sm font-black">{p.authorInitials}</Text>
              </View>
              <View className="flex-1">
                <Text style={{ color: '#ffffff' }} className="font-black text-sm">{p.author}</Text>
                <Text style={{ color: '#94a3b8' }} className="text-xs font-medium">{p.category} • {p.timeAgo}</Text>
              </View>
            </View>

            <Text style={{ color: '#e2e8f0' }} className="text-sm mb-4 leading-relaxed font-medium">
              {p.content}
            </Text>

            <View style={{ borderTopColor: '#1e293b' }} className="flex-row items-center space-x-6 pt-3 border-t">
              <TouchableOpacity 
                onPress={() => likePost(p.id)}
                className="flex-row items-center mr-4"
              >
                <Heart 
                  size={18} 
                  color={p.likedByMe ? '#f43f5e' : '#94a3b8'} 
                  fill={p.likedByMe ? '#f43f5e' : 'transparent'} 
                />
                <Text 
                  style={{ color: p.likedByMe ? '#f43f5e' : '#94a3b8' }} 
                  className="text-xs ml-1.5 font-bold"
                >
                  {p.likes}
                </Text>
              </TouchableOpacity>

              <TouchableOpacity 
                onPress={() => setActivePostForComments(p)}
                className="flex-row items-center"
              >
                <MessageSquare size={18} color="#94a3b8" />
                <Text style={{ color: '#94a3b8' }} className="text-xs ml-1.5 font-medium">
                  {p.commentsCount} comentarios
                </Text>
              </TouchableOpacity>
            </View>
          </View>
        ))}

        <View className="h-10" />
      </ScrollView>

      {/* MODAL: COMENTARIOS DEL POST */}
      <Modal
        visible={activePostForComments !== null}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setActivePostForComments(null)}
      >
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155', maxHeight: '85%' }} className="p-6">
            <View className="flex-row justify-between items-center pb-3 border-b border-slate-800 mb-3">
              <Text style={{ color: '#ffffff' }} className="text-lg font-black">Comentarios 💬</Text>
              <TouchableOpacity onPress={() => setActivePostForComments(null)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full">
                <X size={18} color="#94a3b8" />
              </TouchableOpacity>
            </View>

            <ScrollView className="max-h-60 mb-4" showsVerticalScrollIndicator={false}>
              {activePostForComments?.comments.length === 0 ? (
                <Text style={{ color: '#64748b' }} className="text-xs italic py-6 text-center">
                  Sin comentarios aún. ¡Sé el primero en comentar!
                </Text>
              ) : (
                activePostForComments?.comments.map((c) => (
                  <View key={c.id} style={{ backgroundColor: '#1e293b' }} className="p-3.5 rounded-2xl mb-2.5">
                    <View className="flex-row justify-between mb-1">
                      <Text style={{ color: '#34d399' }} className="font-bold text-xs">{c.author}</Text>
                      <Text style={{ color: '#64748b' }} className="text-[10px]">{c.time}</Text>
                    </View>
                    <Text style={{ color: '#ffffff' }} className="text-xs leading-relaxed">{c.text}</Text>
                  </View>
                ))
              )}
            </ScrollView>

            <View className="flex-row items-center">
              <TextInput
                value={commentText}
                onChangeText={setCommentText}
                placeholder="Escribe un comentario..."
                placeholderTextColor="#64748b"
                style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff' }}
                className="flex-1 px-4 py-3 rounded-2xl border text-xs mr-2"
              />
              <TouchableOpacity 
                onPress={handleSendComment}
                style={{ backgroundColor: '#10b981' }}
                className="p-3 rounded-2xl shadow-lg active:bg-emerald-600"
              >
                <Send size={16} color="#022c22" strokeWidth={2.5} />
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* MODAL: PUBLICAR NUEVO LOGRO / POST */}
      <Modal
        visible={showCreatePostModal}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setShowCreatePostModal(false)}
      >
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center pb-3 border-b border-slate-800 mb-4">
              <Text style={{ color: '#ffffff' }} className="text-xl font-black">Publicar Logro 🏆</Text>
              <TouchableOpacity onPress={() => setShowCreatePostModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="#94a3b8" />
              </TouchableOpacity>
            </View>

            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold mb-2 uppercase">Categoría</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} className="flex-row mb-4 -mx-1">
              {categories.map((cat) => {
                const isSel = newPostCategory === cat;
                return (
                  <TouchableOpacity
                    key={cat}
                    onPress={() => setNewPostCategory(cat)}
                    style={{
                      backgroundColor: isSel ? '#064e3b' : '#1e293b',
                      borderColor: isSel ? '#10b981' : '#334155',
                      borderWidth: 1
                    }}
                    className="px-3.5 py-2 rounded-xl mr-2"
                  >
                    <Text style={{ color: isSel ? '#34d399' : '#94a3b8' }} className="text-xs font-bold">
                      {cat}
                    </Text>
                  </TouchableOpacity>
                );
              })}
            </ScrollView>

            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold mb-2 uppercase">¿Qué logro quieres compartir con la comunidad?</Text>
            <TextInput
              value={newPostContent}
              onChangeText={setNewPostContent}
              placeholder="Ej: Completé mi meta de ahorro en USDT, o alcancé una racha de 20 días..."
              placeholderTextColor="#64748b"
              multiline
              numberOfLines={4}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff' }}
              className="p-4 rounded-2xl border text-sm mb-6 font-medium"
            />

            <TouchableOpacity 
              onPress={handleAddPost}
              style={{ backgroundColor: '#10b981' }}
              className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600"
            >
              <Text style={{ color: '#022c22' }} className="font-black text-base">Compartir con la Comunidad 🚀</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}
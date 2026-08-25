import { create } from 'zustand';

export interface Post {
  id: string;
  author: string;
  authorInitials: string;
  category: string;
  timeAgo: string;
  content: string;
  likes: number;
  likedByMe?: boolean;
  commentsCount: number;
  comments: { id: string; author: string; text: string; time: string }[];
}

export interface Group {
  id: string;
  name: string;
  members: number;
  desc: string;
  category: string;
  iconName: string;
  joined: boolean;
}

export interface GroupMessage {
  id: string;
  groupId: string;
  author: string;
  authorInitials: string;
  text: string;
  time: string;
  isMe: boolean;
}

export interface DirectMessage {
  id: string;
  chatId: string;
  sender: string;
  text: string;
  time: string;
  isMe: boolean;
}

export interface DirectChat {
  id: string;
  name: string;
  role: string;
  avatarText: string;
  lastMessage: string;
  time: string;
  unread: number;
  online: boolean;
}

export interface SupportTicket {
  id: string;
  subject: string;
  category: string;
  status: 'open' | 'resolved' | 'pending';
  date: string;
  messages: { sender: string; text: string; time: string }[];
}

interface SocialState {
  posts: Post[];
  groups: Group[];
  groupMessages: Record<string, GroupMessage[]>;
  directChats: DirectChat[];
  directMessages: Record<string, DirectMessage[]>;
  tickets: SupportTicket[];

  // Post Actions
  addPost: (content: string, category: string, author?: string) => void;
  likePost: (postId: string) => void;
  addComment: (postId: string, text: string, author?: string) => void;

  // Group Actions
  toggleJoinGroup: (groupId: string) => void;
  createGroup: (name: string, desc: string, category: string) => void;
  sendGroupMessage: (groupId: string, text: string, author?: string) => void;

  // Direct Message Actions
  sendDirectMessage: (chatId: string, text: string) => void;
  startDirectChat: (userName: string, initialMessage: string) => string;

  // Support Actions
  createTicket: (subject: string, category: string, message: string) => void;
}

export const useSocialStore = create<SocialState>((set, get) => ({
  posts: [
    {
      id: '1',
      author: 'Alejandro Mendoza',
      authorInitials: 'AM',
      category: 'Ahorro cumplido',
      timeAgo: 'Hace 2h',
      content: '🎯 ¡Completé mi meta de fondo de emergencia de 3 meses usando la tasa BCV! Muy emocionado con la disciplina financiera.',
      likes: 18,
      likedByMe: false,
      commentsCount: 4,
      comments: [
        { id: 'c1', author: 'Carlos Pérez', text: '¡Excelente logro hermano! A seguir ahorrando.', time: 'Hace 1h' },
        { id: 'c2', author: 'Valeria Gómez', text: 'Felicidades, la constancia rinde frutos.', time: 'Hace 45m' }
      ]
    },
    {
      id: '2',
      author: 'Valeria Gómez',
      authorInitials: 'VG',
      category: 'Productividad',
      timeAgo: 'Hace 5h',
      content: '⚡ Racha de 14 días completando todos mis hábitos en KoreLife. ¡La constancia y el control hacen la diferencia!',
      likes: 24,
      likedByMe: true,
      commentsCount: 7,
      comments: [
        { id: 'c3', author: 'Daniela Silva', text: '¿Qué hábitos tienes en tu rutina diaria?', time: 'Hace 3h' }
      ]
    },
    {
      id: '3',
      author: 'Ricardo Torres',
      authorInitials: 'RT',
      category: 'Inversiones',
      timeAgo: 'Hace 1d',
      content: '📊 Diversifiqué mis cuentas en Binance USDT y Banesco con las conversiones en tiempo real de KoreLife. Control total de mis finanzas.',
      likes: 31,
      likedByMe: false,
      commentsCount: 5,
      comments: []
    }
  ],

  groups: [
    {
      id: '1',
      name: 'Inversionistas Venezuela 🇻🇪',
      members: 1242,
      desc: 'Estrategias de inversión, tasas BCV y arbitraje P2P en Binance.',
      category: 'Finanzas',
      iconName: 'TrendingUp',
      joined: true
    },
    {
      id: '2',
      name: 'Productividad GTD & Hábitos ⚡',
      members: 893,
      desc: 'Técnicas de enfoque, Pomodoro, gestión de tareas y rutinas diarias.',
      category: 'Productividad',
      iconName: 'Sparkles',
      joined: true
    },
    {
      id: '3',
      name: 'Presupuestos & Ahorro 💰',
      members: 1530,
      desc: 'Consejos para optimizar gastos hormiga y crear fondos de emergencia.',
      category: 'Finanzas',
      iconName: 'BookOpen',
      joined: false
    },
    {
      id: '4',
      name: 'Emprendedores & Negocios 🚀',
      members: 670,
      desc: 'Comunidad de creadores de negocios y ventas en Venezuela.',
      category: 'Negocios',
      iconName: 'Users',
      joined: false
    }
  ],

  groupMessages: {
    '1': [
      { id: 'm1', groupId: '1', author: 'Roberto S.', authorInitials: 'RS', text: '¿A qué hora suele publicar el BCV la tasa de cierre hoy?', time: '09:15 AM', isMe: false },
      { id: 'm2', groupId: '1', author: 'Alejandro M.', authorInitials: 'AM', text: 'Normalmente entre 4:30 PM y 5:30 PM. KoreLife la actualiza al instante.', time: '09:20 AM', isMe: false },
      { id: 'm3', groupId: '1', author: 'Tú', authorInitials: 'YO', text: 'Excelente, gracias por el dato 👍', time: '09:35 AM', isMe: true }
    ],
    '2': [
      { id: 'm4', groupId: '2', author: 'Valeria G.', authorInitials: 'VG', text: '¿Quién más está usando la técnica Pomodoro para estudiar hoy?', time: '08:00 AM', isMe: false },
      { id: 'm5', groupId: '2', author: 'Tú', authorInitials: 'YO', text: '¡Yo! Ya llevo 3 bloques completados hoy.', time: '08:45 AM', isMe: true }
    ]
  },

  directChats: [
    {
      id: '1',
      name: 'Asistente Financiero IA 🤖',
      role: 'Asistente KoreLife',
      avatarText: 'IA',
      lastMessage: 'Tu presupuesto de alimentación está en 56%. ¡Excelente control!',
      time: '10:30 AM',
      unread: 1,
      online: true
    },
    {
      id: '2',
      name: 'Soporte Técnico KoreLife 🎧',
      role: 'Equipo Oficial',
      avatarText: 'ST',
      lastMessage: 'Tu consulta sobre tasas en tiempo real fue atendida con éxito.',
      time: 'Ayer',
      unread: 0,
      online: true
    },
    {
      id: '3',
      name: 'Alejandro Mendoza',
      role: 'Comunidad',
      avatarText: 'AM',
      lastMessage: 'Hola, ¿cómo configuraste la alerta de vencimiento de metas?',
      time: 'Mar 18',
      unread: 0,
      online: false
    }
  ],

  directMessages: {
    '1': [
      { id: 'dm1', chatId: '1', sender: 'Asistente Financiero IA', text: '¡Hola! Soy tu asistente de finanzas personales en KoreLife.', time: '10:00 AM', isMe: false },
      { id: 'dm2', chatId: '1', sender: 'Asistente Financiero IA', text: 'Tu patrimonio neto ha crecido este mes gracias a tu disciplina de ahorro.', time: '10:02 AM', isMe: false },
      { id: 'dm3', chatId: '1', sender: 'Asistente Financiero IA', text: 'Tu presupuesto de alimentación está en 56%. ¡Excelente control!', time: '10:30 AM', isMe: false }
    ],
    '2': [
      { id: 'dm4', chatId: '2', sender: 'Soporte Técnico', text: 'Hola, gracias por comunicarte con el equipo de KoreLife.', time: 'Ayer', isMe: false },
      { id: 'dm5', chatId: '2', sender: 'Soporte Técnico', text: 'Tu consulta sobre tasas en tiempo real fue atendida con éxito.', time: 'Ayer', isMe: false }
    ],
    '3': [
      { id: 'dm6', chatId: '3', sender: 'Alejandro Mendoza', text: 'Hola, ¿cómo configuraste la alerta de vencimiento de metas?', time: 'Mar 18', isMe: false },
      { id: 'dm7', chatId: '3', sender: 'Tú', text: '¡Hola Alejandro! Puedes activarla en la sección de Ajustes > Notificaciones.', time: 'Mar 18', isMe: true }
    ]
  },

  tickets: [
    {
      id: 'TK-101',
      subject: 'Consulta sobre sincronización de tasas BCV',
      category: 'Tasas & Finanzas',
      status: 'resolved',
      date: '2026-08-20',
      messages: [
        { sender: 'Tú', text: '¿A qué hora se refresca la tasa del BCV?', time: '20 Ago' },
        { sender: 'Soporte KoreLife', text: 'Se sincroniza automáticamente con la API oficial del BCV en tiempo real.', time: '20 Ago' }
      ]
    }
  ],

  addPost: (content, category, author = 'Tú') => {
    const newPost: Post = {
      id: Date.now().toString(),
      author,
      authorInitials: author === 'Tú' ? 'YO' : author.split(' ').map(n => n[0]).join('').substring(0, 2),
      category,
      timeAgo: 'Justo ahora',
      content,
      likes: 0,
      likedByMe: false,
      commentsCount: 0,
      comments: []
    };
    set((s) => ({ posts: [newPost, ...s.posts] }));
  },

  likePost: (postId) => {
    set((s) => ({
      posts: s.posts.map((p) => {
        if (p.id === postId) {
          const nextLiked = !p.likedByMe;
          return {
            ...p,
            likedByMe: nextLiked,
            likes: nextLiked ? p.likes + 1 : p.likes - 1
          };
        }
        return p;
      })
    }));
  },

  addComment: (postId, text, author = 'Tú') => {
    set((s) => ({
      posts: s.posts.map((p) => {
        if (p.id === postId) {
          const newComment = {
            id: Date.now().toString(),
            author,
            text,
            time: 'Ahora'
          };
          return {
            ...p,
            commentsCount: p.commentsCount + 1,
            comments: [...p.comments, newComment]
          };
        }
        return p;
      })
    }));
  },

  toggleJoinGroup: (groupId) => {
    set((s) => ({
      groups: s.groups.map((g) => {
        if (g.id === groupId) {
          const next = !g.joined;
          return {
            ...g,
            joined: next,
            members: next ? g.members + 1 : g.members - 1
          };
        }
        return g;
      })
    }));
  },

  createGroup: (name, desc, category) => {
    const newGroup: Group = {
      id: Date.now().toString(),
      name,
      desc,
      category,
      members: 1,
      iconName: 'Users',
      joined: true
    };
    set((s) => ({ groups: [newGroup, ...s.groups] }));
  },

  sendGroupMessage: (groupId, text, author = 'Tú') => {
    const msg: GroupMessage = {
      id: Date.now().toString(),
      groupId,
      author,
      authorInitials: 'YO',
      text,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isMe: true
    };
    set((s) => ({
      groupMessages: {
        ...s.groupMessages,
        [groupId]: [...(s.groupMessages[groupId] || []), msg]
      }
    }));
  },

  sendDirectMessage: (chatId, text) => {
    const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const msg: DirectMessage = {
      id: Date.now().toString(),
      chatId,
      sender: 'Tú',
      text,
      time,
      isMe: true
    };

    set((s) => {
      const updatedMessages = {
        ...s.directMessages,
        [chatId]: [...(s.directMessages[chatId] || []), msg]
      };

      const updatedChats = s.directChats.map((c) => {
        if (c.id === chatId) {
          return { ...c, lastMessage: text, time, unread: 0 };
        }
        return c;
      });

      return {
        directMessages: updatedMessages,
        directChats: updatedChats
      };
    });

    // Auto-response from AI Assistant or Support
    if (chatId === '1') {
      setTimeout(() => {
        const aiMsg: DirectMessage = {
          id: (Date.now() + 1).toString(),
          chatId: '1',
          sender: 'Asistente Financiero IA 🤖',
          text: '¡Entendido! He registrado tu consulta y estoy analizando tus datos en tiempo real.',
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          isMe: false
        };
        set((s) => ({
          directMessages: {
            ...s.directMessages,
            [chatId]: [...(s.directMessages[chatId] || []), aiMsg]
          },
          directChats: s.directChats.map((c) =>
            c.id === chatId ? { ...c, lastMessage: aiMsg.text } : c
          )
        }));
      }, 1000);
    }
  },

  startDirectChat: (userName, initialMessage) => {
    const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const newChatId = Date.now().toString();
    const newChat: DirectChat = {
      id: newChatId,
      name: userName,
      role: 'Usuario KoreLife',
      avatarText: userName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || 'US',
      lastMessage: initialMessage,
      time,
      unread: 0,
      online: true
    };

    const firstMsg: DirectMessage = {
      id: (Date.now() + 1).toString(),
      chatId: newChatId,
      sender: 'Tú',
      text: initialMessage,
      time,
      isMe: true
    };

    set((s) => ({
      directChats: [newChat, ...s.directChats],
      directMessages: {
        ...s.directMessages,
        [newChatId]: [firstMsg]
      }
    }));

    return newChatId;
  },

  createTicket: (subject, category, message) => {
    const newTicket: SupportTicket = {
      id: `TK-${Math.floor(100 + Math.random() * 900)}`,
      subject,
      category,
      status: 'open',
      date: new Date().toISOString().split('T')[0],
      messages: [
        { sender: 'Tú', text: message, time: 'Ahora' }
      ]
    };
    set((s) => ({ tickets: [newTicket, ...s.tickets] }));
  }
}));

export type Role = 'user' | 'staff' | 'superadmin';
export type Plan = 'free' | 'premium' | 'family' | 'team';

export interface User {
  id: string;
  email: string;
  full_name: string | null;
  avatar_url: string | null;
  role: Role;
  plan: Plan;
  status: 'active' | 'inactive';
  created_at: string;
}

export interface Ticket {
  id: string;
  user_id: string;
  subject: string;
  category: 'billing' | 'technical' | 'general' | 'feedback';
  priority: 'low' | 'medium' | 'high' | 'urgent';
  status: 'open' | 'in_progress' | 'resolved' | 'closed';
  created_at: string;
  updated_at: string;
  user?: Pick<User, 'full_name' | 'email' | 'avatar_url'>;
}

export interface FAQCategory {
  id: string;
  title: string;
  order: number;
}

export interface FAQArticle {
  id: string;
  category_id: string;
  question: string;
  answer: string;
  published: boolean;
}

export interface Announcement {
  id: string;
  title: string;
  content: string;
  type: 'info' | 'warning' | 'success' | 'new_feature';
  target_plan: Plan | 'all';
  active: boolean;
  expires_at: string | null;
  created_at: string;
}

import { create } from 'zustand';
import { Task, Habit, Note } from '../types';
import { useAuthStore } from './authStore';

type Project = {
  id: string;
  name: string;
  description?: string;
  color: string;
  tasks: number;
  completedTasks: number;
  deadline?: string;
  created_at: string;
};

type Subscription = {
  id: string;
  name: string;
  emoji: string;
  amount: number;
  currency: 'VES' | 'USD' | 'EUR';
  period: 'monthly' | 'yearly' | 'weekly';
  nextBilling: string;
  active: boolean;
};

type Goal = {
  id: string;
  title: string;
  emoji: string;
  targetAmount: number;
  currentAmount: number;
  currency: 'VES' | 'USD' | 'EUR';
  deadline: string;
  color: string;
};

const DEMO_TASKS: Task[] = [
  { id: '1', user_id: 'demo', title: 'Pagar factura de luz', completed: false, priority: 'high', due_date: '2026-08-25', created_at: new Date().toISOString() },
  { id: '2', user_id: 'demo', title: 'Llamar al banco Banesco', completed: true, priority: 'medium', due_date: '2026-08-24', created_at: new Date().toISOString() },
  { id: '3', user_id: 'demo', title: 'Comprar víveres del mes', completed: false, priority: 'medium', created_at: new Date().toISOString() },
  { id: '4', user_id: 'demo', title: 'Revisar presupuesto mensual', completed: false, priority: 'high', due_date: '2026-08-28', created_at: new Date().toISOString() },
];

const DEMO_HABITS: Habit[] = [
  { id: 'h1', user_id: 'demo', name: 'Ejercicio matutino', streak: 7, completed_today: false, frequency: 'daily', created_at: new Date().toISOString() },
  { id: 'h2', user_id: 'demo', name: 'Leer 20 minutos', streak: 3, completed_today: true, frequency: 'daily', created_at: new Date().toISOString() },
  { id: 'h3', user_id: 'demo', name: 'Registrar gastos del día', streak: 12, completed_today: false, frequency: 'daily', created_at: new Date().toISOString() },
];

const DEMO_GOALS: Goal[] = [
  { id: 'g1', title: 'Fondo de Emergencia', emoji: '🛡️', targetAmount: 5000, currentAmount: 2100, currency: 'USD', deadline: '2026-12-31', color: '#10b981' },
  { id: 'g2', title: 'Nuevo Teléfono', emoji: '📱', targetAmount: 1200, currentAmount: 900, currency: 'USD', deadline: '2026-09-30', color: '#f59e0b' },
];

type TasksState = {
  tasks: Task[];
  habits: Habit[];
  notes: Note[];
  projects: Project[];
  subscriptions: Subscription[];
  goals: Goal[];
  loading: boolean;
  fetchTasks: () => Promise<void>;
  addTask: (task: Omit<Task, 'id' | 'created_at'>) => void;
  toggleTask: (id: string) => void;
  deleteTask: (id: string) => void;
  addNote: (note: Omit<Note, 'id' | 'created_at'>) => void;
  deleteNote: (id: string) => void;
  addHabit: (habit: Omit<Habit, 'id' | 'created_at'>) => void;
  toggleHabit: (id: string) => void;
  addProject: (project: Omit<Project, 'id' | 'created_at'>) => void;
  addGoal: (goal: Omit<Goal, 'id'>) => void;
  updateGoalAmount: (id: string, amount: number) => void;
  toggleSubscription: (id: string) => void;
};

export const useTasksStore = create<TasksState>((set, get) => ({
  tasks: [],
  habits: [],
  notes: [],
  projects: [],
  subscriptions: [],
  goals: [],
  loading: false,

  fetchTasks: async () => {
    set({ loading: true });
    const { user } = useAuthStore.getState();

    if (user && user.id) {
      // USUARIO REAL: Empezar con lista limpia personalizada para el usuario
      set((state) => {
        if (state.tasks.length === 0 && state.projects.length === 0) {
          return {
            tasks: [
              {
                id: 't_init_' + Date.now(),
                user_id: user.id,
                title: '¡Bienvenido a KoreLife! Completa tu perfil',
                completed: false,
                priority: 'medium',
                created_at: new Date().toISOString()
              }
            ],
            habits: [
              {
                id: 'h_init_' + Date.now(),
                user_id: user.id,
                name: 'Registrar mis gastos diarios',
                streak: 0,
                completed_today: false,
                frequency: 'daily',
                created_at: new Date().toISOString()
              }
            ],
            notes: [],
            projects: [
              {
                id: 'p_main_' + Date.now(),
                name: 'Mis Proyectos Personales',
                description: 'Organización general',
                color: '#10b981',
                tasks: 1,
                completedTasks: 0,
                created_at: new Date().toISOString()
              }
            ],
            subscriptions: [],
            goals: [],
            loading: false
          };
        }
        return { loading: false };
      });
    } else {
      // MODO DEMO:
      set({
        tasks: DEMO_TASKS,
        habits: DEMO_HABITS,
        notes: [],
        projects: [],
        subscriptions: [],
        goals: DEMO_GOALS,
        loading: false
      });
    }
  },

  addTask: (task) => {
    const { user } = useAuthStore.getState();
    const newTask: Task = {
      ...task,
      user_id: user?.id || 'demo',
      id: 'task_' + Date.now(),
      created_at: new Date().toISOString(),
    };
    set((state) => ({ tasks: [newTask, ...state.tasks] }));
  },

  toggleTask: (id) => {
    set((state) => ({
      tasks: state.tasks.map((t) =>
        t.id === id ? { ...t, completed: !t.completed } : t
      ),
    }));
  },

  deleteTask: (id) => {
    set((state) => ({ tasks: state.tasks.filter((t) => t.id !== id) }));
  },

  addNote: (note) => {
    const { user } = useAuthStore.getState();
    const newNote: Note = {
      ...note,
      user_id: user?.id || 'demo',
      id: 'note_' + Date.now(),
      created_at: new Date().toISOString(),
    };
    set((state) => ({ notes: [newNote, ...state.notes] }));
  },

  deleteNote: (id) => {
    set((state) => ({ notes: state.notes.filter((n) => n.id !== id) }));
  },

  addHabit: (habit) => {
    const { user } = useAuthStore.getState();
    const newHabit: Habit = {
      ...habit,
      user_id: user?.id || 'demo',
      id: 'habit_' + Date.now(),
      created_at: new Date().toISOString(),
    };
    set((state) => ({ habits: [...state.habits, newHabit] }));
  },

  toggleHabit: (id) => {
    set((state) => ({
      habits: state.habits.map((h) =>
        h.id === id
          ? { ...h, completed_today: !h.completed_today, streak: !h.completed_today ? h.streak + 1 : Math.max(0, h.streak - 1) }
          : h
      ),
    }));
  },

  addProject: (project) => {
    const newProject: Project = {
      ...project,
      id: 'proj_' + Date.now(),
      created_at: new Date().toISOString(),
    };
    set((state) => ({ projects: [...state.projects, newProject] }));
  },

  addGoal: (goal) => {
    const newGoal: Goal = { ...goal, id: 'goal_' + Date.now() };
    set((state) => ({ goals: [...state.goals, newGoal] }));
  },

  updateGoalAmount: (id, amount) => {
    set((state) => ({
      goals: state.goals.map((g) =>
        g.id === id ? { ...g, currentAmount: Math.min(g.targetAmount, g.currentAmount + amount) } : g
      ),
    }));
  },

  toggleSubscription: (id) => {
    set((state) => ({
      subscriptions: state.subscriptions.map((s) =>
        s.id === id ? { ...s, active: !s.active } : s
      ),
    }));
  },
}));

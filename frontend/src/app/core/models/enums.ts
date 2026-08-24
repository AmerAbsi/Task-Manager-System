export type Role = 'ADMIN' | 'USER';


export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';



export const ROLES: Role[] = ['ADMIN', 'USER'];


export const TASK_STATUSES: TaskStatus[] = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];



export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
  PENDING: 'Pending',
  IN_PROGRESS: 'In Progress',
  COMPLETED: 'Completed'
};



export const TASK_STATUS_CLASSES: Record<TaskStatus, string> = {
  PENDING: 'bg-secondary',
  IN_PROGRESS: 'bg-warning text-dark',
  COMPLETED: 'bg-success'
};


export const ACTION_TYPES = [
  'LOGIN', 'LOGOUT',
  'USER_CREATED', 'USER_UPDATED', 'USER_DELETED',
  'TASK_CREATED', 'TASK_UPDATED', 'TASK_STATUS_CHANGED', 'TASK_DELETED',
  'COMMENT_ADDED', 'PROFILE_UPDATED'
];

export const ACTION_LABELS: Record<string, string> = {
  LOGIN: 'Login',
  LOGOUT: 'Logout',
  USER_CREATED: 'User created',
  USER_UPDATED: 'User updated',
  USER_DELETED: 'User deleted',
  TASK_CREATED: 'Task created',
  TASK_UPDATED: 'Task updated',
  TASK_STATUS_CHANGED: 'Status changed',
  TASK_DELETED: 'Task deleted',
  COMMENT_ADDED: 'Comment added',
  PROFILE_UPDATED: 'Profile updated'
};

export const ACTION_ICONS: Record<string, string> = {
  LOGIN: 'bi-box-arrow-in-right',
  LOGOUT: 'bi-box-arrow-right',
  USER_CREATED: 'bi-person-plus',
  USER_UPDATED: 'bi-person-gear',
  USER_DELETED: 'bi-person-dash',
  TASK_CREATED: 'bi-plus-square',
  TASK_UPDATED: 'bi-pencil-square',
  TASK_STATUS_CHANGED: 'bi-arrow-repeat',
  TASK_DELETED: 'bi-trash',
  COMMENT_ADDED: 'bi-chat-left-text',
  PROFILE_UPDATED: 'bi-person-badge'
};
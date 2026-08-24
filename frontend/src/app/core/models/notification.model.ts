export interface AppNotification {
  id: number;
  type: string;
  message: string;
  read: boolean;
  taskId: number | null;
  taskTitle: string | null;
  createdAt: string;
}
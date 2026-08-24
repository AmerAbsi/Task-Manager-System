import { TaskStatus } from './enums';

export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  dueDate: string | null;
  assignedUserId: number | null;
  assignedUserName: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TaskRequest {
  title: string;
  description: string | null;
  status: TaskStatus;
  dueDate: string | null;
  assignedUserId: number | null;
}

export interface TaskStatusUpdateRequest {
  status: TaskStatus;
}
import { Task } from '../models/task.model';

export function buildPageNumbers(totalPages: number): number[] {
  return Array.from({ length: totalPages }, (_, i) => i);
}

export function isTaskOverdue(task: Task): boolean {
  if (!task.dueDate || task.status === 'COMPLETED') {
    return false;
  }
  return task.dueDate < new Date().toISOString().slice(0, 10);
}
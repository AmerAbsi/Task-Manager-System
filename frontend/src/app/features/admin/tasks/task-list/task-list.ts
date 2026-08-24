import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TaskService } from '../../../../core/services/task.service';
import { UserService } from '../../../../core/services/user.service';
import { Task } from '../../../../core/models/task.model';
import { User } from '../../../../core/models/user.model';
import { TaskStatus, TASK_STATUSES } from '../../../../core/models/enums';
import { StatusBadge } from '../../../../shared/components/status-badge/status-badge';
import { ConfirmDialog } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { TaskForm } from '../task-form/task-form';
import { buildPageNumbers, isTaskOverdue } from '../../../../core/utils/table-utils';
import { Router } from '@angular/router';
@Component({
  selector: 'app-task-list',
  imports: [FormsModule, DatePipe, RouterLink, StatusBadge, ConfirmDialog, TaskForm],
  templateUrl: './task-list.html'
})
export class TaskList implements OnInit {

  private taskService = inject(TaskService);
  private userService = inject(UserService);
  private router = inject(Router);
  tasks = signal<Task[]>([]);
  assignableUsers = signal<User[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);

  showForm = signal(false);
  editingTask = signal<Task | null>(null);
  deletingTask = signal<Task | null>(null);

  searchTerm = '';
  statusFilter: TaskStatus | '' = '';
  assigneeFilter: number | '' = '';

  statuses = TASK_STATUSES;

  ngOnInit() {
    this.loadUsers();
    this.load();
  }

  loadUsers() {
    this.userService.search({ size: 100 }).subscribe({
      next: result => this.assignableUsers.set(result.content)
    });
  }

  load(page = 0) {
    this.loading.set(true);
    this.errorMessage.set('');

    this.taskService.search({
      search: this.searchTerm,
      status: this.statusFilter,
      assignedUserId: this.assigneeFilter === '' ? null : this.assigneeFilter,
      page: page,
      size: 10
    }).subscribe({
      next: result => {
        this.tasks.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.currentPage.set(result.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load tasks.');
        this.loading.set(false);
      }
    });
  }

  applyFilters() {
    this.load(0);
  }

  resetFilters() {
    this.searchTerm = '';
    this.statusFilter = '';
    this.assigneeFilter = '';
    this.load(0);
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.load(page);
    }
  }

  pageNumbers(): number[] {
    return buildPageNumbers(this.totalPages());
  }

  openCreate() {
    this.editingTask.set(null);
    this.showForm.set(true);
  }

  openEdit(task: Task) {
    this.editingTask.set(task);
    this.showForm.set(true);
  }

  closeForm() {
    this.showForm.set(false);
    this.editingTask.set(null);
  }

  onSaved() {
    this.closeForm();
    this.load(this.currentPage());
  }

  confirmDelete(task: Task) {
    this.deletingTask.set(task);
  }

  cancelDelete() {
    this.deletingTask.set(null);
  }

  performDelete() {
    const task = this.deletingTask();
    if (!task) {
      return;
    }

    this.taskService.delete(task.id).subscribe({
      next: () => {
        this.deletingTask.set(null);
        this.load(this.currentPage());
      },
      error: () => {
        this.deletingTask.set(null);
        this.errorMessage.set('Could not delete task.');
      }
    });
  }

  isOverdue(task: Task): boolean {
    return isTaskOverdue(task);
  }

  openTask(id: number) {
  this.router.navigate(['/tasks', id]);
}
}
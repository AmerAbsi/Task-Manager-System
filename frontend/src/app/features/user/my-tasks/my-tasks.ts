import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TaskService } from '../../../core/services/task.service';
import { AuthService } from '../../../core/services/auth.service';
import { Task } from '../../../core/models/task.model';
import { TaskStatus, TASK_STATUSES, TASK_STATUS_LABELS } from '../../../core/models/enums';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';
import { buildPageNumbers, isTaskOverdue } from '../../../core/utils/table-utils';
@Component({
  selector: 'app-my-tasks',
  imports: [FormsModule, DatePipe, RouterLink, StatusBadge],
  templateUrl: './my-tasks.html'
})
export class MyTasks implements OnInit {

  private taskService = inject(TaskService);
  authService = inject(AuthService);

  tasks = signal<Task[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  totalPages = signal(0);
  currentPage = signal(0);

  statusFilter: TaskStatus | '' = '';
  statuses = TASK_STATUSES;
  statusLabels = TASK_STATUS_LABELS;

  pendingCount = computed(() =>
    this.tasks().filter(t => t.status === 'PENDING').length
  );

  inProgressCount = computed(() =>
    this.tasks().filter(t => t.status === 'IN_PROGRESS').length
  );

  completedCount = computed(() =>
    this.tasks().filter(t => t.status === 'COMPLETED').length
  );

  ngOnInit() {
    this.load();
  }

  load(page = 0) {
    this.loading.set(true);
    this.errorMessage.set('');

    this.taskService.search({
      status: this.statusFilter,
      page: page,
      size: 10
    }).subscribe({
      next: result => {
        this.tasks.set(result.content);
        this.totalPages.set(result.totalPages);
        this.currentPage.set(result.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load your tasks.');
        this.loading.set(false);
      }
    });
  }

  applyFilter() {
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

  isOverdue(task: Task): boolean {
    return isTaskOverdue(task);
  }
}
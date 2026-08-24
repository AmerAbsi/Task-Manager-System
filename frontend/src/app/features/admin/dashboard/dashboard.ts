import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { DashboardService } from '../../../core/services/dashboard.service';
import { TaskService } from '../../../core/services/task.service';
import { AuthService } from '../../../core/services/auth.service';
import { DashboardStats } from '../../../core/models/dashboard.model';
import { Task } from '../../../core/models/task.model';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, DatePipe, StatusBadge],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {

  private dashboardService = inject(DashboardService);
  private taskService = inject(TaskService);

  authService = inject(AuthService);

  stats = signal<DashboardStats | null>(null);
  recentTasks = signal<Task[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  completionRate = computed(() => {
    const s = this.stats();
    if (!s || s.totalTasks === 0) {
      return 0;
    }
    return Math.round((s.completedTasks / s.totalTasks) * 100);
  });

  ngOnInit() {
    this.loadStats();
    this.loadRecentTasks();
  }

  loadStats() {
    this.dashboardService.getStats().subscribe({
      next: stats => {
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load statistics.');
        this.loading.set(false);
      }
    });
  }

  loadRecentTasks() {
    this.taskService.search({ page: 0, size: 5 }).subscribe({
      next: result => this.recentTasks.set(result.content)
    });
  }

  percentage(value: number): number {
    const total = this.stats()?.totalTasks ?? 0;
    return total === 0 ? 0 : Math.round((value / total) * 100);
  }
}
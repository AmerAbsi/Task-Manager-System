import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ActivityLogService } from '../../../core/services/activity-log.service';
import { ActivityLog } from '../../../core/models/activity-log.model';
import { ACTION_TYPES, ACTION_LABELS, ACTION_ICONS } from '../../../core/models/enums';
import { buildPageNumbers } from '../../../core/utils/table-utils';

@Component({
  selector: 'app-activity-log',
  imports: [FormsModule, DatePipe],
  templateUrl: './activity-log.html',
  styleUrl: './activity-log.css'
})
export class ActivityLogPage implements OnInit {

  private activityLogService = inject(ActivityLogService);

  logs = signal<ActivityLog[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);

  actionFilter = '';
  usernameFilter = '';

  actionTypes = ACTION_TYPES;
  actionLabels = ACTION_LABELS;
  actionIcons = ACTION_ICONS;

  ngOnInit() {
    this.load();
  }

  load(page = 0) {
    this.loading.set(true);
    this.errorMessage.set('');

    this.activityLogService.search({
      action: this.actionFilter,
      username: this.usernameFilter,
      page: page,
      size: 20
    }).subscribe({
      next: result => {
        this.logs.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.currentPage.set(result.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load activity log.');
        this.loading.set(false);
      }
    });
  }

  applyFilters() {
    this.load(0);
  }

  resetFilters() {
    this.actionFilter = '';
    this.usernameFilter = '';
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

  iconClass(action: string): string {
    return this.actionIcons[action] ?? 'bi-circle';
  }

  label(action: string): string {
    return this.actionLabels[action] ?? action;
  }

  isDestructive(action: string): boolean {
    return action.endsWith('_DELETED');
  }
}
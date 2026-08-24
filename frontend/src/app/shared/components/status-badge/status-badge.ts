import { Component, input, computed } from '@angular/core';
import { TaskStatus, TASK_STATUS_LABELS, TASK_STATUS_CLASSES } from '../../../core/models/enums';

@Component({
  selector: 'app-status-badge',
  imports: [],
  template: `<span class="badge {{ badgeClass() }}">{{ label() }}</span>`
})
export class StatusBadge {

  status = input.required<TaskStatus>();

  label = computed(() => TASK_STATUS_LABELS[this.status()]);
  badgeClass = computed(() => TASK_STATUS_CLASSES[this.status()]);
}
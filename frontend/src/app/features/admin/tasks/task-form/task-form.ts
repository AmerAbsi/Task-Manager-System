import { Component, inject, input, output, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskService } from '../../../../core/services/task.service';
import { Task } from '../../../../core/models/task.model';
import { User } from '../../../../core/models/user.model';
import { TASK_STATUSES, TASK_STATUS_LABELS } from '../../../../core/models/enums';

@Component({
  selector: 'app-task-form',
  imports: [ReactiveFormsModule],
  templateUrl: './task-form.html'
})
export class TaskForm implements OnInit {

  private fb = inject(FormBuilder);
  private taskService = inject(TaskService);

  editingTask = input<Task | null>(null);
  users = input<User[]>([]);

  closed = output<void>();
  saved = output<void>();

  saving = signal(false);
  errorMessage = signal('');

  statuses = TASK_STATUSES;
  statusLabels = TASK_STATUS_LABELS;

  form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', Validators.maxLength(2000)],
    status: ['PENDING', Validators.required],
    dueDate: [''],
    assignedUserId: ['']
  });

  get isEditMode(): boolean {
    return this.editingTask() !== null;
  }

  ngOnInit() {
    const task = this.editingTask();

    if (task) {
      this.form.patchValue({
        title: task.title,
        description: task.description ?? '',
        status: task.status,
        dueDate: task.dueDate ?? '',
        assignedUserId: task.assignedUserId ? String(task.assignedUserId) : ''
      });
    }
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set('');

    const value = this.form.value;

    const payload = {
      title: value.title!,
      description: value.description ? value.description : null,
      status: value.status as any,
      dueDate: value.dueDate ? value.dueDate : null,
      assignedUserId: value.assignedUserId ? Number(value.assignedUserId) : null
    };

    const task = this.editingTask();

    const request = task
      ? this.taskService.update(task.id, payload)
      : this.taskService.create(payload);

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.saved.emit();
      },
      error: err => {
        this.saving.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not save task.');
      }
    });
  }

  close() {
    this.closed.emit();
  }
}
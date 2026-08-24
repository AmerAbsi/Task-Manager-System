import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../core/services/task.service';
import { CommentService } from '../../../core/services/comment.service';
import { AuthService } from '../../../core/services/auth.service';
import { Task } from '../../../core/models/task.model';
import { Comment } from '../../../core/models/comment.model';
import { TaskStatus, TASK_STATUSES, TASK_STATUS_LABELS } from '../../../core/models/enums';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';
import { CommentThread } from '../../../shared/components/comment-thread/comment-thread';

@Component({
  selector: 'app-task-detail',
  imports: [RouterLink, DatePipe, FormsModule, StatusBadge, CommentThread],
  templateUrl: './task-detail.html'
})
export class TaskDetail implements OnInit {

  private route = inject(ActivatedRoute);
  private taskService = inject(TaskService);
  private commentService = inject(CommentService);

  authService = inject(AuthService);

  task = signal<Task | null>(null);
  comments = signal<Comment[]>([]);
  loading = signal(true);
  errorMessage = signal('');
  savingStatus = signal(false);

  newComment = '';
  selectedStatus: TaskStatus = 'PENDING';

  statuses = TASK_STATUSES;
  statusLabels = TASK_STATUS_LABELS;

  private taskId = 0;

  ngOnInit() {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadTask();
    this.loadComments();
  }

  loadTask() {
    this.taskService.getById(this.taskId).subscribe({
      next: task => {
        this.task.set(task);
        this.selectedStatus = task.status;
        this.loading.set(false);
      },
      error: err => {
        this.errorMessage.set(
          err.status === 403
            ? 'You do not have permission to view this task.'
            : 'Task not found.'
        );
        this.loading.set(false);
      }
    });
  }

  loadComments() {
    this.commentService.getByTask(this.taskId).subscribe({
      next: comments => this.comments.set(comments)
    });
  }

  changeStatus() {
    this.savingStatus.set(true);

    this.taskService.updateStatus(this.taskId, { status: this.selectedStatus })
      .subscribe({
        next: updated => {
          this.task.set(updated);
          this.savingStatus.set(false);
        },
        error: () => {
          this.errorMessage.set('Could not update status.');
          this.savingStatus.set(false);
        }
      });
  }

  postComment() {
    const content = this.newComment.trim();
    if (!content) {
      return;
    }

    this.commentService.add(this.taskId, { content, parentId: null }).subscribe({
      next: () => {
        this.newComment = '';
        this.loadComments();
      },
      error: () => this.errorMessage.set('Could not post comment.')
    });
  }

  postReply(event: { parentId: number; content: string }) {
    this.commentService.add(this.taskId, {
      content: event.content,
      parentId: event.parentId
    }).subscribe({
      next: () => this.loadComments(),
      error: () => this.errorMessage.set('Could not post reply.')
    });
  }
}
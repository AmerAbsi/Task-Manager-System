import { Component, inject, signal, effect, OnInit, OnDestroy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../../../core/services/notification.service';
import { WebSocketService } from '../../../core/services/websocket.service';
import { AppNotification } from '../../../core/models/notification.model';
import { AuthService } from '../../../core/services/auth.service';
@Component({
  selector: 'app-notification-bell',
  imports: [DatePipe],
  templateUrl: './notification-bell.html',
  styleUrl: './notification-bell.css'
})
export class NotificationBell implements OnInit, OnDestroy {

  private notificationService = inject(NotificationService);
  private webSocketService = inject(WebSocketService);
  private router = inject(Router);
  private authService = inject(AuthService);
  notifications = signal<AppNotification[]>([]);
  unreadCount = signal(0);
  panelOpen = signal(false);
  loading = signal(false);

  constructor() {
    effect(() => {
      const incoming = this.webSocketService.latestNotification();

      if (incoming) {
        this.notifications.update(list => [incoming, ...list].slice(0, 10));
        this.unreadCount.update(count => count + 1);
      }
    });
  }

  ngOnInit() {
    const token = this.authService.getToken();
    if (token) {
      this.webSocketService.connect(token);
    }
    this.loadUnreadCount();
  }

  ngOnDestroy() {
    this.webSocketService.disconnect();
  }

  loadUnreadCount() {
    this.notificationService.getUnreadCount().subscribe({
      next: result => this.unreadCount.set(result.count)
    });
  }

  togglePanel() {
    const opening = !this.panelOpen();
    this.panelOpen.set(opening);

    if (opening) {
      this.loadNotifications();
    }
  }

  closePanel() {
    this.panelOpen.set(false);
  }

  loadNotifications() {
    this.loading.set(true);

    this.notificationService.getMine(0, 10).subscribe({
      next: result => {
        this.notifications.set(result.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  open(notification: AppNotification) {
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          this.notifications.update(list =>
            list.map(n => n.id === notification.id ? { ...n, read: true } : n)
          );
          this.unreadCount.update(count => Math.max(0, count - 1));
        }
      });
    }

    this.closePanel();

    if (notification.taskId) {
      this.router.navigate(['/tasks', notification.taskId]);
    }
  }

  markAllRead() {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.update(list => list.map(n => ({ ...n, read: true })));
        this.unreadCount.set(0);
      }
    });
  }

  iconFor(type: string): string {
    const icons: Record<string, string> = {
      TASK_ASSIGNED: 'bi-person-check',
      TASK_STATUS_CHANGED: 'bi-arrow-repeat',
      COMMENT_ADDED: 'bi-chat-left-text',
      USER_MODIFIED: 'bi-person-gear'
    };
    return icons[type] ?? 'bi-bell';
  }
}
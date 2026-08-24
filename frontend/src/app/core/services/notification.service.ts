import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from '../models/page.model';
import { AppNotification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/notifications`;

  getMine(page = 0, size = 10) {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<Page<AppNotification>>(this.baseUrl, { params });
  }

  getUnreadCount() {
    return this.http.get<{ count: number }>(`${this.baseUrl}/unread-count`);
  }

  markAsRead(id: number) {
    return this.http.patch<AppNotification>(`${this.baseUrl}/${id}/read`, {});
  }

  markAllAsRead() {
    return this.http.patch<{ updated: number }>(`${this.baseUrl}/read-all`, {});
  }
}
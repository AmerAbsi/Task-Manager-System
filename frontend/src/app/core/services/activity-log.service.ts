import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from '../models/page.model';
import { ActivityLog } from '../models/activity-log.model';

@Injectable({ providedIn: 'root' })
export class ActivityLogService {

  private http = inject(HttpClient);

  search(filters: {
    action?: string;
    username?: string;
    page?: number;
    size?: number;
  }) {
    let params = new HttpParams();

    if (filters.action) {
      params = params.set('action', filters.action);
    }
    if (filters.username) {
      params = params.set('username', filters.username);
    }
    params = params.set('page', filters.page ?? 0);
    params = params.set('size', filters.size ?? 20);

    return this.http.get<Page<ActivityLog>>(
      `${environment.apiUrl}/activity-logs`,
      { params }
    );
  }
}
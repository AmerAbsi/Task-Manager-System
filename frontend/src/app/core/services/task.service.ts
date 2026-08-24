import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from '../models/page.model';
import { Task, TaskRequest, TaskStatusUpdateRequest } from '../models/task.model';
import { TaskStatus } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class TaskService {

  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/tasks`;

  search(filters: {
    search?: string;
    status?: TaskStatus | '';
    assignedUserId?: number | null;
    page?: number;
    size?: number;
  }) {
    let params = new HttpParams();

    if (filters.search) {
      params = params.set('search', filters.search);
    }
    if (filters.status) {
      params = params.set('status', filters.status);
    }
    if (filters.assignedUserId) {
      params = params.set('assignedUserId', filters.assignedUserId);
    }
    params = params.set('page', filters.page ?? 0);
    params = params.set('size', filters.size ?? 10);

    return this.http.get<Page<Task>>(this.baseUrl, { params });
  }

  getById(id: number) {
    return this.http.get<Task>(`${this.baseUrl}/${id}`);
  }

  create(task: TaskRequest) {
    return this.http.post<Task>(this.baseUrl, task);
  }

  update(id: number, task: TaskRequest) {
    return this.http.put<Task>(`${this.baseUrl}/${id}`, task);
  }

  updateStatus(id: number, request: TaskStatusUpdateRequest) {
    return this.http.patch<Task>(`${this.baseUrl}/${id}/status`, request);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
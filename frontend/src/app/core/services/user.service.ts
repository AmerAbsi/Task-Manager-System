import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from '../models/page.model';
import { User, UserRequest, UserUpdateRequest, ProfileUpdateRequest } from '../models/user.model';
import { Role } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class UserService {

  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/users`;

  search(filters: {
    search?: string;
    role?: Role | '';
    active?: boolean | '';
    page?: number;
    size?: number;
  }) {
    let params = new HttpParams();

    if (filters.search) {
      params = params.set('search', filters.search);
    }
    if (filters.role) {
      params = params.set('role', filters.role);
    }
    if (filters.active !== '' && filters.active !== undefined) {
      params = params.set('active', filters.active);
    }
    params = params.set('page', filters.page ?? 0);
    params = params.set('size', filters.size ?? 10);

    return this.http.get<Page<User>>(this.baseUrl, { params });
  }

  getById(id: number) {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  create(user: UserRequest) {
    return this.http.post<User>(this.baseUrl, user);
  }

  update(id: number, user: UserUpdateRequest) {
    return this.http.put<User>(`${this.baseUrl}/${id}`, user);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

    getProfile() {
    return this.http.get<User>(`${environment.apiUrl}/profile`);
  }

  updateProfile(request: ProfileUpdateRequest) {
    return this.http.put<User>(`${environment.apiUrl}/profile`, request);
  }
}
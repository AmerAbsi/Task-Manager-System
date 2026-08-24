import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthUser, LoginRequest, LoginResponse } from '../models/auth.model';
import { WebSocketService } from './websocket.service';
const TOKEN_KEY = 'tm_token';
const USER_KEY = 'tm_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private router = inject(Router);
  private webSocketService = inject(WebSocketService);
  currentUser = signal<AuthUser | null>(this.readUserFromStorage());

  isLoggedIn = computed(() => this.currentUser() !== null);

  isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');

  login(credentials: LoginRequest) {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => this.saveSession(response))
      );
  }

  logout() {
    this.http.post(`${environment.apiUrl}/auth/logout`, {}).subscribe({
      next: () => this.clearSession(),
      error: () => this.clearSession()
    });
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

    clearSession() {
    this.webSocketService.disconnect();
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private saveSession(response: LoginResponse) {
    const user: AuthUser = {
      userId: response.userId,
      username: response.username,
      fullName: response.fullName,
      role: response.role
    };

    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  private readUserFromStorage(): AuthUser | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }


  updateStoredUser(user: AuthUser) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  
}
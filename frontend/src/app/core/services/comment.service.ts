import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Comment, CommentRequest } from '../models/comment.model';

@Injectable({ providedIn: 'root' })
export class CommentService {

  private http = inject(HttpClient);

  getByTask(taskId: number) {
    return this.http.get<Comment[]>(
      `${environment.apiUrl}/tasks/${taskId}/comments`
    );
  }

  add(taskId: number, request: CommentRequest) {
    return this.http.post<Comment>(
      `${environment.apiUrl}/tasks/${taskId}/comments`,
      request
    );
  }
}
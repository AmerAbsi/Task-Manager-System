import { Component, input, output, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Comment } from '../../../core/models/comment.model';

@Component({
  selector: 'app-comment-thread',
  imports: [DatePipe, FormsModule],
  templateUrl: './comment-thread.html',
  styleUrl: './comment-thread.css'
})
export class CommentThread {

  comment = input.required<Comment>();
  depth = input(0);
  maxDepth = input(3);

  replyRequested = output<{ parentId: number; content: string }>();

  showReplyBox = signal(false);
  replyText = '';
  collapsed = signal(false);

  get canNest(): boolean {
    return this.depth() < this.maxDepth();
  }

  get hasReplies(): boolean {
    return this.comment().replies.length > 0;
  }

  initials(name: string): string {
    return name.split(' ').map(p => p.charAt(0)).slice(0, 2).join('').toUpperCase();
  }

  toggleCollapsed() {
    this.collapsed.update(value => !value);
  }

  toggleReply() {
    this.showReplyBox.update(open => !open);
    this.replyText = '';
  }

  submitReply() {
    const text = this.replyText.trim();
    if (!text) {
      return;
    }

    this.replyRequested.emit({
      parentId: this.comment().id,
      content: text
    });

    this.replyText = '';
    this.showReplyBox.set(false);
  }

  bubbleReply(event: { parentId: number; content: string }) {
    this.replyRequested.emit(event);
  }

    get isFlat(): boolean {
    return this.depth() >= this.maxDepth();
  }
}
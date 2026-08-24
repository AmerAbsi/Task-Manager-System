export interface Comment {
  id: number;
  content: string;
  authorId: number;
  authorName: string;
  parentId: number | null;
  createdAt: string;
  replies: Comment[];
}

export interface CommentRequest {
  content: string;
  parentId: number | null;
}
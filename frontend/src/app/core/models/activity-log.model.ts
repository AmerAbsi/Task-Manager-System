export interface ActivityLog {
  id: number;
  action: string;
  details: string;
  userId: number | null;
  username: string;
  fullName: string | null;
  createdAt: string;
}